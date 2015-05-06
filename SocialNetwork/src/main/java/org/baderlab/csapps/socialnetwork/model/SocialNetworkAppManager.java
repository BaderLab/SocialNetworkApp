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

package org.baderlab.csapps.socialnetwork.model;

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import org.apache.commons.io.FilenameUtils;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskManager;

/**
 * The app manager. Contains useful static methods and variables.
 *
 * @author Victor Kofia
 */
public class SocialNetworkAppManager {

    /**
     * Get apply view task factory
     *
     * @return ApplyViewTaskFactory applyVisualStyleTaskFactoryRef
     */
    public static ApplyVisualStyleTaskFactory getApplyVisualStyleTaskFactoryRef() {
        return SocialNetworkAppManager.applyVisualStyleTaskFactoryRef;
    }

    /**
     * A reference to the 'apply visual style' task factory
     */
    private static ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = null;
    /**
     * Currently selected social network
     */
    private SocialNetwork currentlySelectedSocialNetwork = null;
    /**
     * A reference to the cytoscape application manager. Necessary for changing
     * network views.
     */
    private CyApplicationManager cyAppManagerServiceRef = null;
    /**
     * A reference to the cytoscape service registrar. Necessary for
     * unregistering services at user's convenience.
     */
    private CyServiceRegistrar cyServiceRegistrarRef = null;
    /**
     * A reference to the 'destroy network' task factory
     */
    private DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = null;
    /**
     * A map containing the nodes and edges that will eventually be used to form
     * a network
     */
    private Map<Collaboration, ArrayList<AbstractEdge>> map = null;
    /**
     * Name of network
     */
    private String networkName = null;
    /**
     * A reference to the 'create network' task factory
     */
    private CreateNetworkTaskFactory networkTaskFactoryRef = null;
    /**
     * A network that's just about to be destroyed
     */
    private CyNetwork networkToBeDestroyed = null;
    /**
     * Social network map. <br>
     * Key: network name <br>
     * Value: social network
     */
    private Map<String, SocialNetwork> socialNetworkMap = null;
    /**
     * A reference to the cytoscape task manager. As the name suggests the task
     * manager is used for executing tasks.
     */
    private TaskManager<?, ?> taskManagerServiceRef = null;
    /**
     * A reference to user panel action. Controls panel actions (viewing,
     * closing ... etc)
     */
    private ShowUserPanelAction userPanelAction = null;
    /**
     * A reference to the app's user panel. User will interact with app
     * primarily through this panel.
     */
    private UserPanel userPanelRef = null;
    /**
     * Currently selected visual style ID
     */
    private int visualStyleID = VisualStyles.DEFAULT_VISUAL_STYLE;

    /**
     * Set of all visual styles currently supported by app
     */
    private HashSet<String> visualStyleSet = null;
    public static final int ANALYSISTYPE_INCITES = 0;
    public static final int ANALYSISTYPE_SCOPUS = 1;

    private int analysis_type = SocialNetworkAppManager.ANALYSISTYPE_INCITES;

    /**
     * Apply visual style to network
     *
     * @param String visualStyle
     */
    public void applyVisualStyle(String visualStyle) {
        this.setVisualStyleID(new VisualStyles().getVisualStyleID(visualStyle));
        this.getTaskManager().execute(this.getApplyVisualStyleTaskFactoryRef().createTaskIterator());
    }

    /**
     * Close user panel. Method will do nothing if user panel has not been
     * registered prior to it's execution.
     */
    public void closeUserPanel() {
        this.getServiceRegistrar().unregisterService(this.getUserPanelRef(), CytoPanelComponent.class);
        this.getUserPanelAction().putValue(Action.NAME, "Show Social Network");
    }

    /**
     * Create a network. Method marked private in order to prevent users from
     * inadvertently creating a network before all pertinent edge and node info
     * is set.
     */
    private void createNetwork() {

        // Execute network task.
        // NOTE: Relevant node & edge info is not directly coupled with task
        // execution. It is
        // acquired later on through Cytoscape.getMap()
        // This method is a blackbox and should NOT be directly executed under
        // ANY circumstances
        this.getTaskManager().execute(this.getNetworkTaskFactoryRef().createTaskIterator());

    }

    /**
     * Create a network from file
     *
     * @param File networkFile
     * @param int threshold
     */
    public void createNetwork(File networkFile, int threshold) throws FileNotFoundException {
        // Verify that network name is valid
        String networkName = this.userPanelRef.getAcademiaPanel().getFacultyTextFieldRef().getText().trim();
        if (!this.isNameValid(networkName)) {
            CytoscapeUtilities.notifyUser("Network " + networkName + " already exists in Cytoscape." + " Please enter a new name.");
            return;
        }
        // Change mouse cursor
        this.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // Initialize variables
        List<? extends Publication> pubList = null;
        String extension = null;
        SocialNetwork socialNetwork = null;
        String departmentName = null;
        // Create network out of Incites data
        if (this.analysis_type == this.ANALYSISTYPE_INCITES) {
            extension = FilenameUtils.getExtension(networkFile.getPath());
            IncitesParser incitesParser = null;
            // Load data from text file
            if (extension.trim().equalsIgnoreCase("xlsx")) {
                socialNetwork = new SocialNetwork(networkName, Category.INCITES);
                incitesParser = new IncitesParser(networkFile);
                if (incitesParser.getIgnoredRows() >= 1) {
                    CytoscapeUtilities.notifyUser("Some rows could not be parsed.");
                }
                if (incitesParser.getIdentifiedFacultyList().size() == 0) {
                    CytoscapeUtilities.notifyUser("Unable to identify faculty." + "Please verify that Incites data file is valid");
                }
                pubList = incitesParser.getPubList();
                departmentName = incitesParser.getDepartmentName();
                if (pubList == null) {
                    this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    CytoscapeUtilities.notifyUser("Invalid file. This Incites file is corrupt.");
                    return;
                }
                // Notify user of inappropriate file type
            } else {
                this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                CytoscapeUtilities.notifyUser("Invalid file. Incite data files either have to be excel " + "spreadsheets or text files.");
                return;
            }
            // Add summary attributes
            socialNetwork.setPublications(incitesParser.getPubList());
            socialNetwork.setFaculty(incitesParser.getFacultySet());
            socialNetwork.setUnidentifiedFaculty(incitesParser.getUnidentifiedFacultyList());
            socialNetwork.setUnidentified_faculty(incitesParser.getUnidentifiedFacultyString());

            // Add info to social network map(s)
            socialNetwork.getAttrMap().put(IncitesVisualStyle.nodeattr_dept, departmentName);
            // Create network out of Scopus data
        } else if (this.analysis_type == this.ANALYSISTYPE_SCOPUS) {
            extension = FilenameUtils.getExtension(networkFile.getPath());
            Scopus scopus = null;
            if (extension.trim().equalsIgnoreCase("csv")) {
                socialNetwork = new SocialNetwork(networkName, Category.SCOPUS);
                scopus = new Scopus(networkFile);
                pubList = scopus.getPubList();
                socialNetwork.setPublications(scopus.getPubList());
                if (pubList == null) {
                    this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
            } else {
                this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                CytoscapeUtilities.notifyUser("Invalid file. Scopus data files have to be csv " + "spreadsheets");
                return;
            }
        }
        // Create interaction
        Interaction interaction = new Interaction(pubList, Category.ACADEMIA, threshold);
        // Create map
        Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
        if (map.size() == 0) {
            this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network couldn't be loaded. File is corrupt.");
            return;
        }
        this.setMap(map);
        this.setNetworkName(networkName);
        this.getSocialNetworkMap().put(networkName, socialNetwork);
        // Create network using map
        this.createNetwork();
    }

    /**
     * Create a network from search term
     *
     * @param String searchTerm
     * @param int category
     * @param int threshold
     */
    public void createNetwork(String searchTerm, int category, int threshold) {
        // Verify that network name is valid
        if (!this.isNameValid(searchTerm)) {
            CytoscapeUtilities.notifyUser("Network " + this.networkName + " already exists in Cytoscape." + " Please enter a new name.");
            return;
        }
        // Create new search session
        Search search = new Search(searchTerm, category, this);
        // Get a list of the results that are going to serve as edges. Exact
        // result type
        // may vary with website
        List<? extends AbstractEdge> results = search.getResults();
        if (results == null) {
            this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network could not be loaded");
            return;
        }
        if (results.size() == 0) {
            this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Search did not yield any results");
            return;
        }
        Map<Collaboration, ArrayList<AbstractEdge>> map = null;
        Interaction interaction = null;
        SocialNetwork socialNetwork = null;
        switch (category) {
            case Category.ACADEMIA:
                // Change category (to Pubmed)
                // This is only temporary ~
                category = Category.PUBMED;
                interaction = new Interaction(results, category, threshold);
                socialNetwork = new SocialNetwork(searchTerm, category);
                // TODO:figure out how to add publications from pubmed search
                // socialNetwork.setPublications(search.getTotalHits());

                // Create new map using results
                map = interaction.getAbstractMap();
                // Set social network attributes
                // ??
                break;
        }
        this.setNetworkName(searchTerm);
        this.getSocialNetworkMap().put(searchTerm, socialNetwork);
        // Transfer map to Cytoscape's map variable
        this.setMap(map);
        // Create network using map
        this.createNetwork();
    }

    /**
     * Destroy a network
     *
     * @param CyNetwork network
     */
    public void destroyNetwork(CyNetwork network) {
        this.setNetworkToBeDestroyed(network);
        this.getTaskManager().execute(this.getDestroyNetworkTaskFactoryRef().createTaskIterator());
    }

    public int getAnalysis_type() {
        return this.analysis_type;
    }

    /**
     * Get currently selected social network
     *
     * @return SocialNetwork currentlySelectedSocialNetwork
     */
    public SocialNetwork getCurrentlySelectedSocialNetwork() {
        return this.currentlySelectedSocialNetwork;
    }

    /**
     * Get Cytoscape application manager
     *
     * @return CyApplicationManager cyAppManagerServiceRef
     */
    public CyApplicationManager getCyAppManagerServiceRef() {
        return this.cyAppManagerServiceRef;
    }

    /**
     * Get destroy network task factory
     *
     * @return DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef
     */
    public DestroyNetworkTaskFactory getDestroyNetworkTaskFactoryRef() {
        return this.destroyNetworkTaskFactoryRef;
    }

    /**
     * Get network map. Contains edge and node information.
     *
     * @return Map map
     */
    public Map<Collaboration, ArrayList<AbstractEdge>> getMap() {
        return this.map;
    }

    /**
     * Get network name.
     *
     * @return String networkName
     */
    public String getNetworkName() {
        return this.networkName;
    }

    /**
     * Get the name associated with a particular CyNetwork object
     *
     * @param CyNetwork network
     * @return String networkName
     */
    public String getNetworkName(CyNetwork network) {
        ArrayList<CyRow> rowList = (ArrayList<CyRow>) network.getDefaultNetworkTable().getAllRows();
        CyRow row = rowList.get(0);
        String networkName = (String) row.getAllValues().get("name");
        return networkName;
    }

    /**
     * Get network table column names
     *
     * @return String[] networkTableColumnNames
     */
    public String[] getNetworkTableColumnNames() {
        return new String[] { "Name", "Node Count", "Edge Count", "Category" };
    }

    /**
     * Get network task factory
     *
     * @return NetworkTaskFactory networkTaskFactory
     */
    public CreateNetworkTaskFactory getNetworkTaskFactoryRef() {
        return this.networkTaskFactoryRef;
    }

    /**
     * The network that is about to be destroyed
     *
     * @return CyNetwork networkToBeDestroyed
     */
    public CyNetwork getNetworkToBeDestroyed() {
        return this.networkToBeDestroyed;
    }

    /**
     * Get Cytoscape service registrar
     *
     * @return CyServiceRegistrar cyServiceRegistrarRef
     */
    public CyServiceRegistrar getServiceRegistrar() {
        return this.cyServiceRegistrarRef;
    }

    public SocialNetwork getSocialNetwork(String name) {
        if (this.socialNetworkMap != null && this.socialNetworkMap.containsKey(name)) {
            return this.socialNetworkMap.get(name);
        } else {
            return null;
        }
    }

    /*
     * Given the name of a network returns the SocialNetwork object associated
     * with that network name returns null otherwise.
     */

    /**
     * Get social network map
     *
     * @return Map social networks <br>
     * <i>key: network name</i> <br>
     * <i>value: {CyNetwork, Category, CyNetworkView}</i>
     */
    public Map<String, SocialNetwork> getSocialNetworkMap() {
        if (this.socialNetworkMap == null) {
            setSocialNetworkMap(new HashMap<String, SocialNetwork>());
            this.socialNetworkMap.put("DEFAULT", new SocialNetwork("DEFAULT", Category.DEFAULT));
        }
        return this.socialNetworkMap;
    }

    /**
     * Get Cytoscape task manager
     *
     * @return TaskManager taskManager
     */
    public TaskManager<?, ?> getTaskManager() {
        return this.taskManagerServiceRef;
    }

    /**
     * Get user panel action
     *
     * @return UserPanelAction userPanelAction
     */
    public ShowUserPanelAction getUserPanelAction() {
        return this.userPanelAction;
    }

    /**
     * Get user panel reference
     *
     * @return UserPanel userPanelRef
     */
    public UserPanel getUserPanelRef() {
        return this.userPanelRef;
    }

    /**
     * Get visual style ID
     *
     * @return int visualStyleID
     */
    public int getVisualStyleID() {
        return this.visualStyleID;
    }

    /**
     * Return true iff a network with a similar name is <i>not</i> already
     * present in Cytoscape
     *
     * @param String networkName
     * @return boolean
     */
    public Boolean isNameValid(String name) {
        return !getSocialNetworkMap().containsKey(name);
    }

    /**
     * Return true iff visual style is currently supported by app
     *
     * @param String visualStyleName
     * @return boolean bool
     */
    public boolean isValidVisualStyle(String visualStyleName) {
        if (this.visualStyleSet == null) {
            this.visualStyleSet = new HashSet<String>();
            this.visualStyleSet.add("Chipped");
            this.visualStyleSet.add("Vanue");
        }
        return this.visualStyleSet.contains(visualStyleName);
    }

    public void setAnalysis_type(int analysis_type) {
        this.analysis_type = analysis_type;
    }

    /**
     * Set 'apply view' task factory
     *
     * @param ApplyViewTaskFactory applyViewTaskFactoryRef
     */
    public void setApplyVisualStyleTaskFactoryRef(ApplyVisualStyleTaskFactory applyViewTaskFactoryRef) {
        applyVisualStyleTaskFactoryRef = applyViewTaskFactoryRef;
    }

    /**
     * Set currently selected social network
     *
     * @param SocialNetwork currentlySelectedSocialNetwork
     */
    public void setCurrentlySelectedSocialNetwork(SocialNetwork currentlySelectedSocialNetwork) {
        this.currentlySelectedSocialNetwork = currentlySelectedSocialNetwork;
    }

    /**
     * Set network's view as the current view
     *
     * @param String networkName
     */
    public void setCurrentNetworkView(String networkName) {
        CyNetworkView networkView = getSocialNetworkMap().get(networkName).getNetworkView();
        getCyAppManagerServiceRef().setCurrentNetworkView(networkView);
    }

    /**
     * Set Cytoscape application manager
     *
     * @param CyApplicationManager cyAppManagerServiceRef
     */
    public void setCyAppManagerServiceRef(CyApplicationManager cyAppManagerServiceRef) {
        this.cyAppManagerServiceRef = cyAppManagerServiceRef;
    }

    /**
     * Set 'destroy network' task factory
     *
     * @param DestroyNetworkTaskFActory destroyNetworkTaskFactory
     */
    public void setDestroyNetworkTaskFactoryRef(DestroyNetworkTaskFactory destroyNetworkTaskFactory) {
        this.destroyNetworkTaskFactoryRef = destroyNetworkTaskFactory;
    }

    /**
     * Set map
     *
     * @param Map map
     */
    public void setMap(Map<Collaboration, ArrayList<AbstractEdge>> map) {
        this.map = map;
    }

    /**
     * Set network name
     *
     * @param String networkName
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * Set network task factory
     *
     * @param CreateNetworkTaskFactory networkTaskFactory
     */
    public void setNetworkTaskFactoryRef(CreateNetworkTaskFactory networkTaskFactoryRef) {
        this.networkTaskFactoryRef = networkTaskFactoryRef;
    }

    /**
     * Set the network that is about to be destroyed
     *
     * @param CyNetwork networkToBeDestroyed
     */
    public void setNetworkToBeDestroyed(CyNetwork networkToBeDestroyed) {
        this.networkToBeDestroyed = networkToBeDestroyed;
    }

    /**
     * Set Cytoscape service registrar
     *
     * @param CyServiceRegistrar cyServiceRegistrarRef
     */
    public void setServiceRegistrar(CyServiceRegistrar cyServiceRegistrarRef) {
        this.cyServiceRegistrarRef = cyServiceRegistrarRef;
    }

    /**
     * Set social network map
     *
     * @param Map socialNetworkMap
     */
    public void setSocialNetworkMap(Map<String, SocialNetwork> socialNetwork) {
        this.socialNetworkMap = socialNetwork;
    }

    /**
     * Set task manager
     *
     * @param TaskManager taskManager
     */
    public void setTaskManager(TaskManager<?, ?> taskManager) {
        this.taskManagerServiceRef = taskManager;
    }

    /**
     * Set user panel action
     *
     * @param ShowUserPanelAction userPanelAction
     */
    public void setUserPanelAction(ShowUserPanelAction userPanelAction) {
        this.userPanelAction = userPanelAction;
    }

    /**
     * Set user panel reference
     *
     * @param UserPanel userPanelRef
     */
    public void setUserPanelRef(UserPanel userPanelRef) {
        this.userPanelRef = userPanelRef;
    }

    /**
     * Set visual style ID
     *
     * @param int visualStyleID
     */
    public void setVisualStyleID(int visualStyleID) {
        this.visualStyleID = visualStyleID;
    }

}