package org.baderlab.csapps.socialnetwork.listeners;


import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.cytoscape.application.events.SetSelectedNetworksEvent;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.model.CyNetwork;

public class SocialNetworkSelectedListener implements SetSelectedNetworksListener {

	private SocialNetworkAppManager appManager;
	private UserPanel userPanel;
	
	
	
	public SocialNetworkSelectedListener(SocialNetworkAppManager appManager) {
		super();
		this.appManager = appManager;
		this.userPanel = this.appManager.getUserPanelRef();
	}

	/**
	 * Updates UI
	 */
	public void handleEvent(SetSelectedNetworksEvent event) {
		String name = null;
		for (CyNetwork network : event.getNetworks()) {
			name = this.appManager.getNetworkName(network);
			// Update UI iff a social network has been selected
			if (this.appManager.getSocialNetworkMap().containsKey(name)) {
				SocialNetwork socialNetwork = this.appManager.getSocialNetworkMap().get(name);
				this.userPanel.updateNetworkSummaryPanel(socialNetwork);				
				this.userPanel.addNetworkVisualStyle(socialNetwork);
				this.appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
				return;
			}
		}
		this.appManager.setCurrentlySelectedSocialNetwork(null);
		this.userPanel.addNetworkVisualStyle(null);
		this.userPanel.updateNetworkSummaryPanel(null);	
	}
}