package main.java.org.baderlab.csapps.socialnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

/**
 * A Cytoscape group
 * @author Victor Kofia
 */
public class Group {
	
	private static CyGroupFactory cyGroupFactoryServiceRef = null;
	
	/**
	 * Get CyGroup Factory Service Reference
	 * @param null
	 * @return CyGroup cyGroupFactoryServiceRef
	 */
	public static CyGroupFactory getCyGroupFactoryService() {
		return Group.cyGroupFactoryServiceRef;
	}
	
	/**
	 * Set CyGroup Factory Service Reference
	 * @param CyGroup cyGroupFactoryServiceRef
	 * @return null
	 */
	public static void setCyGroupFactoryService(CyGroupFactory cyGroupFactoryServiceRef) {
		Group.cyGroupFactoryServiceRef = cyGroupFactoryServiceRef;
	}
	
	private int groupType = Category.DEFAULT;
	
	/**
	 * A list of all the CyNodes that group encapsulates
	 */
	private List<CyNode> cyNodeList = null;
	
	/**
	 * A list of all of group's internal edges
	 */
	private List<CyEdge> internalCyEdgeList = null;
	
	/**
	 * A list of all of group's external edges
	 */
	private List<CyEdge> externalCyEdgeList = null;
	
	/**
	 * The network that this group belongs to
	 */
	private CyNetwork network = null;
		
	/**
	 * The node attr of interest in this group
	 */
	private String nodeAttrOfInterest = null;
	
	/**
	 * The edge attr of interest in this group
	 */
	private String edgeAttrOfInterest = null;
	
	/**
	 * A list of all the abstract nodes that group encapsulates
	 */
	private List<AbstractNode> abstractNodeList = null;
	
	/**
	 * A list of all the abstract edges that group encapsulates
	 */
	private List<AbstractEdge> abstractEdgeList = null;
	
	/**
	 * True iff group has already been collapsed
	 */
	private boolean isCollapsed = false;
	
	
	/**
	 * SUID for the group node
	 */
	private long nodeSUID = Category.DEFAULT;

	/**
	 * Create a new group
	 * @param CyNetwork network
	 * @param int type
	 */
	public Group(CyNetwork network, int type) {
		this.setNetwork(network);
		this.setGroupType(type);
		this.setAbstractNodeList(new ArrayList<AbstractNode>());
		this.setAbstractEdgeList(new ArrayList<AbstractEdge>());
		this.setCyNodeList(new ArrayList<CyNode>());
		switch (type) {
			case Category.INCITES:
				this.setNodeAttrOfInterest("Times Cited");
				this.setEdgeAttrOfInterest("# of copubs");
				break;
		}
	}
	
	/**
	 * Add edges
	 * @param ArrayList edgeList
	 * @return null
	 */
	public void addEdges(List<? extends AbstractEdge> edgeArray) {
		this.getAbstractEdgeList().addAll(edgeArray);
	}
	
	/**
	 * Add node
	 * @param AbstractNode node
	 * @return null
	 */
	public void addNode(AbstractNode node) {
		if (! this.getAbstractNodeList().contains(node)) {
			this.getAbstractNodeList().add(node);
			this.getCyNodeList().add(node.getCyNode());
		}
	}
	
	/**
	 * Collapse group
	 * @param null
	 * @return null
	 */
	private void collapseGroup() {
		CyGroup group = Group.getCyGroupFactoryService().createGroup(this.getNetwork(), true);
		group.addNodes(this.getCyNodeList());
		group.collapse(this.getNetwork());
		this.setNodeSUID(group.getGroupNode().getSUID());
		this.setInternalCyEdgeList(group.getInternalEdgeList());
		ArrayList<CyEdge> externalCyEdgeList = new ArrayList<CyEdge>();
		for (Object edgeObject : group.getExternalEdgeList().toArray()) {
			externalCyEdgeList.add((CyEdge) edgeObject);
		}
		this.setExternalCyEdgeList(externalCyEdgeList);
		this.isCollapsed = true;
	}
	
	/**
	 * @return the abstractEdgeList
	 */
	public List<AbstractEdge> getAbstractEdgeList() {
		return abstractEdgeList;
	}
	
	/**
	 * @return the abstractNodeList
	 */
	public List<AbstractNode> getAbstractNodeList() {
		return abstractNodeList;
	}
	
	/**
	 * Get internal edge list
	 * @param null
	 * @return ArrayList internalCyedgeList
	 */
	public List<CyEdge> getInternalCyEdgeList() {
		return this.internalCyEdgeList;
	}
	
	/**
	 * Get node list
	 * @param null
	 * @return ArrayList nodeList
	 */
	public List<CyNode> getCyNodeList() {
		return this.cyNodeList;
	}

	/**
	 * @return the edgeAttrOfInterest
	 */
	public String getEdgeAttrOfInterest() {
		return edgeAttrOfInterest;
	}
	
	/**
	 * Get group type
	 * @param null
	 * @return int groupType
	 */
	public int getGroupType() {
		return this.groupType;
	}


	/**
	 * Get network
	 * @param null
	 * @return CyNetwork network
	 */
	public CyNetwork getNetwork() {
		return this.network;
	}
	
	/**
	 * Update the group's edge attributes
	 * @param null
	 * @return null
	 */
	public void updateEdgeAttributes() {
		// Collapse group first
		if (! this.isCollapsed) {
			this.collapseGroup();
		}
		
		// Get column names
		CyTable edgeTable = this.getNetwork().getDefaultEdgeTable();
		ArrayList<String> columns = new ArrayList<String>();
		CyColumn column = null;
		for (Object object : edgeTable.getColumns().toArray()) {
			column = (CyColumn) object;
			columns.add(column.getName());
		}
		
		Map<CyNode, CyEdge> nodeToEdgeMap = new HashMap<CyNode, CyEdge>();
				
		for (CyEdge externalEdge : this.getExternalCyEdgeList()) {
			nodeToEdgeMap.put(externalEdge.getSource(), externalEdge);
		}
		
		CyNode targetNode = null, sourceNode = null;
		Object valueOfInterest = null;
		CyEdge externalEdge = null;
		
		for (CyEdge internalEdge : this.getInternalCyEdgeList()) {
			sourceNode = internalEdge.getSource();
			targetNode = internalEdge.getTarget();
			if (nodeToEdgeMap.containsKey(sourceNode)) {
				externalEdge = nodeToEdgeMap.get(sourceNode);
				valueOfInterest = edgeTable.getRow(externalEdge.getSUID()).getRaw(this.edgeAttrOfInterest);
				if (valueOfInterest == null) {
					Object value = null;
					for (String col : columns) {
						value = edgeTable.getRow(internalEdge.getSUID()).getRaw(col);
						edgeTable.getRow(externalEdge.getSUID()).set(col, value);
					}
				} else {
					int value = (Integer) valueOfInterest +
							    (Integer) edgeTable.getRow(internalEdge.getSUID()).getRaw(this.getEdgeAttrOfInterest());
					edgeTable.getRow(externalEdge.getSUID()).set(this.edgeAttrOfInterest, value);
				}
			} else if (nodeToEdgeMap.containsKey(targetNode)) {
				externalEdge = nodeToEdgeMap.get(targetNode);
				valueOfInterest = edgeTable.getRow(externalEdge.getSUID()).getRaw(this.edgeAttrOfInterest);
				if (valueOfInterest == null) {
					Object value = null;
					for (String col : columns) {
						value = edgeTable.getRow(internalEdge.getSUID()).getRaw(col);
						edgeTable.getRow(externalEdge.getSUID()).set(col, value);
					}
				} else {
					int value = (Integer) valueOfInterest +
						    (Integer) edgeTable.getRow(internalEdge.getSUID()).getRaw(this.getEdgeAttrOfInterest());
					edgeTable.getRow(externalEdge.getSUID()).set(this.edgeAttrOfInterest, value);
				}
			}
 		}
	}

	/**
	 * Return map containing the group node's attributes
	 * @param null
	 * @return Map attrMap
	 */
	public Map<Object[], Object> getNodeAttrMap() {
		// Collapse group first
		if (! this.isCollapsed) {
			this.collapseGroup();
		}
		updateEdgeAttributes();
		Map<Object[], Object> nodeAttrMap = new HashMap<Object[], Object>();
		AbstractNode abstractNode = null;
		// Update node attribute of interest (i.e. Times Cited)
		int attribute = 0, i = 0;
		while (i < this.getAbstractNodeList().size()) {
			abstractNode = this.getAbstractNodeList().get(i);
			attribute += (Integer) abstractNode.getNodeAttrMap().get(this.getNodeAttrOfInterest());
			i++;
		}
		abstractNode.getNodeAttrMap().put(this.getNodeAttrOfInterest(), attribute);
		// Add all node attributes along with attribute of interest to
		// group node attributes
		for (Entry<String, Object> attr : abstractNode.getNodeAttrMap().entrySet()) {
			nodeAttrMap.put(new Object[] {this.getNodeSUID(), attr.getKey()}, attr.getValue());
		}
		return nodeAttrMap;
	}

	/**
	 * Get node attribute of interest
	 * @param null
	 * @return String nodeAttrOfInterest
	 */
	public String getNodeAttrOfInterest() {
		return nodeAttrOfInterest;
	}

	/**
	 * Get node SUID
	 * @param null
	 * @return long nodeSUID
	 */
	public long getNodeSUID() {
		return this.nodeSUID;
	}

	/**
	 * Set abstract edge list
	 * @param List abstractEdgeList 
	 * @return null
	 */
	public void setAbstractEdgeList(List<AbstractEdge> abstractEdgeList) {
		this.abstractEdgeList = abstractEdgeList;
	}

	/**
	 * Set abstract node list
	 * @param List abstractNodeList 
	 * @return null
	 */
	public void setAbstractNodeList(List<AbstractNode> abstractNodeList) {
		this.abstractNodeList = abstractNodeList;
	}

	/**
	 * Set internal edge list
	 * @param ArrayList internalCyEdgeList
	 * @return null
	 */
	public void setInternalCyEdgeList(List<CyEdge> internalCyEdgeList) {
		this.internalCyEdgeList = internalCyEdgeList;
	}

	/**
	 * Set node list
	 * @param ArrayList nodeList
	 * @return null
	 */
	public void setCyNodeList(List<CyNode> nodeList) {
		this.cyNodeList = nodeList;
	}

	/**
	 * @param edgeAttrOfInterest the edgeAttrOfInterest to set
	 */
	public void setEdgeAttrOfInterest(String edgeAttrOfInterest) {
		this.edgeAttrOfInterest = edgeAttrOfInterest;
	}

	/**
	 * Set group type
	 * @param int groupType
	 * @return null
	 */
	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	/**
	 * Set network
	 * @param Network network
	 * @return null
	 */
	public void setNetwork(CyNetwork network) {
		this.network = network;
	}

	/**
	 * @param String nodeAttrOfInterest
	 */
	public void setNodeAttrOfInterest(String nodeAttrOfInterest) {
		this.nodeAttrOfInterest = nodeAttrOfInterest;
	}


	/**
	 * Set node SUID
	 * @param long nodeSUID
	 * @return null
	 */
	public void setNodeSUID(long nodeSUID) {
		this.nodeSUID = nodeSUID;
	}

	/**
	 * Get external edge list
	 * @param null
	 * @return ArrayList externalCyEdgeList
	 */
	public List<CyEdge> getExternalCyEdgeList() {
		return externalCyEdgeList;
	}

	/**
	 * Set external edge list
	 * @param ArrayList externalCyEdgeList
	 * @return null
	 */
	public void setExternalCyEdgeList(List<CyEdge> externalCyEdgeList) {
		this.externalCyEdgeList = externalCyEdgeList;
	}

}
