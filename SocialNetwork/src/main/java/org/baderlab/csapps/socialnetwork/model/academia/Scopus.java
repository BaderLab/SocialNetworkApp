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

package org.baderlab.csapps.socialnetwork.model.academia;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.cytoscape.work.TaskMonitor;

/**
 * Tools for manipulating Scopus data (for developer use)
 *
 * @author Victor Kofia
 */
public class Scopus {

    /**
     * This exception is thrown when a year can't be parsed (i.e. incorrect
     * formatting)
     *
     * @author Victor Kofia
     */
    public static class UnableToParseYearException extends Exception {

        private static final long serialVersionUID = 1L;
    }

    private static final Logger logger = Logger.getLogger(Scopus.class.getName());

    /**
     * Progress bar variables
     */
    private int currentSteps = 0;
    private int totalSteps = 0;
    private double progress = 0.0;
    private TaskMonitor taskMonitor = null;

    /**
     * Construct Scopus attribute map
     *
     * @return Map nodeAttrMap
     */
    public static HashMap<String, Object> constructScopusAttrMap(Author author) {
        HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
        nodeAttrMap.put(NodeAttribute.Label.toString(), "");
        nodeAttrMap.put(NodeAttribute.FirstName.toString(), "");
        nodeAttrMap.put(NodeAttribute.LastName.toString(), "");
        nodeAttrMap.put(NodeAttribute.TimesCited.toString(), 0);
        nodeAttrMap.put(NodeAttribute.NumPublications.toString(), 0);
        nodeAttrMap.put(NodeAttribute.Publications.toString(), new ArrayList<String>());
        List<Integer> intervalList = new ArrayList<Integer>();
        intervalList.add(0);
        nodeAttrMap.put(NodeAttribute.YearlyPublications.toString(), intervalList);
        return nodeAttrMap;
    }

    /**
     * Reference to Scopus publication list
     */
    private ArrayList<Publication> pubList = null;

    /**
     * Create new Scopus object
     *
     * @param File csv
     * @param TaskMonitor taskMonitor
     */
    public Scopus(File csv, TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
        this.parseScopusPubList(csv);
    }

    /**
     * Get publication list
     *
     * @return ArrayList pubList
     */
    public ArrayList<Publication> getPubList() {
        if (this.pubList == null) {
            this.pubList = new ArrayList<Publication>();
        }
        return this.pubList;
    }

    /**
     * Match year. Used for verifying validity of Scopus data file.
     *
     * @param String rawText
     * @return boolean bool
     */
    private boolean matchYear(String rawText) {
        Pattern pattern = Pattern.compile("^\\d{4}$");
        Matcher matcher = pattern.matcher(rawText.trim());
        return matcher.find();
    }

    /**
     * Return authors
     *
     * @param String authors
     * @return ArrayList authorList
     */
    private ArrayList<Author> parseAuthors(String authors) {
        ArrayList<Author> authorList = new ArrayList<Author>();
        String[] contents = authors.split("\\.,");
        for (String authorInfo : contents) {
            authorList.add(new Author(authorInfo.trim(), Category.SCOPUS));
        }
        return authorList;
    }

    /**
     * Get Scopus publication list
     *
     * @param {@link File} csv
     */
    private void parseScopusPubList(File csv) {
        Scanner in = null;
        try {
            setProgressMonitor("Parsing Scopus CSV ...", totalSteps);
            in = new Scanner(csv);
            // Skip column headers
            String line = null, authors = null, year = null;
            String[] columns = null;
            Publication pub = null;
            ArrayList<Author> coauthorList = new ArrayList<Author>();
            String title = null, subjectArea = null, timesCited = null;
            String numericalData = null;
            // Parse for publications
            while (in.hasNext()) {
                line = in.nextLine();
                // Only split by commas not contained in quotes.
                columns = splitQuoted("\"", ",", line);
                authors = columns[0].replace("\"", "");
                coauthorList = this.parseAuthors(authors);
                title = columns[1].replace("\"", "");
                year = columns[2].replace("\"", "");
                if (!this.matchYear(year)) {
                    // the year doesn't match assume something is wonky with
                    // this line
                    // skip this record, print out the line and continue
                    logger.log(Level.WARNING, "Unable to parse scopus line: " + line.toString());
                    continue;
                }
                subjectArea = columns[3].replace("\"", "");
                numericalData = (columns[10] != null) ? columns[10].replace("\"", "") : columns[10];
                if (numericalData == null || numericalData.equalsIgnoreCase("")) {
                    timesCited = "0";
                } else {
                    timesCited = numericalData;
                }
                pub = new Publication(title, year, subjectArea, timesCited, null, coauthorList);
                this.getPubList().add(pub);
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            taskMonitor.setStatusMessage("File not found");
            CytoscapeUtilities.notifyUser("Unable to locate Scopus data file.\nPlease re-load" + " file and try again.");
        } finally {
            if (in != null) {
                in.close();
            }
        }

    }

    /**
     * Set publication list
     *
     * @param ArrayList pubList
     */
    private void setPubList(ArrayList<Publication> pubList) {
        this.pubList = pubList;
    }

    /**
     * Split quote using separator
     *
     * @param String quote
     * @param String separator
     * @param String s
     * @return String[] array
     */
    private String[] splitQuoted(String quote, String separator, String s) {
        // Scopus file has 14 fields.
        String[] columns = new String[14];
        int current = 0;
        int index = 0;
        int startindex = 0;
        int firstquote = s.indexOf(quote);
        int nextquote = s.indexOf(quote, current + 1);
        int nextcomma = s.indexOf(separator);
        while (current < s.length() && index < 14) {
            if (nextcomma > current && (nextcomma < firstquote || nextcomma > nextquote)) {
                columns[index] = (s.substring(startindex, nextcomma));
                startindex = nextcomma + 1;
                index++;
            }
            current = nextcomma;
            nextcomma = s.indexOf(separator, current + 1);
            // if next comma is -1 then we have gotten to end of the string.
            nextcomma = (nextcomma == -1) ? s.length() : nextcomma;
            if (current > nextquote) {
                firstquote = s.indexOf(quote, nextquote + 1);
                nextquote = s.indexOf(quote, firstquote + 1);
            }
        }
        return columns;
    }

    /**
     * Set progress monitor
     *
     * @param TaskMonitor taskMonitor
     * @param String taskName
     * @param int totalSteps
     */
    private void setProgressMonitor(String taskName, int totalSteps) {
        this.taskMonitor.setTitle(taskName);
        this.taskMonitor.setProgress(0.0);
        this.currentSteps = 0;
        this.totalSteps = totalSteps;
    }

    /**
     * Return progress as a percentage
     *
     * @param Double progress
     * @return String percentage
     */
    private String toPercent(double progress) {
        progress = progress * 100;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(progress) + "%";
    }

    /**
     * Update progress monitor
     *
     * @param int currentSteps
     */
    private void updateProgress() {
        this.currentSteps += 1;
        this.progress = (double) this.currentSteps / this.totalSteps;
        this.taskMonitor.setStatusMessage("Complete: " + toPercent(this.progress));
        this.taskMonitor.setProgress(this.progress);
    }

}
