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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.AbstractNode;
import org.baderlab.csapps.socialnetwork.model.visualstyles.academia.EdgeAttribute;
import org.cytoscape.model.CyEdge;

/**
 * A publication (article, review, scientific paper)
 *
 * @author Victor Kofia
 */
public class Publication extends AbstractEdge {

    /**
     * A list of all authors who collaborated on Publication
     */
    private ArrayList<Author> authorList = new ArrayList<Author>();
    /**
     * The expected number of citations Publication expects to receive
     */
    private String expectedCitations = null;
    /**
     * The journal to which Publication belongs
     */
    private String journal = null;
    /**
     * Location that most of the authors are from
     */
    private String location = "N/A";
    /**
     * Publication's release date
     */
    private String pubYear = null;
    /**
     * The total amount of times Publication has been cited
     */
    private String timesCited = null;
    /**
     * Publication's title
     */
    private String title = null;
    /**
     * A unique identifier for this publication
     */
    private String pmid = null;

    /**
     * Create new publication
     *
     * @param String pubYear
     * @param String title
     * @param String journal
     * @param String timesCited
     * @param String expectedCitations
     * @param List coauthorList
     */
    public Publication(String title, String pubYear, String journal, String timesCited, String expectedCitations, List<Author> coauthorList) {
        this.pubYear = pubYear;
        this.title = title;
        this.journal = journal;
        this.authorList.addAll(coauthorList);
        if (timesCited != null && Pattern.matches("[0-9]+", timesCited)) {
            this.timesCited = timesCited;
        }
        this.expectedCitations = expectedCitations;
        constructEdgeAttrMap();

        // Calculate the most used location
        this.calculateLocation();
    }

    /**
     * Calculate the location
     */
    public void calculateLocation() {
        String maxlocation = "N/A";
        Integer max = 0;
        HashMap<String, Integer> all_locations = new HashMap<String, Integer>();

        // Add a comma between each author
        for (Author author : this.authorList) {
            // get author locations
            String current_location = author.getLocation();

            if (all_locations.containsKey(current_location)) {
                Integer count = all_locations.get(current_location) + 1;
                all_locations.put(current_location, count);
                // only set the max count if it doesn't belong to "N/A" group
                if ((count > max) && (!current_location.equalsIgnoreCase("N/A"))) {
                    max = count;
                    maxlocation = current_location;
                }
            } else {
                all_locations.put(current_location, 1);
            }
        }

        this.location = maxlocation;

    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractEdge#constructEdgeAttrMap()
     */
    @Override
    public void constructEdgeAttrMap() {
        this.edgeAttrMap = new HashMap<String, Object>();
        this.edgeAttrMap.put(EdgeAttribute.TimesCited.toString(), this.timesCited);
        this.edgeAttrMap.put(EdgeAttribute.PublicationDate.toString(), this.pubYear);
        this.edgeAttrMap.put(EdgeAttribute.Journal.toString(), this.journal);
        this.edgeAttrMap.put(EdgeAttribute.Title.toString(), this.title);
    }

    /**
     * Get author list
     *
     * @return ArrayList<Author> authorList
     */
    public ArrayList<Author> getAuthorList() {
        return this.authorList;
    }

    /**
     * Return a text representation of all of publication's authors
     *
     * @return String authors
     */
    public String getAuthors() {
        String allAuthors = "";
        // Add a comma between each author
        for (Author author : this.authorList) {
            allAuthors += author + ", ";
        }
        return allAuthors;
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractEdge#getCyEdge()
     */
    @Override
    public CyEdge getCyEdge() {
        return super.cyEdge;
    }

    /**
     * Get edge attribute map
     *
     * @return Map edgeAttrMap
     */
    @Override
    public Map<String, Object> getEdgeAttrMap() {
        return this.edgeAttrMap;
    }

    /**
     * Get expected citations
     *
     * @return String expectedCitations
     */
    public String getExpectedCitations() {
        return this.expectedCitations;
    }

    /**
     * Get the location associated with this publication
     *
     * @return String location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Get the authors of this publication
     *
     * @return List authorList
     */
    @Override
    public List<? extends AbstractNode> getNodes() {
        return this.authorList;
    }

    /**
     * Get the PMID of this publication
     * 
     * @return String pmid
     */
    public String getPMID() {
        return this.pmid;
    }

    /**
     * Get publication year
     *
     * @return String pubYear
     */
    public String getPubYear() {
        return this.pubYear;
    }

    /**
     * Get times cited
     *
     * @return int timesCited
     */
    public int getTimesCited() {
        if (timesCited != null && Pattern.matches("[0-9]+", timesCited)) {
            return Integer.parseInt(this.timesCited);
        } else {
            return 0;
        }
    }

    /**
     * Get publication title
     *
     * @return String title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Return true iff publication was authored by a single individual
     *
     * @return boolean bool
     */
    public boolean isSingleAuthored() {
        return this.authorList.size() == 2 && this.authorList.get(0).equals(this.authorList.get(1));
    }

    /**
     * Set the author list
     *
     * @param ArrayList<AuthorList> authorList
     */
    public void setAuthorList(ArrayList<Author> authorList) {
        this.authorList = authorList;
    }

    /**
     * Set publication authors
     *
     * @param ArrayList authors
     */
    public void setAuthors(ArrayList<Author> authors) {
        this.authorList = authors;
    }

    /**
     * Set CyEdge
     *
     * @param CyEdge cyEdge
     */
    @Override
    public void setCyEdge(CyEdge cyEdge) {
        this.cyEdge = cyEdge;
    }

    /**
     * Set expected citations
     *
     * @param String expectedCitations
     */
    public void setExpectedCitations(String expectedCitations) {
        this.expectedCitations = expectedCitations;
    }

    /**
     * Set location
     *
     * @param String location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Set the PMID of this publication
     * 
     * @param String pmid
     */
    public void setPMID(String pmid) {
        this.pmid = pmid;
    }

    /**
     * Set the year in which this publication was published
     *
     * @param String pubYear
     */
    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    /**
     * Set times cited
     *
     * @param int timesCited
     */
    public void setTimesCited(int timesCited) {
        this.timesCited = String.valueOf(timesCited);
    }

    /**
     * Set times cited
     * 
     * @param String timesCited
     */
    public void setTimesCited(String timesCited) {
        this.timesCited = timesCited;
    }

    /**
     * Set publication title
     *
     * @param String title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Return a string representation of the publication in the format: <br>
     * Title: <i>title</i> <br>
     * Pub-Date: <i>pubdate</i> <br>
     * Authors: <i>author</i>
     *
     * @return String publication
     */
    @Override
    public String toString() {
        return "Title: " + this.title + "\nTimes Cited: " + this.timesCited;
    }
    

    @Override
    public int hashCode() {
        final int prime = 13;
        int result = 1;
        result += prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        result += prime * result + this.timesCited.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        Publication otherPub = (Publication) obj;
        isEqual = otherPub.getTitle().equalsIgnoreCase(this.getTitle());
        isEqual = isEqual && otherPub.getTimesCited() == this.getTimesCited();
        return isEqual;
    }

}
