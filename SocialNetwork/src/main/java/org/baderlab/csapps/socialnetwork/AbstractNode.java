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
	protected Map<String, String> attrMap;
	
	/**
	 * Return map containing all of node's attributes
	 * @param null
	 * @return Map attrMap
	 */
	public abstract Map<String, String> getAttrMap();
	
	/**
	 * Construct an attribute map for node
	 * @param null
	 * @return null
	 */
	public abstract void constructAttrMap();
	
}