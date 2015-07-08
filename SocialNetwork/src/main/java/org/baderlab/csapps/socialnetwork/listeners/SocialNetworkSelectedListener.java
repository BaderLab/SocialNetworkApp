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

package org.baderlab.csapps.socialnetwork.listeners;

import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.cytoscape.application.events.SetSelectedNetworksEvent;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.model.CyNetwork;

/**
 * Listens for social networks that have been selected by the user and displays
 * any relevant information on the UI
 *
 * @author Victor Kofia
 */
public class SocialNetworkSelectedListener implements SetSelectedNetworksListener {

    private SocialNetworkAppManager appManager;
    private UserPanel userPanel;

    /**
     * Creates a new {@link SocialNetworkAppManager} object
     *
     * @param {@link SocialNetworkAppManager} appManager
     */
    public SocialNetworkSelectedListener(SocialNetworkAppManager appManager) {
        super();
        this.appManager = appManager;
        this.userPanel = this.appManager.getUserPanelRef();
    }

    /**
     * Updates the user interface
     *
     * @param {@link SetSelectedNetworksEvent} event
     */
    public void handleEvent(SetSelectedNetworksEvent event) {
        String name = null;
        for (CyNetwork network : event.getNetworks()) {
            name = SocialNetworkAppManager.getNetworkName(network);
            // Update UI iff a social network has been selected
            if (this.appManager.getSocialNetworkMap().containsKey(name)) {
                SocialNetwork socialNetwork = this.appManager.getSocialNetworkMap().get(name);
                this.userPanel.updateNetworkSummaryPanel(socialNetwork);
                this.userPanel.addNetworkVisualStyle(socialNetwork);
                this.appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
                return;
            }
        }
        this.appManager.setCurrentlySelectedSocialNetwork(null);
        this.userPanel.addNetworkVisualStyle(null);
        this.userPanel.updateNetworkSummaryPanel(null);
    }
}