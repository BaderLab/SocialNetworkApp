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

import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * All the categories currently supported by the Social Network App. <br>
 * NOTE: IPs are merely used to identify each attribute. They do not have a
 * hidden significance.
 *
 * @author Victor Kofia
 */
public class Category {

    /**
     * Create default info panel
     *
     * @return JPanel defaultInfoPanel
     */
    public static JPanel createDefaultInfoPanel() {
        JPanel defaultInfoPanel = new JPanel();
        defaultInfoPanel.setName("--SELECT CATEGORY--");
        return defaultInfoPanel;
    }
    
    /**
     * Create LinkedIn info panel
     *
     * @return JPanel linkedInInfoPanel
     */
    public static JPanel createLinkedInInfoPanel() {
        JPanel linkedInInfoPanel = new JPanel();
        linkedInInfoPanel.setName("LinkedIn");
        linkedInInfoPanel.setBorder(BorderFactory.createTitledBorder("LinkedIn"));
        return linkedInInfoPanel;
    }

    /**
     * Create Twitter info panel
     *
     * @return JPanel twitterInfoPanel
     */
    public static JPanel createTwitterInfoPanel() {
        JPanel twitterInfoPanel = new JPanel();
        twitterInfoPanel.setName("Twitter");
        twitterInfoPanel.setBorder(BorderFactory.createTitledBorder("Twitter"));
        return twitterInfoPanel;
    }

    /**
     * Create Youtube info panel
     *
     * @return JPanel youtubeInfoPanel
     */
    public static JPanel createYoutubeInfoPanel() {
        JPanel youtubeInfoPanel = new JPanel();
        youtubeInfoPanel.setName("Youtube");
        youtubeInfoPanel.setBorder(BorderFactory.createTitledBorder("Youtube"));
        return youtubeInfoPanel;
    }

    /**
     * Get unique id (numeral) associated with category
     *
     * @param String category
     * @return int categoryID
     */
    public static int getCategoryID(String category) {
        if (Category.categoryMap == null) {
            Category.categoryMap = new HashMap<String, Integer>();
            String[] columns = new String[] { "--SELECT CATEGORY--", "Academia", "Twitter", "LinkedIn", "Youtube", "InCites", "Scopus", "PubMed" };
            int[] ids = new int[] { DEFAULT, ACADEMIA, TWITTER, LINKEDIN, YOUTUBE, INCITES, SCOPUS, PUBMED };
            for (int i = 0; i < 7; i++) {
                categoryMap.put(columns[i], ids[i]);
            }
        }
        return categoryMap.get(category);
    }

    /**
     * Get list of all categories currently supported by app
     *
     * @return List categoryList
     */
    public static String[] getCategoryList() {
        String[] categoryList = { "Academia" };
        return categoryList;
    }

    /**
     * Get list of search filters. Filter type varies with category.
     *
     * @param int selectedCategory
     * @return String[] searchFilterList
     */
    public static String[] getSearchFilterList(int selectedCategory) {
        String[] searchFilterList = null;
        switch (selectedCategory) {
            case Category.DEFAULT:
                searchFilterList = new String[] { "--SELECT FILTER--" };
                break;
            case Category.ACADEMIA:
                searchFilterList = new String[] { "Authors" };
                break;
        }
        return searchFilterList;
    }

    /**
     * Return string representation of category
     *
     * @param int categoryID
     * @return String category
     * @throws UnableToIdentifyCategoryException 
     */
    public static String toString(int categoryID) throws UnableToIdentifyCategoryException {
        String category = null;
        switch (categoryID) {
            case Category.DEFAULT:
                category = "--SELECT CATEGORY--";
                break;
            case Category.ACADEMIA:
                category = "Academia";
                break;
            case Category.FACULTY:
            	category = "Faculty";
            	break;
            case Category.INCITES:
                category = "InCites";
                break;
            case Category.LINKEDIN:
            	category = "LinkedIn";
            	break;
            case Category.PUBMED:
            	category = "PubMed";
            	break;
            case Category.SCOPUS:
                category = "Scopus";
                break;
            case Category.TWITTER:
            	category = "Twitter";
            	break;
            case Category.YOUTUBE:
            	category = "Youtube";
            	break;
            default:
            	throw new UnableToIdentifyCategoryException("Unknown");
        }
        return category;
    }
    
    /**
     * Return category represented by string
     *
     * @param String categoryID
     * @return int category
     * @throws UnableToIdentifyCategoryException 
     */
    public static int toCategory(String categoryID) throws UnableToIdentifyCategoryException {
    	if (categoryID.equalsIgnoreCase("academia")) {
    		return Category.ACADEMIA;
    	} else if (categoryID.equalsIgnoreCase("default")) {
    		return Category.DEFAULT;
    	} else if (categoryID.equalsIgnoreCase("faculty")) {
    		return Category.FACULTY;
    	} else if (categoryID.equalsIgnoreCase("incites")) {
    		return Category.INCITES;
    	} else if (categoryID.equalsIgnoreCase("linkedin")) {
    		return Category.LINKEDIN;
    	} else if (categoryID.equalsIgnoreCase("pubmed")) {
    		return Category.PUBMED;
    	} else if (categoryID.equalsIgnoreCase("scopus")) {
    		return Category.SCOPUS;
    	} else if (categoryID.equalsIgnoreCase("twitter")) {
    		return Category.TWITTER;
    	} else if (categoryID.equalsIgnoreCase("youtube")) {
    		return Category.YOUTUBE;
    	}
    	throw new UnableToIdentifyCategoryException(categoryID);
    }

    /**
     * Academia (IP = 74.125.226.112)
     */
    final public static int ACADEMIA = (74 << 24) + (125 << 16) + (226 << 8) + 112;

    /**
     * Default category
     */
    final public static int DEFAULT = -1;

    /**
     * Faculty category
     */
    final public static int FACULTY = -2;

    /**
     * InCites (IP = 84.18.180.87)
     */
    final public static int INCITES = (84 << 24) + (18 << 16) + (180 << 8) + 87;

    /**
     * LinkedIn (IP = 216.52.242.80)
     */
    final public static int LINKEDIN = (216 << 24) + (52 << 16) + (242 << 8) + 80;

    /**
     * PubMed (IP = 130.14.29.110)
     */
    final public static int PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;

    /**
     * Scopus (IP = 198.185.19.57)
     */
    final public static int SCOPUS = (198 << 24) + (185 << 16) + (19 << 8) + 57;

    /**
     * Twitter (IP = 199.59.150.39)
     */
    final public static int TWITTER = (199 << 24) + (59 << 16) + (150 << 8) + 39;

    /**
     * Youtube (IP = 74.125.226.101)
     */
    final public static int YOUTUBE = (74 << 24) + (125 << 16) + (226 << 8) + 101;

    /**
     * A category map <br>
     * key: String representation of category <br>
     * value: category ID
     */
    private static Map<String, Integer> categoryMap = null;

}
