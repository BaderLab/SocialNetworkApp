package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.AbstractEdge;
import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;
import main.java.org.baderlab.csapps.socialnetwork.Consortium;

/**
 * A co-publication
 * @author Victor Kofia
 */
public class Copublications extends AbstractEdge {
	/**
	 * List of co-publications
	 */
	private ArrayList<Publication> pubList = null;
	/**
	 * An edge attribute map
	 * <br> Keys: <i>Attribute type (i.e. name)</i>
	 * <br> Value: <i>Attribute value (i.e. Cytoscape app store)</i>
	 */
	private Map<String, Object> edgeAttrMap = null;
	
	/**
	 * Get total # of co-publications
	 * @param null
	 * @return int totalPubs
	 */
	public int getTotalPubs() {
		return this.getPubList().size();
	}
	
	/**
	 * Set publist
	 * @param ArrayList pubList
	 * @return null
	 */
	private void setPubList(ArrayList<Publication> pubList) {
		this.pubList = pubList;
	}
	
	/**
	 * Get publist
	 * @param null
	 * @return ArrayList pubList
	 */
	private ArrayList<Publication> getPubList() {
		return this.pubList;
	}
 	
	/**
	 * Create a new co-publications tracker for consortium.
	 *@param Consortium consortium
	 *@param Publication publication
	 *@return null
	 */
	public Copublications(Consortium consortium, Publication publication) {
		this.setPubList(new ArrayList<Publication>());
		this.getPubList().add(publication);
		this.constructEdgeAttrMap();
	}
	
	/**
	 * Add a publication
	 * @param Publication publication
	 * @return null
	 */
	public void addPublication(Publication publication) {
		this.getPubList().add(publication);
		this.getEdgeAttrMap().put("# of copubs", this.getPubList().size());
		((ArrayList<String>) this.getEdgeAttrMap().get("publications")).add(publication.getTitle());
	}

	/**
	 * Get the edge attribute map
	 * @param null
	 * @return Map edgeAttrMap
	 */
	public Map<String, Object> getEdgeAttrMap() {
		return this.edgeAttrMap;
	}
	
	/**
	 * Set the edge attribute map
	 * @param Map edgeAttrMap
	 * @return null
	 */
	public void setEdgeAttrMap(Map<String, Object> edgeAttrMap) {
		this.edgeAttrMap = edgeAttrMap;
	}

	/**
	 * Construct an edge attribute map
	 * @param null
	 * @return null
	 */
	public void constructEdgeAttrMap() {
		this.setEdgeAttrMap(new HashMap<String, Object>());
		this.getEdgeAttrMap().put("# of copubs", this.getPubList().size());
		ArrayList<String> titles = new ArrayList<String>();
		titles.add(this.getPubList().get(0).getTitle());
		this.getEdgeAttrMap().put("publications", titles);
	}

	/**
	 * NON-FUNCTIONAL ATAVISM. DO NOT USE.
	 * @param null
	 * @return null
	 */
	public List<? extends AbstractNode> getNodes() {
		return null;
	}

}