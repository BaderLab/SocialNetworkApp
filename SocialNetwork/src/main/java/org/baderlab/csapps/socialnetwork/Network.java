package main.java.org.baderlab.csapps.socialnetwork;

import org.cytoscape.model.CyNetwork;

public class Network {
	/**
	 * # of nodes in network
	 */
	private int nodeCount = 0;
	/**
	 * # of edges in network
	 */
	private int edgeCount = 0;
	/**
	 * Network type
	 */
	private String type = null;
	/**
	 * Network name
	 */
	private String name = null;
	/**
	 * Network reference
	 */
	private CyNetwork networkRef = null;
	/**
	 * Raw network type
	 */
	private int rawType = Category.DEFAULT;
	
	/**
	 * Create a new network
	 * @param null
	 * @return null
	 */
	public Network(String name, CyNetwork networkRef, int type) {
		this.name = name;
		this.networkRef = networkRef;
		this.nodeCount = networkRef.getNodeCount();
		this.edgeCount = networkRef.getEdgeCount();
		switch (type) {
			case Category.ACADEMIA:
				this.type = "Academia";
				break;
			case Category.TWITTER:
				this.type = "Twitter";
				break;
			case Category.LINKEDIN:
				this.type = "LinkedIn";
				break;
			case Category.YOUTUBE:
				this.type = "Youtube";
				break;
		}
		this.rawType = type;
	}

	/**
	 * Get column names
	 * @param null
	 * @return Object[] columnNames
	 * @return
	 */
	public Object[] getColumnNames() {
		return new Object[] {"Name", "Node Count", "Edge Count", " Network Type"};
	}
	
	/**
	 * Get network attribute array (for use in JTable)
	 * @param null
	 * @return String[] networkAttrArray
	 */
	public Object[] getNetworkAttrArray() {
		return new Object[] { this.name, this.nodeCount, this.edgeCount, this.type};
	}
	
	/**
	 * Get visual style selector raw type
	 * @param null
	 * @return int rawType
	 */
	public int getRawVisualStyleSelectorType() {
		return this.rawType;
	}
	
	/**
	 * Get string version of visual style selector type
	 * @param null
	 * @return String visualStyleSelectorType
	 */
	public String getVisualStyleSelectorType() {
		return this.type;
	}
	
	/**
	 * Get network name
	 * @param null
	 * @return String name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get network ref
	 * @param null
	 * @return CyNetwork networkRef
	 */
	public CyNetwork getNetworkRef() {
		return this.networkRef;
	}
	
}
