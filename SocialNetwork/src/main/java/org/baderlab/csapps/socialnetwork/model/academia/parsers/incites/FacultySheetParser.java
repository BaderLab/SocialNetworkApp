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

import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for faculty spreadsheet (SAX parser)
 *
 * @author Victor Kofia
 */
public class FacultySheetParser extends DefaultHandler {

    /**
     * XML parsing variables. Used to store data temporarily.
     */
    private String cellContents = "", rowContents = "", cellID = "";

    /**
     * Reference to Incites parser object
     */
    private IncitesParser incitesParser = null;

    /**
     * Reference to XLSX table. Necessary for extracting cell contents (cell IDs
     * are used to extract cell contents)
     */
    private SharedStringsTable sst = null;

    /**
     * Create new faculty sheet parser
     *
     * @param SharedStringsTable sst
     * @param Incites incites
     */
    public FacultySheetParser(SharedStringsTable sst, IncitesParser incitesParser) {
        this.sst = sst;
        this.incitesParser = incitesParser;
    }

    /*(non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    	// Collect tag contents
        this.cellID += new String(ch, start, length);
    }

    /*(non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

        if (name.equals("v")) {
            // Extract cell contents
            this.cellContents = new XSSFRichTextString(this.sst.getEntryAt(Integer.parseInt(this.cellID))).toString();
            // Add to row
            this.rowContents += this.incitesParser.parseFacultyName(this.cellContents.trim().toLowerCase()) + ";";
        }

        if (name.equals("row")) {
            // Parse all row contents. Ignore the first row (i.e. last name,
            // first name, department ... etc)
            if (!this.rowContents.trim().isEmpty() && !(this.rowContents.contains("department") || this.rowContents.contains("first name"))) {

                this.incitesParser.getFacultySet().add(new Author(this.rowContents, Category.FACULTY));
                this.incitesParser.setDepartmentName(this.cellContents);

                // We are expecting 3 objects in the Faculty Sheet
                // Last name, first name, department --> if there are only 2
                // assume department is missing
                if (this.rowContents.split(";").length == 2) {
                    this.incitesParser.setDepartmentName("not_specified");
                }
            }
        }

    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

        // Reset row contents
        if (name.equals("row")) {
            this.rowContents = "";
        }

        // Reset cell id
        if (name.equals("v")) {
            this.cellID = "";
        }

    }

}
