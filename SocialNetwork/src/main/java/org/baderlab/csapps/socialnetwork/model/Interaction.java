package main.java.org.baderlab.csapps.socialnetwork.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.model.academia.Author;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Copublications;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.Publication;

/**
 * This class is used to create maps that will
 * later on function as building blocks for
 * networks.
 * @author Victor Kofia
 */
public class Interaction {
	
	/**
	 * Abstract map
	 * <br>Key: <i>Consortium</i>
	 * <br>Value: <i>Interaction</i>
	 */
	private Map<Collaboration, ArrayList<AbstractEdge>> map = null;
	
	/**
	 * Create new interaction
	 * @param List<AbstractEdge> edgeList
	 * @param Integer type
	 * @return null
	 */
	public Interaction(List<? extends AbstractEdge> edgeList, int type) {
		switch(type) {
			case Category.PUBMED:
				this.setAbstractMap(this.loadAcademiaMap(edgeList));
				break;
			case Category.ACADEMIA:
				this.setAbstractMap(this.loadAcademiaMap(edgeList));
				break;
		}
	}
	
	/**
	 * Get abstract map. Keys are all distinct consortiums found in map.
	 * Values are the various interactions that each individual consortium 
	 * shares.
	 *<br>Key: <i>Consortium</i>
	 * <br>Value: <i>Interaction</i>
	 * @param null
	 * @return Map abstractMap
	 */
	public Map<Collaboration, ArrayList<AbstractEdge>> getAbstractMap() {
		return this.map;
	}
	
	/**
	 * Set abstract map. Keys are all distinct consortiums found in map.
	 * Values are the various interactions that each individual consortium 
	 * shares.
	 *<br>Key: <i>Consortium</i>
	 * <br>Value: <i>Interaction</i>
	 * @param null
	 * @return Map abstractMap
	 */
	private void setAbstractMap(Map<Collaboration, ArrayList<AbstractEdge>> abstractMap) {
		this.map = abstractMap;
	}
		
	
	/**
	 * Load new abstract, consortium & edgeList hash-map 
	 * @param ArrayList abstractEdgeList
	 * @return Map abstractMap
	 */
	private Map<Collaboration, ArrayList<AbstractEdge>> 
	    loadAbstractMap(List<? extends AbstractEdge> abstractEdgeList) {
		// Create new map
		Map<Collaboration, ArrayList<AbstractEdge>> abstractMap = new 
				        HashMap<Collaboration, ArrayList<AbstractEdge>>();
		// Iterate through each edge
		for (AbstractEdge edge : abstractEdgeList) {
			int i = 0, j = 0;
			Collaboration consortium = null;
			ArrayList<AbstractEdge> edgeList = null;
			AbstractNode node1 = null;
			AbstractNode node2 = null;
			// Link each node to a consortium consisting of all the other nodes
			// it is attached to via this edge
			while (i < edge.getNodes().size()) {
				node1 = edge.getNodes().get(i);
				j = i + 1;
				while (j < edge.getNodes().size()) {
					node2 = edge.getNodes().get(j);
					consortium = new Collaboration(node1, node2);
					// Check for consortium's existence before 
					// it's entered into map
					if (! abstractMap.containsKey(consortium)) {
						edgeList = new ArrayList<AbstractEdge>();
						edgeList.add(edge);
						abstractMap.put(consortium, edgeList);
					} else {
						abstractMap.get(consortium).add(edge);
					}
					j += 1;
				}
				i += 1;
			}
		}
		return abstractMap;
	}
	
	/**
	 * Create new Academia hash-map 
	 * @param facultyName 
	 * @param ArrayList abstractEdgeList
	 * @return Map academiaMap
	 */
	private Map<Collaboration, ArrayList<AbstractEdge>> loadAcademiaMap(List<? extends AbstractEdge> results) {		
		// Create new academia map
		Map<Collaboration, ArrayList<AbstractEdge>> academiaMap = 
				      new HashMap<Collaboration, ArrayList<AbstractEdge>>();
		// Create new author map 
		// Key: author's facsimile
		// Value: actual author
		Map<Author, Author> authorMap = new HashMap<Author, Author>();
		int h = 0, i = 0, j = 0;
		Collaboration consortium = null;
		Author author1 = null, author2 = null;
		Copublications copublications = null;
		Publication publication = null;
		// Iterate through each publication
		while (h <= results.size() - 1) {
			i = 0;
			j = 0;
			consortium = null;
			author1 = null;
			author2 = null;
			copublications = null;
			publication = (Publication) results.get(h);
			while (i < publication.getNodes().size()) {
				// Add author#1 to map if he / she is not present
				author1 = (Author) publication.getNodes().get(i);
				if (authorMap.get(author1) == null) {
					authorMap.put(author1, author1);
				} 
				// Get author#1 from map and update his / her times cited value with
				// the one registered in publication
				authorMap.get(author1).addTimesCited(publication);
				j = i + 1;
				while (j < publication.getNodes().size()) {
					// Add author#2 to map if he / she is not present
					author2 = (Author) publication.getNodes().get(j);
					if (authorMap.get(author2) == null) {
						authorMap.put(author2, author2);
					}
					// Create consortium out of both authors
					consortium = new Collaboration(authorMap.get(author1), authorMap.get(author2));
					// Check for consortium's existence before it's entered into map
					if (! academiaMap.containsKey(consortium)) {
						copublications = new Copublications(consortium, (Publication) publication);
						ArrayList<AbstractEdge> edgeList = new ArrayList<AbstractEdge>();
						edgeList.add(copublications);
						academiaMap.put(consortium, edgeList);
					} else {
						ArrayList<AbstractEdge> array = academiaMap.get(consortium);
						copublications = (Copublications) array.get(0);
						copublications.addPublication((Publication) publication);
					}
					j++;
				}
				i++;
			}
			if (publication.isSingleAuthored()) {
				// AlreadyCounted variable has to be set to false
				// to allow author's future citations to be 
				// properly registered
				authorMap.get(author1).setAlreadyBeenCounted(false);
			}
			h++;
		}

		
		return academiaMap;
	}

}