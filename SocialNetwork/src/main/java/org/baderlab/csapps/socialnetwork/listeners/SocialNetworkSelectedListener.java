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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.baderlab.csapps.socialnetwork.panels.InfoPanel;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.CreateChartTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.HideAuthorsTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ShowAllNodesTaskFactory;
import org.cytoscape.application.events.SetSelectedNetworksEvent;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;

/**
 * Listens for social networks that have been selected by the user and displays
 * any relevant information on the UI
 *
 * @author Victor Kofia
 */
public class SocialNetworkSelectedListener implements SetSelectedNetworksListener {
    
    private static final Logger logger = Logger.getLogger(SocialNetworkSelectedListener.class.getName());

    private SocialNetwork socialNetwork;
    
    private final UserPanel userPanel;
    private final SocialNetworkAppManager appManager;
    private final TaskManager<?, ?> taskManager;
    private final HideAuthorsTaskFactory updateVisualStyleTaskFactory;
    private final CyServiceRegistrar serviceRegistrar;
    private final CyNetworkViewManager networkViewManager;
    private final ShowAllNodesTaskFactory showAllNodesTaskFactory;
    private final CreateChartTaskFactory createChartTaskFactory;

    public SocialNetworkSelectedListener(
    		SocialNetworkAppManager appManager,
    		CyServiceRegistrar serviceRegistrar, 
            TaskManager<?, ?> taskManager,
            HideAuthorsTaskFactory updateVisualStyleTaskFactory, 
            CyNetworkViewManager networkViewManager,
            ShowAllNodesTaskFactory showAllNodesTaskFactory,
            CreateChartTaskFactory createChartTaskFactory
    ) {
        this.networkViewManager = networkViewManager;
        this.appManager = appManager;
        this.serviceRegistrar = serviceRegistrar;
        this.taskManager = taskManager;
        this.userPanel = appManager.getUserPanelRef();
        this.updateVisualStyleTaskFactory = updateVisualStyleTaskFactory;
        this.showAllNodesTaskFactory = showAllNodesTaskFactory;
        this.createChartTaskFactory = createChartTaskFactory;
    }
    
    private InfoPanel initializeInfoPanel() {
        return new InfoPanel(taskManager, updateVisualStyleTaskFactory, socialNetwork, serviceRegistrar,
                showAllNodesTaskFactory, createChartTaskFactory);        
    }

    /**
     * Updates the user interface
     */
    @Override
	public void handleEvent(SetSelectedNetworksEvent event) {
        String name = null;
        
        for (var network : event.getNetworks()) {
            name = SocialNetworkAppManager.getNetworkName(network);
            
            // Update UI if a social network has been selected
            if (appManager.getSocialNetworkMap().containsKey(name)) {
                socialNetwork = appManager.getSocialNetworkMap().get(name);
                userPanel.updateNetworkSummaryPanel(socialNetwork);
                userPanel.addNetworkVisualStyle(socialNetwork);
                appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
                
                if (network.getDefaultNodeTable().getColumn(NodeAttribute.PUBS_PER_YEAR.toString()) != null) {
                    var networkViews = networkViewManager.getNetworkViews(network);
					
                    if (!networkViews.isEmpty()) {
	                    var view = networkViews.iterator().next();
	                    socialNetwork.setNetworkView(view);
                    }
	                                        
                    var infoPanel = SocialNetworkAppManager.getInfoPanel();
                    
                    if (infoPanel == null) {
                        infoPanel = initializeInfoPanel();                
                        serviceRegistrar.registerService(infoPanel, CytoPanelComponent.class, new Properties());
                        SocialNetworkAppManager.setInfoPanel(infoPanel);
                    } else {
                        infoPanel.update(socialNetwork);;
                    }
                    
                    var cytoPanelEast = serviceRegistrar.getService(CySwingApplication.class).getCytoPanel(CytoPanelName.EAST);
	                    
                    // If the state of the cytoPanelEast is HIDE, show it
                    if (cytoPanelEast.getState() == CytoPanelState.HIDE)
                        cytoPanelEast.setState(CytoPanelState.DOCK);
                    
                    // Select my panel
                    int index = cytoPanelEast.indexOfComponent(infoPanel);
                    
                    if (index == -1)
                        return;
                    
                    cytoPanelEast.setSelectedIndex(index);
                }
                
                return;
            } else {
            	if (socialNetwork != null)
					logger.log(Level.WARNING, String.format(
							"Display Options panel disabled because %s does not contain the pubs per year attribute.",
							socialNetwork.getNetworkName()));
            }
        }
            
        appManager.setCurrentlySelectedSocialNetwork(null);
        userPanel.addNetworkVisualStyle(null);
        userPanel.updateNetworkSummaryPanel(null);
    }
}
