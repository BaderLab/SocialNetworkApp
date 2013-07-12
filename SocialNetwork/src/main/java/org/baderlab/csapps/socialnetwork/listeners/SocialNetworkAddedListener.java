package main.java.org.baderlab.csapps.socialnetwork.listeners;

import java.util.ArrayList;
import java.util.Collections;

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
				System.out.println("The cutoff for is " + Double.toString(cutoff));
				System.out.println("The smallest in the list is " + Integer.toString(list.get(i)));
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
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, 
							new Object[] {"Last Name"});
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_LABEL, 
							new Object[] {"# of copubs"});
					
					CyTable edgeTable = event.getNetwork().getDefaultEdgeTable();
					CyColumn copubColumn = edgeTable.getColumn("# of copubs");
					ArrayList<Integer> copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
					
					int minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
					System.out.println("This is the smallest number of copubs " + Integer.toString(minEdgeWidth));
					int maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
					System.out.println("This is the largest number of copubs " + Integer.toString(maxEdgeWidth));
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, 
							new Object[] {"# of copubs", minEdgeWidth + 1, maxEdgeWidth});
					
					CyTable nodeTable = event.getNetwork().getDefaultNodeTable();
					CyColumn timesCitedColumn = nodeTable.getColumn("Times Cited");
					ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
					
					int minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
					System.out.println("This is the smallest number of times cited " + Integer.toString(minNodeSize));
					int maxNodeSize = getLargestInCutoff(timesCitedList, 85.0);
					System.out.println("This is the largest number of times cited " + Integer.toString(maxNodeSize));
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
							new Object[] {"Times Cited", minNodeSize + 1, maxNodeSize});
					
					socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, 
							                             new Object[] {"# of copubs"});
					
					break;
			}
			Cytoscape.setCurrentlySelectedSocialNetwork(socialNetwork);
		}
	}
}