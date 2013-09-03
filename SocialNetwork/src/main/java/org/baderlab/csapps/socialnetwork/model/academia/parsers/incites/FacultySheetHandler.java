package main.java.org.baderlab.csapps.socialnetwork.model.academia.parsers.incites;

import main.java.org.baderlab.csapps.socialnetwork.model.Category;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Author;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for faculty spreadsheet (SAX parser)
 * @author Victor Kofia
 */
public class FacultySheetHandler extends DefaultHandler {
	/**
	 * XML parsing variables. Used to store data temporarily. 
	 */
	private String cellContents = "", rowContents = "", cellID = "";
	/**
	 * Reference to Incites parser object
	 */
	private IncitesParser incitesParser = null;
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
	public FacultySheetHandler(SharedStringsTable sst, IncitesParser incitesParser) {
		this.sst = sst;
		this.incitesParser = incitesParser;	
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
			rowContents += incitesParser.parseFacultyName(cellContents.trim().toLowerCase()) + ";";
		}
		
		if (name.equals("row")) {
			// Parse all row contents. Ignore the first row (i.e. last name, first name, department ... etc)
			if (! rowContents.trim().isEmpty() && ! rowContents.contains("department")) {
				incitesParser.getFacultySet().add(new Author(rowContents, Category.FACULTY));
				incitesParser.setDepartmentName(cellContents);
			}
		}
		
	}
	
}

