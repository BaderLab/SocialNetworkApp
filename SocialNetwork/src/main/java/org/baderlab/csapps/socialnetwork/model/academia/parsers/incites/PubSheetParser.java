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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for publications spreadsheet (SAX Parser)
 *
 * @author Victor Kofia
 */
public class PubSheetParser extends DefaultHandler {
    
    private static final Logger logger = Logger.getLogger(Scopus.class.getName());


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
    Publication pub = null;

    /**
     * Reference to XLSX table. Necessary for extracting cell contents (cell IDs
     * are used to extract cell contents)
     */
    private SharedStringsTable sst = null;

    /**
     * Publication attributes.
     */
    String title, timesCited = null, expectedCitations = "0.00";

    String year = null, subjectArea = null, authors = null;

    /**
     * Create new pub sheet parser
     *
     * @param SharedStringsTable sst
     * @param Incites incites
     */
    public PubSheetParser(SharedStringsTable sst, IncitesParser incitesParser) {
        this.sst = sst;
        this.incitesParser = incitesParser;
    }

    // Collect tag contents
    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.cellID += new String(ch, start, length);
    }

    @Override
    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String name) throws SAXException {

        // Extract cell contents
        if (name.equals("v")) {
            // Use cellID to get the cell contents (if applicable)
            if (this.isString) {
                this.cellContents = new XSSFRichTextString(this.sst.getEntryAt(Integer.parseInt(this.cellID))).toString();
                this.isString = false;
                // Set the cell contents to cellID if cellID holds an actual
                // value
            } else {
                this.cellContents = this.cellID;
            }
            this.rowContents += this.cellContents + "\t";
        }

        if (name.equals("row") && !this.rowContents.trim().isEmpty()) {
            this.columns = this.rowContents.split("\t");
            // Column length varies. As of right now, the app only supports rows
            // with
            // column #s between 4-6. Rows that deviate from this range will be
            // ignored
            // and the user will be notified of this.
            if (this.columns.length == 6 && !this.columns[0].equalsIgnoreCase(BasicSocialNetworkVisualstyle.nodeattr_timescited)) {
                // Get publication info
                this.timesCited = this.columns[0].trim().isEmpty() ? "0" : this.columns[0].trim();
                this.expectedCitations = this.columns[1].trim().isEmpty() ? "0.00" : this.columns[1].trim();
                this.year = this.columns[2].trim().isEmpty() ? "0" : this.columns[2].trim();
                this.subjectArea = this.columns[3];
                this.authors = this.columns[4];
                this.title = this.columns[5];
                // Get author list
                try {
                    this.coauthorList = this.incitesParser.parseAuthors(this.authors);
                    // If the # of authors in pub is only one,
                    // duplicate the author so that the pub now
                    // contains two authors.
                    // NOTE: The interaction map needs two authors
                    // per pub AT MINIMUM to function adequately
                    if (this.coauthorList.size() == 1) {
                        Author loneAuthor = this.coauthorList.get(0);
                        this.incitesParser.getLoneAuthorList().add(loneAuthor);
                        this.coauthorList.add(loneAuthor);
                    }
                    // As soon as an erroneous entry materializes
                    // , terminate parsing --> compromises entire file
                } catch (UnableToParseAuthorException e) {
                    logger.log(Level.SEVERE, "Exception occurred", e);
                    return;
                }
                // Set publication info
                this.pub = new Publication(this.title, this.year, this.subjectArea, this.timesCited, this.expectedCitations, this.coauthorList);
                this.incitesParser.getPubList().add(this.pub);
            } else if (this.columns.length == 5) {
                this.incitesParser.updateDefectiveRows();
                // Get publication info
                this.timesCited = this.columns[0].trim().isEmpty() ? "0" : this.columns[0].trim();
                this.expectedCitations = "0.00";
                this.year = this.columns[1].trim().isEmpty() ? "0" : this.columns[1].trim();
                this.subjectArea = this.columns[2];
                this.authors = this.columns[3];
                this.title = this.columns[4];

                try {
                    this.coauthorList = this.incitesParser.parseAuthors(this.authors);
                    // If the # of authors in pub is only one,
                    // duplicate the author so that the pub now
                    // contains two authors.
                    // NOTE: The interaction map needs two authors
                    // per pub AT MINIMUM to function adequately
                    if (this.coauthorList.size() == 1) {
                        Author loneAuthor = this.coauthorList.get(0);
                        this.incitesParser.getLoneAuthorList().add(loneAuthor);
                        this.coauthorList.add(loneAuthor);
                    }
                    // As soon as an erroneous entry materializes
                    // , terminate parsing --> compromises entire file
                } catch (UnableToParseAuthorException e) {
                    logger.log(Level.SEVERE, "Exception occurred", e);
                    return;
                }
                // Set publication info
                this.pub = new Publication(this.title, this.year, this.subjectArea, this.timesCited, this.expectedCitations, this.coauthorList);
                this.incitesParser.getPubList().add(this.pub);
            } else if (this.columns.length == 4) {
                this.incitesParser.updateDefectiveRows();
                // Get publication info
                this.timesCited = this.columns[0].trim().isEmpty() ? "0" : this.columns[0].trim();
                this.expectedCitations = "0.00";
                this.year = this.columns[1].trim().isEmpty() ? "0" : this.columns[1].trim();
                this.subjectArea = "N/A";
                this.authors = this.columns[2];
                this.title = this.columns[3];

                try {
                    this.coauthorList = this.incitesParser.parseAuthors(this.authors);
                    // If the # of authors in pub is only one,
                    // duplicate the author so that the pub now
                    // contains two authors.
                    // NOTE: The interaction map needs two authors
                    // per pub AT MINIMUM to function adequately
                    if (this.coauthorList.size() == 1) {
                        Author loneAuthor = this.coauthorList.get(0);
                        this.incitesParser.getLoneAuthorList().add(loneAuthor);
                        this.coauthorList.add(loneAuthor);
                    }
                    // As soon as an erroneous entry materializes
                    // , terminate parsing --> compromises entire file
                } catch (UnableToParseAuthorException e) {
                    logger.log(Level.SEVERE, "Exception occurred", e);
                    return;
                }
                // Set publication info
                this.pub = new Publication(this.title, this.year, this.subjectArea, this.timesCited, this.expectedCitations, this.coauthorList);
                this.incitesParser.getPubList().add(this.pub);
            } else {
                this.incitesParser.updateIgnoredRows();
            }
        }
    }

    @Override
    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        // Reset row contents
        if (name.equals("row")) {
            this.rowContents = "";
        }

        // Reset cell id
        if (name.equals("c")) {
            // If the 't' tag is not empty this means that
            // 'c' holds a reference to a cell that contains
            // a string value
            if (attributes.getValue("t") != null) {
                this.isString = true;
            }
            // If the 't' tag is empty this means that
            // 'c' contains a cell value and not a reference to
            // a cell
            this.cellID = "";
        }
    }
}