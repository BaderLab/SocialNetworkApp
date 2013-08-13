package main.java.org.baderlab.csapps.socialnetwork.listeners;

import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.SocialNetwork;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

import org.cytoscape.application.events.SetSelectedNetworksEvent;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.model.CyNetwork;

public class SocialNetworkSelectedListener implements SetSelectedNetworksListener {
	public void handleEvent(SetSelectedNetworksEvent event) {
		String name = null;
		for (CyNetwork network : event.getNetworks()) {
			name = Cytoscape.getNetworkName(network);
			if (Cytoscape.getSocialNetworkMap().containsKey(name)) {
				UserPanel.addNetworkVisualStyle(name);
				SocialNetwork socialNetwork = Cytoscape.getSocialNetworkMap().get(name);
				Cytoscape.setCurrentlySelectedSocialNetwork(socialNetwork);
				return;
			}
		}
		Cytoscape.setCurrentlySelectedSocialNetwork(null);
		UserPanel.addNetworkVisualStyle("DEFAULT");
	}
}