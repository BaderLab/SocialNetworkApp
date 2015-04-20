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

/**
 * Examples of visual styles currently in use by the app
 *
 * @author Victor Kofia
 */
public class VisualStyles {

    /**
     * Default visual style
     */
    final public static int DEFAULT_VISUAL_STYLE = -99;

    /**
     * Default visual style help message
     */
    final public static String DEFAULT_VISUAL_STYLE_HELP =  "The default visual style is the bas" +
            "e visual style. It makes use of no " +
            "network attributes.";

    /**
     * Pubmed 'Lite' visual style
     */
    final public static int PUBMED_LITE_VISUAL_STYLE = -100;

    /**
     * Pubmed 'Lite' visual style help message
     */
    final public static String PUBMED_LITE_VISUAL_STYLE_HELP =  "The Pubmed 'Lite' visual style is d" +
            "esigned for Pubmed networks. Pubmed" +
            " networks typically lack institutio" +
            "n information. Thus, no color codin" +
            "g is present. However they do conta" +
            "in all the standard list of attribu" +
            "tes found in Academia data-sets. In" +
            "formation on the total citation cou" +
            "nt and total co-publication count o" +
            "f each individual author is present" +
            " and can be visualized via node-siz" +
            "e and edge width respectively.";

    /**
     * Scopus 'Lite' visual style
     */
    final public static int SCOPUS_LITE_VISUAL_STYLE = -101;

    /**
     * Scopus 'Lite' visual style help message
     */
    final public static String SCOPUS_LITE_VISUAL_STYLE_HELP =  "The Scopus 'Lite' visual style is d" +
            "esigned for Scopus networks. Scopus" +
            " networks typically lack institutio" +
            "n information. Thus, no color codin" +
            "g is present. However they do conta" +
            "in all the standard list of attribu" +
            "tes found in Academia data-sets. In" +
            "formation on the total citation cou" +
            "nt and total co-publication count o" +
            "f each individual author is present" +
            " and can be visualized via node-siz" +
            "e and edge width respectively.";

    /**
     * Incites 'Lite' visual style
     */
    final public static int INCITES_LITE_VISUAL_STYLE = -103;

    /**
     * Incites 'Lite' visual style help message
     */
    final public static String INCITES_LITE_VISUAL_STYLE_HELP = "The Incites 'Lite' visual style is " +
            "designed for Incites networks. Inci" +
            "tes networks are fully annotated. T" +
            "his means that in addition to the s" +
            "tandard list of attributes typicall" +
            "y found in Academia data-sets they " +
            "also contain institution informatio" +
            "n. The Incites 'Lite' visual style " +
            "works by matching institutions to t" +
            "heir respective locations and color" +
            "-coding them. Institutions for whic" +
            "h no location has been found are cl" +
            "assified as 'other' (purple). Locat" +
            "ion matching is determined by a loc" +
            "al map and as such may or may not b" +
            "e adequate. To add an institution a" +
            "nd it's associated location go to:<" +
            "br> <b>Tools</b> - <b>Incites</b> -" +
            " <b>Add institution</b>.";

    /**
     * A visual style map
     * <br> Key: string representation of visual style
     * <br> Value: visual style ID
     */
    private Map<String, Integer> visualStyleMap = null;

    /**
     * Get unique id (numeral) associated with visual style
     * @param String visualStyle
     * @return int visualStyleID
     */
    public int getVisualStyleID(String visualStyle) {
        if (this.visualStyleMap == null) {
            this.visualStyleMap = new HashMap<String, Integer>();
            this.visualStyleMap.put("--SELECT NETWORK VISUAL STYLE--", Category.DEFAULT);
            this.visualStyleMap.put("Incites 'Lite'", INCITES_LITE_VISUAL_STYLE);
            this.visualStyleMap.put("Pubmed 'Lite'", PUBMED_LITE_VISUAL_STYLE);
            this.visualStyleMap.put("Scopus 'Lite'", PUBMED_LITE_VISUAL_STYLE);
        }
        return this.visualStyleMap.get(visualStyle);
    }

    /**
     * Get visual style list of a certain type
     * @param int visualStyleSelectorType
     * @return String[] visualStyleList
     */
    public String[] getVisualStyleList(int visualStyleSelectorType) {
        String[] visualStyleList = null;
        switch(visualStyleSelectorType) {
            case VisualStyles.DEFAULT_VISUAL_STYLE:
                visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--"};
                break;
            case VisualStyles.INCITES_LITE_VISUAL_STYLE:
                visualStyleList = new String[] {"Incites 'Lite'"};
                break;
            case VisualStyles.PUBMED_LITE_VISUAL_STYLE:
                visualStyleList = new String[] {"Pubmed 'Lite'"};
                break;
            case VisualStyles.SCOPUS_LITE_VISUAL_STYLE:
                visualStyleList = new String[] {"Scopus 'Lite'"};
                break;
        }
        return visualStyleList;
    }

}

