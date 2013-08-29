package main.java.org.baderlab.csapps.socialnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

/**
 * A social network
 * @author Victor Kofia
 */
public class SocialNetwork {
	/**
	 * The network's CyNetworkView reference
	 */
	private CyNetworkView cyNetworkViewRef = null;
	/**
	 * The network's name
	 */
	private String networkName = "";
	/**
	 * The network's CyNetwork reference
	 */
	private CyNetwork cyNetwork = null;
	/**
	 * The network's type
	 */
	private int networkType = Category.DEFAULT;
	/**
	 * Visual style map
	 * <br>Key: Object VisualStyle
	 * <br>Value: Object[] {String attrName, int min, int max}
	 */
	private Map<Object, Object[]> visualStyleMap = null;
	/**
	 * The network's default visual style
	 */
	private int defaultVisualStyle = Category.DEFAULT;
	/**
	 * The network's attribute map (stores all network table attr)
	 */
	private Map<String, Object> attrMap = null;
	/**
	 * The network's summary list
	 */
	private ArrayList<Object[]> summaryList = null;
	/**
	 * The network summary (outlines network issues)
	 */
	private String networkSummary = null;
	
	
	/**
	 * Create a new social network
	 * @param String networkName
	 * @param int networkType
	 * @return null
	 */
	public SocialNetwork(String networkName, int networkType) {
		this.setNetworkName(networkName);
		this.setNetworkType(networkType);
		// Set default visual styles
		switch(networkType) {
			case Category.INCITES:
				this.setDefaultVisualStyle(Category.CHIPPED);
				break;
			case Category.SCOPUS:
				this.setDefaultVisualStyle(Category.VANUE);
				break;
			case Category.PUBMED:
				this.setDefaultVisualStyle(Category.VANUE);
				break;
		}
	}

	/**
	 * Get network name
	 * @param null
	 * @return String networkName
	 */
	public String getNetworkName() {
		return networkName;
	}

	/**
	 * Get CyNetwork reference
	 * @param null
	 * @return CyNetwork cyNetwork
	 */
	public CyNetwork getCyNetwork() {
		return this.cyNetwork;
	}

	/**
	 * Get network type
	 * @param null
	 * @return int category
	 */
	public int getNetworkType() {
		return networkType;
	}

	/**
	 * Get network view reference
	 * @param null
	 * @return CyNetworkView cyNetworkViewRef
	 */
	public CyNetworkView getNetworkView() {
		return cyNetworkViewRef;
	}

	/**
	 * Get visual style map
	 * @param null
	 * @return Map visualStyleMap
	 */
	public Map<Object, Object[]> getVisualStyleMap() {
		if (this.visualStyleMap == null) {
			this.setVisualStyleMap(new HashMap<Object, Object[]>());
		}
		return this.visualStyleMap;
	}

	/**
	 * Set network name
	 * @param String networkName
	 * @return null 
	 */
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	/**
	 * Set CyNetwork reference
	 * @param CyNetwork cyNetwork
	 * @return null
	 */
	public void setCyNetwork(CyNetwork cyNetwork) {
		this.cyNetwork = cyNetwork;
	}

	/**
	 * Set network type
	 * @param int networkType
	 * @return null
	 */
	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	/**
	 * Set network view reference
	 * @param CyNetworkView cyNetworkViewRef
	 * @return null
	 */
	public void setNetworkView(CyNetworkView cyNetworkViewRef) {
		this.cyNetworkViewRef = cyNetworkViewRef;
	}

	/**
	 * Set visual style map
	 * @param Map visualStyleMap
	 * @return null
	 */
	public void setVisualStyleMap(Map<Object, Object[]> visualStyleMap) {
		this.visualStyleMap = visualStyleMap;
	}

	/**
	 * Get network's default visual style
	 * @param null
	 * @return int defaultVisualStyle
	 * @return
	 */
	public int getDefaultVisualStyle() {
		return defaultVisualStyle;
	}

	/**
	 * Set network's default visual style
	 * @param int defaultVisualStyle
	 * @return null
	 */
	public void setDefaultVisualStyle(int defaultVisualStyle) {
		this.defaultVisualStyle = defaultVisualStyle;
	}

	/**
	 * Get network attribute map
	 * @param null
	 * @return Map attrMap
	 */
	public Map<String, Object> getAttrMap() {
		if (this.attrMap == null) {
			this.setAttrMap(new HashMap<String, Object>());
		}
		return this.attrMap;
	}

	/**
	 * Set network attribute map
	 * @param Map attrMap
	 * @return null
	 */
	public void setAttrMap(Map<String, Object> attrMap) {
		this.attrMap = attrMap;
	}

	/**
	 * Get summary list
	 * @param null
	 * @return ArrayList summaryList
	 */
	public ArrayList<Object[]> getSummaryList() {
		if (this.summaryList == null) {
			this.setSummaryList(new ArrayList<Object[]>());
		}
		return summaryList;
	}

	/**
	 * Set summary
	 * @param ArrayList summaryList
	 * @return null 
	 */
	private void setSummaryList(ArrayList<Object[]> summary) {
		this.summaryList = summary;
	}

	/**
	 * Get network summary
	 * @param null
	 * @return String summary
	 */
	public String getSummary() {
		if (this.networkSummary == null) {
			String info = "<html>", key = null, value = null;
			for (Object[] summary : this.getSummaryList()) {
				key = (String) summary[0];
				value = (String) summary[1];
				info += key + ": " + value + "<br>";
			}
			this.networkSummary = info + "</html>";
		}
		return this.networkSummary;
	}

	/**
	 * Set network summary
	 * @param String summary
	 * @return null
	 */
	public void setSummary(String summary) {
		this.networkSummary = summary;
	}
	
		
}
