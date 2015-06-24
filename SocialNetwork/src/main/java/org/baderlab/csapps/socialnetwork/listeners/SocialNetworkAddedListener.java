/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates,
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.listeners;

import java.awt.Cursor;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.IncitesVisualStyle;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;

/**
 * Adds relevant network information to network table once
 * Cytoscape generates a network
 *
 * @author Victor Kofia
 *
 */
public class SocialNetworkAddedListener implements NetworkAddedListener {

    private SocialNetworkAppManager appManager = null;
    private CyNetworkManager cyNetworkManagerServiceRef = null;

    /**
     * Create a new {@link SocialNetworkAddedListener} object
     *
     * @param {@link SocialNetworkAppManager} appManager
     */
    public SocialNetworkAddedListener(SocialNetworkAppManager appManager, 
            CyNetworkManager cyNetworkManagerServiceRef) {
        super();
        this.appManager = appManager;
        this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
    }

    /**
     * Adds network to network table and configures visual styles if necessary.
     *
     * @param {@link NetworkAddedEvent} event
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
                    // Create instance InCites visual style
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