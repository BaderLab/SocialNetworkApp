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

package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.academia.Incites_InstitutionLocationMap;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 * Enables users to add a new institution to a local location map
 *
 * @author Victor Kofia
 */
public class AddInstitutionAction extends AbstractCyAction {

    /**
     *
     */
    private static final long serialVersionUID = -4694044300149844000L;

    /**
     * Set of all accepted locations
     */
    private HashSet<String> locationSet = null;

    /**
     * Add an institution
     *
     * @param Map configProps
     * @param {@link CyApplicationManager} applicationManager
     * @param {@link CyNetworkViewManager} networkViewManager
     */
    public AddInstitutionAction(Map<String, String> configProps,
            CyApplicationManager applicationManager,
            CyNetworkViewManager networkViewManager) {
        super(configProps, applicationManager, networkViewManager);
        putValue(Action.NAME, "Add Institution");
        HashSet<String> set = new HashSet<String>();
        String[] locations = new String[] { "univ toronto", "ontario",
                "canada", "united states", "international", "other" };
        for (String location : locations) {
            set.add(location);
        }
        this.setLocationSet(set);
    }

    /**
     * Invoked when an action is performed
     *
     * @param {@link ActionEvent} arg0
     */
    public void actionPerformed(ActionEvent arg0) {
        JTextField institutionTextField = new JTextField(5);
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
            outcome = JOptionPane.showConfirmDialog(null, myPanel, "Login",
                    JOptionPane.OK_CANCEL_OPTION);
            if (outcome == JOptionPane.OK_OPTION) {
                institution = institutionTextField.getText().trim();
                location = locationTextField.getText().trim();
                if (institution.trim().isEmpty() && location.trim().isEmpty()) {
                    CytoscapeUtilities
                    .notifyUser("Please specify both an institution and a location");
                } else {
                    if (institution.trim().isEmpty()) {
                        CytoscapeUtilities
                        .notifyUser("Please specify an institution");
                    } else if (location.trim().isEmpty()) {
                        CytoscapeUtilities
                        .notifyUser("Please specify a location");
                    } else {
                        if (!this.getLocationSet().contains(
                                location.toLowerCase())) {
                            CytoscapeUtilities
                            .notifyUser("Location does not exist. Please enter a valid location.");
                        } else {
                            institution = institution.toUpperCase();
                            // Format location (in case casing was done
                            // improperly)
                            // i.e. united states becomes 'United States'
                            String[] words = location.split("\\s");
                            location = "";
                            for (String word : words) {
                                location += word.replaceAll("^\\w", word
                                        .substring(0, 1).toUpperCase())
                                        + " ";
                            }
                            location = location.trim();
                            outcome = JOptionPane.CANCEL_OPTION;
                        }
                    }

                }
            }
        }

        if (!institution.trim().isEmpty() && !location.trim().isEmpty()) {
            try {
                // Get map file in jar
                InputStream in = Incites_InstitutionLocationMap.class
                        .getClassLoader().getResourceAsStream("map.sn");
                ObjectInputStream ois = new ObjectInputStream(in);
                @SuppressWarnings("unchecked")
                Map<String, String> map = (HashMap<String, String>) ois
                .readObject();
                // Add institution / location info to map
                map.put(institution, location);

                // TODO: Update file dynamically - store a local version that we
                // check as well.
                // Update map being used by Incites
                // Incites_InstitutionLocationMap.setLocationMap(map);
            } catch (IOException e) {
                e.printStackTrace();
                CytoscapeUtilities
                .notifyUser("Location map could not be accessed.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                CytoscapeUtilities
                .notifyUser("Location map could not be accessed.");
            }

        }

    }

    /**
     * Get location set
     *
     * @return HashSet locationSet
     */
    private HashSet<String> getLocationSet() {
        return this.locationSet;
    }

    /**
     * Set location set
     *
     * @param HashSet locationSet
     */
    private void setLocationSet(HashSet<String> locationSet) {
        this.locationSet = locationSet;
    }

}
