package org.baderlab.csapps.socialnetwork.model;

import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;

/**
 * A Cytoscape edge
 * @author Victor Kofia
 */
public abstract class AbstractEdge {
	
	/**
	 * CyEdge reference
	 */
	protected CyEdge cyEdge;
	
	/**
	 * A map containing all of edge's attributes
	 */
	protected Map<String, Object> edgeAttrMap;
		
	/**
	 * Return map containing all of edge's attributes
	 * @param null
	 * @return Map attrMap
	 */
	public abstract Map<String, Object> getEdgeAttrMap();
	
	/**
	 * Construct an attribute map for edge
	 * @param null
	 * @return null
	 */
	public abstract void constructEdgeAttrMap();
	
	/**
	 * Return all nodes attached to edge
	 * @param null
	 * @return List nodes
	 */
	public abstract List<? extends AbstractNode> getNodes();
	
	/**
	 * Get CyEdge
	 * @param null
	 * @return CyEdge cyEdge
	 */
	public abstract CyEdge getCyEdge();
	
	/**
	 * Set CyEdge
	 * @param CyEdge cyEdge
	 * @return null
	 */
	public abstract void setCyEdge(CyEdge cyEdge); 

	
}
