/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular 
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates, 
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.  
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.model.academia.parsers.incites;

import java.util.List;


import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for publications spreadsheet (SAX Parser)
 * @author Victor Kofia
 */
public class PubSheetHandler extends DefaultHandler {
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
	private IncitesParser incitesParser = null;
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
	public PubSheetHandler(SharedStringsTable sst, IncitesParser incitesParser) {
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
			// column #s between 4-6. Rows that deviate from this range will be ignored
            // and the user will be notified of this.
			if (columns.length == 6 && ! columns[0].equalsIgnoreCase(BasicSocialNetworkVisualstyle.nodeattr_timescited)) {
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
					coauthorList = incitesParser.parseAuthors(authors);
					// If the # of authors in pub is only one, 
					// duplicate the author so that the pub now
					// contains two authors.
					// NOTE: The interaction map needs two authors
					// per pub AT MINIMUM to function adequately
					if (coauthorList.size() == 1) {
						Author loneAuthor = coauthorList.get(0);
						incitesParser.getLoneAuthorList().add(loneAuthor);
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
				incitesParser.getPubList().add(pub);
			} else if (columns.length == 5) {
				incitesParser.updateDefectiveRows();
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
					coauthorList = incitesParser.parseAuthors(authors);
					// If the # of authors in pub is only one, 
					// duplicate the author so that the pub now
					// contains two authors.
					// NOTE: The interaction map needs two authors
					// per pub AT MINIMUM to function adequately
					if (coauthorList.size() == 1) {
						Author loneAuthor = coauthorList.get(0);
						incitesParser.getLoneAuthorList().add(loneAuthor);
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
				incitesParser.getPubList().add(pub);
			} else if (columns.length == 4) {
				incitesParser.updateDefectiveRows();
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
					coauthorList = incitesParser.parseAuthors(authors);
					// If the # of authors in pub is only one, 
					// duplicate the author so that the pub now
					// contains two authors.
					// NOTE: The interaction map needs two authors
					// per pub AT MINIMUM to function adequately
					if (coauthorList.size() == 1) {
						Author loneAuthor = coauthorList.get(0);
						incitesParser.getLoneAuthorList().add(loneAuthor);
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
				incitesParser.getPubList().add(pub);										
			} else {
				incitesParser.updateIgnoredRows();
			}
		}
	}
}