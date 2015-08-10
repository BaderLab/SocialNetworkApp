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

import java.awt.Cursor;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.ChartVisualStyle;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.IncitesVisualStyle;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.baderlab.csapps.socialnetwork.panels.InfoPanel;
import org.baderlab.csapps.socialnetwork.tasks.UpdateVisualStyleTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;

/**
 * Adds relevant network information to network table once Cytoscape generates a
 * network
 *
 * @author Victor Kofia
 *
 */
public class SocialNetworkAddedListener implements NetworkAddedListener {
    
    private static final Logger logger = Logger.getLogger(SocialNetworkAddedListener.class.getName());
    
    private SocialNetworkAppManager appManager = null;
    private CyNetworkManager cyNetworkManagerServiceRef = null;
    private VisualMappingManager vmmServiceRef = null;
    private VisualStyleFactory visualStyleFactoryServiceRef;
    private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
    private VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    private VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    private CyNetwork network = null;
    private SocialNetwork socialNetwork = null;
    private CytoPanel cytoPanelEast = null;
    private InfoPanel infoPanel = null;
    private CyServiceRegistrar cyServiceRegistrarRef = null;
    private CySwingApplication cySwingApplicationServiceRef = null;
    private TaskManager<?, ?> taskManager = null;
    private UpdateVisualStyleTaskFactory updateVisualStyleTaskFactory = null;
    private boolean initialized = false;
    private CyApplicationManager cyApplicationManagerServiceRef = null;

    /**
     * Create a new {@link SocialNetworkAddedListener} object
     *
     * @param {@link SocialNetworkAppManager} appManager
     */
    public SocialNetworkAddedListener(SocialNetworkAppManager appManager, CyNetworkManager cyNetworkManagerServiceRef,
            VisualMappingManager vmmServiceRef, VisualStyleFactory visualStyleFactoryServiceRef, VisualMappingFunctionFactory passthroughMappingFactoryServiceRef,
            VisualMappingFunctionFactory continuousMappingFactoryServiceRef, VisualMappingFunctionFactory discreteMappingFactoryServiceRef, 
            CyServiceRegistrar cyServiceRegistrarRef, CySwingApplication cySwingApplicationServiceRef, TaskManager<?, ?> taskManager,
            UpdateVisualStyleTaskFactory updateVisualStyleTaskFactory, CyApplicationManager cyApplicationManagerServiceRef) {
        super();
        this.appManager = appManager;
        this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
        this.vmmServiceRef = vmmServiceRef;
        this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
        this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
        this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
        this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
        this.cyServiceRegistrarRef = cyServiceRegistrarRef;
        this.cytoPanelEast = cySwingApplicationServiceRef.getCytoPanel(CytoPanelName.EAST);
        this.taskManager = taskManager;
        this.updateVisualStyleTaskFactory = updateVisualStyleTaskFactory;
        this.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
    }
    
    private void initializeInfoPanel(SocialNetwork network) {
        this.infoPanel = new InfoPanel(this.taskManager, this.updateVisualStyleTaskFactory, this.socialNetwork);        
    }
    
    private void handleNetwork(CyNetwork cyNetwork) {
        // Set mouse cursor to default (network's already been loaded)
        this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        String name = SocialNetworkAppManager.getNetworkName(cyNetwork);
        // If the network being added is a social network, then
        // add it to network table
        if (this.appManager.getSocialNetworkMap().containsKey(name)) {
            // Add to network table
            this.socialNetwork = this.appManager.getSocialNetworkMap().get(name);
            String startYearTxt = SocialNetworkAppManager.getStartDateTextFieldRef().getText().trim();
            String endYearTxt = SocialNetworkAppManager.getEndDateTextFieldRef().getText().trim();
            int startYear = -1, endYear = -1;
            if (Pattern.matches("[0-9]+", startYearTxt) && Pattern.matches("[0-9]+", endYearTxt)) {
                startYear = Integer.parseInt(startYearTxt); 
                endYear = Integer.parseInt(endYearTxt);            
            }
            this.socialNetwork.setStartYear(startYear);
            this.socialNetwork.setEndYear(endYear);
            this.socialNetwork.setCyNetwork(cyNetwork);
            this.appManager.getUserPanelRef().addNetworkToNetworkPanel(socialNetwork);
            // Show app information panel (docked to the east)
            // ------------------------------------------------------------------------------------------------------------
            if (cyNetwork.getDefaultNodeTable().getColumn(NodeAttribute.PUBS_PER_YEAR.toString()) != null) {
                if (!initialized) {
                    initializeInfoPanel(this.socialNetwork);                
                    this.cyServiceRegistrarRef.registerService(this.infoPanel, CytoPanelComponent.class, new Properties());
                    SocialNetworkAppManager.setInfoPanel(this.infoPanel);
                    initialized = true;
                } else {
                    this.infoPanel.update(this.socialNetwork);;
                }
                // If the state of the cytoPanelEast is HIDE, show it
                if (this.cytoPanelEast.getState() == CytoPanelState.HIDE) {
                    this.cytoPanelEast.setState(CytoPanelState.DOCK);
                }
                // Select my panel
                int index = this.cytoPanelEast.indexOfComponent(this.infoPanel);
                if (index == -1) {
                    return;
                }
                this.cytoPanelEast.setSelectedIndex(index);         
            } else {
                logger.log(Level.WARNING, String.format("Display Options panel disabled because %s does not contain the pubs per year attribute.",
                        this.socialNetwork.getNetworkName()));
            }
            // -----------------------------------------------------------------------------------------------------------          
            int networkID = socialNetwork.getNetworkType();
            // Specify the default visual styles
            switch (networkID) {
                case Category.INCITES:
                    // TODO:
                    if (CytoscapeUtilities.getVisualStyle("InCites", this.vmmServiceRef) == null) {
                        IncitesVisualStyle incitesVisualStyle = new IncitesVisualStyle(cyApplicationManagerServiceRef, cyNetwork, this.socialNetwork, 
                                this.visualStyleFactoryServiceRef, this.passthroughMappingFactoryServiceRef, this.continuousMappingFactoryServiceRef,
                                this.discreteMappingFactoryServiceRef, false);
                        this.vmmServiceRef.addVisualStyle(incitesVisualStyle.getVisualStyle());                        
                    }
                    break;
                case Category.SCOPUS:
                    // TODO:
                    if (CytoscapeUtilities.getVisualStyle("Scopus", this.vmmServiceRef) == null) {
                        BaseAcademiaVisualStyle basicVisualStyle = new BaseAcademiaVisualStyle(cyApplicationManagerServiceRef, cyNetwork, this.socialNetwork, 
                                this.visualStyleFactoryServiceRef, this.passthroughMappingFactoryServiceRef, this.continuousMappingFactoryServiceRef,
                                this.discreteMappingFactoryServiceRef, false);
                        this.vmmServiceRef.addVisualStyle(basicVisualStyle.getVisualStyle());                        
                    }
                    break;
                case Category.PUBMED:
                    // TODO:
                    if (CytoscapeUtilities.getVisualStyle("PubMed", this.vmmServiceRef) == null) {
                        BaseAcademiaVisualStyle basicVisualStyle = new BaseAcademiaVisualStyle(cyApplicationManagerServiceRef, cyNetwork, this.socialNetwork, 
                                this.visualStyleFactoryServiceRef, this.passthroughMappingFactoryServiceRef, this.continuousMappingFactoryServiceRef,
                                this.discreteMappingFactoryServiceRef, false);
                        this.vmmServiceRef.addVisualStyle(basicVisualStyle.getVisualStyle());                        
                    }
                    break;
            }
            if (CytoscapeUtilities.getVisualStyle("Social Network Chart", this.vmmServiceRef) ==  null) {
                ChartVisualStyle chartVisualStyle = new ChartVisualStyle(cyApplicationManagerServiceRef, cyNetwork, this.socialNetwork, 
                        this.visualStyleFactoryServiceRef, this.passthroughMappingFactoryServiceRef, this.continuousMappingFactoryServiceRef,
                        this.discreteMappingFactoryServiceRef, true);
                this.vmmServiceRef.addVisualStyle(chartVisualStyle.getVisualStyle());                                                        
            }
            this.appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
        }
    }

    /**
     * Adds network to network table and configures visual styles if necessary.
     *
     * @param {@link NetworkAddedEvent} event
     */
    public void handleEvent(NetworkAddedEvent event) {
        this.network = event.getNetwork();
        this.handleNetwork(this.network);
    }
}