package org.baderlab.csapps.socialnetwork.listeners;

import java.awt.Cursor;

import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.IncitesVisualStyle;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;

public class SocialNetworkAddedListener implements NetworkAddedListener {
	
	
	private SocialNetworkAppManager appManager = null;
	
	
	
	public SocialNetworkAddedListener(SocialNetworkAppManager appManager) {
		super();
		this.appManager = appManager;
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

			switch (networkID) {
				case Category.INCITES:
					
					//create instance incites visual style
					IncitesVisualStyle vs = new IncitesVisualStyle();
					vs.applyVisualStyle(event.getNetwork(), socialNetwork);
										
					break;
				case Category.SCOPUS:
					
					BasicSocialNetworkVisualstyle vs_scopus = new BasicSocialNetworkVisualstyle();
					vs_scopus.applyVisualStyle(event.getNetwork(), socialNetwork);
					
					break;
				case Category.PUBMED:
					BasicSocialNetworkVisualstyle vs_pubmed = new BasicSocialNetworkVisualstyle();
					vs_pubmed.applyVisualStyle(event.getNetwork(), socialNetwork);
					break;
			}
			this.appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
		}
	}
}