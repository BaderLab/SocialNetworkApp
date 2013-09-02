package main.java.org.baderlab.csapps.socialnetwork.listeners;

import java.util.Map;

import javax.swing.table.DefaultTableModel;

import main.java.org.baderlab.csapps.socialnetwork.model.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

/**
 * Keeps track of major events and handles cleanup
 * @author Victor Kofia
 */
public class SocialNetworkDestroyedListener implements NetworkAboutToBeDestroyedListener {
	private CyNetworkManager cyNetworkManagerServiceRef = null;
	
	public SocialNetworkDestroyedListener(CyNetworkManager cyNetworkManagerServiceRef) {
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
	}
	
	/**
	 * Get the row in table that contains the specified name. 
	 * Assumes that each row stores a unique name.
	 * @param String model
	 * @param String name
	 * @return int row
	 */
	private int getRow(DefaultTableModel model, String name) {
		for (int row = 0; row < model.getRowCount(); row++) {
			if (((String) model.getValueAt(row, 0)).equalsIgnoreCase(name)) {
				return row;
			}
		}
		return -1;
	}
	
	public void handleEvent(NetworkAboutToBeDestroyedEvent event) {
		String name = Cytoscape.getNetworkName(event.getNetwork());
		if (Cytoscape.getSocialNetworkMap().containsKey(name)) {
  			// Remove network from table
			DefaultTableModel model = (DefaultTableModel) UserPanel.getNetworkTableRef().getModel();
			model.removeRow(getRow(model, name));
			Map<String, SocialNetwork> map = Cytoscape.getSocialNetworkMap();
			map.remove(name);
			Cytoscape.setCurrentlySelectedSocialNetwork(null);
			UserPanel.addNetworkVisualStyle(null);
			UserPanel.updateNetworkSummaryPanel(null);
		}
	}
	
}