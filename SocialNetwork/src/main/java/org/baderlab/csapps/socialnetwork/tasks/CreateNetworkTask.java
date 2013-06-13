package main.java.org.baderlab.csapps.socialnetwork.tasks;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import main.java.org.baderlab.csapps.socialnetwork.AbstractEdge;
import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;
import main.java.org.baderlab.csapps.socialnetwork.Consortium;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;

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
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.xml.sax.SAXException;

/**
 * Create a new network
 * @author Victor Kofia
 */
public class CreateNetworkTask extends AbstractTask {
	private CyNetworkFactory cyNetworkFactoryServiceRef;
	private CyNetworkViewFactory cyNetworkViewFactoryServiceRef;
	private CyNetworkViewManager cyNetworkViewManagerServiceRef;
	private CyNetworkManager cyNetworkManagerServiceRef;
	private CyNetworkNaming cyNetworkNamingServiceRef;
	private CyLayoutAlgorithmManager cyLayoutManagerServiceRef;
	private VisualMappingManager vmmServiceRef;
	private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
	private VisualStyleFactory visualStyleFactoryServiceRef;
	private int currentSteps = 0;
	private int totalSteps = 0;
	private double progress = 0.0;
	private TaskMonitor monitor = null;

	/**
	 * Create a new network task
	 * @param null
	 * @return null
	 */
	public CreateNetworkTask(CyNetworkNaming cyNetworkNamingServiceRef, CyNetworkFactory cyNetworkFactoryServiceRef, CyNetworkManager cyNetworkManagerServiceRef, 
			CyNetworkViewFactory cyNetworkViewFactoryServiceRef, CyNetworkViewManager cyNetworkViewManagerServiceRef, CyLayoutAlgorithmManager cyLayoutManagerServiceRef, 
			VisualStyleFactory visualStyleFactoryServiceRef, VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingManager vmmServiceRef) {
		this.cyNetworkNamingServiceRef = cyNetworkNamingServiceRef;
		this.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
		this.cyNetworkViewFactoryServiceRef = cyNetworkViewFactoryServiceRef;
		this.cyNetworkViewManagerServiceRef = cyNetworkViewManagerServiceRef;
		this.cyLayoutManagerServiceRef = cyLayoutManagerServiceRef;
		this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
		this.vmmServiceRef = vmmServiceRef;
		this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
	}
	
	/**
	 * Add a label to each node
	 * @param VisualStyle visualStyle
	 * @return  VisualStyle visualStyle
	 */
	public VisualStyle addNodeLabels(VisualStyle visualStyle) {
		// Set node color map to attribute ???
		PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>)
			passthroughMappingFactoryServiceRef.createVisualMappingFunction("Last Name", Integer.class, BasicVisualLexicon.NODE_LABEL);
					
		visualStyle.addVisualMappingFunction(mapping);	
		
		return visualStyle;
	}
	
	/**
	 * Return a network containing author's co-authors and all corresponding publications
	 * @param String authorName
	 * @return CyNetwork network
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public CyNetwork loadNetwork(Map<Consortium, ArrayList<AbstractEdge>> map) throws ParserConfigurationException, SAXException, IOException {
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
		for (String attr : key.getAttrMap().keySet()) {
			nodeTable.createColumn(attr, String.class, false);
		}
		 	    
 	    // Add all columns to edge table
		// WIP: WORK IN PROGRESS
		// ????
 	    edgeTable = myNet.getDefaultEdgeTable();
 	    Object[] values = map.values().toArray();
 	    @SuppressWarnings("unchecked")
		ArrayList<AbstractEdge> values2 = (ArrayList<AbstractEdge>) values[0];
 	    AbstractEdge value = values2.get(0);
 	    for (String attr : value.getAttrMap().keySet()) {
 	    	edgeTable.createColumn(attr, String.class, false);
 	    }
		
		// Set network name
		myNet.getDefaultNetworkTable().getRow(myNet.getSUID())
		.set("name", cyNetworkNamingServiceRef.getSuggestedNetworkTitle("App test"));
		
		// Build network
		Consortium consortium = null;
		AbstractNode node1 = null;
		AbstractNode node2 = null;
		List<? extends AbstractEdge> edgeArray = null;
		CyEdge edgeRef = null;
		CyNode nodeRef = null;
		
		Map<AbstractNode, CyNode> nodeMap  = new HashMap<AbstractNode, CyNode>();
		
		// Get all of author's co-authors and publications
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
				for (Entry<String, String> attr : node1.getAttrMap().entrySet()) {
					nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), attr.getValue());
				}
				nodeMap.put(node1, nodeRef);
			}
			
			if (! nodeMap.containsKey(node2)) {
				nodeRef = myNet.addNode();
				// Add each node attribute to its respective column
				for (Entry<String, String> attr : node2.getAttrMap().entrySet()) {
					nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), attr.getValue());
				}
				nodeMap.put(node2, nodeRef);
			}
			
			for (AbstractEdge edge : edgeArray) {
				edgeRef = myNet.addEdge(nodeMap.get(node1), nodeMap.get(node2), false);
				// Add each edge attribute to it's respective column
				for (Entry<String, String> attr : edge.getAttrMap().entrySet()) {
					edgeTable.getRow(edgeRef.getSUID()).set(attr.getKey(), attr.getValue());
				}
			}
			
			// Set progress
			updateProgress();
			
		}
		return myNet;
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
	 * Run network task
	 * @param TaskMonitor taskMonitor
	 * @return null
	 */
	public void run(TaskMonitor monitor) throws Exception {
		// ??
		CyNetworkManager networkManager = cyNetworkManagerServiceRef;
		CyNetworkViewManager networkViewManager = cyNetworkViewManagerServiceRef;

		// Get map
		Map<Consortium, ArrayList<AbstractEdge>> map = Cytoscape.getMap();
		
		if (map == null) {
			Cytoscape.notifyUser("Map has a value of null. Must be an issue w/ Cytoscape.getMap() or the file or term you're trying to build your network out of is invalid.");
		} else {
			
			//Set monitor parameters
			this.monitor = monitor;
			this.monitor.setTitle("Loading Network");
			this.monitor.setProgress(0.0);
			
			// Total amount of steps to completion has to be set for progress to be tracked
			this.totalSteps = map.size() + 2;

			// Load network
			CyNetwork network = loadNetwork(map);

			if (network == null)
				return;
			networkManager.addNetwork(network);

			final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(network);
			CyNetworkView networkView = null;
			if(views.size() != 0)
				networkView = views.iterator().next();

			if (networkView == null) {
				// Create a new view for my network
				networkView = cyNetworkViewFactoryServiceRef.createNetworkView(network);
				networkViewManager.addNetworkView(networkView);
			} else {
				Cytoscape.notifyUser("Network already present");
			}
			
			// MAJOR STEP: Update progress
			updateProgress();

			// Set the variable destroyView to true, the following snippet of code
			// will destroy a view
			boolean destroyView = false;
			if (destroyView) {
				networkViewManager.destroyNetworkView(networkView);
			}

			// Set visuals
			// create a new visual style
			VisualStyle vs= visualStyleFactoryServiceRef.createVisualStyle("Social-Network-App Visual Style");

			// ??
			addNodeLabels(vs);
			
			updateProgress();

			// Add the new visual style to manager
			vmmServiceRef.setCurrentVisualStyle(vs);

			// Auto apply layout
			CyLayoutAlgorithm layout = cyLayoutManagerServiceRef.getLayout("force-directed");
			String layoutAttribute = null;
			insertTasksAfterCurrentTask(layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, layoutAttribute));
		}
	}
}