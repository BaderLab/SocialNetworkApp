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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.Action;
import org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.InfoPanel;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.UpdateVisualStyleTaskFactory;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;

/**
 * Scans the properties file for any important information on social networks
 * about to be loaded in a session file.
 *
 * @author Victor Kofia
 */
public class RestoreSocialNetworksFromProp implements SessionLoadedListener {

    private SocialNetworkAppManager appManager = null;
    private CyServiceRegistrar cyServiceRegistrar = null;
    private CytoPanel cytoPanelWest = null, cytoPanelEast = null;
    private ShowUserPanelAction userPanelAction = null;
    private CyNetworkViewManager viewManager = null;
    private UserPanel userPanel = null;
    private InfoPanel infoPanel = null;
    private TaskManager<?, ?> taskManager = null;
    private UpdateVisualStyleTaskFactory updateVisualStyleTaskFactory = null;
    private boolean initialized = false;

    /**
     * Constructor for {@link RestoreSocialNetworksFromProp}
     *
     * @param {@link SocialNetworkAppManager} appManager
     * @param {@link CyNetworkViewManager} viewManager
     */
    public RestoreSocialNetworksFromProp(SocialNetworkAppManager appManager, CyNetworkViewManager viewManager, CyServiceRegistrar cyServiceRegistrar,
            CySwingApplication cySwingApplicationService, ShowUserPanelAction userPanelAction, UserPanel userPanel,
            TaskManager<?, ?> taskManager, UpdateVisualStyleTaskFactory updateVisualStyleTaskFactory) {
        super();
        this.appManager = appManager;
        this.viewManager = viewManager;
        this.cyServiceRegistrar = cyServiceRegistrar;
        this.cytoPanelWest = cySwingApplicationService.getCytoPanel(CytoPanelName.WEST);
        this.cytoPanelEast = cySwingApplicationService.getCytoPanel(CytoPanelName.EAST);
        this.userPanelAction = userPanelAction;
        this.userPanel = userPanel;
        this.taskManager = taskManager;
        this.updateVisualStyleTaskFactory = updateVisualStyleTaskFactory;
    }
    
    private void updateInfoPanel(SocialNetwork socialNetwork) {
        String startYearTxt = SocialNetworkAppManager.getStartDateTextFieldRef().getText().trim();
        String endYearTxt = SocialNetworkAppManager.getEndDateTextFieldRef().getText().trim();
        int startYear = -1, endYear = -1;
        if (Pattern.matches("[0-9]+", startYearTxt) && Pattern.matches("[0-9]+", endYearTxt)) {
            startYear = Integer.parseInt(startYearTxt); 
            endYear = Integer.parseInt(endYearTxt);            
        }
        
        this.infoPanel.setStartYear(startYear);
        this.infoPanel.setEndYear(endYear);
        
        /* Text field */
        this.infoPanel.getTextField().setText(String.valueOf(startYear));
        
        /* Slider button */
        this.infoPanel.getSliderButton().setMinimum(startYear);
        this.infoPanel.getSliderButton().setMaximum(endYear);
        this.infoPanel.getSliderButton().setValue(startYear);
        this.infoPanel.getSliderButton().repaint();
        
        this.infoPanel.setSocialNetwork(socialNetwork);
        
        this.infoPanel.updateUI();
    }
    
    private void initializeInfoPanel(SocialNetwork socialNetwork) {
        this.infoPanel = new InfoPanel(this.taskManager, this.updateVisualStyleTaskFactory, socialNetwork);        
    }


    /**
     * Invoked when a session is about to be loaded
     *
     * @param {@link SessionLoadedEvent} e
     */
    public void handleEvent(SessionLoadedEvent e) {
        if (e.getLoadedSession().getAppFileListMap() == null || e.getLoadedSession().getAppFileListMap().size() == 0) {
            return;
        }
        List<File> files = e.getLoadedSession().getAppFileListMap().get("socialnetwork");
        if (files == null || files.size() == 0) {
            return;
        }
        // Show Social Network App user panel

        String currentName = (String) this.userPanelAction.getValue(Action.NAME);
        if (currentName.trim().equalsIgnoreCase("Show Social Network")) {
            this.cyServiceRegistrar.registerService(this.userPanel, CytoPanelComponent.class, new Properties());
            // If the state of the cytoPanelWest is HIDE, show it
            if (this.cytoPanelWest.getState() == CytoPanelState.HIDE) {
                this.cytoPanelWest.setState(CytoPanelState.DOCK);
            }
            // Select my panel
            int index = this.cytoPanelWest.indexOfComponent(this.userPanel);
            if (index == -1) {
                return;
            }
            this.cytoPanelWest.setSelectedIndex(index);
            this.userPanelAction.putValue(Action.NAME, "Hide Social Network");            
        }

        try {
            File propFile = files.get(0);
            // Parse socialnetwork.prop file (CSV)
            BufferedReader in = new BufferedReader(new FileReader(propFile));
            in.readLine(); // Skip header

            String line = in.readLine();
            String[] networkData = null;
            ArrayList<String> listOfSocialNetworks = new ArrayList<String>();
            ArrayList<String> listOfTypes = new ArrayList<String>();
            ArrayList<String> listOfNumPubs = new ArrayList<String>();
            ArrayList<String> listOfNumFaculty = new ArrayList<String>();
            ArrayList<String> listOfNumUnidenFaculty = new ArrayList<String>();
            while (line != null) {
                networkData = line.split(",");
                listOfSocialNetworks.add(networkData[0]);
                listOfTypes.add(networkData[1]);
                listOfNumPubs.add(networkData[2]);
                listOfNumFaculty.add(networkData[3]);
                listOfNumUnidenFaculty.add(networkData[4]);
                line = in.readLine();
            }

            SocialNetwork socialNetwork = null;
            CyNetworkView networkView = null;
            Collection<CyNetworkView> views = null;
            int index = -1;
            // Identify the social networks in the loaded session
            SocialNetwork network = null;
            for (CyNetwork n : e.getLoadedSession().getNetworks()) {
                index = listOfSocialNetworks.indexOf(n.toString());
                if (index > -1) {
                    socialNetwork = new SocialNetwork(n.toString(), Category.toCategory(listOfTypes.get(index)));
                    socialNetwork.setCyNetwork(n);
                    socialNetwork.setNum_publications(Integer.valueOf(listOfNumPubs.get(index)));
                    socialNetwork.setNum_faculty(Integer.valueOf(listOfNumFaculty.get(index)));
                    socialNetwork.setNum_uniden_faculty(Integer.valueOf(listOfNumUnidenFaculty.get(index)));
                    views = this.viewManager.getNetworkViews(n);
                    if (views.size() != 0) {
                        networkView = views.iterator().next();
                    }
                    socialNetwork.setNetworkView(networkView);
                    this.appManager.getSocialNetworkMap().put(n.toString(), socialNetwork);
                    this.appManager.getUserPanelRef().addNetworkToNetworkPanel(socialNetwork);
                    network = socialNetwork;
                }
            }
            in.close();
            if (network != null) {
                if (!initialized) {
                    initializeInfoPanel(network);                
                    this.cyServiceRegistrar.registerService(this.infoPanel, CytoPanelComponent.class, new Properties());
                    initialized = true;
                } else {
                    updateInfoPanel(network);
                }
            }
            // If the state of the cytoPanelEast is HIDE, show it
            if (this.cytoPanelEast.getState() == CytoPanelState.HIDE) {
                this.cytoPanelEast.setState(CytoPanelState.DOCK);
            }
            // Select my panel
            index = this.cytoPanelEast.indexOfComponent(this.infoPanel);
            if (index == -1) {
                return;
            }
            this.cytoPanelEast.setSelectedIndex(index);
            // ---------------------------------------------------------------------------------------------------------             
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }

}
