package main.java.org.baderlab.csapps.socialnetwork.listeners;

import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;

public class SocialNetworkAddedListener implements NetworkAddedListener {
	public void handleEvent(NetworkAddedEvent event) {
		String name = Cytoscape.getNetworkName(event.getNetwork());
		// If the network being added is a social network, then
		// add it to network table
		if (Cytoscape.getSocialNetworkMap().containsKey(name)) {
			UserPanel.addToNetworkPanel(event.getNetwork());
		}
	}
}
