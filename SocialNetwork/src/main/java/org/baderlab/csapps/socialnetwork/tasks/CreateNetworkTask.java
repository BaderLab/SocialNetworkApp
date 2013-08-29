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
import main.java.org.baderlab.csapps.socialnetwork.Group;
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
	public CyNetwork loadNetwork(Map<Consortium, ArrayList<AbstractEdge>> map, TaskMonitor taskMonitor) {
		try {
			// Create an empty network 
			CyNetwork myNet = this.cyNetworkFactoryServiceRef.createNetwork();
			// Get network node table
			CyTable nodeTable = null;
			// Get network edge table
			CyTable edgeTable = null;

			// Add all columns to node table
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
			Map<Integer, Group> groupMap = new HashMap<Integer, Group>();

			// Set 'Load Network' progress monitor
			this.setProgressMonitor(taskMonitor, "Loading Network", map.size());

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
					node1.setCyNode(nodeRef);
					// Add each node attribute to its respective column
					for (Entry<String, Object> attr : node1.getNodeAttrMap().entrySet()) {
						nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), 
								attr.getValue());
					}
					nodeMap.put(node1, nodeRef);
				}

				if (! nodeMap.containsKey(node2)) {
					nodeRef = myNet.addNode();
					node2.setCyNode(nodeRef);
					// Add each node attribute to its respective column
					for (Entry<String, Object> attr : node2.getNodeAttrMap().entrySet()) {
						nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), 
								         attr.getValue());
					}
					nodeMap.put(node2, nodeRef);
				}

				for (AbstractEdge edge : edgeArray) {
					edgeRef = myNet.addEdge(nodeMap.get(node1), 
							                nodeMap.get(node2), false);
					edge.setCyEdge(edgeRef);
					// Add each edge attribute to it's respective column
					for (Entry<String, Object> attr : edge.getEdgeAttrMap().entrySet()) {
						edgeTable.getRow(edgeRef.getSUID()).set(attr.getKey(), 
								         attr.getValue());
					}
				}
				
				int networkType = Cytoscape.getSocialNetworkMap().get(Cytoscape.getNetworkName()).getNetworkType();
				int groupID = 0;
				Group group = null;
				
				if (node1.isGrouped) {
					groupID = node1.hashCode();
					group = null;
					if (groupMap.containsKey(groupID)) {
						group = groupMap.get(groupID);
						group.addNode(node1);
					} else {
						group = new Group(myNet, networkType);
						group.addNode(node1);
						groupMap.put(groupID, group);
					}
				}
				
				if (node2.isGrouped) {
					groupID = node2.hashCode();
					group = null;
					if (groupMap.containsKey(groupID)) {
						group = groupMap.get(groupID);
						group.addNode(node2);
					} else {
						group = new Group(myNet, networkType);
						group.addNode(node2);
						groupMap.put(groupID, group);
					}
				}	
				
				updateProgress(taskMonitor);
				
			}
			
			// Set 'Fix Duplicates' progress monitor
			this.setProgressMonitor(taskMonitor, "Fixing Duplicates ...", groupMap.values().size());
			
			for (Group group : groupMap.values()) {
				
				Long row = null;
				String column = null;
				Object[] table = null;
				
				// Add node attributes (Cytoscape groups can only have a single node)
				for (Entry<Object[], Object> attr : group.getNodeAttrMap().entrySet()) {
					table = attr.getKey();
					row = (Long) table[0];
					column = (String) table[1];
					nodeTable.getRow(row).set(column, attr.getValue());
				}
				
//				// Add edge attributes (Cytoscape groups can have multiple edges)
//				for (Entry<Object[], Object> attr : group.getEdgeAttrMap().entrySet()) {
//					table = attr.getKey();
//					row = (Long) table[0];
//					column = (String) table[1];
//					nodeTable.getRow(row).set(column, attr.getValue());
//				}
				
				this.updateProgress(taskMonitor);
				
			}

			return myNet;
		} catch (ArrayIndexOutOfBoundsException exception) {
			exception.printStackTrace();
			Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Cytoscape.notifyUser("Network could not be loaded. Array Index Out Of Bounds.");
			Cytoscape.getSocialNetworkMap().remove(Cytoscape.getNetworkName());
			Cytoscape.setNetworkName(null);
			taskMonitor = null;
			return null;
		}
	}
	
	/**
	 * Run network task
	 * @param TaskMonitor taskMonitor
	 * @return null
	 */
	public void run(TaskMonitor monitor) throws Exception {
		CyNetworkManager networkManager = this.cyNetworkManagerServiceRef;
		CyNetworkViewManager networkViewManager = this.cyNetworkViewManagerServiceRef;
		
		// Get map
		Map<Consortium, ArrayList<AbstractEdge>> map = Cytoscape.getMap();
				
		if (map == null) {
			Cytoscape.notifyUser
			("Network could not be loaded. Cytoscape network map could not be accessed.");
		} else {
									
			// Load network
			CyNetwork network = loadNetwork(map, monitor);
						
			if (network == null) {
				return;
			}
			
			this.setProgressMonitor(monitor, "Loading Network View ...", -1);
						
			networkManager.addNetwork(network);
			
			final Collection<CyNetworkView> views = networkViewManager
					                                .getNetworkViews(network);
						
			CyNetworkView networkView = null;
			if(views.size() != 0) {
				networkView = views.iterator().next();
			}
			if (networkView == null) {
				// Create a new view for my network
				networkView = this.cyNetworkViewFactoryServiceRef
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
			socialNetwork.setNetworkView(networkView);
			
			// Auto apply visual style and layout
			CyLayoutAlgorithm layout = this.cyLayoutManagerServiceRef
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
	private void updateProgress(TaskMonitor taskMonitor) {
		this.currentSteps += 1;
		this.progress = (double)this.currentSteps / this.totalSteps;
		taskMonitor.setStatusMessage("Complete: " + toPercent(this.progress));
		taskMonitor.setProgress(this.progress);
	}
	
	/**
	 * Set progress monitor 
	 * @param TaskMonitor taskMonitor
	 * @param String taskName
	 * @param int totalSteps
	 * @return null
	 */
	private void setProgressMonitor(TaskMonitor taskMonitor, 
			                        String taskName, 
			                        int totalSteps) {
		taskMonitor.setTitle(taskName);
		taskMonitor.setProgress(0.0);
		this.currentSteps = 0;
		this.totalSteps = totalSteps;
	}
	
}