package main.java.org.baderlab.csapps.socialnetwork;

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.org.baderlab.csapps.socialnetwork.academia.Incites;
import main.java.org.baderlab.csapps.socialnetwork.academia.Publication;
import main.java.org.baderlab.csapps.socialnetwork.actions.UserPanelAction;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;
import main.java.org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskManager;

import main.java.org.baderlab.csapps.socialnetwork.networks.IncitesNetwork;
import main.java.org.baderlab.csapps.socialnetwork.networks.SocialNetwork;

/**
 * Cytoscape
 * @author Victor Kofia
 */
public class Cytoscape {
	
	/**
	 * A reference to the 'apply visual style' task factory
	 */
	private static ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = null;
	/**
	 * A reference to the cytoscape application manager
	 */
	private static CyApplicationManager cyAppManagerServiceRef = null;
	/**
	 * A reference to the cytoscape service registrar. Necessary for 
	 * unregistering services at user's convenience.
	 */
	private static CyServiceRegistrar cyServiceRegistrarRef = null;
	/**
	 * A reference to the 'destroy network' task factory 
	 */
	private static DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = null;
	/**
	 * A map containing the nodes and edges that will eventually be used
	 * to form a network
	 */
	private static Map<Consortium, ArrayList<AbstractEdge>> map = null;
	/**
	 * Name of network
	 */
	private static String networkName = null;
	/**
	 * A reference to the 'create network' task factory
	 */
	private static CreateNetworkTaskFactory networkTaskFactoryRef = null;
	/**
	 * A network that's just about to be destroyed
	 */
	private static CyNetwork networkToBeDestroyed = null;
	/**
	 *Social network map. 
	 *<br>Key: network name 
	 *<br>Value: social network
	 */
	private static Map<String, SocialNetwork> socialNetworkMap = null;
	/**
	 * A reference to the cytoscape task manager
	 */
	private static TaskManager<?, ?> taskManagerServiceRef = null;
	/**
	 * A reference to the user panel action. Used for closing the panel.
	 */
	private static UserPanelAction userPanelAction = null;
	/**
	 * A reference to the app's user panel. User will interact with app primarily through
	 * this panel.
	 */
	private static UserPanel userPanelRef = null;
	/**
	 * Selected network view
	 */
	private static int visualStyleID = Category.DEFAULT;
	/**
	 * Currently selected social network
	 */
	private static SocialNetwork currentlySelectedSocialNetwork = null;
	
	/**
	 * Apply visual style to network
	 * @param String visualStyle
	 * @return null
	 */
	public static void applyVisualStyle(String visualStyle) {
		Cytoscape.setVisualStyleID(Category.getVisualStyleID(visualStyle));
		Cytoscape.getTaskManager().execute(Cytoscape.getApplyVisualStyleTaskFactoryRef()
				                  .createTaskIterator());
	}
	
	/**
	 * Close user panel. Method will do nothing if user panel
	 * has not been registered prior to it's execution.
	 * @param null
	 * @return null
	 */
	public static void closeUserPanel() {
		Cytoscape.getServiceRegistrar().unregisterService
		(Cytoscape.getUserPanelRef(), CytoPanelComponent.class);
		Cytoscape.getUserPanelAction().setName("View Panel");
	}
	
	/**
	 * Create a network. Method marked private in order to prevent users from inadvertently
	 * creating a network before all pertinent edge and node info is set.
	 * @param null
	 * @return null
	 */
	private static void createNetwork() {
		
		// Execute network task. 
		// NOTE: Relevant node & edge info is not directly coupled with task execution. It is
		// acquired later on through Cytoscape.getMap()
		// This method is a blackbox and should NOT be directly executed under ANY circumstances
		Cytoscape.getTaskManager().execute(Cytoscape.getNetworkTaskFactoryRef()
				 .createTaskIterator());
	
	}
	
	/**
	 * Create a network from file
	 * @param File networkFile
	 * @return null
	 */	
	public static void createNetwork(File networkFile) throws FileNotFoundException {
	
		// Parse for list of publications
//		List<? extends Publication> pubList = Incites.getPublications(networkFile);
		List<? extends Publication> pubList = Tester.getPubList(networkFile);
		
		// Get faculty set
		Object[] facultyAttr = Tester.getFacultyAttr(networkFile);
		String facultyName = (String) facultyAttr[0];
		HashSet facultyHashSet = (HashSet) facultyAttr[1];
				
		if (pubList == null) {
			Cytoscape.notifyUser("Invalid file. Please load a valid Incites data file.");
		} else {
			Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getAcademiaMap(pubList, facultyName, facultyHashSet);
			if (map.size() == 0) {
				Cytoscape.notifyUser("Network couldn't be loaded. File is corrupt.");
				return;
			}
//			Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getAbstractMap(pubList); 
			Cytoscape.setMap(map);
			String networkName = Incites.getFacultyTextFieldRef().getText().trim();
			// Check if a network with a similar name already exists
			if (Cytoscape.isNameValid(networkName)) {
				Cytoscape.setNetworkName(networkName);
				IncitesNetwork incitesNetwork = new IncitesNetwork(Category.INCITES);
				incitesNetwork.setFacultyName(facultyName);
				Cytoscape.getSocialNetworkMap().put(networkName, 
						                            incitesNetwork);
				// Change mouse cursor
		        Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
				// Create network using map
				Cytoscape.createNetwork();
			} else {
				Cytoscape.notifyUser("Network " + networkName + " already exists in Cytoscape."
						                                      + " Please enter a new name.");
			}
		}
	}

	/**
	 * Create a network from search term
	 * @param String searchTerm
	 * @param int category
	 * @return null
	 */
	public static void createNetwork(String searchTerm, int category) {
		
			// Create new search session
			Search search = new Search(searchTerm, category);
			
			// Get a list of the results that are going to serve as edges. Exact result type
			// may vary with website
			List<? extends AbstractEdge> results = search.getResults();
			
			if (results == null) {
				Cytoscape.notifyUser("Network could not be loaded");
				return;
			} 
			
			if (results.size() == 0) {
				Cytoscape.notifyUser("Search didn't yield any results");
				return;
			}
			
			// 
	        
			// Create new map using results
			Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getAbstractMap(results);
			// Check if a similar network already exists
			if (Cytoscape.isNameValid(searchTerm)) {
				Cytoscape.setNetworkName(searchTerm);
				Cytoscape.getSocialNetworkMap().put(searchTerm, 
					  new SocialNetwork(category));
				// Transfer map to Cytoscape's map variable
				Cytoscape.setMap(map);
				// Change mouse cursor
		        Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
				// Create network using map
				Cytoscape.createNetwork();
			} else {
				Cytoscape.notifyUser("Network " + searchTerm + " already exists in Cytoscape. "
						           + "Please enter a new name.");
			}
			
	}

	/**
	 * Destroy a network
	 * @param CyNetwork network
	 * @return null
	 */
	public static void destroyNetwork(CyNetwork network) {
		Cytoscape.setNetworkToBeDestroyed(network);
		Cytoscape.getTaskManager().execute(Cytoscape.getDestroyNetworkTaskFactoryRef()
				                                    .createTaskIterator());
	}

	/**
	 * Get apply view task factory
	 * @param ApplyViewTaskFactory applyVisualStyleTaskFactoryRef
	 * @return null
	 */
	private static ApplyVisualStyleTaskFactory getApplyVisualStyleTaskFactoryRef() {
		return Cytoscape.applyVisualStyleTaskFactoryRef;
	}

	/**
	 * Get Cytoscape application manager
	 * @param null
	 * @return CyApplicationManager cyAppManagerServiceRef
	 */
	public static CyApplicationManager getCyAppManagerServiceRef() {
		return cyAppManagerServiceRef;
	}

	/**
	 * Get destroy network task factory
	 * @param null
	 * @return DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef 
	 */
	public static DestroyNetworkTaskFactory getDestroyNetworkTaskFactoryRef() {
		return Cytoscape.destroyNetworkTaskFactoryRef;
	}

	/**
	 * Get network map. Contains edge and node information.
	 * @param null
	 * @return Map map
	 */
	public static Map<Consortium, ArrayList<AbstractEdge>> getMap() {
		return Cytoscape.map;
	}

	/**
	 * Get network name.
	 * @param null
	 * @return String networkName
	 */
	public static String getNetworkName() {
		return Cytoscape.networkName;
	}

	/**
	 * Get the name associated with a particular CyNetwork object
	 * @param CyNetwork network
	 * @return String networkName
	 */
	public static String getNetworkName(CyNetwork network) {
		ArrayList<CyRow> rowList = (ArrayList<CyRow>) network.getDefaultNetworkTable()
				                                             .getAllRows();
		CyRow row = rowList.get(0);
		String networkName = (String) row.getAllValues().get("name");
		return networkName;
	}

	/**
	 * Get network task factory
	 * @param null
	 * @return NetworkTaskFactory networkTaskFactory
	 */
	public static CreateNetworkTaskFactory getNetworkTaskFactoryRef() {
		return Cytoscape.networkTaskFactoryRef;
	}

	/**
	 * Get network to be destroyed
	 * @param null
	 * @return CyNetwork networkToBeDestroyed
	 */
	public static CyNetwork getNetworkToBeDestroyed() {
		return Cytoscape.networkToBeDestroyed;
	}

	/**
	 * Get Cytoscape service registrar
	 * @param null
	 * @return CyServiceRegistrar cyServiceRegistrarRef
	 */
	public static CyServiceRegistrar getServiceRegistrar() {
		return Cytoscape.cyServiceRegistrarRef;
	}

	/**
	 * Get social network map
	 *<br>Key: network name 
	 *<br>Value: {CyNetwork, Category, CyNetworkView}
	 * @param null
	 * @return Map social networks
	 */
	public static Map<String, SocialNetwork> getSocialNetworkMap() {
		if (Cytoscape.socialNetworkMap == null) {
			Cytoscape.setSocialNetworkMap(new HashMap<String, SocialNetwork>());
			Cytoscape.socialNetworkMap.put("DEFAULT", 
					                       new SocialNetwork(Category.DEFAULT));
		}
		return Cytoscape.socialNetworkMap;
	}

	/**
	 * Get network table column names
	 * @param null
	 * @return String[] networkTableColumnNames
	 * @return
	 */
	public static String[] getNetworkTableColumnNames() {
		return new String[] {"Name", "Node Count", "Edge Count", "Category"};
	}

	/**
	 * Get Cytoscape task manager
	 * @param null
	 * @return TaskManager taskManager
	 */
	public static TaskManager<?, ?> getTaskManager() {
		return Cytoscape.taskManagerServiceRef;
	}
	
	/**
	 * Get user panel action
	 * @param null
	 * @return UserPanelAction userPanelAction
	 */
	public static UserPanelAction getUserPanelAction() {
		return Cytoscape.userPanelAction;
	}

	/**
	 * Get user panel reference
	 * @param null
	 * @return UserPanel userPanelRef
	 */
	public static UserPanel getUserPanelRef() {
		return Cytoscape.userPanelRef;
	}

	/**
	 * Get visual style ID
	 * @param null
	 * @return int visualStyleID
	 */
	public static int getVisualStyleID() {
		return Cytoscape.visualStyleID;
	}

	/**
	 * Return true iff a network with a similar name is 
	 * not already present in Cytoscape
	 * @param String networkName
	 * @return boolean
	 */
	public static Boolean isNameValid(String name) {
		return ! Cytoscape.getSocialNetworkMap().containsKey(name);
	}
	
	/**
	 * Notify user of an issue
	 * @param String message
	 * @return null
	 */
	public static void notifyUser(String message) {
		JOptionPane.showMessageDialog(new JPanel(), message);
	}
	
	/**
	 * Set 'apply view' task factory
	 * @param ApplyViewTaskFactory applyViewTaskFactoryRef
	 * @return null
	 */
	public static void setApplyVisualStyleTaskFactoryRef
	                   (ApplyVisualStyleTaskFactory applyViewTaskFactoryRef) {
		Cytoscape.applyVisualStyleTaskFactoryRef = applyViewTaskFactoryRef;
	}
	
	/**
	 * Set network's view as the current view
	 * @param String networkName
	 * @return null
	 */
	public static void setCurrentNetworkView(String networkName) {
		CyNetworkView networkView = (CyNetworkView) 
				                    Cytoscape.getSocialNetworkMap()
				                    .get(networkName).getNetworkView();
		Cytoscape.getCyAppManagerServiceRef().setCurrentNetworkView(networkView);
	}

	/**
	 * Set Cytoscape application manager
	 * @param CyApplicationManager cyAppManagerServiceRef
	 * @return null
	 */
	public static void setCyAppManagerServiceRef
	              (CyApplicationManager cyAppManagerServiceRef) {
		Cytoscape.cyAppManagerServiceRef = cyAppManagerServiceRef;
	}
	
	/**
	 * Set 'destroy network' task factory
	 * @param DestroyNetworkTaskFActory destroyNetworkTaskFactory
	 * @return null
	 */
	public static void setDestroyNetworkTaskFactoryRef(DestroyNetworkTaskFactory 
			                                       destroyNetworkTaskFactory) {
		Cytoscape.destroyNetworkTaskFactoryRef = destroyNetworkTaskFactory;
	}
	
	/**
	 * Set map
	 * @param Map map
	 * @return null
	 */
	public static void setMap(Map<Consortium, ArrayList<AbstractEdge>> map) {
		Cytoscape.map = map;
	}
	
	/**
	 * Set network name
	 * @param String networkName
	 * @return null
	 */
	public static void setNetworkName(String networkName) {
		Cytoscape.networkName = networkName;
	}
	
	/**
	 * Set network task factory
	 * @param CreateNetworkTaskFactory networkTaskFactory
	 * @return null
	 */
	public static void setNetworkTaskFactoryRef
	                   (CreateNetworkTaskFactory networkTaskFactoryRef) {
		Cytoscape.networkTaskFactoryRef = networkTaskFactoryRef;
	}
	
	/**
	 * Set network to be destroyed
	 * @param CyNetwork networkToBeDestroyed
	 * @return null
	 */
	public static void setNetworkToBeDestroyed(CyNetwork networkToBeDestroyed) {
		Cytoscape.networkToBeDestroyed = networkToBeDestroyed;
	}
	
	/**
	 * Set Cytoscape service registrar
	 * @param CyServiceRegistrar cyServiceRegistrarRef
	 * @return null
	 */
	public static void setServiceRegistrar
	                   (CyServiceRegistrar cyServiceRegistrarRef) {
		Cytoscape.cyServiceRegistrarRef = cyServiceRegistrarRef;
	}
	
	/**
	 * Set social network map
	 * @param Map socialNetworkMap
	 * @return null
	 */
	public static void setSocialNetworkMap
	                   (Map<String, SocialNetwork> socialNetwork) {
		Cytoscape.socialNetworkMap = socialNetwork;
	}
	
	/**
	 * Set task manager
	 * @param TaskManager taskManager
	 * @return null
	 */
	public static void setTaskManager(TaskManager<?, ?> taskManager) {
		Cytoscape.taskManagerServiceRef = taskManager;
	}
	
	/**
	 * Set user panel action
	 * @param UserPanelAction userPanelAction
	 * @return null
	 */
	public static void setUserPanelAction(UserPanelAction userPanelAction) {
		Cytoscape.userPanelAction = userPanelAction;
	}
	
	/**
	 * Set user panel reference
	 * @param UserPanel userPanelRef
	 * @return null
	 */
	public static void setUserPanelRef(UserPanel userPanelRef) {
		Cytoscape.userPanelRef = userPanelRef;
	}

	/**
	 * Set visual style ID
	 * @param int visualStyleID
	 * @return null
	 */
	public static void setVisualStyleID(int visualStyleID) {
		Cytoscape.visualStyleID = visualStyleID;
	}

	/**
	 * Get currently selected social network
	 * @param null
	 * @return SocialNetwork currentlySelectedSocialNetwork
	 */
	public static SocialNetwork getCurrentlySelectedSocialNetwork() {
		return Cytoscape.currentlySelectedSocialNetwork;
	}

	/**
	 * Set currently selected social network
	 * @param SocialNetwork currentlySelectedSocialNetwork
	 * @return null
	 */
	public static void setCurrentlySelectedSocialNetwork(
			SocialNetwork currentlySelectedSocialNetwork) {
		Cytoscape.currentlySelectedSocialNetwork = currentlySelectedSocialNetwork;
	}
}
