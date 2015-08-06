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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.JTextField;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.PropsReader;
import org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ExportNthDegreeNeighborsTask;
import org.baderlab.csapps.socialnetwork.tasks.ParseSocialNetworkFileTask;
import org.baderlab.csapps.socialnetwork.tasks.ParseSocialNetworkFileTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.SearchPubMedTask;
import org.baderlab.csapps.socialnetwork.tasks.SearchPubMedTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskManager;

/**
 * Manager for the Social Network app. Contains useful static methods and
 * variables for developers.
 *
 * @author Victor Kofia
 */
public class SocialNetworkAppManager {
    
    private static JTextField startDateTextFieldRef = null;
    private static JTextField endDateTextFieldRef = null;
    private static int selectedYear = -1;
    
    public static int getSelectedYear() {
        return selectedYear;
    }
    
    public static void setSelectedYear(int year) {
        selectedYear = year;
    }
    
    public static JTextField getStartDateTextFieldRef() {
        if (SocialNetworkAppManager.startDateTextFieldRef == null) {
            int year = Calendar.getInstance().get(Calendar.YEAR) - 5;
            startDateTextFieldRef = new JTextField(String.valueOf(year));
        }
        return SocialNetworkAppManager.startDateTextFieldRef;
    }
    
    public static JTextField getEndDateTextFieldRef() {
        if (SocialNetworkAppManager.endDateTextFieldRef == null) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            endDateTextFieldRef = new JTextField(String.valueOf(year));
        }
        return SocialNetworkAppManager.endDateTextFieldRef;
    }


    /**
     * Get <i>ApplyVisualStyle</i> task factory
     *
     * @return ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef
     */
    public static ApplyVisualStyleTaskFactory getApplyVisualStyleTaskFactoryRef() {
        return SocialNetworkAppManager.applyVisualStyleTaskFactoryRef;
    }

    /**
     * Get the name associated with a particular CyNetwork object
     *
     * @param CyNetwork network
     * @return String networkName
     */
    public static String getNetworkName(CyNetwork network) {
        ArrayList<CyRow> rowList = (ArrayList<CyRow>) network.getDefaultNetworkTable().getAllRows();
        CyRow row = rowList.get(0);
        String networkName = (String) row.getAllValues().get("name");
        return networkName;
    }

    /**
     * A reference to the <i>ApplyVisualStyle</i> task factory
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
     * unregistering services.
     */
    private CyServiceRegistrar cyServiceRegistrarRef = null;
    /**
     * A reference to the task factory for <i>DestroyNetworkTask</i>
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
     * A reference to the task factory for <i>CreateNetworkTask</i>
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
     * A reference to the app's main user panel. User will interact with app
     * primarily through this panel.
     */
    private UserPanel userPanelRef = null;
    /**
     * Currently selected visual style ID
     */
    private int visualStyleID = VisualStyles.DEFAULT_VISUAL_STYLE;
    /**
     * A data file that's been exported by the user manually.
     */
    private File networkFile = null; // TODO:
    private int maxAuthorThreshold = -1; // TODO:

    private int networkFileType = -1;
    /**
     * Set of all visual styles currently supported by app
     */
    private HashSet<String> visualStyleSet = null;
    private PropsReader propsReader = null;
    public static final int ANALYSISTYPE_INCITES = (84 << 24) + (18 << 16) + (180 << 8) + 87;
    public static final int ANALYSISTYPE_SCOPUS = (198 << 24) + (185 << 16) + (19 << 8) + 57;

    public static final int ANALYSISTYPE_PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;

    private int analysis_type = SocialNetworkAppManager.ANALYSISTYPE_INCITES;
    
    private static final Logger logger = Logger.getLogger(SocialNetworkAppManager.class.getName());
    
    public void setPropsReader(PropsReader propsReader) {
        this.propsReader = propsReader;
        if (this.propsReader.getProperties().isEmpty()) {
            try {
                InputStream in = this.getClass().getResourceAsStream("locationsmap.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String sCurrentLine = null;
                while ((sCurrentLine = br.readLine()) != null) {
                    // Tokenize the line
                    String[] tokens = sCurrentLine.split("\t");
                    // Properly formed line
                    if (tokens.length == 2) {
                       this.propsReader.getProperties().put(tokens[0], tokens[1]);
                    } else {
                        logger.log(Level.WARNING, "Misformed line in locationmap file\n \"" + sCurrentLine + "\n");
                    }
                }
            } catch (FileNotFoundException e) {
                logger.log(Level.SEVERE, "Exception occurred", e);
                CytoscapeUtilities.notifyUser("Failed to load location map. FileNotFoundException.");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Exception occurred", e);
                CytoscapeUtilities.notifyUser("Failed to load location map. IOException.");
            }
        }
    }
    
    private PropsReader getPropsReader() {
        return this.propsReader;
    }
    
    /**
     * Create a new Social Network App manager
     */
    public SocialNetworkAppManager() {

    }

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
     * Create a network from file
     *
     * @param File networkFile
     * @param int maxAuthorThreshold
     */
    public void createNetwork(File networkFile, int maxAuthorThreshold) throws FileNotFoundException {
        this.setNetworkFile(networkFile);
        String networkName = this.getUserPanelRef().getAcademiaPanel().getFacultyTextFieldRef().getText().trim();
        this.setNetworkName(networkName);
        this.setMaxAuthorThreshold(maxAuthorThreshold);
        visualizeNetwork();
    }
    

    /**
     * Get the start year in the interval specified by the user
     * 
     * @return {@code int} startYear
     */
    public static int getStartYear() {
        int startYear = -1;
        String startYearTxt = SocialNetworkAppManager.getStartDateTextFieldRef().getText().trim();
        if (Pattern.matches("[0-9]+", startYearTxt)) {
            startYear = Integer.parseInt(startYearTxt);
        }
        return startYear;
    }
    
    
    /**
     * Get the end year in the interval specified by the user
     * 
     * @return {@code int} endYear
     */
    public static int getEndYear() {
        int endYear = -1;
        String endYearTxt = SocialNetworkAppManager.getEndDateTextFieldRef().getText().trim();
        if (Pattern.matches("[0-9]+", endYearTxt)) {
            endYear = Integer.parseInt(endYearTxt);
        }
        return endYear;
    }
    
    /**
     * Visualize a social network. Method marked private in order to prevent users from
     * inadvertently creating a network before all pertinent edge and node info
     * is set.
     */
    private void visualizeNetwork() {
        // Execute network task.
        // NOTE: Relevant node & edge info is not directly coupled with task
        // execution. It is
        // acquired later on through Cytoscape.getMap()
        // This method is a blackbox and should NOT be directly executed under
        // ANY circumstances
        this.getTaskManager().execute(this.getParseSocialNetworkFileTaskFactory().createTaskIterator());
    }
    
    /**
     * The task factory for {@link ParseSocialNetworkFileTask}
     */
    private ParseSocialNetworkFileTaskFactory parseSocialNetworkFileTaskFactoryRef = null;
    
    /**
     * Set the task factory for {@link ParseSocialNetworkFileTask}
     * 
     * @param ParseSocialNetworkFileTaskFactory parseSocialNetworkFileTaskFactoryRef
     */
    public void setParseSocialNetworkFileTaskFactory(ParseSocialNetworkFileTaskFactory parseSocialNetworkFileTaskFactoryRef) {
        this.parseSocialNetworkFileTaskFactoryRef = parseSocialNetworkFileTaskFactoryRef;
    }
    
    /**
     * Get the task factory for {@link ParseSocialNetworkFileTask}
     * 
     * @return ParseSocialNetworkFileTaskFactory parseSocialNetworkFileTaskFactoryRef
     */
    public ParseSocialNetworkFileTaskFactory getParseSocialNetworkFileTaskFactory() {
        return this.parseSocialNetworkFileTaskFactoryRef;
    }
    
    /**
     * Search term that the user has inputed into the search box
     */
    private String searchTerm = null;
    
    /**
     * Set search term. Will later be retrieved by {@link SearchPubMedTask}
     * 
     * @param String searchTerm
     */
    private void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    /**
     * Get search term that user has inputed into the search box
     * 
     * @return String searchTerm
     */
    public String getSearchTerm() {
        return this.searchTerm;
    }

    /**
     * Create a network from search term
     *
     * @param String searchTerm
     * @param int category
     * @param int maxAuthorThreshold
     */
    public void createNetwork(String searchTerm, int category, int maxAuthorThreshold) {
        switch (category) {
            case Category.ACADEMIA:
                this.setSearchTerm(searchTerm);
                this.setMaxAuthorThreshold(maxAuthorThreshold);
                searchPubMed();
                break;
        }
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

    /**
     * Get the type of the analysis being performed
     *
     * @return int analysis_type
     */
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
     * Get the max author threshold
     * 
     * @return int maxAuthorThreshold
     */
    public int getMaxAuthorThreshold() {
        return maxAuthorThreshold;
    }

    /**
     * Get network file
     * 
     * @return File networkFile
     */
    public File getNetworkFile() {
        return this.networkFile;
    }

    /**
     * Get the network file type
     * 
     * @return int networkFileType
     */
    public int getNetworkFileType() {
        return networkFileType;
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

    /**
     * Given the name of a network returns the SocialNetwork object associated
     * with that network name returns null otherwise.
     *
     * @param String name
     * @return SocialNetwork socialNetwork
     */
    public SocialNetwork getSocialNetwork(String name) {
        if (this.socialNetworkMap != null && this.socialNetworkMap.containsKey(name)) {
            return this.socialNetworkMap.get(name);
        } else {
            return null;
        }
    }

    /**
     * Get social network map
     *
     * @return Map social networks <br>
     * <i>key: network name</i> <br>
     * <i>value: social network</i>
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
     * @return boolean
     */
    public boolean isValidVisualStyle(String visualStyleName) {
        if (this.visualStyleSet == null) {
            this.visualStyleSet = new HashSet<String>();
            this.visualStyleSet.add("Scopus");
            this.visualStyleSet.add("PubMed");
            this.visualStyleSet.add("InCites");
        }
        return this.visualStyleSet.contains(visualStyleName);
    }

    /**
     * Set the type of analysis
     *
     * @param int analysis_type
     */
    public void setAnalysis_type(int analysis_type) {
        this.analysis_type = analysis_type;
    }

    /**
     * Set <i>ApplyView</i> task factory
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
     * Set <i>DestroyNetwork</i> task factory
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
     * Set the max author threshold
     * 
     * @param int maxAuthorThreshold
     */
    private void setMaxAuthorThreshold(int maxAuthorThreshold) {
        this.maxAuthorThreshold = maxAuthorThreshold;
    }

    /**
     * Set network file
     * 
     * @param File networkFile
     */
    public void setNetworkFile(File networkFile) {
        this.networkFile = networkFile;
    }

    /**
     * Set the network file type
     * 
     * @param int networkFileType
     */
    public void setNetworkFileType(int networkFileType) {
        this.networkFileType = networkFileType;
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
     * Set <i>CreateNetwork</i> task factory
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
    
    /**
     * Perform a search on PubMed. Should not be executed directly.
     */
    private void searchPubMed() {
        this.getTaskManager().execute(this.getSearchPubMedTaskFactoryRef().createTaskIterator());
    }
    
    /**
     * A reference to the task factory for <i>SearchPubMedTask</i>
     */
    private SearchPubMedTaskFactory searchPubMedTaskFactoryRef = null;
    
    /**
     * Get the task factory for <i>SearchPubMedTask</i>
     * 
     * @return SearchPubMedTaskFactory searchPubMedTaskFactoryRef
     */
    private SearchPubMedTaskFactory getSearchPubMedTaskFactoryRef() {
        return this.searchPubMedTaskFactoryRef;
    }
    
    /**
     * Set the task factory for <i>SearchPubMedTask</i>
     * 
     * @param SearchPubMedTaskFactory searchPubMedTaskFactoryRef
     */
    public void setSearchPubMedTaskFactoryRef(SearchPubMedTaskFactory searchPubMedTaskFactoryRef) {
        this.searchPubMedTaskFactoryRef = searchPubMedTaskFactoryRef;
    }
    
    private int neighborListDegree = 0;
    
    /**
     * Set neighbor list degree. Will be retrieved later by {@link ExportNthDegreeNeighborsTask}.
     * 
     * @param int degree
     */
    public void setNeighborListDegree(int degree) {
        this.neighborListDegree = degree;
    }
    
    /**
     * Get neighbor list degree.
     * 
     * @return int degree
     */
    public int getNeighborListDegree() {
        return this.neighborListDegree;
    }
    
    private CyNetwork neighborListNetwork = null;
    
    /**
     * Set neighbor list network. Will be retrieved later by {@link ExportNthDegreeNeighborsTask}.
     * @param CyNetwork network
     */
    public void setNeighborListNetwork(CyNetwork network) {
        this.neighborListNetwork = network;
    }
    
    /**
     * Get neighbor list network.
     * 
     * @return CyNetwork neighborListNetwork
     */
    public CyNetwork getNeighborListNetwork() {
        return this.neighborListNetwork;
    }
    
    private File neighborListFolder = null;
    
    /**
     * Set neighbor list folder. Will be retrieved later by {@link ExportNthDegreeNeighborsTask}.
     * 
     * @param File folder
     */
    public void setNeighborListFolder(File folder) {
        this.neighborListFolder = folder;
    }
    
    /**
     * Get neighbor list folder. 
     * 
     * @return File folder
     */
    public File getNeighborListFolder() {
        return this.neighborListFolder;
    }
    
    private String neighborListAttribute = null;
    
    /**
     * Set neighbor list attribute. Will be retrieved later by {@link ExportNthDegreeNeighborsTask}.
     * 
     * @param String attribute
     */
    public void setNeighborListAttribute(String attribute) {
        this.neighborListAttribute = attribute;
    }
    
    /**
     * Get neighbor list attribute. 
     * 
     * @return String attribute
     */
    public String getNeighborListAttribute() {
        return this.neighborListAttribute;
    }

}