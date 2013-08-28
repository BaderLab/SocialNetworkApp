package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JRadioButton;


import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Tools for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites {
	
	/**
	 * Reference to Incites radio button
	 */
	public static JRadioButton incitesRadioButton = null;

	/**
	 * Author location map
	 */
	private static Map<String, String> locationMap = null;

	/**
	 * # of defective rows in Incites document
	 */
	private int defectiveRows = 0;

	/**
	 * Name of faculty extracted from Incites data file
	 */
	private String facultyName = null;

	/**
	 * Set of faculty members extracted from Incites data file
	 */
	private HashSet<Author> facultySet = null;

	/**
	 * List of all identified faculty members
	 */
	private ArrayList<Author> identifiedFacultyList = null;

	/**
	 * # of ignored rows in Incites document
	 * (not including the column titles)
	 */
	private int ignoredRows = -1;

	/**
	 * List containing all lone authors found in
	 * Incites document. A lone author is one 
	 * who has only published once and whose sole
	 * publication is a solo effort
	 */
	private ArrayList<Author> loneAuthorList = null;

	/**
	 * List of publications extracted from Incites data file
	 */
	private ArrayList<Publication> pubList = null;

	/**
	 * List of all unidentified faculty members
	 */
	private ArrayList<Author> unidentifiedFacultyList = null;


	/**
	 * Create new Incites using data file
	 * @param File file
	 * @return null
	 */
	public Incites(File file) {
		this.loadFaculty(file);
		this.loadPubList(file);
		this.calcFacultyStats();
	}

	/**
	 * Calculate faculty stats
	 * i.e. which faculty members got identified?
	 * <br> which ones didn't?
	 * @param null
	 * @return null
	 */
	private void calcFacultyStats() {
		ArrayList<Author> identifiedAuthors = new ArrayList<Author>();
		ArrayList<Author> unidentifiedAuthors = new ArrayList<Author>();
		Author author = null;
		for (Object object : this.getFacultySet().toArray()) {
			author = (Author) object;
			if (author.isIdentified()) {
				identifiedAuthors.add(author);
			} else {
				unidentifiedAuthors.add(author);
			}
		}
		this.setIdentifiedFacultyList(identifiedAuthors);
		this.setUnidentifiedFacultyList(unidentifiedAuthors);
	}

	/**
	 * Get XLSX sheet parser
	 * @param SharedStringsTable sst
	 * @param int sheet
	 * @return XMLReader parser
	 * @throws SAXException
	 */
	private XMLReader fetchSheetParser(SharedStringsTable sst, int sheet) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader();
		switch (sheet) {
			// Sheet#3: publication data
			case 3:
				parser.setContentHandler(new PubSheetHandler(sst, this));
				break;
			// Sheet#4: faculty data
			case 4:
				parser.setContentHandler(new FacultySheetHandler(sst, this));
				break;
		}
		return parser;
	}

	/**
	 * Handler for faculty spreadsheet (SAX parser)
	 * @author Victor Kofia
	 */
	private static class FacultySheetHandler extends DefaultHandler {
		/**
		 * XML parsing variables. Used to store data temporarily. 
		 */
		private String cellContents = "", rowContents = "", cellID = "";
		/**
		 * Reference to Incites object
		 */
		private Incites incites = null;
		/**
		 * Reference to XLSX table. Necessary for extracting cell 
		 * contents (cell IDs are used to extract cell contents)
		 */
		private SharedStringsTable sst = null;
		
		/**
		 * Create new faculty sheet handler
		 * @param SharedStringsTable sst
		 * @param Incites incites
		 * @return null
		 */
		private FacultySheetHandler(SharedStringsTable sst, Incites incites) {
			this.sst = sst;
			this.incites = incites;
		}
		
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			
			// Reset row contents
			if (name.equals("row")) {
				rowContents = "";
			}
			
			// Reset cell id
			if(name.equals("v")) {
				cellID = "";
			}
			
		}

		// Collect tag contents
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			cellID += new String(ch, start, length);
		}
		
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			
			if(name.equals("v")) {
				// Extract cell contents
				cellContents = new XSSFRichTextString(sst.getEntryAt(Integer.parseInt(cellID))).toString();
				// Add to row
				rowContents += Incites.parseFacultyName(cellContents.trim().toLowerCase()) + ";";
			}
			
			if (name.equals("row")) {
				// Parse all row contents. Ignore the first row (i.e. last name, first name, department ... etc)
				if (! rowContents.trim().isEmpty() && ! rowContents.contains("department")) {
					incites.getFacultySet().add(new Author(rowContents, Category.FACULTY));
					incites.setFacultyName(cellContents);
				}
			}
			
		}
		
	}
	
	/**
	 * Handler for publications spreadsheet (SAX Parser)
	 * @author Victor Kofia
	 */
	private static class PubSheetHandler extends DefaultHandler {
		/**
		 * List of authors in each publication
		 */
		List<Author> coauthorList = null;
		/**
		 * The columns of a row.
		 */
		private String[] columns = null;
		/**
		 * XML Parsing variables. Used to store data temporarily.
		 */
		private String cellID = "", cellContents = "", rowContents = "";
		/**
		 * Reference to Incites object
		 */
		private Incites incites = null;
		private boolean isString = false;
		/**
		 * Reference to individual publication
		 */
		Publication pub;
		/**
		 * Reference to XLSX table. Necessary for extracting cell 
		 * contents (cell IDs are used to extract cell contents)
		 */
		private SharedStringsTable sst = null;
		/**
		 * Publication attributes. 
		 */
		String title, timesCited = null, expectedCitations = "0.00";
		String year = null, subjectArea = null, authors = null;
		
		/**
		 * Create new pub sheet handler
		 * @param SharedStringsTable sst
		 * @param Incites incites
		 * @return null
		 */
		private PubSheetHandler(SharedStringsTable sst, Incites incites) {
			this.sst = sst;
			this.incites = incites;
		}
		
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			// Reset row contents
			if (name.equals("row")) {
				rowContents = "";
			}
			
			// Reset cell id
			if(name.equals("c")) {
				// If the 't' tag is not empty this means that
				// 'c' holds a reference to a cell that contains
				// a string value
				if (attributes.getValue("t") != null) {
					isString = true;
				}
				// If the 't' tag is empty this means that
				// 'c' contains a cell value and not a reference to
				// a cell
				cellID = "";
			}
		}

		// Collect tag contents
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			cellID += new String(ch, start, length);
		}
		
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			
			// Extract cell contents
			if(name.equals("v")) {
				// Use cellID to get the cell contents (if applicable)
				if (isString) {
					cellContents = new XSSFRichTextString(sst.getEntryAt(Integer.parseInt(cellID))).toString();
					isString = false;
				// Set the cell contents to cellID if cellID holds an actual value
				} else {
					cellContents = cellID;
				}
				rowContents += cellContents + "\t";
			}
			
			if (name.equals("row") && ! rowContents.trim().isEmpty()) {
				columns = rowContents.split("\t");
				// Column length varies. As of right now, the app only supports rows with 
				// column #s erring between 4-6. Any row that deviates from this range will 
				// be ignored and the user will be notified of this.
				if (columns.length == 6 && ! columns[0].equalsIgnoreCase("Times Cited")) {
					// Get publication info
					timesCited = columns[0].trim().isEmpty() 
							? "0" : columns[0].trim();
					expectedCitations = columns[1].trim().isEmpty() 
							? "0.00" : columns[1].trim();
					year = columns[2].trim().isEmpty() 
							? "0" : columns[2].trim();
					subjectArea = columns[3];
					authors = columns[4];
					title = columns[5];
					// Get author list
					try {
						coauthorList = incites.parseAuthors(authors);
						// If the # of authors in pub is only one, 
						// duplicate the author so that the pub now
						// contains two authors.
						// NOTE: The interaction map needs two authors
						// per pub AT MINIMUM to function adequately
						if (coauthorList.size() == 1) {
							Author loneAuthor = coauthorList.get(0);
							incites.getLoneAuthorList().add(loneAuthor);
							coauthorList.add(loneAuthor);
						}
					// As soon as an erroneous entry materializes
					// , terminate parsing --> compromises entire file
					} catch (UnableToParseAuthorException e) {
						e.printStackTrace();
						return;
					}
					// Set publication info
					pub = new Publication(title, year, subjectArea, 
							timesCited, expectedCitations, coauthorList);
					incites.getPubList().add(pub);
				} else if (columns.length == 5) {
					incites.updateDefectiveRows();
					// Get publication info
					timesCited = columns[0].trim().isEmpty() 
							? "0" : columns[0].trim();
					expectedCitations = "0.00";
					year = columns[1].trim().isEmpty() 
							? "0" : columns[1].trim();
					subjectArea = columns[2];
					authors = columns[3];
					title = columns[4];

					try {
						coauthorList = incites.parseAuthors(authors);
						// If the # of authors in pub is only one, 
						// duplicate the author so that the pub now
						// contains two authors.
						// NOTE: The interaction map needs two authors
						// per pub AT MINIMUM to function adequately
						if (coauthorList.size() == 1) {
							Author loneAuthor = coauthorList.get(0);
							incites.getLoneAuthorList().add(loneAuthor);
							coauthorList.add(loneAuthor);
						}
						// As soon as an erroneous entry materializes
						// , terminate parsing --> compromises entire file
					} catch (UnableToParseAuthorException e) {
						e.printStackTrace();
						return;
					}
					// Set publication info
					pub = new Publication(title, year, subjectArea, 
							timesCited, expectedCitations, coauthorList);
					incites.getPubList().add(pub);
				} else if (columns.length == 4) {
					incites.updateDefectiveRows();
					// Get publication info
					timesCited = columns[0].trim().isEmpty() 
							? "0" : columns[0].trim();
					expectedCitations = "0.00";
					year = columns[1].trim().isEmpty() 
							? "0" : columns[1].trim();
					subjectArea = "N/A";
					authors = columns[2];
					title = columns[3];

					try {
						coauthorList = incites.parseAuthors(authors);
						// If the # of authors in pub is only one, 
						// duplicate the author so that the pub now
						// contains two authors.
						// NOTE: The interaction map needs two authors
						// per pub AT MINIMUM to function adequately
						if (coauthorList.size() == 1) {
							Author loneAuthor = coauthorList.get(0);
							incites.getLoneAuthorList().add(loneAuthor);
							coauthorList.add(loneAuthor);
						}
						// As soon as an erroneous entry materializes
						// , terminate parsing --> compromises entire file
					} catch (UnableToParseAuthorException e) {
						e.printStackTrace();
						return;
					}
					// Set publication info
					pub = new Publication(title, year, subjectArea, 
							timesCited, expectedCitations, coauthorList);
					incites.getPubList().add(pub);										
				} else {
					incites.updateIgnoredRows();
				}
			}
		}
	}
	
	/**
	 * This exception is thrown when an author can't be parsed (i.e. 
	 * incorrect formatting)
	 * @author Victor Kofia
	 */
	public static class UnableToParseAuthorException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Return true iff author is valid
	 * @param Author author
	 * @return boolean
	 */
	private static boolean checkIfAuthorValid(Author author) {
		return ! (author.getFirstName().equalsIgnoreCase("N/A")
			  && author.getLastName().equalsIgnoreCase("N/A"));
	}
	
	/**
	 * Get Incites radio button
	 * @param null
	 * @return JRadioButton incitesRadioButton
	 */
	public static JRadioButton getIncitesRadioButton() {
		return Incites.incitesRadioButton;
	}
	
	/**
	 * Get location map
	 * @param null
	 * @return Map locationMap
	 */
	public static Map<String, String> getLocationMap() {
		if (Incites.locationMap == null) {
			try {
				File folder = new File("Apps/SocialNetworkApp/");
				File file = new File("Apps/SocialNetworkApp/map.sn");
				InputStream in = null;
				if (folder.exists()) {
					if (file.exists()) {
						in = new FileInputStream(file.getAbsolutePath());
					} else {
						in = Incites.class.getClassLoader().getResourceAsStream("map.sn");
					}
				} else {
					in = Incites.class.getClassLoader().getResourceAsStream("map.sn");
				}
				ObjectInputStream ois = new ObjectInputStream(in);
				Incites.setLocationMap((Map<String, String>) ois.readObject());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Cytoscape.notifyUser("Failed to load location map. FileNotFoundException.");
			} catch (IOException e) {
				e.printStackTrace();
				Cytoscape.notifyUser("Failed to load location map. IOException.");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				Cytoscape.notifyUser("Failed to load location map. ClassNotFoundException.");
			}
		}
		return Incites.locationMap;
	}
	
	/**
	 * Parse faculty name
	 * @param String facultyText
	 * @param  String facultyName
	 */
	private static String parseFacultyName(String facultyText) {
		Pattern firstNamePattern = Pattern.compile("(.+?)(\\(|$)");
		Matcher firstNameMatcher = firstNamePattern.matcher(facultyText.trim());
		if (firstNameMatcher.find()) {
			return firstNameMatcher.group(1).trim();
		}
		return "N/A";
	}
	
	/**
	 * Parse author's first name
	 * @param String incitesText
	 * @return String firstName
	 */
	public static String parseFirstName(String incitesText) {
		Pattern firstNamePattern = Pattern.compile(",(.+?)(\\s|\\.|$|\\()");
		Matcher firstNameMatcher = firstNamePattern.matcher(incitesText.trim());
		if (firstNameMatcher.find()) {
			return firstNameMatcher.group(1).trim();
		}
		System.out.println("CHECK: " + incitesText);
		return "N/A";
	}
	
	/**
	 * Parse author's institution
	 * @param String incitesText
	 * @return String institution
	 */
	public static String parseInstitution(String incitesText) {
		Pattern institution = Pattern.compile("\\((.+?)\\)");
		Matcher matchInstitution = institution.matcher(incitesText);
		if (matchInstitution.find()) {
			return matchInstitution.group(1).trim();
		}
		return "N/A";
	}
	/**
	 * Parse author's last-name
	 * @param String incitesText
	 * @return String lastName
	 */
	public static String parseLastName(String incitesText) {
		Pattern lastNamePattern = Pattern.compile("\"?\\s?(.+?),");
		Matcher lastNameMatcher = lastNamePattern.matcher(incitesText);
		if (lastNameMatcher.find()) {
			return lastNameMatcher.group(1).trim();
		}
		return "N/A";
	}
	
	/**
	 * Parse author's middle initial
	 * @param String incitesText
	 * @return String middleInitial
	 */
	public static String parseMiddleInitial(String incitesText) {
		Pattern middleInitialPattern = Pattern.compile("\\s(\\w)\\.");
		Matcher middleInitialMatcher = middleInitialPattern.matcher(incitesText);
		if (middleInitialMatcher.find()) {
			return middleInitialMatcher.group(1).trim();
		}
		return "N/A";
	}

	/**
	 * Set Incites radio button
	 * @param JRadioButton incitesRadioButton
	 * @return null
	 */
	public static void setIncitesRadioButton(JRadioButton incitesRadioButton) {
		Incites.incitesRadioButton = incitesRadioButton;
	}

	/**
	 * Set location map
	 * @param Map locationMap
	 * @return null
	 */
	public static void setLocationMap(Map<String, String> locationMap) {
	Incites.locationMap = locationMap;
	}

	/**
	 * Get the # of defective rows
	 * @param null
	 * @return int defectiveRows
	 */
	public int getDefectiveRows() {
		return this.defectiveRows;
	}

	/**
	 * Get list of faculty attributes
	 * @param null
	 * @return Object[] {faculty name, faculty set}
	 */
	public Object[] getFaculty() {
		return new Object[] {this.getFacultyName(), this.getFacultySet()};
	}

	/**
	 * Get faculty name
	 * @param null
	 * @return String facultyName
	 */
	public String getFacultyName() {
		return this.facultyName;
	}

	/**
	 * Get faculty set
	 * @param null
	 * @return HashSet facultySet
	 */
	public HashSet<Author> getFacultySet() {
		if (this.facultySet == null) {
			this.setFacultySet(new HashSet<Author>());
		}
		return this.facultySet;
	}

	/**
	 * Get identified faculty list
	 * @param null
	 * @return ArrayList identifiedFacultyList
	 */
	public ArrayList<Author> getIdentifiedFacultyList() {
		return this.identifiedFacultyList;
	}

	/**
	 * Get the # of ignored rows
	 * @param null
	 * @return int ignoredRows
	 */
	public int getIgnoredRows() {
		return ignoredRows;
	}

	/**
	 * Get lone author list
	 * @param null
	 * @return ArrayList loneAuthorList
	 */
	public ArrayList<Author> getLoneAuthorList() {
		if (this.loneAuthorList == null) {
			this.setLoneAuthorList(new ArrayList<Author>());
		}
		return loneAuthorList;
	}
	
	/**
	 * Get publication list
	 * @param null
	 * @return ArrayList pubList
	 */
	public ArrayList<Publication> getPubList() {
		if (this.pubList == null) {
			this.setPubList(new ArrayList<Publication>());
		}
		return this.pubList;
	}

	/**
	 * Get unidentified faculty list
	 * @return ArrayList unidentifiedFacultyList
	 */
	public ArrayList<Author> getUnidentifiedFacultyList() {
		return unidentifiedFacultyList;
	}

	/**
	 * Load all faculty contained in data file
	 * @param File xlsxFile
	 * @return null
	 */
	private void loadFaculty(File xlsxFile) {
		try {
			OPCPackage pkg = OPCPackage.open(xlsxFile.getAbsolutePath());
			XSSFReader r = new XSSFReader( pkg );
			SharedStringsTable sst = r.getSharedStringsTable();
			// Faculty data found in sheet#4 of spreadsheet
			XMLReader parser = fetchSheetParser(sst, 4);
			InputStream sheet = r.getSheet("rId4");
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		// Users do not have to be notified about these errors
		// but printed stack traces are useful (i.e. debugging)
		} catch (IOException e) {
			e.printStackTrace();
			this.setFacultyName("N/A");
			this.setFacultySet(new HashSet<Author>());
		} catch (OpenXML4JException e) {
			e.printStackTrace();
			this.setFacultyName("N/A");
			this.setFacultySet(new HashSet<Author>());
		} catch (SAXException e) {
			e.printStackTrace();
			this.setFacultyName("N/A");
			this.setFacultySet(new HashSet<Author>());
		}
	}
	
	/**
	 * Load all publications (as well as all associated author info) 
	 * contained in data file.
	 * Note that each publication serves as an edge and each author a node. 
	 * Node info is embedded inside each edge.
	 * @param File xlsxFile
	 * @return null
	 */
	private void loadPubList(File xlsxFile) {
		try {
			OPCPackage pkg = OPCPackage.open(xlsxFile.getAbsolutePath());
			XSSFReader r = new XSSFReader( pkg );
			SharedStringsTable sst = r.getSharedStringsTable();
			// Publication data is found in sheet#3 of spreadsheet
			XMLReader parser = fetchSheetParser(sst, 3);
			InputStream sheet = r.getSheet("rId3");
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		// Users do not have to be notified about these exceptions
		// but printed stack traces are useful (i.e. debugging)
		} catch (IOException e) {
			e.printStackTrace();
			this.setPubList(null);
		} catch (SAXException e) {
			e.printStackTrace();
			this.setPubList(null);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			this.setPubList(null);
		} catch (OpenXML4JException e) {
			e.printStackTrace();
			this.setPubList(null);
		}
	}
	
	/**
	 * Return true iff author in authorList
	 * @param List authorList
	 * @param Author author
	 * @return boolean bool
	 */
	public static boolean authorInList(ArrayList<Author> authorList, Author author) {
		for (Author unknown : authorList) {
			if (author.getLastName().equalsIgnoreCase(unknown.getLastName()) &&
				author.getFirstName().equalsIgnoreCase(unknown.getFirstName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Parse raw author text and return array list containing all authors
	 * and their associated info
	 * @param String rawAuthorText
	 * @return ArrayList authorList
	 */
	private ArrayList<Author> parseAuthors(String rawAuthorText) 
			throws UnableToParseAuthorException {
		String[] authors = rawAuthorText.split(";");
		if (authors.length == 0) {
			throw new UnableToParseAuthorException();
		}
		ArrayList<Author> pubAuthorList = new ArrayList<Author>();
		Author author = null;
		HashSet<Author> facultySet = this.getFacultySet();
		String facultyName = this.getFacultyName();
		for (String authorText : authors) {
			author = new Author(authorText.trim(), Category.INCITES);
			if (Incites.checkIfAuthorValid(author)) {
				if (! Incites.authorInList(pubAuthorList,  author)) {
					if (facultySet.contains(author)) {
						author.setFaculty(facultyName);
					}
					pubAuthorList.add(author);
				}
			} else {
				// Authors whose first & last names could not be
				// resolved properly for one reason or another (mostly formatting issues)
				
				// WIP (Work in Progress)
				System.out.println(author);
			}
		}
		return pubAuthorList;
	}

	/**
	 * Set faculty name
	 * @param String facultyName
	 * @return null
	 */
	private void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	/**
	 * Set 'faculty set'
	 * @param HashSet facultySet
	 * @return null
	 */
	private void setFacultySet(HashSet<Author> facultySet) {
		this.facultySet = facultySet;
	}

	/**
	 * Set identified faculty list
	 * @param ArrayList identifiedFaculty
	 * @return null
	 */
	public void setIdentifiedFacultyList(ArrayList<Author> identifiedFacultyList) {
		this.identifiedFacultyList = identifiedFacultyList;
	}

	/**
	 * Set lone author list
	 * @param ArrayList loneAuthorList
	 * @return null
	 */
	public void setLoneAuthorList(ArrayList<Author> loneAuthorList) {
		this.loneAuthorList = loneAuthorList;
	}

	/**
	 * Set publication list
	 * @param ArrayList pubList
	 * @return null
	 */
	private void setPubList(ArrayList<Publication> pubList) {
		this.pubList = pubList;
	}

	/**
	 * Set unidentified faculty list
	 * @param ArrayList unidentifiedFacultyList
	 * @return null
	 */
	public void setUnidentifiedFacultyList(ArrayList<Author> unidentifiedFacultyList) {
		this.unidentifiedFacultyList = unidentifiedFacultyList;
	}

	/**
	 * Update the number of defective columns (add 1)
	 * @param null
	 * @return null
	 */
	private void updateDefectiveRows() {
		this.defectiveRows++;
	}

	/**
	 * Update the # of ignored rows (add 1)
	 * @param int ignoredRows
	 * @return null
	 */
	private void updateIgnoredRows() {
		this.ignoredRows++;
	}

	/**
	 * Construct Incites attribute map
	 * @param null
	 * @return Map nodeAttrMap
	 */
	public static TreeMap<String, Object> constructIncitesAttrMap(Author author) {
		TreeMap<String, Object> nodeAttrMap = new TreeMap<String, Object>();
		String lastName = author.getLastName();
		String firstName = author.getFirstName();
		nodeAttrMap.put("Label", firstName + "_" + lastName);
		nodeAttrMap.put("Last Name", lastName);
		nodeAttrMap.put("First Name", author.getFirstName());
		nodeAttrMap.put("Times Cited", author.getTimesCited());
		nodeAttrMap.put("Institution", author.getInstitution());
		nodeAttrMap.put("Location", author.getLocation());
		nodeAttrMap.put("Faculty", author.getFaculty());
		return nodeAttrMap;
	}

	
}