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

package org.baderlab.csapps.socialnetwork.model.visualstyles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;

/**
 * Examples of visual styles currently in use by the app
 *
 * @author Victor Kofia
 */
public class VisualStyles {
    
    private static final Logger logger = Logger.getLogger(VisualStyles.class.getName());

    /**
     * Get the help message associated with the visual style type
     *
     * @param int visualStyleType
     * @return String helpMssg
     */
    public static String getHelpMessage(int visualStyleType) {
        String fileName = null;
        switch(visualStyleType) {
            case VisualStyles.INCITES_VISUAL_STYLE:
                if (INCITES_VISUAL_STYLE_HELP == null) {
                    fileName = "incites_visual_style_help.txt";
                    INCITES_VISUAL_STYLE_HELP = VisualStyles.loadHelpMessage(fileName);
                }
                return INCITES_VISUAL_STYLE_HELP;
            case VisualStyles.PUBMED_VISUAL_STYLE:
                if (PUBMED_VISUAL_STYLE_HELP == null) {
                    fileName = "pubmed_visual_style_help.txt";
                    PUBMED_VISUAL_STYLE_HELP = VisualStyles.loadHelpMessage(fileName);
                }
                return PUBMED_VISUAL_STYLE_HELP;
            case VisualStyles.SCOPUS_VISUAL_STYLE:
                if (SCOPUS_VISUAL_STYLE_HELP == null) {
                    fileName = "scopus_visual_style_help.txt";
                    SCOPUS_VISUAL_STYLE_HELP = VisualStyles.loadHelpMessage(fileName);
                }
                return SCOPUS_VISUAL_STYLE_HELP;
            case VisualStyles.DEFAULT_VISUAL_STYLE:
            default:
                if (DEFAULT_VISUAL_STYLE_HELP == null) {
                    fileName = "default_visual_style_help.txt";
                    DEFAULT_VISUAL_STYLE_HELP = VisualStyles.loadHelpMessage(fileName);
                }
                return DEFAULT_VISUAL_STYLE_HELP;
        }
    }

    /**
     * Get an instance of {@link VisualStyles}. Useful for loading resources.
     *
     * @return {@link VisualStyles} instance
     */
    private static VisualStyles getInstance() {
        return new VisualStyles();
    }

    /**
     * Load the help message from fileName
     *
     * @param String fileName
     * @return String helpMssg
     */
    private static String loadHelpMessage(String fileName) {
        String helpMssg = "";
        try {
            URL url = getInstance().getClass().getResource(fileName);
            InputStream in = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String sCurrentLine = null;
            while ((sCurrentLine = br.readLine()) != null) {
                helpMssg += sCurrentLine;
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser(String.format("Failed to load %s. FileNotFoundException.", fileName));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser(String.format("Failed to load %s. IOException.", fileName));
        }
        return helpMssg;
    }

    /**
     * <i>Default</i> visual style
     */
    final public static int DEFAULT_VISUAL_STYLE = -1;

    /**
     * <i>Default</i> visual style help message
     */
    private static String DEFAULT_VISUAL_STYLE_HELP = null;

    /**
     * <i>PubMed</i> visual style
     */
    final public static int PUBMED_VISUAL_STYLE = (130 << 24) + (14 << 16) + (29 << 8) + 110;

    /**
     * <i>PubMed</i> visual style help message
     */
    private static String PUBMED_VISUAL_STYLE_HELP = null;

    /**
     * <i>Scopus</i> visual style
     */
    final public static int SCOPUS_VISUAL_STYLE = (198 << 24) + (185 << 16) + (19 << 8) + 57;

    /**
     * <i>Scopus</i> visual style help message
     */
    private static String SCOPUS_VISUAL_STYLE_HELP = null;

    /**
     * <i>InCites</i> visual style
     */
    final public static int INCITES_VISUAL_STYLE = (84 << 24) + (18 << 16) + (180 << 8) + 87;

    /**
     * <i>InCites</i> visual style help message
     */
    private static String INCITES_VISUAL_STYLE_HELP = null;

    /**
     * A visual style map
     * <br> Key: string representation of visual style
     * <br> Value: visual style ID
     */
    private Map<String, Integer> visualStyleMap = null;

    /**
     * Get unique id (numeral) associated with visual style
     *
     * @param String visualStyle
     * @return int visualStyleID
     */
    public int getVisualStyleID(String visualStyle) {
        if (this.visualStyleMap == null) {
            this.visualStyleMap = new HashMap<String, Integer>();
            this.visualStyleMap.put("--SELECT NETWORK VISUAL STYLE--", Category.DEFAULT);
            this.visualStyleMap.put("InCites", INCITES_VISUAL_STYLE);
            this.visualStyleMap.put("PubMed", PUBMED_VISUAL_STYLE);
            this.visualStyleMap.put("Scopus", PUBMED_VISUAL_STYLE);
        }
        return this.visualStyleMap.get(visualStyle);
    }

    /**
     * Get visual style list of a certain type
     *
     * @param int visualStyleSelectorType
     * @return String[] visualStyleList
     */
    public String[] getVisualStyleList(int visualStyleSelectorType) {
        String[] visualStyleList = null;
        switch(visualStyleSelectorType) {
            case VisualStyles.DEFAULT_VISUAL_STYLE:
                visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--"};
                break;
            case VisualStyles.INCITES_VISUAL_STYLE:
                visualStyleList = new String[] {"InCites"};
                break;
            case VisualStyles.PUBMED_VISUAL_STYLE:
                visualStyleList = new String[] {"PubMed"};
                break;
            case VisualStyles.SCOPUS_VISUAL_STYLE:
                visualStyleList = new String[] {"Scopus"};
                break;
        }
        return visualStyleList;
    }
}

