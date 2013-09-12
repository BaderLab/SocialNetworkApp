package org.baderlab.csapps.socialnetwork.listeners;

import java.util.Map;

import javax.swing.table.DefaultTableModel;


import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

/**
 * Keeps track of major events and handles cleanup
 * @author Victor Kofia
 */
public class SocialNetworkDestroyedListener implements NetworkAboutToBeDestroyedListener {
	private CyNetworkManager cyNetworkManagerServiceRef = null;
	private SocialNetworkAppManager appManager = null;
	private UserPanel userPanel = null;
	
	public SocialNetworkDestroyedListener(CyNetworkManager cyNetworkManagerServiceRef, SocialNetworkAppManager appManager) {
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
		this.appManager = appManager;
		this.userPanel = this.appManager.getUserPanelRef();
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
		String name = this.appManager.getNetworkName(event.getNetwork());
		if (this.appManager.getSocialNetworkMap().containsKey(name)) {
  			// Remove network from table
			DefaultTableModel model = (DefaultTableModel) this.userPanel.getNetworkTableRef().getModel();
			model.removeRow(getRow(model, name));
			Map<String, SocialNetwork> map = this.appManager.getSocialNetworkMap();
			map.remove(name);
			if (this.cyNetworkManagerServiceRef.getNetworkSet().size() == 1) {
				this.appManager.setCurrentlySelectedSocialNetwork(null);
				this.userPanel.addNetworkVisualStyle(null);
				this.userPanel.updateNetworkSummaryPanel(null);
			}
		}
	}
	
}