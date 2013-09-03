package main.java.org.baderlab.csapps.socialnetwork.model;

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import main.java.org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Incites;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Publication;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import main.java.org.baderlab.csapps.socialnetwork.panels.AcademiaPanel;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;
import main.java.org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;

import org.apache.commons.io.FilenameUtils;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskManager;

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
	 * Currently selected social network
	 */
	private static SocialNetwork currentlySelectedSocialNetwork = null;
	/**
	 * A reference to the cytoscape application manager. Necessary for
	 * changing network views.
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
	private static Map<Collaboration, ArrayList<AbstractEdge>> map = null;
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
	 * A reference to the cytoscape task manager. As the name suggests
	 * the task manager is used for executing tasks.
	 */
	private static TaskManager<?, ?> taskManagerServiceRef = null;
	/**
	 * A reference to user panel action. Controls panel actions (viewing,
	 * closing ... etc)
	 */
	private static ShowUserPanelAction userPanelAction = null;
	/**
	 * A reference to the app's user panel. User will interact with app primarily through
	 * this panel.
	 */
	private static UserPanel userPanelRef = null;
	/**
	 * Currently selected visual style ID
	 */
	private static int visualStyleID = VisualStyles.DEFAULT_VISUAL_STYLE;
	/**
	 * Set of all visual styles currently supported by app
	 */
	private static HashSet<String> visualStyleSet = null;
	
	/**
	 * Return true iff visual style is currently supported
	 * by app
	 * @param String visualStyleName
	 * @return boolean bool
	 */
	public static boolean isValidVisualStyle(String visualStyleName) {
		if (visualStyleSet == null) {
			Cytoscape.visualStyleSet = new HashSet<String>();
			Cytoscape.visualStyleSet.add("Chipped");
			Cytoscape.visualStyleSet.add("Vanue");
		}
		return Cytoscape.visualStyleSet.contains(visualStyleName);
	}
	
	/**
	 * Apply visual style to network
	 * @param String visualStyle
	 * @return null
	 */
	public static void applyVisualStyle(String visualStyle) {
		Cytoscape.setVisualStyleID(VisualStyles.getVisualStyleID(visualStyle));
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
		Cytoscape.getUserPanelAction().putValue(Action.NAME, "View Panel");
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
		// Verify that network name is valid
		String networkName = AcademiaPanel.getFacultyTextFieldRef().getText().trim();
		if (! Cytoscape.isNameValid(networkName)) {
			Cytoscape.notifyUser("Network " + networkName + " already exists in Cytoscape."
					           + " Please enter a new name.");
			return;
		}
		// Change mouse cursor
		Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		// Initialize variables
		List<? extends Publication> pubList = null;
		String extension = null;
		SocialNetwork socialNetwork = null;
		String departmentName = null;
		// Create network out of Incites data
		if (Incites.getIncitesRadioButton().isSelected()) {
			extension = FilenameUtils.getExtension(networkFile.getPath());
			IncitesParser incitesParser = null;
			// Load data from text file
			if (extension.trim().equalsIgnoreCase("xlsx")) {
				socialNetwork = new SocialNetwork(networkName, Category.INCITES);
				incitesParser = new IncitesParser(networkFile);
				if (incitesParser.getIgnoredRows() >= 1) {
					Cytoscape.notifyUser("Some rows could not be parsed.");
				}
				if (incitesParser.getIdentifiedFacultyList().size() == 0) {
					Cytoscape.notifyUser("Unable to identify faculty." 
							          +  "Please verify that Incites data file is valid");
				}
				pubList = incitesParser.getPubList();
				departmentName = incitesParser.getDepartmentName();
				if (pubList == null) {
					Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					Cytoscape.notifyUser("Invalid file. This Incites file is corrupt.");
					return;
				}
			// Notify user of inappropriate file type
			} else {
				Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				Cytoscape.notifyUser("Invalid file. Incite data files either have to be excel " +
						             "spreadsheets or text files.");
				return;
			}
			// Add summary attributes
			socialNetwork.getSummaryList().add(new String[] {"Total # of publications: ", Integer.toString(incitesParser.getPubList().size())});
			socialNetwork.getSummaryList().add(new String[] {"Total # of faculty: ", Integer.toString(incitesParser.getFacultySet().size())});
			socialNetwork.getSummaryList().add(new String[] {"Total # of unidentified faculty: ", Integer.toString(incitesParser.getUnidentifiedFacultyList().size())});
			socialNetwork.getSummaryList().add(new String[] {"<hr><br>UNIDENTIFIED FACULTY", incitesParser.getUnidentifiedFacultyString()});
			// Add info to social network map(s)
			socialNetwork.getAttrMap().put("Department", departmentName);
		// Create network out of Scopus data
		} else if (Scopus.getScopusRadioButton().isSelected()) {
			extension = FilenameUtils.getExtension(networkFile.getPath());
			Scopus scopus = null;
			if (extension.trim().equalsIgnoreCase("csv")) {
				socialNetwork = new SocialNetwork(networkName, Category.SCOPUS);
				scopus = new Scopus(networkFile);
				pubList = scopus.getPubList();
				socialNetwork.getSummaryList().add(new String[] {"Total # of publications: ", Integer.toString(scopus.getPubList().size())});
				if (pubList == null) {
					Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return;
				}
			} else {
				Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				Cytoscape.notifyUser("Invalid file. Scopus data files have to be csv " +
						             "spreadsheets");
				return;
			}
		}
		// Create interaction
		Interaction interaction = new Interaction(pubList, Category.ACADEMIA);
		// Create map
		Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
		if (map.size() == 0) {
			Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Cytoscape.notifyUser("Network couldn't be loaded. File is corrupt.");
			return;
		}
		Cytoscape.setMap(map);
		Cytoscape.setNetworkName(networkName);
		Cytoscape.getSocialNetworkMap().put(networkName, socialNetwork);
		// Create network using map
		Cytoscape.createNetwork();
	}

	/**
	 * Create a network from search term
	 * @param String searchTerm
	 * @param int category
	 * @return null
	 */
	public static void createNetwork(String searchTerm, int category) {
		// Verify that network name is valid
		if (! Cytoscape.isNameValid(searchTerm)) {
			Cytoscape.notifyUser("Network " + networkName + " already exists in Cytoscape."
					+ " Please enter a new name.");
			return;
		}
		// Create new search session
		Search search = new Search(searchTerm, category);
		// Get a list of the results that are going to serve as edges. Exact result type
		// may vary with website
		List<? extends AbstractEdge> results = (List<? extends AbstractEdge>) search.getResults();
		if (results == null) {
			Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Cytoscape.notifyUser("Network could not be loaded");
			return;
		} 
		if (results.size() == 0) {
			Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Cytoscape.notifyUser("Search did not yield any results");
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
				interaction = new Interaction(results, category);
				socialNetwork = new SocialNetwork(searchTerm, category);
				socialNetwork.getSummaryList().add(new String[] {"Total # of publications: ", Integer.toString(search.getTotalHits())});
				// Create new map using results
				map = interaction.getAbstractMap();
				// Set social network attributes
				// ??
				break;
		}
		Cytoscape.setNetworkName(searchTerm);
		Cytoscape.getSocialNetworkMap().put(searchTerm, socialNetwork);
		// Transfer map to Cytoscape's map variable
		Cytoscape.setMap(map);
		// Create network using map
		Cytoscape.createNetwork();			
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
	 * @param null
	 * @return ApplyViewTaskFactory applyVisualStyleTaskFactoryRef
	 */
	public static ApplyVisualStyleTaskFactory getApplyVisualStyleTaskFactoryRef() {
		return Cytoscape.applyVisualStyleTaskFactoryRef;
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
	public static Map<Collaboration, ArrayList<AbstractEdge>> getMap() {
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
	 * Get network table column names
	 * @param null
	 * @return String[] networkTableColumnNames
	 * @return
	 */
	public static String[] getNetworkTableColumnNames() {
		return new String[] {"Name", "Node Count", "Edge Count", "Category"};
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
	 * @param null
	 * @return Map social networks
	 * <br><i>key: network name</i> 
	 * <br><i>value: {CyNetwork, Category, CyNetworkView}</i>
	 */
	public static Map<String, SocialNetwork> getSocialNetworkMap() {
		if (Cytoscape.socialNetworkMap == null) {
			Cytoscape.setSocialNetworkMap(new HashMap<String, SocialNetwork>());
			Cytoscape.socialNetworkMap.put("DEFAULT", 
					                       new SocialNetwork("DEFAULT", Category.DEFAULT));
		}
		return Cytoscape.socialNetworkMap;
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
	public static ShowUserPanelAction getUserPanelAction() {
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
	 * <i>not</i> already present in Cytoscape
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
		JOptionPane.showMessageDialog(new JPanel(), "<html><body style='width: 200px'>" + message);
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
	 * Set currently selected social network
	 * @param SocialNetwork currentlySelectedSocialNetwork
	 * @return null
	 */
	public static void setCurrentlySelectedSocialNetwork(
			SocialNetwork currentlySelectedSocialNetwork) {
		Cytoscape.currentlySelectedSocialNetwork = currentlySelectedSocialNetwork;
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
	public static void setMap(Map<Collaboration, ArrayList<AbstractEdge>> map) {
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
	 * @param ShowUserPanelAction userPanelAction
	 * @return null
	 */
	public static void setUserPanelAction(ShowUserPanelAction userPanelAction) {
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
}