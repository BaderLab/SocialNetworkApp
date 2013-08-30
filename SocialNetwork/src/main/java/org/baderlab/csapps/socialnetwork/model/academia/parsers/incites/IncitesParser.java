package main.java.org.baderlab.csapps.socialnetwork.model.academia.parsers.incites;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import main.java.org.baderlab.csapps.socialnetwork.model.Category;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Author;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Incites;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Publication;

/**
 * For parsing Incites' xlsx spreadsheets
 * @author Victor Kofia
 */
public class IncitesParser {
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
	 * who has published a paper on their own.
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
	 * String representation of all unidentified faculty
	 */
	private String unidentifiedFacultyString = null;

	
	/**
	 * Create new Incites parser object
	 * @param File xlsx
	 * @return null
	 */
	public IncitesParser(File xlsx) {
		this.parseFaculty(xlsx);
		this.parsePublications(xlsx);
		this.calculateSummary();
	}
	
	/**
	 * Return true iff author in authorList
	 * @param List authorList
	 * @param Author author
	 * @return boolean bool
	 */
	public boolean authorInList(ArrayList<Author> authorList, Author author) {
		for (Author unknown : authorList) {
			if (author.getLastName().equalsIgnoreCase(unknown.getLastName()) &&
				author.getFirstName().equalsIgnoreCase(unknown.getFirstName())) {
				Incites.validateInstitution(author, unknown);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculate network summary
	 * i.e. which faculty members got identified?
	 * <br> which ones didn't?
	 * @param null
	 * @return null
	 */
	private void calculateSummary() {
		ArrayList<Author> identifiedAuthorsList = new ArrayList<Author>();
		ArrayList<Author> unidentifiedAuthorsList = new ArrayList<Author>();
		String unidentifiedAuthorsString = "<ol>";
		Author author = null;
		for (Object object : this.getFacultySet().toArray()) {
			author = (Author) object;
			if (author.isIdentified()) {
				identifiedAuthorsList.add(author);
			} else {
				unidentifiedAuthorsList.add(author);
				unidentifiedAuthorsString += "<li>" + author.toString() + "</li>";
			}
		}
		this.setIdentifiedFacultyList(identifiedAuthorsList);
		this.setUnidentifiedFacultyList(unidentifiedAuthorsList);
		this.setUnidentifiedFacultyString(unidentifiedAuthorsString + "</ol>");
	}
	
	/**
	 * Return true iff author is valid
	 * @param Author author
	 * @return boolean
	 */
	public boolean checkIfAuthorValid(Author author) {
		return ! (author.getFirstName().equalsIgnoreCase("N/A")
			  && author.getLastName().equalsIgnoreCase("N/A"));
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
	 * Get unidentified faculty string
	 * <br>last name, first name
	 * @param null
	 * @return String unidentifiedFacultyString
	 */
	public String getUnidentifiedFacultyString() {
		return unidentifiedFacultyString;
	}

	/**
	 * Parse raw author text and return array list containing all authors
	 * and their associated info
	 * @param String rawAuthorText
	 * @return ArrayList authorList
	 */
	public ArrayList<Author> parseAuthors(String rawAuthorText) 
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
			if (this.checkIfAuthorValid(author)) {
				if (! this.authorInList(pubAuthorList,  author)) {
					if (facultySet.contains(author)) {
						author.setFaculty(facultyName);
					}
					pubAuthorList.add(author);
				}
			} else {
				// Authors whose first & last names could not be
				// resolved properly for one reason or another (mostly formatting issues)
				
				// 
				System.out.println(author);
			}
		}
		return pubAuthorList;
	}

	/**
	 * Parse all faculty contained in data file
	 * @param File xlsxFile
	 * @return null
	 */
	private void parseFaculty(File xlsxFile) {
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
	 * Parse faculty name
	 * @param String facultyText
	 * @param  String facultyName
	 */
	public String parseFacultyName(String facultyText) {
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
	 * Parse all publications (as well as all associated author info) 
	 * contained in data file.
	 * Note that each publication serves as an edge and each author a node. 
	 * Node info is embedded inside each edge.
	 * @param File xlsxFile
	 * @return null
	 */
	private void parsePublications(File xlsxFile) {
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
	 * Set faculty name
	 * @param String facultyName
	 * @return null
	 */
	public void setFacultyName(String facultyName) {
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
	 * Set unidentified faculty string
	 * <br>last name, first name
	 * @param String unidentifiedFacultyString
	 * @return null
	 */
	public void setUnidentifiedFacultyString(String unidentifiedFacultyString) {
		this.unidentifiedFacultyString = unidentifiedFacultyString;
	}

	
	/**
	 * Update the number of defective columns (add 1)
	 * @param null
	 * @return null
	 */
	public void updateDefectiveRows() {
		this.defectiveRows++;
	}
	
	/**
	 * Update the # of ignored rows (add 1)
	 * @param int ignoredRows
	 * @return null
	 */
	public void updateIgnoredRows() {
		this.ignoredRows++;
	}
	

}
