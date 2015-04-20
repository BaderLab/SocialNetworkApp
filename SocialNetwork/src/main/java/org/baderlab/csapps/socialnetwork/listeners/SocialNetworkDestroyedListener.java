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
		Map<String, SocialNetwork> map = this.appManager.getSocialNetworkMap();
		if (map.containsKey(name)) {
			map.remove(name);
			// Remove network from table
			DefaultTableModel  model = (DefaultTableModel) this.userPanel.getNetworkTableRef().getModel();		    
			int row = getRow(model, name);
			if (row > -1) {
				model.removeRow(getRow(model, name));			  
			}
			if (this.cyNetworkManagerServiceRef.getNetworkSet().size() == 1) {
				this.appManager.setCurrentlySelectedSocialNetwork(null);
				this.userPanel.addNetworkVisualStyle(null);
				this.userPanel.updateNetworkSummaryPanel(null);				
			}
		}
	}

}