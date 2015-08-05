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

package org.baderlab.csapps.socialnetwork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * Public utilities class with static methods used repeatedly by the code
 */
public class CytoscapeUtilities {
    
    // TODO: Add documentation
    private static final Logger logger = Logger.getLogger(CytoscapeUtilities.class.getName());
    private static HashSet<String> locationSet = null;
    
    public static HashSet<String> getLocationSet() {
        if (CytoscapeUtilities.locationSet == null) {
            CytoscapeUtilities.locationSet = new HashSet<String>();
            String[] locations = new String[] { "univ toronto", "ontario", "canada", "united states", "international", "other" };
            for (String location : locations) {
                CytoscapeUtilities.locationSet.add(location);
            }            
        }
        return CytoscapeUtilities.locationSet;        
    }
    
    /**
     * Return the visual style with the specified name in the set of all visual
     * styles. null is returned if no visual style is found.
     * 
     * @param String name
     * @param VisualMappingManager visualMappingManager
     * 
     * @return VisualStyle visualStyle
     */
    public static VisualStyle getVisualStyle(String name, VisualMappingManager visualMappingManager) {
        Iterator<VisualStyle> it = visualMappingManager.getAllVisualStyles().iterator();
        VisualStyle visualStyle = null;
        while (it.hasNext()) {
            visualStyle = it.next();
            if (visualStyle.getTitle().equalsIgnoreCase(name)) {
                break;
            }
            visualStyle = null;
        }
        return visualStyle;
    }

    /**
     * Notify user of an issue (indicated by message)
     *
     * @param String message
     */
    public static void notifyUser(String message) {
        NotificationThread notify = new NotificationThread(message);
        notify.start();
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> getLocationMap() {
        String outputDir = System.getProperty("user.home");
        String basename = outputDir + System.getProperty("file.separator") + "social_network_locations.sn";
        Map<String, String> locationMap = null;
        try {
            InputStream in = new FileInputStream(basename);
            ObjectInputStream ois = new ObjectInputStream(in);
            locationMap = (HashMap<String, String>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException fileNotFoundException) {
            logger.log(Level.SEVERE, "Exception occurred", fileNotFoundException);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Exception occurred", ioException);
        } catch (ClassNotFoundException classNotFoundException) {
            logger.log(Level.SEVERE, "Exception occurred", classNotFoundException);
        }
        return locationMap;
    }
    
    public static void saveLocationMap(Map<String, String> locationMap) {
        String outputDir = System.getProperty("user.home");
        String basename = outputDir + System.getProperty("file.separator") + "social_network_locations.sn";
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(basename);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(locationMap);
            oos.close();
        } catch (FileNotFoundException fileNotFoundException) {
            logger.log(Level.SEVERE, "Exception occurred", fileNotFoundException);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Exception occurred", ioException);
        }
    }
    
    private static void updateLocationMap(String institution, String location, String defaultInstitution, CyNetwork cyNetwork, Long SUID) {
        if (!institution.trim().isEmpty() && !location.trim().isEmpty()) {
            Map<String, String> locationMap = CytoscapeUtilities.getLocationMap();
            locationMap.put(institution, location);
            CytoscapeUtilities.saveLocationMap(locationMap);
            if (defaultInstitution != null) {
                CyTable cyTable = cyNetwork.getDefaultNodeTable();
                CytoscapeUtilities.setCyTableAttribute(cyTable, SUID, "Location", location);
            }
        }
    }
    
    //TODO: Write method description (perhaps change method name)
    public static void createDialogBox(String title, String defaultInstitution, CyNetwork cyNetwork, Long SUID) {
        JTextField institutionTextField = new JTextField(5);
        if (defaultInstitution != null) {
            institutionTextField.setText(defaultInstitution);
            institutionTextField.setEditable(false);
        }
        JTextField locationTextField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        myPanel.add(new JLabel("Institution"));
        myPanel.add(institutionTextField);
        myPanel.add(new JLabel("Location"));
        myPanel.add(locationTextField);

        int outcome = JOptionPane.OK_OPTION;
        String institution = "N/A", location = "N/A";
        while (outcome == JOptionPane.OK_OPTION) {
            // Display dialog box and get user's outcome.
            outcome = JOptionPane.showConfirmDialog(null, myPanel, title, JOptionPane.OK_CANCEL_OPTION);
            if (outcome == JOptionPane.OK_OPTION) {
                institution = institutionTextField.getText().trim();
                location = locationTextField.getText().trim();
                if (institution.isEmpty() && location.isEmpty()) {
                    CytoscapeUtilities.notifyUser("Please specify both an institution and a location");
                } else {
                    if (institution.isEmpty()) {
                        CytoscapeUtilities.notifyUser("Please specify an institution");
                    } else if (location.isEmpty()) {
                        CytoscapeUtilities.notifyUser("Please specify a location");
                    } else {
                        if (!getLocationSet().contains(location.toLowerCase())) {
                            CytoscapeUtilities.notifyUser("Location does not exist. Please enter a valid location.");
                        } else {
                            institution = institution.toUpperCase();
                            // Format location (in case casing was done improperly)
                            // i.e. 'united states' becomes 'United States'
                            String[] words = location.split("\\s");
                            location = "";
                            for (String word : words) {
                                location += word.replaceAll("^\\w", word.substring(0, 1).toUpperCase()) + " ";
                            }
                            location = location.trim();
                            outcome = JOptionPane.CANCEL_OPTION;
                            CytoscapeUtilities.updateLocationMap(institution, location, defaultInstitution, cyNetwork, SUID);
                        }
                    }
                }
            }
        }    
    }
    
    /**
     * Return the author with the specified label in the collaboration array
     * 
     * @param String label
     * @param String[] authorArray
     * 
     * @return Author author
     */
    public static Author getAuthor(String label, Collaboration[] collabArray) {
        Collaboration collab = null;
        Author author1 = null, author2 = null;
        for (int i = 0; i < collabArray.length; i++) {
            collab = collabArray[i];
            author1 = (Author) collab.getNode1();
            author2 = (Author) collab.getNode2();
            if (author1.getLabel().equals(label)) {
                return author1;
            }
            if (author2.getLabel().equals(label)) {
                return author2;
            }
        }
        return null;
    }
    
    /**
     * Set the specified table attribute to a new value
     * 
     * @param CyTable cyTable
     * @param Long SUID
     * @param String attributeName
     * @param Object value
     */
    public static void setCyTableAttribute(CyTable cyTable, Long SUID, String attributeName, Object value) {
        CyRow cyRow = cyTable.getRow(SUID);
        cyRow.set(attributeName, value);
    }
    
    /**
     * Get the specified table attribute
     * 
     * @param CyTable cyTable
     * @param Long SUID
     * @param String attributeName
     */
    public static Object getCyTableAttribute(CyTable cyTable, Long SUID, String attributeName) {
        CyRow cyRow = cyTable.getRow(SUID);
        return cyRow.getAllValues().get(attributeName);
    }

    public static String buildId = "";
    public static String pluginReleaseSuffix = "";
    public static String pluginUrl = "";
    public static String pluginVersion = "";

    public static String userManualUrl = "";

    public Properties build_props = new Properties();
    public Properties plugin_props = new Properties();

    public String pluginName = "";

    /**
     * Create a new {@link CytsocapeUtilities} object to store important
     * information
     */
    public CytoscapeUtilities() {
        try {
            this.plugin_props = getPropertiesFromClasspath("plugin.props", false);
        } catch (IOException ei) {
            System.out.println("Neither of the configuration files could be found");
        }
        CytoscapeUtilities.pluginUrl = this.plugin_props.getProperty("pluginURL", "http://baderlab.org/Software/SocialNetworkApp");
        CytoscapeUtilities.userManualUrl = CytoscapeUtilities.pluginUrl + "/UserManual";
        CytoscapeUtilities.pluginVersion = this.plugin_props.getProperty("pluginVersion", "0.1");
        CytoscapeUtilities.pluginReleaseSuffix = this.plugin_props.getProperty("pluginReleaseSuffix", "");
        this.pluginName = this.plugin_props.getProperty("pluginName", "SocialNetworkApp");
        // read buildId properties:
        // properties available in revision.txt ( git.branch,git.commit.id,
        // git.build.user.name,
        // git.build.user.email, git.build.time,
        // git.commit.id,git.commit.id.abbrev
        // , build.user,build.timestamp, build.os, build.java_version,
        // build.number)
        try {
            this.build_props = getPropertiesFromClasspath("revision.txt", true);
        } catch (IOException e) {
            // TODO: write Warning
            // "Could not load 'buildID.props' - using default settings"
            this.build_props.setProperty("build.number", "0");
            this.build_props.setProperty("git.commit.id", "0");
            this.build_props.setProperty("build.user", "user");
            // Enrichment_Map_Plugin.build_props.setProperty("build.host",
            // "host");-->can't access with maven implementaion
            this.build_props.setProperty("git.build.time", "1900/01/01 00:00:00 +0000 (GMT)");
        }

        CytoscapeUtilities.buildId = "Build: " + this.build_props.getProperty("build.number") + " from GIT: "
                + this.build_props.getProperty("git.commit.id") + " by: " + this.build_props.getProperty("build.user");

    }

    /**
     * Examine the classpath and retrieve the properties file
     *
     * @param String propFileName
     * @param boolean inMaindir
     * @return Properties properties
     * @throws IOException
     */
    private Properties getPropertiesFromClasspath(String propFileName, boolean inMaindir) throws IOException {
        // Loading properties file from the classpath
        Properties props = new Properties();
        InputStream inputStream = null;
        if (inMaindir) {
            inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
        } else {
            inputStream = this.getClass().getResourceAsStream(propFileName);
        }
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        props.load(inputStream);
        return props;
    }

}
