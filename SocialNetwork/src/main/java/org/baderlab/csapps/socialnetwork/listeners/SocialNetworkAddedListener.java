package main.java.org.baderlab.csapps.socialnetwork.listeners;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.SocialNetwork;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;

public class SocialNetworkAddedListener implements NetworkAddedListener {
	
	/**
	 * Get smallest value constrained by the desired
	 * cut-off point.
	 * @param List list
	 * @param Double cutoff
	 * @return value
	 */
	private int getSmallestInCutoff(ArrayList<Integer> list, Double cutoff) {
		
		Collections.sort(list);
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (int value : list) {
			stats.addValue(value);
		}
		
		Double percentile = stats.getPercentile(cutoff);
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) >= percentile) {
//				System.out.println("The cutoff for is " + Double.toString(cutoff));
//				System.out.println("The smallest in the list is " + Integer.toString(list.get(i)));
				return list.get(i);
			}
		}
		
		return list.get(0);
		
	}
	
	/**
	 * Get largest value constrained by the desired cut-off
	 * point
	 * @param List list
	 * @param Double cutoff
	 * @return value
	 */
	private int getLargestInCutoff(ArrayList<Integer> list, Double cutoff) {
		
		Collections.sort(list);
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (int value : list) {
			stats.addValue(value);
		}
		
		Double percentile = stats.getPercentile(cutoff);
		
		for (int i = list.size() - 1; i > -1; i--) {
			if (list.get(i) <= percentile) {
				return list.get(i);
			}
		}
		
		return list.get(list.size() - 1);
		
	}
	
	public void handleEvent(NetworkAddedEvent event) {
		// Set cursor to default (network's already been loaded)
        Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		String name = Cytoscape.getNetworkName(event.getNetwork());
		// If the network being added is a social network, then
		// add it to network table
		if (Cytoscape.getSocialNetworkMap().containsKey(name)) {
			
			// Add to network table
			UserPanel.addToNetworkPanel(event.getNetwork());
			
			// Configure network visual styles
			
			// NOTE: Might be resource intensive. Create a new thread?
			SocialNetwork socialNetwork = Cytoscape.getSocialNetworkMap().get(name);
			int networkID = socialNetwork.getNetworkType();
			switch (networkID) {
				case Category.INCITES:
					
					// Specify NODE_LABEL
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, 
							new Object[] {"Last Name"});
					
					
					// Specify EDGE_WIDTH
					CyTable edgeTable = event.getNetwork().getDefaultEdgeTable();
					CyColumn copubColumn = edgeTable.getColumn("# of copubs");
					ArrayList<Integer> copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
					int minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
					int maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, 
							new Object[] {"# of copubs", minEdgeWidth + 1, maxEdgeWidth});
					
					
					// Specify Node_SIZE
					CyTable nodeTable = event.getNetwork().getDefaultNodeTable();
					CyColumn timesCitedColumn = nodeTable.getColumn("Times Cited");
					ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
					int minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
					int maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
							new Object[] {"Times Cited", minNodeSize + 1, maxNodeSize});
					
					
					// Specify EDGE_TRANSPARENCY
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, 
							                                     new Object[] {"# of copubs"});
					
					
					// Specify NODE_FILL_COLOR
					Map<String, HashMap<String, Color>> colorAttrMap = new HashMap<String, HashMap<String, Color>>();
					HashMap<String, Color> locationsMap = new HashMap<String, Color>();
					locationsMap.put("Ontario", new Color(255,137,41));
					locationsMap.put("Canada", new Color(255,13,35));
					locationsMap.put("United States", new Color(42,78,222));
					locationsMap.put("International", new Color(42,230,246));
					colorAttrMap.put("Location", locationsMap);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_FILL_COLOR, 
						                              new Object[] {colorAttrMap});
					
					// Specify NODE_SHAPE
					Map<String, HashMap<String, NodeShape>> shapeAttrMap = new HashMap<String, HashMap<String, NodeShape>>();
					HashMap<String, NodeShape> institutionsMap = new HashMap<String, NodeShape>();
					institutionsMap.put("UNIV TORONTO", NodeShapeVisualProperty.TRIANGLE);
					shapeAttrMap.put("Institution", institutionsMap);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SHAPE, 
                            new Object[] {shapeAttrMap});
					
					break;
			}
			
			Cytoscape.setCurrentlySelectedSocialNetwork(socialNetwork);
			
		}
	}
}