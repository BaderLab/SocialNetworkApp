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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Public utilities class with static methods used repeatedly by the code
 */
public class CytoscapeUtilities {

    /**
     * Notify user of an issue (indicated by message)
     *
     * @param String message
     */
    public static void notifyUser(String message) {
        NotificationThread notify = new NotificationThread(message);
        notify.start();
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
        InputStream inputStream;
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
