package main.java.org.baderlab.csapps.socialnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.academia.Author;
import main.java.org.baderlab.csapps.socialnetwork.academia.Copublications;
import main.java.org.baderlab.csapps.socialnetwork.academia.Publication;

/**
 * Interaction class creates the map that will
 * eventually be used to build network.
 * @author Victor Kofia
 */
public class Interaction {	
	
	/**
	 * Get abstract map. Keys are all distinct consortiums found in map.
	 * Values are the various interactions that each individual consortium shares.
	 * @param null
	 * @return Map abstractMap
	 */
	public static Map<Consortium, ArrayList<AbstractEdge>> getAbstractMap(List<? extends AbstractEdge> edgeList) {
		return Interaction.loadAbstractMap(edgeList);
	}
		
	/**
	 * Get academia map. Keys are all distinct consortiums found in map.
	 * Values are the copublications associated with these consortiums.
	 * @param null
	 * @return Map academiaMap
	 */
	public static Map<Consortium, ArrayList<AbstractEdge>> getAcademiaMap(List<? extends Publication> pubList) {
		return Interaction.loadAcademiaMap(pubList);
	}
	
	/**
	 * Create a new abstract, consortium & edgeList hash-map 
	 * @param ArrayList abstractEdgeList
	 * @return Map abstractMap
	 */
	private static Map<Consortium, ArrayList<AbstractEdge>> loadAbstractMap(List<? extends AbstractEdge> abstractEdgeList) {
		// Create new map
		Map<Consortium, ArrayList<AbstractEdge>> abstractMap = new HashMap<Consortium, ArrayList<AbstractEdge>>();
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
					// Check for consortium's existence before it's entered into map
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
	 * @param ArrayList abstractEdgeList
	 * @return Map academiaMap
	 */
	private static Map<Consortium, ArrayList<AbstractEdge>> loadAcademiaMap(List<? extends Publication> publicationList) {
		// Create new academia map
		Map<Consortium, ArrayList<AbstractEdge>> academiaMap = new HashMap<Consortium, ArrayList<AbstractEdge>>();
		Map<Author, Author> authorMap = new HashMap<Author, Author>();
		int i = 0, j = 0;
		Consortium consortium = null;
		Author author1 = null, author2 = null;
		Copublications copublications = null;
		// Iterate through each publication
		for (Publication publication : publicationList) {
			i = 0;
			j = 0;
			consortium = null;
			author1 = null;
			author2 = null;
			copublications = null;
			while (i < publication.getNodes().size()) {
				author1 = (Author) publication.getNodes().get(i);
				if (authorMap.get(author1) == null) {
					authorMap.put(author1, author1);
				} 
				// Update time cited for both author#1 and author#2
//				if (author1.getLastName().equalsIgnoreCase("Hanley")) {
//					System.out.println(publication.getNodes());
//					System.out.println("I am author #1");
//					System.out.println("This is the publication I'm reading " + publication.getTitle());
//					System.out.println("This is my times cited before adding " + Integer.toString(authorMap.get(author1).getTimesCited()));
//					System.out.println("This is what I'm about to add " + Integer.toString(publication.getTimesCited()));
//					System.out.println("\n\n");
//				}
				authorMap.get(author1).setTimesCited(authorMap.get(author1).getTimesCited() 
						 + (publication.getTimesCited()));
				j = i + 1;
				while (j < publication.getNodes().size()) {
					author2 = (Author) publication.getNodes().get(j);
					if (authorMap.get(author2) == null) {
						authorMap.put(author2, author2);
					}
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
					j += 1;
				}
				i += 1;
			}
		}
		return academiaMap;
	}
	
}