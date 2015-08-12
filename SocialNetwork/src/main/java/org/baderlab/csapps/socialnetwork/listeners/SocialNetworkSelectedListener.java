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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.baderlab.csapps.socialnetwork.panels.InfoPanel;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.HideAuthorsTaskFactory;
import org.cytoscape.application.events.SetSelectedNetworksEvent;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskManager;

/**
 * Listens for social networks that have been selected by the user and displays
 * any relevant information on the UI
 *
 * @author Victor Kofia
 */
public class SocialNetworkSelectedListener implements SetSelectedNetworksListener {
    
    private static final Logger logger = Logger.getLogger(SocialNetworkSelectedListener.class.getName());

    private SocialNetworkAppManager appManager = null;
    private UserPanel userPanel = null;
    private SocialNetwork socialNetwork = null;
    private CytoPanel cytoPanelEast = null;
    private TaskManager<?, ?> taskManager = null;
    private HideAuthorsTaskFactory updateVisualStyleTaskFactory = null;
    private CyServiceRegistrar cyServiceRegistrarRef = null;

    /**
     * Creates a new {@link SocialNetworkAppManager} object
     *
     * @param {@link SocialNetworkAppManager} appManager
     */
    public SocialNetworkSelectedListener(SocialNetworkAppManager appManager, CyServiceRegistrar cyServiceRegistrarRef, 
            TaskManager<?, ?> taskManager, HideAuthorsTaskFactory updateVisualStyleTaskFactory) {
        super();
        this.appManager = appManager;
        this.cyServiceRegistrarRef = cyServiceRegistrarRef;
        this.taskManager = taskManager;
        this.userPanel = this.appManager.getUserPanelRef();
        this.updateVisualStyleTaskFactory = updateVisualStyleTaskFactory;
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
                this.socialNetwork = this.appManager.getSocialNetworkMap().get(name);
                this.userPanel.updateNetworkSummaryPanel(socialNetwork);
                this.userPanel.addNetworkVisualStyle(socialNetwork);
                this.appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
                if (network.getDefaultNodeTable().getColumn(NodeAttribute.PUBS_PER_YEAR.toString()) != null) {
                    InfoPanel infoPanel = SocialNetworkAppManager.getInfoPanel();
                    infoPanel.update(this.socialNetwork);;
                    // If the state of the cytoPanelEast is HIDE, show it
                    if (this.cytoPanelEast.getState() == CytoPanelState.HIDE) {
                        this.cytoPanelEast.setState(CytoPanelState.DOCK);
                    }
                    // Select my panel
                    int index = this.cytoPanelEast.indexOfComponent(infoPanel);
                    if (index == -1) {
                        return;
                    }
                    this.cytoPanelEast.setSelectedIndex(index);         
                }
                return;
            } else {
                logger.log(Level.WARNING, String.format("Display Options panel disabled because %s does not contain the pubs per year attribute.",
                        this.socialNetwork.getNetworkName()));
            }
        }
        this.appManager.setCurrentlySelectedSocialNetwork(null);
        this.userPanel.addNetworkVisualStyle(null);
        this.userPanel.updateNetworkSummaryPanel(null);
    }
}