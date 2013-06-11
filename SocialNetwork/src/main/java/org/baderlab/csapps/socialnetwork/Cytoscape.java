package main.java.org.baderlab.csapps.socialnetwork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;


import main.java.org.baderlab.csapps.socialnetwork.tasks.NetworkTaskFactory;

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
	 * CoAuthorNetworkTaskFactory produces the required CoAuthorNetworkTask(s) slated
	 * for execution by the task manager
	 */
	private static NetworkTaskFactory networkTaskFactory = null;
	
	/**
	 * Set map
	 * @param Map map
	 * @return null
	 */
	public static void setMap(Map<Consortium, ArrayList<AbstractEdge>> map2) {
		Cytoscape.map = map2;
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
	 * @param NetworkTaskFactory networkTaskFactory
	 * @return null
	 */
	public static void setNetworkTaskFactory(NetworkTaskFactory networkTaskFactory) {
		Cytoscape.networkTaskFactory = networkTaskFactory;
	}
	
	/**
	 * Get network task factory
	 * @param null
	 * @return NetworkTaskFactory networkTaskFactory
	 */
	public static NetworkTaskFactory getNetworkTaskFactory() {
		return Cytoscape.networkTaskFactory;
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
		// Acquire map from network file
		List<? extends AbstractEdge> pubList = Incites.getPublications(networkFile);
		Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getMap(pubList);
		
		// Transfer map to Cytoscape's map variable
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
		
		// Create new map using results
		Map<Consortium, ArrayList<AbstractEdge>> map = Interaction.getMap(results); 
		
		// Transfer map to Cytoscape's map variable
		Cytoscape.setMap(map);
		
		// Create network using map
		Cytoscape.createNetwork();
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
		// This method is a blackbox and should not be executed directly under ANY circumstances
		Cytoscape.getTaskManager().execute(Cytoscape.getNetworkTaskFactory().createTaskIterator());
		
	}
}
