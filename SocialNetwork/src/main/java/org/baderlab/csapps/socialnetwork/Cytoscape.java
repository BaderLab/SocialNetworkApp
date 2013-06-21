package main.java.org.baderlab.csapps.socialnetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import main.java.org.baderlab.csapps.socialnetwork.academia.Incites;
import main.java.org.baderlab.csapps.socialnetwork.actions.UserPanelAction;
import main.java.org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskManager;
import org.xml.sax.SAXException;

/**
 * Cytoscape
 * @author Victor Kofia
 */
public class Cytoscape {
	
	/**
	 * A map containing the nodes and edges that will eventually be used
	 * to form a network
	 */
	private static Map<Consortium, ArrayList<AbstractEdge>> map = null;
	/**
	 * A reference to Cytoscape's Task Manager Service. Handy when executing
	 * tasks
	 */
	private static TaskManager<?, ?> taskManagerServiceRef = null;
	/**
	 * CreateNetworkTaskFactory produces the required CoAuthorNetworkTask(s). Slated
	 * for execution by the task manager
	 */
	private static CreateNetworkTaskFactory networkTaskFactoryRef = null;
	/**
	 * ApplyViewTaskFactory applies the selected view to the current network
	 */
	private static ApplyVisualStyleTaskFactory applyViualStyleTaskFactoryRef = null;
	/**
	 * A reference to the app's user panel. User will interact with app primarily through
	 * this panel.
	 */
	private static UserPanel userPanelRef = null;
	/**
	 * A reference to the user panel action. Cytoscape needs it to close the panel
	 */
	private static UserPanelAction userPanelAction = null;
	/**
	 * A reference to CyServiceRegistrar. Necessary for unregistering services at user's 
	 * convenience.
	 */
	private static CyServiceRegistrar cyServiceRegistrarRef = null;
	/**
	 * Network name
	 */
	private static String networkName = "DEFAULT";
	/**
	 * Network type. This information is for creating appropriate visual styles.
	 */
	private static int networkType = Category.DEFAULT;
	/**
	 * Selected network view
	 */
	private static int visualStyle = Cytoscape.DEFAULT;
	/**
	 * Default network view
	 */
	final public static int DEFAULT = -2;
	/**
	 * Chipped network view
	 */
	final public static int CHIPPED = -7;
	
	/**
	 * Set network type
	 * @param int networkType
	 * @return null
	 */
	public static void setNetworkType(int networkType) {
		Cytoscape.networkType = networkType;
	}
	
	/**
	 * Get network type
	 * @param null
	 * @return int networkType
	 */
	public static int getNetworkType() {
		return Cytoscape.networkType;
	}
	
	/**
	 * Set apply view task factory reference
	 * @param ApplyViewTaskFactory applyViewTaskFactoryRef
	 * @return null
	 */
	public static void setApplyVisualStyleTaskFactoryRef(ApplyVisualStyleTaskFactory applyViewTaskFactoryRef) {
		Cytoscape.applyViualStyleTaskFactoryRef = applyViewTaskFactoryRef;
	}
	
	/**
	 * Get apply view task factory reference
	 * @param ApplyViewTaskFactory applyVisualStyleTaskFactoryRef
	 * @return null
	 */
	private static ApplyVisualStyleTaskFactory getApplyVisualStyleTaskFactoryRef() {
		return Cytoscape.applyViualStyleTaskFactoryRef;
	}
	
	
	/**
	 * Set visual style
	 * @param int visualStyle
	 * @return null
	 */
	public static void setVisualStyle(int networkView) {
		Cytoscape.visualStyle = networkView;
	}
	
	/**
	 * Get visual style
	 * @param null
	 * @return int visualStyle
	 */
	public static int getVisualStyle() {
		return Cytoscape.visualStyle;
	}

	
	/**
	 * Set network name
	 * @param String networkName
	 * @return null
	 */
	private static void setNetworkName(String networkName) {
		Cytoscape.networkName = networkName;
	}
	
	/**
	 * Get network name
	 * @param null
	 * @return String networkName
	 */
	public static String getNetworkName() {
		return Cytoscape.networkName;
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
	 * Get user panel action
	 * @param null
	 * @return UserPanelAction userPanelAction
	 */
	public static UserPanelAction getUserPanelAction() {
		return Cytoscape.userPanelAction;
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
	 * Get map
	 * @param null
	 * @return Map map
	 */
	public static Map<Consortium, ArrayList<AbstractEdge>> getMap() {
		return Cytoscape.map;
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
	 * Set network task factory
	 * @param CreateNetworkTaskFactory networkTaskFactory
	 * @return null
	 */
	public static void setNetworkTaskFactoryRef(CreateNetworkTaskFactory networkTaskFactoryRef) {
		Cytoscape.networkTaskFactoryRef = networkTaskFactoryRef;
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
	 * Return task manager
	 * @param null
	 * @return TaskManager taskManager
	 */
	public static TaskManager<?, ?> getTaskManager() {
		return Cytoscape.taskManagerServiceRef;
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
	 * Create a network from file
	 * @param File networkFile
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */	
	public static void createNetwork(File networkFile) throws FileNotFoundException {
		
		// Parse for list of publications
		List<? extends AbstractEdge> pubList = Incites.getPublications(networkFile);
		
		if (pubList == null) {
			Cytoscape.notifyUser("Invalid file. Please load a valid Incites data file.");
		} else {
			// Set network name
			Cytoscape.setNetworkName("Faculty: " + Incites.getFacultyTextFieldRef().getText().trim());
			// Set network type
			Cytoscape.setNetworkType(UserPanel.getSelectedCategory());
			// Construct map
			Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getMap(pubList); 
			// Store map
			Cytoscape.setMap(map);
			// Create network using map
			Cytoscape.createNetwork();
		}
		
	}
	
	/**
	 * Create a network from search term
	 * @param String searchTerm
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void createNetwork(String searchTerm, int category) {
		
			// Create new search session
			Search search = new Search(searchTerm, category);
			// Get a list of the results that are going to serve as edges. Exact result type
			// may vary with website
			List<? extends AbstractEdge> results = search.getResults();
			
			if (results == null) {
				Cytoscape.notifyUser("Network could not be loaded");
			} else {
				Cytoscape.setNetworkName(searchTerm + "\'s copublication network");
				Cytoscape.setNetworkType(UserPanel.getSelectedCategory());
				// Create new map using results
				Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getMap(results);
				// Transfer map to Cytoscape's map variable
				Cytoscape.setMap(map);
				Cytoscape.createNetwork();
			}
	}
	
	/**
	 * Create a network. Method marked private in order to prevent users from inadvertently
	 * creating a network before all pertinent edge and node info is set.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static void createNetwork() {
		
		// Execute network task. 
		// NOTE: Relevant node & edge info is not directly coupled with task execution. It is
		// acquired later on through Cytoscape.getMap()
		// This method is a blackbox and should NOT be directly executed under ANY circumstances
		Cytoscape.getTaskManager().execute(Cytoscape.getNetworkTaskFactoryRef().createTaskIterator());
		
	}
	
	/**
	 * Close user panel. Method will do nothing if user panel
	 * has not been registered prior to it's execution.
	 * @param null
	 * @return null
	 */
	public static void closeUserPanel() {
		Cytoscape.getServiceRegistrar().unregisterService(Cytoscape.getUserPanelRef(), CytoPanelComponent.class);
		Cytoscape.getUserPanelAction().setName("View Panel");
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
	 * Get user panel reference
	 * @param null
	 * @return UserPanel userPanelRef
	 */
	public static UserPanel getUserPanelRef() {
		return Cytoscape.userPanelRef;
	}
	
	/**
	 * Set service registrar reference
	 * @param CyServiceRegistrar cyServiceRegistrarRef
	 * @return null
	 */
	public static void setServiceRegistrar(CyServiceRegistrar cyServiceRegistrarRef) {
		Cytoscape.cyServiceRegistrarRef = cyServiceRegistrarRef;
	}
	
	/**
	 * Get service registrar reference
	 * @param null
	 * @return CyServiceRegistrar cyServiceRegistrarRef
	 */
	public static CyServiceRegistrar getServiceRegistrar() {
		return Cytoscape.cyServiceRegistrarRef;
	}
	
	/**
	 * Apply visual style to network
	 * @param String visualStyle
	 * @return null
	 */
	public static void applyVisualStyle(String visualStyle) {
		// Set visual style
		Cytoscape.setVisualStyle(Category.getVisualStyleMap().get(visualStyle));
		// Apply visual style
		Cytoscape.getTaskManager().execute(Cytoscape.getApplyVisualStyleTaskFactoryRef().createTaskIterator());

	}
}
