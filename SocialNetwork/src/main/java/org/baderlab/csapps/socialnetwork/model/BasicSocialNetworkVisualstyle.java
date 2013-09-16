package org.baderlab.csapps.socialnetwork.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class BasicSocialNetworkVisualstyle {
	public static final String nodeattr_label = "Label";
	public static final String nodeattr_fname = "First Name";
	public static final String nodeattr_lname = "Last Name";
	public static final String nodeattr_pub = "Publications";
	public static final String edgeattr_numcopubs = "# of copubs";
	public static final String nodeattr_timescited = "Times Cited";
	
	public static final String networkattr_totalPub = "Total Publications";
	
	private CyNetwork network;
	private SocialNetwork socialNetwork;
	
	public void applyVisualStyle(CyNetwork network, SocialNetwork socialNetwork){
		this.network = network;
		this.socialNetwork = socialNetwork;
		applyNodeStyle(this.network, this.socialNetwork);
		applyEdgeStyle(this.network, this.socialNetwork);
	}
	
	protected void applyNodeStyle(CyNetwork network, SocialNetwork socialNetwork){
		// Node table reference
		CyTable nodeTable = null;
		// Node size variables
		int minNodeSize = 0;
		int maxNodeSize = 0;
		
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, 
				new Object[] {nodeattr_label});
		
		// Specify Node_SIZE
		nodeTable = network.getDefaultNodeTable();
		CyColumn timesCitedColumn = nodeTable.getColumn(nodeattr_timescited);
		ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
		minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
		maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
				new Object[] {nodeattr_timescited, minNodeSize + 1, maxNodeSize});
		
		
	
	}
	
	protected void applyEdgeStyle(CyNetwork network, SocialNetwork socialNetwork){
		// Edge table reference 
		CyTable edgeTable = network.getDefaultEdgeTable();
		// Edge width variables
		int minEdgeWidth = 0;
		int maxEdgeWidth = 0;
		// Specify EDGE_WIDTH
		edgeTable = network.getDefaultEdgeTable();
		CyColumn copubColumn = edgeTable.getColumn(edgeattr_numcopubs);
		ArrayList<Integer> copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
		copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
		minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
		maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, 
				new Object[] {edgeattr_numcopubs, minEdgeWidth + 1, maxEdgeWidth});
		
		// Specify EDGE_TRANSPARENCY
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, 
				                                     new Object[] {edgeattr_numcopubs});
	}
	
	
	
	/**
	 * Get smallest value given cutoff
	 * @param List list
	 * @param Double cutoff
	 * @return Integer value
	 */
	protected int getSmallestInCutoff(ArrayList<Integer> list, Double cutoff) {
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
	protected int getLargestInCutoff(ArrayList<Integer> list, Double cutoff) {
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
	
	
	

}
