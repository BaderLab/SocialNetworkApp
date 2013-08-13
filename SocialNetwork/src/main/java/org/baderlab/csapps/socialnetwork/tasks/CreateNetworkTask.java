package main.java.org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.java.org.baderlab.csapps.socialnetwork.AbstractEdge;
import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;
import main.java.org.baderlab.csapps.socialnetwork.Consortium;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.SocialNetwork;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Create a new network
 * @author Victor Kofia
 */
public class CreateNetworkTask extends AbstractTask {
	private int currentSteps = 0;
	private CyLayoutAlgorithmManager cyLayoutManagerServiceRef;
	private CyNetworkFactory cyNetworkFactoryServiceRef;
	private CyNetworkManager cyNetworkManagerServiceRef;
	private CyNetworkNaming cyNetworkNamingServiceRef;
	private CyNetworkViewFactory cyNetworkViewFactoryServiceRef;
	private CyNetworkViewManager cyNetworkViewManagerServiceRef;
	private TaskMonitor monitor = null;
	private double progress = 0.0;
	private int totalSteps = 0;
	
	/**
	 * Create a new network task
	 * @param null
	 * @return null
	 */
	public CreateNetworkTask(CyNetworkNaming cyNetworkNamingServiceRef, 
			                 CyNetworkFactory cyNetworkFactoryServiceRef, 
			                 CyNetworkManager cyNetworkManagerServiceRef, 
			                 CyNetworkViewFactory cyNetworkViewFactoryServiceRef, 
			                 CyNetworkViewManager cyNetworkViewManagerServiceRef, 
			                 CyLayoutAlgorithmManager cyLayoutManagerServiceRef) {
		this.cyNetworkNamingServiceRef = cyNetworkNamingServiceRef;
		this.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
		this.cyNetworkViewFactoryServiceRef = cyNetworkViewFactoryServiceRef;
		this.cyNetworkViewManagerServiceRef = cyNetworkViewManagerServiceRef;
		this.cyLayoutManagerServiceRef = cyLayoutManagerServiceRef;
	}
	
	/**
	 * Return a network containing node's siblings and all corresponding edges
	 * @param Map map
	 * @return CyNetwork network
	 */
	public CyNetwork loadNetwork(Map<Consortium, ArrayList<AbstractEdge>> map) {
		try {
			// Create an empty network 
			CyNetwork myNet = cyNetworkFactoryServiceRef.createNetwork();
			// Get network node table
			CyTable nodeTable = null;
			// Get network edge table
			CyTable edgeTable = null;

			// Add all columns to node table
			// WIP: WORK IN PROGRESS
			// ????
			nodeTable = myNet.getDefaultNodeTable();
			Object[] keys = map.keySet().toArray();
			AbstractNode key = ((Consortium) keys[0]).getNode1();
			for (Entry<String, Object> attr : key.getNodeAttrMap().entrySet()) {
				String attrName = attr.getKey();
				Object attrType = attr.getValue();
				if (attrType instanceof String) {
					nodeTable.createColumn(attrName, String.class, false);
				} else if (attrType instanceof Integer) {
					nodeTable.createColumn(attrName, Integer.class, false);
				}
			}

			// Add all columns to edge table
			// WIP: WORK IN PROGRESS
			// ????
			edgeTable = myNet.getDefaultEdgeTable();
			Object[] values = map.values().toArray();
			@SuppressWarnings("unchecked")
			ArrayList<AbstractEdge> values2 = (ArrayList<AbstractEdge>) values[0];
			AbstractEdge value = values2.get(0);
			for (Entry<String, Object> attr : value.getEdgeAttrMap().entrySet()) {
				String attrName = attr.getKey();
				Object attrType = attr.getValue();
				if (attrType instanceof String) {
					edgeTable.createColumn(attrName, String.class, false);
				} else if (attrType instanceof Integer) {
					edgeTable.createColumn(attrName, Integer.class, false);
				} else if (attrType instanceof List) {
					edgeTable.createListColumn(attrName, String.class, false);
				}
			}

			// Set network name
			myNet.getDefaultNetworkTable().getRow(myNet.getSUID())
			.set("name", cyNetworkNamingServiceRef.getSuggestedNetworkTitle
					(Cytoscape.getNetworkName()));

			// Build network
			Consortium consortium = null;
			AbstractNode node1 = null;
			AbstractNode node2 = null;
			List<? extends AbstractEdge> edgeArray = null;
			CyEdge edgeRef = null;
			CyNode nodeRef = null;

			Map<AbstractNode, CyNode> nodeMap  = new HashMap<AbstractNode, CyNode>();

			// Get all consortiums and their corresponding edges
			for (Entry<Consortium, ArrayList<AbstractEdge>> entry : map.entrySet()) {
				consortium = entry.getKey();
				edgeArray= entry.getValue();

				// Get nodes
				node1 = consortium.getNode1();
				node2 = consortium.getNode2();

				// Check for nodes in nodeMap
				if (! nodeMap.containsKey(node1)) {
					nodeRef = myNet.addNode();
					// Add each node attribute to its respective column
					for (Entry<String, Object> attr : node1.getNodeAttrMap().entrySet()) {
						nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), 
								attr.getValue());
					}
					nodeMap.put(node1, nodeRef);
				}

				if (! nodeMap.containsKey(node2)) {
					nodeRef = myNet.addNode();
					// Add each node attribute to its respective column
					for (Entry<String, Object> attr : node2.getNodeAttrMap().entrySet()) {
						nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), 
								attr.getValue());
					}
					nodeMap.put(node2, nodeRef);
				}

				for (AbstractEdge edge : edgeArray) {
					edgeRef = myNet.addEdge(nodeMap.get(node1), nodeMap.get(node2), false);
					// Add each edge attribute to it's respective column
					for (Entry<String, Object> attr : edge.getEdgeAttrMap().entrySet()) {
						edgeTable.getRow(edgeRef.getSUID()).set(attr.getKey(), 
								attr.getValue());
					}
				}

				updateProgress();

			}

			this.monitor.setTitle("Creating network view");
			this.monitor.setProgress(0.25);
			this.monitor.setStatusMessage("Complete 25%");
			return myNet;
		} catch (ArrayIndexOutOfBoundsException exception) {
			exception.printStackTrace();
			Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Cytoscape.notifyUser("Network could not be loaded. Array Index Out Of Bounds.");
			Cytoscape.getSocialNetworkMap().remove(Cytoscape.getNetworkName());
			Cytoscape.setNetworkName(null);
			this.monitor = null;
			return null;
		}
	}
	
	/**
	 * Run network task
	 * @param TaskMonitor taskMonitor
	 * @return null
	 */
	public void run(TaskMonitor monitor) throws Exception {
		CyNetworkManager networkManager = cyNetworkManagerServiceRef;
		CyNetworkViewManager networkViewManager = cyNetworkViewManagerServiceRef;

		// Get map
		Map<Consortium, ArrayList<AbstractEdge>> map = Cytoscape.getMap();
				
		if (map == null) {
			Cytoscape.notifyUser
			("Network could not be loaded. Cytoscape network map could not be accessed.");
		} else {
			
			//Set monitor parameters
			this.monitor = monitor;
			this.monitor.setTitle("Loading Network");
			this.monitor.setProgress(0.0);
			this.totalSteps = map.size();

			// Load network
			CyNetwork network = loadNetwork(map);

			if (network == null) {
				return;
			}
			
			networkManager.addNetwork(network);
			
			final Collection<CyNetworkView> views = networkViewManager
					                                .getNetworkViews(network);
			
			this.monitor.setProgress(0.75);
			this.monitor.setStatusMessage("Complete: 75%");
			CyNetworkView networkView = null;
			if(views.size() != 0) {
				networkView = views.iterator().next();
			}
			if (networkView == null) {
				// Create a new view for my network
				networkView = cyNetworkViewFactoryServiceRef
						      .createNetworkView(network);
				networkViewManager.addNetworkView(networkView);
			} else {
				Cytoscape.notifyUser("Network already present");
			}
			
		
			// Set the variable destroyView to true, the following snippet of code
			// will destroy a view
			boolean destroyView = false;
			if (destroyView) {
				networkViewManager.destroyNetworkView(networkView);
			}
			
			SocialNetwork socialNetwork = Cytoscape.getSocialNetworkMap()
					                              .get(Cytoscape.getNetworkName());
			socialNetwork.setNetworkRef(network);
			socialNetwork.setNetworkView(networkView);
			
			// Auto apply visual style and layout
			CyLayoutAlgorithm layout = cyLayoutManagerServiceRef
					                   .getLayout("force-directed");
			String layoutAttribute = null;
			TaskIterator layoutTaskIterator = layout.createTaskIterator
							(networkView, 
							 layout.createLayoutContext(), 
							 CyLayoutAlgorithm.ALL_NODE_VIEWS, 
							 layoutAttribute);
			TaskIterator taskIterator = new TaskIterator();
			taskIterator.append(layoutTaskIterator);
			int visualStyleID = Cytoscape.getCurrentlySelectedSocialNetwork()
                                         .getDefaultVisualStyle();
			Cytoscape.setVisualStyleID(visualStyleID);
			UserPanel.setSelectedVisualStyle(visualStyleID);
			taskIterator.append(Cytoscape.getApplyVisualStyleTaskFactoryRef()
				                                    .createTaskIterator());
			insertTasksAfterCurrentTask(taskIterator);

		}
	}
	
	/**
	 * Return progress as a percentage
	 * @param Double progress
	 * @return String percentage
	 */
	private String toPercent(double progress) {
		progress = progress * 100;
		DecimalFormat df = new DecimalFormat("00");
		return df.format(progress) + "%";
	}
	
	/**
	 * Update progress monitor
	 * @param int currentSteps
	 * @return null
	 */
	private void updateProgress() {
		this.currentSteps += 1;
		this.progress = (double)this.currentSteps / this.totalSteps;
		this.monitor.setStatusMessage("Complete: " + toPercent(this.progress));
		this.monitor.setProgress(this.progress);
	}
	
}