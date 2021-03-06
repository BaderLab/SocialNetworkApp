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

package org.baderlab.csapps.socialnetwork.model.academia.parsers;

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * Tools for manipulating Scopus data (for developer use)
 *
 * @author Victor Kofia
 */
public class ParseScopus extends AbstractTask {

    /**
     * This exception is thrown when a year can't be parsed (i.e. incorrect
     * formatting)
     *
     * @author Victor Kofia
     */
    public static class UnableToParseYearException extends Exception {

        private static final long serialVersionUID = 1L;
    }

    /**
     * Construct Scopus attribute map
     *
     * @return Map nodeAttrMap
     */
    public static HashMap<String, Object> constructScopusAttrMap() {
        HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
        nodeAttrMap.put(NodeAttribute.LABEL.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.FIRST_NAME.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.LAST_NAME.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.MAIN_INSTITUTION.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.TIMES_CITED.toString(), 0);
        nodeAttrMap.put(NodeAttribute.PUBLICATION_COUNT.toString(), 0);
        nodeAttrMap.put(NodeAttribute.PUBLICATIONS.toString(), new ArrayList<String>());
        nodeAttrMap.put(NodeAttribute.INSTITUTIONS.toString(), new ArrayList<String>());
        List<Integer> pubsPerYearList = new ArrayList<Integer>();
        pubsPerYearList.add(0);
        nodeAttrMap.put(NodeAttribute.PUBS_PER_YEAR.toString(), pubsPerYearList);
        String startYearTxt = SocialNetworkAppManager.getStartDateTextFieldRef().getText().trim();
        String endYearTxt = SocialNetworkAppManager.getEndDateTextFieldRef().getText().trim();
        if (Pattern.matches("[0-9]+", startYearTxt) && Pattern.matches("[0-9]+", endYearTxt)) {
            int startYear = Integer.parseInt(startYearTxt), endYear = Integer.parseInt(endYearTxt);
            List<Integer> years = new ArrayList<Integer>();
            for (int i = startYear; i <= endYear; i++) {
                years.add(i);
            }
            nodeAttrMap.put(NodeAttribute.YEARS_ACTIVE.toString(), years);
        }
        return nodeAttrMap;   
    }

    private static final Logger logger = Logger.getLogger(ParseScopus.class.getName());
    /**
     * Progress bar variables
     */
    private int currentSteps = 0;
    private int totalSteps = 0;
    private double progress = 0.0;

    private TaskMonitor taskMonitor = null;
    
    private File csv = null;
    private SocialNetwork socialNetwork = null;
    
    /**
     * A reference to the {@link SocialNetworkAppManager}. Used to
     * retrieve the name of the network (usually same as the Scopus CSV
     * filename), the network file and a reference to the user panel.
     */
    private SocialNetworkAppManager appManager = null;

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
    public ParseScopus(File csv,SocialNetwork socialNetwork, SocialNetworkAppManager appManader) {
    	this.csv = csv;
    	this.socialNetwork = socialNetwork;
    	this.appManager = appManager;
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
     * Match the co-authors in the co-author list with the specified affiliations
     * 
     * @param ArrayList coauthorList
     * @param String affiliations
     */
    private void matchAuthors(ArrayList<Author> coauthorList, String affiliations) {
    	Map<String, Author> coauthorMap = new HashMap<String, Author>();
    	for (Author coauthor : coauthorList) {
    		coauthorMap.put(String.format("%s, %s", coauthor.getLastName().toLowerCase(), coauthor.getFirstInitial().toLowerCase()), coauthor);
    	}
    	String[] affilArray = affiliations.split(";"), content = null;
    	String authorName = null;
    	StringBuffer institution = new StringBuffer();
    	for (int i = 0; i < affilArray.length; i++) {
    		content = affilArray[i].split("\\.,");
    		if (content.length > 1) {
    			authorName = content[0].trim().toLowerCase();
    			Pattern r = Pattern.compile("(^[a-z]+-?[a-z]+, [a-z])(\\..+)?$");
    			Matcher m = r.matcher(authorName);
    			if (m.find()) {
    			    authorName = m.group(1);
    			}
    			institution.setLength(0);
    			for (int j = 1; j < content.length; j++) {
    				institution.append(content[j].trim());    				
    			}
    			if (coauthorMap.get(authorName) != null) {
    				coauthorMap.get(authorName).addInstitution(institution.toString());
    			}
    		}
    	}
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
        BufferedReader in = null;
        try {
            setProgressMonitor("Parsing Scopus CSV ...", totalSteps);
            in = new BufferedReader(new FileReader(csv));
            // Get # of columns
            String currentLine;
            if ((currentLine = in.readLine())==null) {
            	in.close();
            	return;
            }
            String columnTitlesRawText = currentLine;
            String[] columnTitlesArray = columnTitlesRawText.split(",");
            int numColumns = columnTitlesArray.length;
            
            //Remove hard coded columns and try and discover which
            //Column the data is in.
            int titleColumn = -1,  yearColumn = -1, 
            		timesCitedColumn = -1, subjectColumn = -1, affiliationsColumn = -1;
            
            //For some reason it is not matching the Authors column names
        	//eventhough it is clearly Authors in the file
        	//default the authors to the first column if not found.
            int authorColumn = 0;
            //get the ids for each column from the header. 
            for(int i=0;i<columnTitlesArray.length;i++) {

            	if(columnTitlesArray[i].equalsIgnoreCase("Title"))
            		titleColumn = i;
            	if(columnTitlesArray[i].equalsIgnoreCase("Year"))
            		yearColumn = i;
            	if(columnTitlesArray[i].equalsIgnoreCase("Cited By"))
            		timesCitedColumn = i;
            	if(columnTitlesArray[i].equalsIgnoreCase("Source title"))
            		subjectColumn = i;
            	if(columnTitlesArray[i].equalsIgnoreCase("Affiliations"))
            		affiliationsColumn = i;
            	if(columnTitlesArray[i].trim().equalsIgnoreCase("Authors"))
            		authorColumn = i;
            }
            
            
            // Skip column headers
            String line = null, authors = null, year = null;
            String[] columns = null;
            Publication pub = null;
            ArrayList<Author> coauthorList = new ArrayList<Author>();
            String title = null, subjectArea = null, timesCited = null;
            String numericalData = null, affiliations = null;
            // Parse for publications
            while ((line = in.readLine()) != null) {
                
                // Only split by commas not contained in quotes.
                columns = splitQuoted("\"", ",", line, numColumns);
                //only parse line if it has the same number of columns as the title line
                authors = (columns[authorColumn]!=null)?columns[authorColumn].replace("\"", ""):"";
                coauthorList = this.parseAuthors(authors);
                title = (columns[titleColumn]!=null)?columns[titleColumn].replace("\"", ""):"";
                year = (columns[yearColumn]!=null)?columns[yearColumn].replace("\"", ""):"";
                // Add affiliations only if the Scopus document has the 'Authors with Affiliations' column
                if (affiliationsColumn != -1) {
                	affiliations = (columns[affiliationsColumn]!=null)?columns[affiliationsColumn].replace("\"", ""):"";
                	matchAuthors(coauthorList, affiliations);                	
                }
                if (!this.matchYear(year)) {
                    	// the year doesn't match assume something is wonky with
                    	// this line
                    	// skip this record, print out the line and continue
                    	logger.log(Level.WARNING, String.format("Unable to parse Scopus line: %s", line.toString()));
                	
                }
                else{
                	subjectArea = (columns[subjectColumn]!=null)?columns[subjectColumn].replace("\"", ""):"";
                	numericalData = (columns[timesCitedColumn] != null) ? columns[timesCitedColumn].replace("\"", "") : columns[timesCitedColumn];
                	if (numericalData == null || numericalData.equalsIgnoreCase("")) {
                    		timesCited = "0";
                	} else {
                    		timesCited = numericalData;
                	}
                	pub = new Publication(title, year, subjectArea, timesCited, null, coauthorList);
                	this.getPubList().add(pub);
                }
            }
            if (in != null) {
                in.close();
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            taskMonitor.setStatusMessage("File not found");
            CytoscapeUtilities.notifyUser("Unable to locate Scopus data file.\nPlease re-load" + " file and try again.");
        } catch (IOException eb) {
            logger.log(Level.SEVERE, "Exception occurred", eb);
            taskMonitor.setStatusMessage("Unable to read file");
            CytoscapeUtilities.notifyUser("Unable to locate Scopus data file.\nPlease re-load" + " file and try again.");
        } 
            
        

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
     * @param int numColumns
     * 
     * @return String[] array
     */
    private String[] splitQuoted(String quote, String separator, String s, int numColumns) {
        String[] columns = new String[numColumns];
        int current = 0;
        int index = 0;
        int startindex = 0;
        int firstquote = s.indexOf(quote);
        int nextquote = s.indexOf(quote, current + 1);
        int nextcomma = s.indexOf(separator);
        while (current < s.length() && index < numColumns) {
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

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
        this.taskMonitor = taskMonitor;
		
		this.parseScopusPubList(csv);
		
		ArrayList<Publication> pubList = getPubList();
        socialNetwork.setPublications(getPubList());
        if (pubList == null) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            return;
        }

		
	}

}
