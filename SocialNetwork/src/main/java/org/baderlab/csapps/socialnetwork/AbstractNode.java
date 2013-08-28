package main.java.org.baderlab.csapps.socialnetwork;

import java.util.Map;

import org.cytoscape.model.CyNode;

/**
 * A Cytoscape node
 * @author Victor Kofia
 */
public abstract class AbstractNode {
	
	/**
	 * CyNode reference
	 */
	protected CyNode cyNode;
	
	/**
	 * True iff node is in a group
	 */
	public boolean isGrouped;
	
	/**
	 * A map containing all of node's attributes
	 */
	protected Map<String, Object> nodeAttrMap;
	
	/**
	 * Return map containing all of node's attributes
	 * @param null
	 * @return Map attrMap
	 */
	public abstract Map<String, Object> getNodeAttrMap();
	
	/**
	 * Set map containing all of node's attributes
	 * @param Map attrMap
	 * @return null
	 */
	public abstract void setNodeAttrMap(Map<String, Object> attrMap);

//	/**
//	 * Get node's group. If node doesn't belong to a
//	 * group null will be returned.
//	 * @param null
//	 * @return AbstractGroup group
//	 */
//	public abstract Group getGroup();
//
//	/**
//	 * Set node's group
//	 * @param Group group
//	 * @return null
//	 */
//	public abstract void setGroup(Group group);
	
	/**
	 * Get CyNode
	 * @param null
	 * @return CyNode cyNode
	 */
	public abstract CyNode getCyNode();
	
	/**
	 * Set CyNode
	 * @param CyNode cyNode
	 * @return null
	 */
	public abstract void setCyNode(CyNode cyNode); 
	
}