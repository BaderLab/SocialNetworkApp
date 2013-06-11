package main.java.org.baderlab.csapps.socialnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interaction 
 * @author Victor Kofia
 */
public class Interaction {
	
	/**
	 * Create new consortium & edgeList hash-map 
	 * @param ArrayList authorListNoDuplicates
	 * @param ArrayList publicationList
	 * @return 
	 * @return null
	 */
	private static Map<Consortium, ArrayList<AbstractEdge>> loadMap(List<? extends AbstractEdge> edgeList) {
		// Create new map
		Map<Consortium, ArrayList<AbstractEdge>> map = new HashMap<Consortium, ArrayList<AbstractEdge>>();
		// Iterate through each publication
		for (AbstractEdge edge : edgeList) {
			int i = 0, j = 0;
			Consortium consortium = null;
			ArrayList<AbstractEdge> nodeList = null;
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
					if (! map.containsKey(consortium)) {
						nodeList = new ArrayList<AbstractEdge>();
						nodeList.add(edge);
						map.put(consortium, nodeList);
					} else {
						map.get(consortium).add(edge);
					}
					j += 1;
				}
				i += 1;
			}
		}
		return map;
	}
		
	
	/**
	 * Return map. Keys are all distinct consortiums found in map.
	 * Values are the various interactions that each individual consortium has had.
	 * @param null
	 * @return Map consortiumEdgeMap
	 */
	public static Map<Consortium, ArrayList<AbstractEdge>> getMap(List<? extends AbstractEdge> edgeList) {
		return Interaction.loadMap(edgeList);
	}

}
