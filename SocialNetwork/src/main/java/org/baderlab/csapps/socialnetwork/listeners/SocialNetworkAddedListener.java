package org.baderlab.csapps.socialnetwork.listeners;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;

public class SocialNetworkAddedListener implements NetworkAddedListener {
	
	
	private SocialNetworkAppManager appManager = null;
	
	
	
	public SocialNetworkAddedListener(SocialNetworkAppManager appManager) {
		super();
		this.appManager = appManager;
	}

	/**
	 * Get smallest value given cutoff
	 * @param List list
	 * @param Double cutoff
	 * @return Integer value
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
				return list.get(i);
			}
		}
		return list.get(0);
	}
	
	/**
	 * Get largest value given cut-off
	 * point
	 * @param List list
	 * @param Double cutoff
	 * @return Integer value
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
	
	/**
	 * Adds network to network table and configures visual styles
	 * if necessary.
	 * @param NetworkAddedEvent event
	 * @return null
	 */
	public void handleEvent(NetworkAddedEvent event) {
		// Set mouse cursor to default (network's already been loaded)
        this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		String name = this.appManager.getNetworkName(event.getNetwork());
		// If the network being added is a social network, then
		// add it to network table
		if (this.appManager.getSocialNetworkMap().containsKey(name)) {
			
			// Add to network table
			SocialNetwork socialNetwork = this.appManager.getSocialNetworkMap().get(name);
			socialNetwork.setCyNetwork(event.getNetwork());
			this.appManager.getUserPanelRef().addNetworkToNetworkPanel(socialNetwork);
			int networkID = socialNetwork.getNetworkType();
			// Node table reference
			CyTable nodeTable = null;
			// Node size variables
			int minNodeSize = 0;
			int maxNodeSize = 0;
			// Edge table reference 
			CyTable edgeTable = event.getNetwork().getDefaultEdgeTable();
			// Edge width variables
			int minEdgeWidth = 0;
			int maxEdgeWidth = 0;
			switch (networkID) {
				case Category.INCITES:
					// Specify NODE_LABEL
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, 
							new Object[] {"Label"});
					// Specify EDGE_WIDTH
					edgeTable = event.getNetwork().getDefaultEdgeTable();
					CyColumn copubColumn = edgeTable.getColumn("# of copubs");
					ArrayList<Integer> copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
					copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
					minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
					maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, 
							new Object[] {"# of copubs", minEdgeWidth + 1, maxEdgeWidth});
					// Specify Node_SIZE
					nodeTable = event.getNetwork().getDefaultNodeTable();
					CyColumn timesCitedColumn = nodeTable.getColumn("Times Cited");
					ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
					minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
					maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
							new Object[] {"Times Cited", minNodeSize + 1, maxNodeSize});
					// Specify EDGE_TRANSPARENCY
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, 
							                                     new Object[] {"# of copubs"});
					// Specify NODE_FILL_COLOR
					Map<String, HashMap<String, Color>> colorAttrMap = new HashMap<String, HashMap<String, Color>>();
					HashMap<String, Color> locationsMap = new HashMap<String, Color>();
					locationsMap.put("Ontario", new Color(255,137,41));
					//locationsMap.put("Canada", new Color(235,235,52));
					locationsMap.put("Canada", Color.red);
					locationsMap.put("United States", new Color(42,78,222));
					locationsMap.put("International", new Color(42,230,246));
					locationsMap.put("Other", new Color(211, 3, 253));
					locationsMap.put("UNIV TORONTO", new Color(20, 253, 3));
					locationsMap.put("N/A", Color.gray);
					colorAttrMap.put("Location", locationsMap);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_FILL_COLOR, 
						                              new Object[] {colorAttrMap});
					// Specify NODE_SHAPE
					Map<String, HashMap<String, NodeShape>> shapeAttrMap = new HashMap<String, HashMap<String, NodeShape>>();
					HashMap<String, NodeShape> departmentMap = new HashMap<String, NodeShape>();
					departmentMap.put((String) (socialNetwork.getAttrMap().get("Department")), NodeShapeVisualProperty.TRIANGLE);
					departmentMap.put("N/A", NodeShapeVisualProperty.RECTANGLE);
					shapeAttrMap.put("Department", departmentMap);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SHAPE, 
                            new Object[] {shapeAttrMap});
					break;
				case Category.SCOPUS:
					// Specify NODE_LABEL
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, 
							new Object[] {"Label"});
					// Specify Node_SIZE
					nodeTable = event.getNetwork().getDefaultNodeTable();
					timesCitedColumn = nodeTable.getColumn("Times Cited");
					timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
					minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
					maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
							new Object[] {"Times Cited", minNodeSize + 1, maxNodeSize});
					// Specify EDGE_WIDTH
					edgeTable = event.getNetwork().getDefaultEdgeTable();
					copubColumn = edgeTable.getColumn("# of copubs");
					copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
					minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
					maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, 
							new Object[] {"# of copubs", minEdgeWidth + 1, maxEdgeWidth});
					// Specify EDGE_TRANSPARENCY
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, 
							                                     new Object[] {"# of copubs"});
					break;
				case Category.PUBMED:
					// Specify NODE_LABEL
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, 
							new Object[] {"Label"});
					// Specify Node_SIZE
					nodeTable = event.getNetwork().getDefaultNodeTable();
					timesCitedColumn = nodeTable.getColumn("Times Cited");
					timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
					minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
					maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
							new Object[] {"Times Cited", minNodeSize + 1, maxNodeSize});
					// Specify EDGE_WIDTH
					edgeTable = event.getNetwork().getDefaultEdgeTable();
					copubColumn = edgeTable.getColumn("# of copubs");
					copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
					minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
					maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, 
							new Object[] {"# of copubs", minEdgeWidth + 1, maxEdgeWidth});
					// Specify EDGE_TRANSPARENCY
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, 
							                                     new Object[] {"# of copubs"});
					break;
			}
			this.appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
		}
	}
}