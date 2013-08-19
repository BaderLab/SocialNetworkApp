package main.java.org.baderlab.csapps.socialnetwork;

import java.util.Map;

/**
 * A Cytoscape node
 * @author Victor Kofia
 */
public abstract class AbstractNode {
	
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
	
}