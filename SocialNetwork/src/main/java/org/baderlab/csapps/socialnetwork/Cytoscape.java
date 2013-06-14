package main.java.org.baderlab.csapps.socialnetwork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import main.java.org.baderlab.csapps.socialnetwork.actions.UserPanelAction;
import main.java.org.baderlab.csapps.socialnetwork.pubmed.Incites;
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
	 * CreateNetworkTaskFactory produces the required CoAuthorNetworkTask(s) slated
	 * for execution by the task manager
	 */
	private static CreateNetworkTaskFactory networkTaskFactoryRef = null;
	/**
	 * A reference to the app's user panel. User will interact with app primary through
	 * this panel.
	 */
	private static UserPanel userPanelRef = null;
	/**
	 * A reference to the user panel action. Class cytoscape needs it to close the panel
	 */
	private static UserPanelAction userPanelAction = null;
	/**
	 * A reference to CyServiceRegistrar. Necessary for unregistering services at user's 
	 * express wish and convenience.
	 */
	private static CyServiceRegistrar cyServiceRegistrarRef = null;
	
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
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */	
	public static void createNetwork(File networkFile) throws ParserConfigurationException, SAXException, IOException {
		
		// Parse for list of publications
		List<? extends AbstractEdge> pubList = Incites.getPublications(networkFile);
		
		// Construct map
		Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getMap(pubList); 
		
		// Store map
		Cytoscape.setMap(map);
		
		// Create network using map
		Cytoscape.createNetwork();
		
	}
	
	/**
	 * Create a network from search term
	 * @param String searchTerm
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void createNetwork(String searchTerm, int website) throws ParserConfigurationException, SAXException, IOException {
		
			// Create new search session
			Search search = new Search(searchTerm, website);
			// Get a list of the results that are going to serve as edges. Result type
			// may differ between different websites
			List<? extends AbstractEdge> results = search.getResults();
			
			if (results != null) {
				// Create new map using results
				Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getMap(results);
				// Transfer map to Cytoscape's map variable
				Cytoscape.setMap(map);
				// Create network using map
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
	private static void createNetwork() throws ParserConfigurationException, SAXException, IOException {
		
		// Execute network task. 
		// NOTE: Relevant node & edge info is not directly coupled with task execution. It is
		// acquired later on through Cytoscape.getMap()
		// This method is a blackbox and should NOT be executed directly under ANY circumstances
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
		Cytoscape.getUserPanelAction().setName("Display Panel");
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
	
}
