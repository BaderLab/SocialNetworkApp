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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;

/**
 * Tools for manipulating Incites data
 *
 * @author Victor Kofia
 */
public class Incites_InstitutionLocationMap {

    /**
     * Author location map
     */
    private Map<String, String> locationMap = null;

    /**
     * Location ranking map. <br>
     * key: <i>location</i> <br>
     * value: <i>rank</i>
     */
    private Map<String, Integer> locationRankingMap = null;

    /**
     * Initialize the map with values from a file stored in the jar File is
     * currently stored as binary hashmap
     */
    public Incites_InstitutionLocationMap() {
        if (this.locationMap == null) {
            try {
                InputStream in = this.getClass().getResourceAsStream("locationsmap.txt");
                this.locationMap = new HashMap<String, String>();

                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String sCurrentLine = null;
                while ((sCurrentLine = br.readLine()) != null) {
                    // Tokenize the line
                    String[] tokens = sCurrentLine.split("\t");
                    // Properly formed line
                    if (tokens.length == 2) {
                        this.locationMap.put(tokens[0], tokens[1]);
                    } else {
                        System.out.println("misformed line in locationmap file\n \"" + sCurrentLine + "\n");
                    }
                }
                /*
                 * ObjectInputStream ois = new ObjectInputStream(in);
                 * this.setLocationMap((Map<String, String>) ois.readObject());
                 */
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                CytoscapeUtilities.notifyUser("Failed to load location map. FileNotFoundException.");
            } catch (IOException e) {
                e.printStackTrace();
                CytoscapeUtilities.notifyUser("Failed to load location map. IOException.");
            }
        }
    }

    /**
     * Get location map
     *
     * @return Map locationMap
     */
    public Map<String, String> getLocationMap() {
        return this.locationMap;
    }

    /**
     * Get location ranking map
     *
     * @return Map locationRankingMap
     */
    public Map<String, Integer> getLocationRankingMap() {
        if (this.locationRankingMap == null) {
            Map<String, Integer> map = new HashMap<String, Integer>();
            String[] locations = new String[] { "UNIV TORONTO", "Ontario", "Canada", "United States", "International", "Other" };
            for (int i = 0; i < 6; i++) {
                map.put(locations[i], 6 - i);
            }
            this.setLocationRankingMap(map);
        }
        return this.locationRankingMap;
    }

    /**
     * Set location map
     *
     * @param Map locationMap
     */
    public void setLocationMap(Map<String, String> locationMap) {
        this.locationMap = locationMap;
    }

    /**
     * Set location ranking map
     *
     * @param Map locationRankingMap
     */
    private void setLocationRankingMap(Map<String, Integer> map) {
        this.locationRankingMap = map;
    }

}