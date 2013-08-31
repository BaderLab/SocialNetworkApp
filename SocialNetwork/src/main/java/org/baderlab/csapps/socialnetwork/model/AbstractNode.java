package main.java.org.baderlab.csapps.socialnetwork.model;

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
	 * CyNode label
	 */
	protected String label;
	
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
	
	/**
	 * Get label
	 * @param null
	 * @return String label
	 */
	public abstract String getLabel();
	
	/**
	 * Set label
	 * @param String label
	 * @return null
	 */
	public abstract void setLabel(String label);
	
}