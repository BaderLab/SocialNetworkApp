package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;


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
 * Methods for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites {
	/**
	 * Reference to Incites checkbox
	 */
	public static JCheckBox incitesCheckBox = null;
	/**
	 * Author location map
	 */
	private static Map<String, String> locationMap = null;
	/**
	 * List of publications extracted from Incites data file
	 */
	private static ArrayList<Publication> pubList = null;
	/**
	 * Set of faculty members extracted from Incites data file
	 */
	private static HashSet<Author> facultySet = null;
	/**
	 * Name of faculty extracted from Incites data file
	 */
	private static String facultyName = null;
	
	/**
	 * This exception is thrown when an author can't be parsed (i.e. 
	 * incorrect formatting)
	 * @author Victor Kofia
	 */
	public static class UnableToParseAuthorException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Return true iff the provided line comes from an Incites data file
	 * @param String line
	 * @return boolean
	 */
	public static boolean checkIfValid(Scanner in) {
		boolean isValid = false;
		for (int i = 0; i < 5; i++) {
			String line = in.nextLine().trim();
			String[] contents = line.split("[\n\t]");
			if (contents.length != 6) {
				return false;
			} else {
				boolean hasTimesCited = false;
				boolean hasExpectedCitations = false;
				boolean hasPublicationYear = false;
				boolean hasSubjectArea = false;
				boolean hasAuthors = false;
				boolean hasTitle = false;
				String year = null;
				String subjectArea = null;
				String authors = null;
				String title = null;
				String timesCited = null;
				String expectedCitations = "0.00";
				List<Author> coauthorList = null;
				Publication pub;
				timesCited = contents[0].trim().isEmpty() ? "0" : contents[0].trim();
				hasTimesCited = timesCited.matches("\\d+?") ? true : false;
				expectedCitations = contents[1].trim().isEmpty() 
						? "0.00" : contents[1].trim();
				hasExpectedCitations = expectedCitations.matches
						("(\\d+?)\\.?(\\d+?)") ? true : false;
				year = contents[2].trim().isEmpty() ? "0" : contents[2].trim();
				hasPublicationYear = year.matches("\\d+?") ? true : false;
				subjectArea = contents[3];
				hasSubjectArea = subjectArea.matches("[A-Z]+?") ? true : false;
				authors = contents[4];
				try {
					coauthorList = Incites.parseAuthors(authors);
					hasAuthors = true;
				} catch (UnableToParseAuthorException e) {
					hasAuthors = false;
				}
				// Difficult to identify an Incites specific title. Thus, true by default.
				title = contents[5];
				hasTitle = true;
				// Consolidate
				isValid = hasTimesCited && hasExpectedCitations 
						&& hasPublicationYear && hasSubjectArea
						&& hasAuthors && hasTitle;
				if (isValid) {
					pub = new Publication(title, year, subjectArea, timesCited, 
							expectedCitations, coauthorList);
					pubList.add(pub);
				} else {
					return ! isValid;
				}
			}
		}
		return ! isValid;
	}

	/**
	 * Get Incites checkbox
	 * @param null
	 * @return JCheckBox incitesCheckBox
	 */
	public static JCheckBox getIncitesCheckBox() {
		return Incites.incitesCheckBox;
	}

	/**
	 * Get location map
	 * @param null
	 * @return Map locationMap
	 */
	public static Map<String, String> getLocationMap() {
		if (Incites.locationMap == null) {
			try {
				InputStream in = Incites.class.getClassLoader().getResourceAsStream("map.sn");
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
	 * Parse data in TXT file and return an array containing
	 * faculty attributes
	 * @param File txtFile
	 * @return Object[] {facultyName, facultySet}
	 */
	public static Object[] getTXTFacultyAttr(File txtFile) {	
		HashSet<Author> facultySet = new HashSet<Author>();
		String facultyName = "N/A";
		try {
			Scanner in = new Scanner(txtFile);
			// Skip superfluous lines
			in.nextLine();
			in.nextLine();
			facultySet = new HashSet<Author>();
			String line = null;
			String[] columns = null;
			String[] names = new String[2];
			String authorData = "";
			// Add faculty members to hash-set
			while (in.hasNext()) {
				line = in.nextLine();
				columns = line.split("\\t");
				names[0] = Incites.matchName(columns[0]);
				names[1] = Incites.matchName(columns[1]);
				for (String name : names) {
					authorData += name + ";";
				}
				facultySet.add(new Author(authorData, Category.FACULTY));
				authorData = "";
			}
			facultyName = columns[columns.length - 1];
			return new Object[] {facultyName, facultySet};

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Faculty file could not be found.\n" +
					             "Faculty info will not be included" +
					             " in final network.");
		} catch (Exception e) {
			e.printStackTrace();
			Cytoscape.notifyUser("An error prevented the faculty file" +
					             " from being loaded");
		}
		return new Object[] {facultyName, facultySet};
	}

	/**
	 * Return all publications (as well as all associated author info) 
	 * contained in text file.
	 * Note that each publication serves as an edge and each author a node. 
	 * Node info is embedded inside each edge.
	 * @param File textFile
	 * @return List pubList
	 * @throws FileNotFoundException 
	 */
	public static List<Publication> getTXTPubList(File textFile) 
			throws FileNotFoundException {
		Scanner in = new Scanner(textFile);
		String line;
		String[] contents;
		String year = null;
		String subjectArea = null;
		String authors = null;
		String title = null;
		String timesCited = null;
		String expectedCitations = "0.00";
		Publication pub;
		boolean isValid = false;
		List<Author> coauthorList = null;
		Incites.pubList = new ArrayList<Publication>();
		// Verify that file is in fact derived from Incites
		if (! in.hasNext()) {
			isValid = false;
		} else {
			isValid = Incites.checkIfValid(in);
		}
		// Read Incites data file
		if (isValid) {
			while (in.hasNext()) {
				line = in.nextLine().trim();
				contents = line.split("[\t\n]");
				if (contents.length == 6) {
					// Get publication info
					timesCited = contents[0].trim().isEmpty() 
							? "0" : contents[0].trim();
					expectedCitations = contents[1].trim().isEmpty() 
							? "0.00" : contents[1].trim();
					year = contents[2].trim().isEmpty() 
							? "0" : contents[2].trim();
					subjectArea = contents[3];
					authors = contents[4];
					title = contents[5];
					// Get author list
					try {
						coauthorList = Incites.parseAuthors(authors);
					// As soon as an infelicitous entry materializes
					// , terminate parsing --> compromises entire file
					} catch (UnableToParseAuthorException e) {
						e.printStackTrace();
						return null;
					}
					// Set publication info
					pub = new Publication(title, year, subjectArea, 
							timesCited, expectedCitations, coauthorList);
					//Add publication to overall list
					Incites.pubList.add(pub);
				} else {
//					Cytoscape.notifyUser("The columns in this file do not align properly. This will compromise network accuracy.");
					/**
					 * DEAL WITH THIS PLS
					 */
//					System.out.println("The columns in this file do not align properly. "
//					           + "This will compromise network accuracy.");
				}
			}
			return Incites.pubList;
		}
		// If file is invalid, null will be returned
		return null;
	}

//	/**
//	 * Parse data in XLSX file and return an array containing
//	 * faculty attributes
//	 * @param File xlsxFile
//	 * @return Object[] {facultyName, facultySet}
//	 */
//	public static Object[] getXLSXFacultySet(File xlsxFile) {	
//		HashSet<Author> facultySet = null;
//		String facultyName = "";
//		try {
//			FileInputStream fileInputStream = new FileInputStream(xlsxFile);
//
//			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
//			XSSFSheet sheet = workbook.getSheetAt(3); //4th sheet from workbook
//
//			Iterator<Row> rowIterator = sheet.iterator();
//			Iterator<Cell> cellIterator = null;
//
//			int j = 0;
//			String authorAttr = "", cellValue = "";
//			facultySet = new HashSet<Author>();
//
//			// Skip the first two rows
//			Row row = rowIterator.next();
//			rowIterator.next();
//
//			Cell cell = null;
//
//			// Get faculty data
//			while(rowIterator.hasNext()) {
//				row = rowIterator.next();
//				cellIterator = row.cellIterator();
//				j = 0;
//				while(j < 2) {
//					cell = cellIterator.next();
//					cellValue = cell.getStringCellValue();
//					if (cellValue.trim().isEmpty()) {
//						return new Object[] {facultyName, facultySet};
//					}
//					authorAttr += cell.getStringCellValue().trim() + ";";
//					j++;
//				}
//				facultySet.add(new Author(authorAttr, Category.FACULTY));
//				authorAttr = "";
//			}
//
//			// Get faculty name
//			cell = cellIterator.next();
//			facultyName = cell.getStringCellValue();
//
//			fileInputStream.close();
//
//		} catch (FileNotFoundException e) {
//			Cytoscape.notifyUser("File cannot be located. Please verify that" +
//		             " spreadsheet file has been loaded correctly.");
//		} catch (IOException e) {
//			Cytoscape.notifyUser("Cytoscape is unable to read the spreadsheet file." +
//				     "This is an internal issue. Peruse FEI for troubleshooting tips.");
//		}
//
//		return new Object[] {facultyName, facultySet};
//	}
	
	/**
	 * Handler for faculty spreadsheet (SAX parser)
	 * @author Victor Kofia
	 */
	private static class FacultySheetHandler extends DefaultHandler {
		private SharedStringsTable sst = null;
		private String cell = "", row = "", id = "";
		private FacultySheetHandler(SharedStringsTable sst) {
			this.sst = sst;
		}
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			if (name.equals("row")) {
				row = "";
			}
			if(name.equals("v")) {
				id = "";
			}
		}
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			if(name.equals("v")) {
				cell = new XSSFRichTextString(sst.getEntryAt(Integer.parseInt(id))).toString();
				row += Incites.parseFacultyName(cell.trim()) + ";";
			}
			if (name.equals("row")) {
				if (! row.trim().isEmpty()) {
					Incites.getFacultySet().add(new Author(row, Category.FACULTY));
					Incites.setFacultyName(cell);
				}
			}
		}
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			id += new String(ch, start, length);
		}
	}
	
	/**
	 * Handler for publications spreadsheet
	 * @author Victor Kofia
	 */
	private static class PubSheetHandler extends DefaultHandler {
		private SharedStringsTable sst = null;
		private String id = "", cell = "", row = "";
		private String[] columns = null;
		private boolean isString = false;
		String year = null, subjectArea = null, authors = null;
		String title, timesCited = null, expectedCitations = "0.00";
		List<Author> coauthorList = null;
		Publication pub;
		private PubSheetHandler(SharedStringsTable sst) {
			this.sst = sst;
		}
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			if (name.equals("row")) {
				row = "";
			}
			if(name.equals("c")) {
				if (attributes.getValue("t") != null) {
					isString = true;
				}
				id = "";
			}
		}
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			if(name.equals("v")) {
				if (isString) {
					cell = new XSSFRichTextString(sst.getEntryAt(Integer.parseInt(id))).toString();
					isString = false;
				} else {
					cell = id;
				}
				row += cell.trim() + "\t";
			}
			if (name.equals("row") && ! row.trim().isEmpty()) {
				columns = row.split("\t");
				if (columns.length == 6) {
					/**
					 * PLEASE THINK OF A MORE ELEGANT SOLUTION TO THIS
					 */
					if (! columns[0].equalsIgnoreCase("Times Cited")) {
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
//						System.out.println("Times Cited: " + columns[0]
//				         + "\nExpected Citations: " + columns[1]
//				         + "\nYear: " + columns[2]
//				         + "\nSubject Area: " + columns[3]
//				         + "\nAuthors: " + columns[4]
//				         + "\nTitle: " + columns[5] + "\n\n");
						// Get author list
						try {
							coauthorList = Incites.parseAuthors(authors);
						// As soon as an infelicitous entry materializes
						// , terminate parsing --> compromises entire file
						} catch (UnableToParseAuthorException e) {
							e.printStackTrace();
							return;
						}
						// Set publication info
						pub = new Publication(title, year, subjectArea, 
								timesCited, expectedCitations, coauthorList);
						Incites.getPubList().add(pub);
					}
				} else {
//					Cytoscape.notifyUser("The columns in this file do not align properly. "
//							           + "This will compromise network accuracy.");
					/**
					 * DEAL WITH THIS PLS
					 */
//					System.out.println("The columns in this file do not align properly. "
//					           + "This will compromise network accuracy.");
				}
			}
		}
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			id += new String(ch, start, length);
		}
	}

	
	/**
	 * Get XLSX sheet parser
	 * @param SharedStringsTable sst
	 * @param int sheet
	 * @return XMLReader parser
	 * @throws SAXException
	 */
	public static XMLReader fetchSheetParser(SharedStringsTable sst, 
			                                 int sheet) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader();
		switch (sheet) {
			// Sheet#3: publication data
			case 3:
				parser.setContentHandler(new PubSheetHandler(sst));
				break;
			// Sheet#4: faculty data
			case 4:
				parser.setContentHandler(new FacultySheetHandler(sst));
				break;
		}
		return parser;
	}

	
	/**
	 * Get all publications (as well as all associated author info) 
	 * contained in xlsx file.
	 * Note that each publication serves as an edge and each author a node. 
	 * Node info is embedded inside each edge.
	 * <br> Faculty info not present
	 * @param File file
	 * @return ArrayList pubList
	 */
	public static ArrayList<Publication> getXLSXPubList(File file) {
		try {
			OPCPackage pkg = OPCPackage.open(file.getAbsolutePath());
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
			Incites.setPubList(null);
		} catch (SAXException e) {
			e.printStackTrace();
			Incites.setPubList(null);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			Incites.setPubList(null);
		} catch (OpenXML4JException e) {
			e.printStackTrace();
			Incites.setPubList(null);
		}
		return Incites.getPubList();
	}
	
	/**
	 * Parse data in XLSX file and return an array containing
	 * faculty attributes
	 * @param File xlsxFile
	 * @return Object[] {facultyName, facultySet}
	 */
	public static Object[] getXLSXFacultyAttr(File xlsxFile) {
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
			Incites.setFacultyName("N/A");
			Incites.setFacultySet(new HashSet<Author>());
		} catch (OpenXML4JException e) {
			e.printStackTrace();
			Incites.setFacultyName("N/A");
			Incites.setFacultySet(new HashSet<Author>());
		} catch (SAXException e) {
			e.printStackTrace();
			Incites.setFacultyName("N/A");
			Incites.setFacultySet(new HashSet<Author>());
		}
		return new Object[] {Incites.getFacultyName(), Incites.getFacultySet()};
	}

//	/**
//	 * Load all publications (as well as all associated author info) 
//	 * contained in xlsx file.
//	 * Note that each publication serves as an edge and each author a node. 
//	 * Node info is embedded inside each edge.
//	 * <br> Faculty info not present
//	 * @param File file
//	 * @return null
//	 */
//	public static ArrayList<Publication> loadXLSXPubList(File file) {
//		ArrayList<Publication> pubList = null;
//		try {
//			FileInputStream fileInputStream = new FileInputStream(file);
//
//			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
//			XSSFSheet sheet = workbook.getSheetAt(2); //3rd sheet from workbook
//			
//			Iterator<Row> rowIterator = sheet.iterator();
//			Iterator<Cell> cellIterator = null;
//
//			int j = 0;
//			Publication pub = null;
//			String[] pubAttr = new String[7];
//			pubList = new ArrayList<Publication>();
//
//			Row row = rowIterator.next();
//
//			// Get publication and author data
//			while(rowIterator.hasNext()) {
//				row = rowIterator.next();
//				//For each row, iterate through all the columns
//				cellIterator = row.cellIterator();
//				j = 0;
//				while(cellIterator.hasNext()) {
//					Cell cell = cellIterator.next();
//					switch(cell.getCellType()) {
//					case Cell.CELL_TYPE_NUMERIC:
//						pubAttr[j] = String.format("%.2f%n", 
//								 cell.getNumericCellValue());
//						break;
//					case Cell.CELL_TYPE_STRING:
//						pubAttr[j] = cell.getStringCellValue();
//						break;
//					}
//					j++;
//				}
//				String title = pubAttr[6];
//				String year = Incites.toFloor(pubAttr[2]);
//				String subjectArea = pubAttr[3];
//				String timesCited = Incites.toFloor(pubAttr[0]);
//				String expectedCitations = pubAttr[1];
//				ArrayList<Author> authors = parseAuthors(pubAttr[4]);
//				
//				// If author list has a single author, add a duplicate
//				// in order to ensure consistency
//				// i.e. a consortium has to contain at minimum two authors
//				if (authors.size() == 1) {
//					authors.add(authors.get(0));
//				}
//				
//				pub = new Publication(title, year, subjectArea, 
//						timesCited, expectedCitations, authors);
//				pubList.add(pub);
//			}
//			
//			fileInputStream.close();
//			
//		} catch (FileNotFoundException e) {
//			Cytoscape.notifyUser("File cannot be located. Please verify that" +
//					             " spreadsheet file has been loaded correctly.");
//		} catch (IOException e) {
//			Cytoscape.notifyUser("Cytoscape is unable to read the spreadsheet file." +
//					     "This is an internal issue. Peruse FEI for troubleshooting tips.");
//		} catch (UnableToParseAuthorException e) {
//			Cytoscape.notifyUser("File contains corrupt author data. Please use a valid" +
//					"Incites spreadsheet.");
//		}
//		
//		return pubList;
//
//	}

	/**
	 * Match author's canonical name (i.e. matches 'Bubbles' in string 'Bubbles PK.')
	 * @param String rawLastName
	 * @return String lastName
	 */
	private static String matchName(String rawName) {
		Pattern pattern = Pattern.compile("^(.+?)\\s");
		Matcher matcher = pattern.matcher(rawName);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return rawName;
	}

	/**
	 * Parse raw author text and return array list containing all authors
	 * and their associated info
	 * @param String rawAuthorText
	 * @return ArrayList authorList
	 */
	public static ArrayList<Author> parseAuthors(String rawAuthorText) 
			throws UnableToParseAuthorException {
		String[] authors = rawAuthorText.split(";");
		if (authors.length == 0) {
			throw new UnableToParseAuthorException();
		}
		ArrayList<Author> pubAuthorList = new ArrayList<Author>();
		Author author = null;
		for (String authorText : authors) {
			author = new Author(authorText, Category.INCITES);
			if (! pubAuthorList.contains(author) && Incites.checkIfAuthorValid(author)) {
				pubAuthorList.add(author);
			} else {
				/**
				 * KEEP RECORDS OF ALL AUTHORS THAT DIDN'T MAKE THE CUT
				 * INFO WILL BE SHOWN TO USER UPON NETWORK COMPLETION
				 */
				System.out.println("\n\n" + author + "\n\n");
			}
		}
		return pubAuthorList;
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
		Pattern firstNamePattern = Pattern.compile(",(.+?)(\\s|\\.|$)");
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
	 * Set Incites checkbox
	 * @param JCheckBox incitesCheckBox
	 * @return null
	 */
	public static void setIncitesCheckBox(JCheckBox incitesCheckBox) {
		Incites.incitesCheckBox = incitesCheckBox;
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
	 * Return the floor of the number contained in string
	 * @param String num
	 * @return String floor
	 */
	public static String toFloor(String floor) {
		Pattern pattern = Pattern.compile("(\\d+?)\\.");
		Matcher matcher = pattern.matcher(floor);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "N/A";
	}

	/**
	 * Get faculty set
	 * @param null
	 * @return HashSet facultySet
	 */
	public static HashSet<Author> getFacultySet() {
		if (Incites.facultySet == null) {
			Incites.setFacultySet(new HashSet<Author>());
		}
		return Incites.facultySet;
	}

	/**
	 * Set 'faculty set'
	 * @param HashSet facultySet
	 * @return null
	 */
	public static void setFacultySet(HashSet<Author> facultySet) {
		Incites.facultySet = facultySet;
	}

	/**
	 * Get faculty name
	 * @param null
	 * @return String facultyName
	 */
	public static String getFacultyName() {
		return facultyName;
	}

	/**
	 * Set faculty name
	 * @param String facultyName
	 * @return null
	 */
	public static void setFacultyName(String facultyName) {
		Incites.facultyName = facultyName;
	}
	
	/**
	 * Get publication list
	 * @param null
	 * @return ArrayList pubList
	 */
	private static ArrayList<Publication> getPubList() {
		if (Incites.pubList == null) {
			Incites.setPubList(new ArrayList<Publication>());
		}
		return Incites.pubList;
	}
	
	/**
	 * Set publication list
	 * @param ArrayList pubList
	 * @return null
	 */
	private static void setPubList(ArrayList<Publication> pubList) {
		Incites.pubList = pubList;
	}
}