package main.java.org.baderlab.csapps.socialnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.academia.Author;
import main.java.org.baderlab.csapps.socialnetwork.academia.Copublications;
import main.java.org.baderlab.csapps.socialnetwork.academia.Publication;

/**
 * This class is used to create maps that will
 * later on function as building blocks for
 * networks.
 * @author Victor Kofia
 */
public class Interaction {	
	
	/**
	 * Get abstract map. Keys are all distinct consortiums found in map.
	 * Values are the various interactions that each individual consortium 
	 * shares.
	 * @param List edgeList
	 * @return Map abstractMap
	 */
	public static Map<Consortium, ArrayList<AbstractEdge>> 
	              getAbstractMap(List<? extends AbstractEdge> edgeList) {
		return Interaction.loadAbstractMap(edgeList);
	}
		
	/**
	 * Get Academia map.
	 * @param List pubList
	 * @param String facultyName
	 * @param HashSet facultySet
	 * @return Map academiaMap
	 * <br><i>Key: Consortium</i>
	 * <br><i>Value: Co-publication</i>
	 */
	public static Map<Consortium, ArrayList<AbstractEdge>> 
	              getAcademiaMap(List<? extends AbstractEdge> results,
								String facultyName, HashSet<Author> facultySet) {
		return Interaction.loadAcademiaMap(results, facultyName, facultySet);
	}
	
	/**
	 * Create a new abstract, consortium & edgeList hash-map 
	 * @param ArrayList abstractEdgeList
	 * @return Map abstractMap
	 */
	private static Map<Consortium, ArrayList<AbstractEdge>> 
	     loadAbstractMap(List<? extends AbstractEdge> abstractEdgeList) {
		// Create new map
		Map<Consortium, ArrayList<AbstractEdge>> abstractMap = new 
				          HashMap<Consortium, ArrayList<AbstractEdge>>();
		// Iterate through each edge
		for (AbstractEdge edge : abstractEdgeList) {
			int i = 0, j = 0;
			Consortium consortium = null;
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
					consortium = new Consortium(node1, node2);
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
	private static Map<Consortium, ArrayList<AbstractEdge>> 
	          loadAcademiaMap(List<? extends AbstractEdge> results,
			                  String facultyName, HashSet<Author> facultySet) {		
		// Create new academia map
		Map<Consortium, ArrayList<AbstractEdge>> academiaMap = 
				      new HashMap<Consortium, ArrayList<AbstractEdge>>();
		// Create new author map 
		// Key: author's facsimile
		// Value: original author
		Map<Author, Author> authorMap = new HashMap<Author, Author>();
		int h = 0, i = 0, j = 0;
		Consortium consortium = null;
		Author author1 = null, author2 = null;
		Copublications copublications = null;
		Publication publication = null;
		boolean someFaculty = false;
		// Iterate through each publication
		while (h < results.size() - 1) {
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
					if (facultySet != null && facultySet.contains(author1)) {
						someFaculty = true;
						author1.setFaculty(facultyName);
					}
					authorMap.put(author1, author1);
				} 
				// Get author#1 from map and update his / her times cited value
				authorMap.get(author1).setTimesCited(authorMap.get(author1).getTimesCited() 
						 + (publication.getTimesCited()));
				j = i + 1;
				while (j < publication.getNodes().size()) {
					// Add author#2 to map if he / she is not present
					author2 = (Author) publication.getNodes().get(j);
					if (authorMap.get(author2) == null) {
						if (facultySet != null && facultySet.contains(author2)) {
							someFaculty = true;
							author2.setFaculty(facultyName);
						}
						authorMap.put(author2, author2);
					}
					// Get author#2 from map and update his / her times cited value
					authorMap.get(author2).setTimesCited(authorMap.get(author2).getTimesCited() 
							 + (publication.getTimesCited()));
					// Create consortium out of both authors
					consortium = new Consortium(authorMap.get(author1), authorMap.get(author2));
					// Check for consortium's existence before it's entered into map
					if (! academiaMap.containsKey(consortium)) {
						copublications = new Copublications(consortium, (Publication) publication);
						ArrayList<AbstractEdge> list = new ArrayList<AbstractEdge>();
						list.add(copublications);
						academiaMap.put(consortium, list);
					} else {
						copublications = (Copublications) academiaMap.get(consortium).get(0);
						copublications.addPublication((Publication) publication);
					}
					j++;
				}
				i++;
			}
			h++;
		}
		
		// If network does not require faculty data then don't show error message. 
		// However, if the network does require faculty data, notify the user on
		// whether or not the data's inclusion happened successfully.
		if (facultySet != null && someFaculty == false) {
			Cytoscape.notifyUser("Faculty information could not be loaded into the network.\n");
		}
		
		return academiaMap;
	}

}