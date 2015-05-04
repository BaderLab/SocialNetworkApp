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

package org.baderlab.csapps.socialnetwork.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

/**
 * A social network
 *
 * @author Victor Kofia
 */
public class SocialNetwork {

    /**
     * The network's CyNetworkView reference
     */
    private CyNetworkView cyNetworkViewRef = null;
    /**
     * The network's name
     */
    private String networkName = "";
    /**
     * The network's CyNetwork reference
     */
    private CyNetwork cyNetwork = null;
    /**
     * The network's type
     */
    private int networkType = Category.DEFAULT;
    /**
     * Visual style map <br>
     * Key: Object VisualStyle <br>
     * Value: Object[] {String attrName, int min, int max}
     */
    private Map<Object, Object[]> visualStyleMap = null;
    /**
     * The network's default visual style
     */
    private int defaultVisualStyle = VisualStyles.DEFAULT_VISUAL_STYLE;
    /**
     * The network's attribute map (stores all network table attr)
     */
    private Map<String, Object> attrMap = null;

    /**
     * The network's set of publications used to create it
     */
    private ArrayList<Publication> publications = null;
    private ArrayList<Author> identifiedFaculty = null;
    private ArrayList<Author> unidentifiedFaculty = null;
    private HashSet<Author> faculty = null;

    /**
     * The network summary (outlines network's salient attributes and any
     * issues)
     */
    private String networkSummary = null;
    // Summary attributes for this network
    private HashMap<String, Integer> locations_totalpubsummary = null;
    private HashMap<String, Integer> locations_totalcitsummary = null;
    private int num_publications = 0;
    private int num_faculty = 0;
    private int num_uniden_faculty = 0;
    private String unidentified_faculty = "";

    /**
     * Create a new social network
     *
     * @param String networkName
     * @param int networkType
     */
    public SocialNetwork(String networkName, int networkType) {
        this.setNetworkName(networkName);
        this.setNetworkType(networkType);
        // Set default visual styles
        switch (networkType) {
            case Category.INCITES:
                this.setDefaultVisualStyle(VisualStyles.INCITES_LITE_VISUAL_STYLE);
                break;
            case Category.SCOPUS:
                this.setDefaultVisualStyle(VisualStyles.SCOPUS_LITE_VISUAL_STYLE);
                break;
            case Category.PUBMED:
                this.setDefaultVisualStyle(VisualStyles.PUBMED_LITE_VISUAL_STYLE);
                break;
        }
    }

    /**
     * Get network attribute map
     *
     * @return Map attrMap
     */
    public Map<String, Object> getAttrMap() {
        if (this.attrMap == null) {
            this.setAttrMap(new HashMap<String, Object>());
        }
        return this.attrMap;
    }

    /**
     * Get {@link CyNetwork} reference
     *
     * @return {@link CyNetwork} cyNetwork
     */
    public CyNetwork getCyNetwork() {
        return this.cyNetwork;
    }

    /**
     * Get network's default visual style
     *
     * @return int defaultVisualStyle
     * @return
     */
    public int getDefaultVisualStyle() {
        return this.defaultVisualStyle;
    }

    /**
     * Get all faculty
     *
     * @return HashSet faculty
     */
    public HashSet<Author> getFaculty() {
        return this.faculty;
    }

    /**
     * Get identified faculty
     *
     * @return ArrayList identifiedFaculty
     */
    public ArrayList<Author> getIdentifiedFaculty() {
        return this.identifiedFaculty;
    }

    /**
     * Get network name
     *
     * @return String networkName
     */
    public String getNetworkName() {
        return this.networkName;
    }

    /**
     * Get network type
     *
     * @return int category
     */
    public int getNetworkType() {
        return this.networkType;
    }

    /**
     * Get network view reference
     *
     * @return {@link CyNetworkView} cyNetworkViewRef
     */
    public CyNetworkView getNetworkView() {
        return this.cyNetworkViewRef;
    }

    /**
     * Get number of faculty members
     *
     * @return int numFaculty
     */
    public int getNum_faculty() {
        return this.num_faculty;
    }

    /**
     * Get number of publications
     *
     * @return int numPublications
     */
    public int getNum_publications() {
        return this.num_publications;
    }

    /**
     * Get number of unidentified faculty
     *
     * @return int numUnidentifiedFaculty
     */
    public int getNum_uniden_faculty() {
        return this.num_uniden_faculty;
    }

    /**
     * Get list of publications
     *
     * @return ArrayList publications
     */
    public ArrayList<Publication> getPublications() {
        return this.publications;
    }

    /**
     * Get network summary
     *
     * @return String summary
     */
    public String getSummary() {

        String info;
        // Print out the summary information
        if (this.networkType == Category.INCITES) {
            info = "<html>" + "Total # of publications: " + this.num_publications + "<br>" + "Total # of faculty: " + this.num_faculty + "<br>"
                    + "Total # of unidentified faculty: " + this.num_uniden_faculty + "<br>" + "<hr><br>UNIDENTIFIED FACULTY"
                    + this.unidentified_faculty;

        } else {
            info = "<html>" + "Total # of publications: " + this.num_publications + "<br>";
        }

        this.networkSummary = info + "</html>";

        return this.networkSummary;
    }

    /**
     * Get unidentified faculty member
     *
     * @return String unidentifiedFaculty
     */
    public String getUnidentified_faculty() {
        return this.unidentified_faculty;
    }

    /**
     * Get list of unidentified faculty members
     *
     * @return ArrayList unidentifiedFacultyMembers
     */
    public ArrayList<Author> getUnidentifiedFaculty() {
        return this.unidentifiedFaculty;
    }

    /**
     * Get visual style map
     *
     * @return Map visualStyleMap
     */
    public Map<Object, Object[]> getVisualStyleMap() {
        if (this.visualStyleMap == null) {
            this.setVisualStyleMap(new HashMap<Object, Object[]>());
        }
        return this.visualStyleMap;
    }

    /**
     * Set network attribute map
     *
     * @param Map attrMap
     */
    public void setAttrMap(Map<String, Object> attrMap) {
        this.attrMap = attrMap;
    }

    /**
     * Set CyNetwork reference
     *
     * @param {@link CyNetwork} cyNetwork
     */
    public void setCyNetwork(CyNetwork cyNetwork) {
        this.cyNetwork = cyNetwork;
    }

    /**
     * Set network's default visual style
     *
     * @param int defaultVisualStyle
     * @return null
     */
    public void setDefaultVisualStyle(int defaultVisualStyle) {
        this.defaultVisualStyle = defaultVisualStyle;
    }

    /**
     * Set faculty members
     *
     * @param HashSet faculty
     */
    public void setFaculty(HashSet<Author> faculty) {
        this.faculty = faculty;
        this.setNum_faculty(faculty.size());
    }

    /**
     * Set list of identified faculty members
     *
     * @param ArrayList identifiedFaculty
     */
    public void setIdentifiedFaculty(ArrayList<Author> identifiedFaculty) {
        this.identifiedFaculty = identifiedFaculty;
    }

    /**
     * Set network name
     *
     * @param String networkName
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * Set network type
     *
     * @param int networkType
     */
    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    /**
     * Set network view reference
     *
     * @param {@link CyNetworkView} cyNetworkViewRef
     */
    public void setNetworkView(CyNetworkView cyNetworkViewRef) {
        this.cyNetworkViewRef = cyNetworkViewRef;
    }

    /**
     * Set the number of faculty members
     *
     * @param int num_faculty
     */
    private void setNum_faculty(int num_faculty) {
        this.num_faculty = num_faculty;
    }

    /**
     * Set the number of publications
     *
     * @param int num_publications
     */
    private void setNum_publications(int num_publications) {
        this.num_publications = num_publications;
    }

    /**
     * Set the number of unidentified faculty
     *
     * @param int num_uniden_faculty
     */
    private void setNum_uniden_faculty(int num_uniden_faculty) {
        this.num_uniden_faculty = num_uniden_faculty;
    }

    /**
     * Set the list of publications
     *
     * @param ArrayList publications
     */
    public void setPublications(ArrayList<Publication> publications) {
        this.publications = publications;
        this.setNum_publications(publications.size());
        if (this.networkType == Category.INCITES) {
            // Calculate the break down of locations for the set of publications
            this.locations_totalcitsummary = this.summarize_totalCitations();
            this.locations_totalpubsummary = this.summarize_totalPublications();
        }
    }

    /**
     * Set network summary
     *
     * @param String summary
     */
    public void setSummary(String summary) {
        this.networkSummary = summary;
    }

    /**
     * Set unidentified faculty member
     *
     * @param String unidentified_faculty
     */
    public void setUnidentified_faculty(String unidentified_faculty) {
        this.unidentified_faculty = unidentified_faculty;
    }

    /**
     * Set list of unidentified faculty members
     *
     * @param ArrayList unidentifiedFaculty
     */
    public void setUnidentifiedFaculty(ArrayList<Author> unidentifiedFaculty) {
        this.unidentifiedFaculty = unidentifiedFaculty;
        this.setNum_uniden_faculty(unidentifiedFaculty.size());
    }

    /**
     * Set visual style map
     *
     * @param Map visualStyleMap
     */
    public void setVisualStyleMap(Map<Object, Object[]> visualStyleMap) {
        this.visualStyleMap = visualStyleMap;
    }

    /**
     * for each publication in the set of publications for this network Each
     * publication is assigned a majority rules location Add up the citations
     * based on location. Return hashamp of location --> summed citations
     */
    private HashMap<String, Integer> summarize_totalCitations() {
        HashMap<String, Integer> locations_summary = new HashMap<String, Integer>();

        if (this.publications != null) {
            for (Publication pub : this.publications) {
                // for each publication get its most common location, add to
                // counts
                // get author locations
                String current_location = pub.getLocation();

                if (locations_summary.containsKey(current_location)) {
                    Integer count = locations_summary.get(current_location) + pub.getTimesCited();
                    locations_summary.put(current_location, count);

                } else {
                    locations_summary.put(current_location, pub.getTimesCited());
                }
            }
        }
        return locations_summary;
    }

    /**
     * for each publication in the set of publications for this network Each
     * publication is assigned a majority rules location Add up the publications
     * based on location. Return hashamp of location --> total publications
     */
    private HashMap<String, Integer> summarize_totalPublications() {
        HashMap<String, Integer> locations_summary = new HashMap<String, Integer>();

        if (this.publications != null) {
            for (Publication pub : this.publications) {
                // for each publication get its most common location, add to
                // counts
                // get author locations
                String current_location = pub.getLocation();

                if (locations_summary.containsKey(current_location)) {
                    Integer count = locations_summary.get(current_location) + 1;
                    locations_summary.put(current_location, count);

                } else {
                    locations_summary.put(current_location, 1);
                }
            }
        }
        return locations_summary;
    }

}
