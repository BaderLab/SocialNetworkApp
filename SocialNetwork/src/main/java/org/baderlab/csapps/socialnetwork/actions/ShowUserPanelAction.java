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

package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Properties;
import javax.swing.Action;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 * Display or hide the main app panel
 *
 * @author Victor Kofia
 */
public class ShowUserPanelAction extends AbstractCyAction {

    private static final long serialVersionUID = -4717114252027573487L;
    private CyServiceRegistrar cyServiceRegistrarRef = null;
    private CytoPanel cytoPanelWest = null;
    private UserPanel userPanel = null;

    /**
     * Create new Cytoscape action.
     *
     * @param Map configProps
     * @param CyApplicationManager cyApplicationManagerServiceRef
     * @param CyNetworkViewManager cyNetworkViewManagerServiceRef
     * @param CySwingApplication cySwingApplicationServiceRef
     * @param CyServiceRegistrar cyServiceRegistrarRef
     * @param UserPanel userPanel
     * @return null
     */
    public ShowUserPanelAction(Map<String, String> configProps, CyApplicationManager cyApplicationManagerServiceRef,
            CyNetworkViewManager cyNetworkViewManagerServiceRef, CySwingApplication cySwingApplicationServiceRef,
            CyServiceRegistrar cyServiceRegistrarRef, UserPanel userPanel) {

        super(configProps, cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef);
        putValue(Action.NAME, "View Panel");
        this.cytoPanelWest = cySwingApplicationServiceRef.getCytoPanel(CytoPanelName.WEST);
        this.cyServiceRegistrarRef = cyServiceRegistrarRef;
        this.userPanel = userPanel;
    }

    /**
     * Display / Hide the main panel
     *
     * @param ActionEvent event
     * @return null
     */
    public void actionPerformed(ActionEvent event) {
        String currentName = (String) getValue(Action.NAME);
        if (currentName.trim().equalsIgnoreCase("View Panel")) {
            this.cyServiceRegistrarRef.registerService(this.userPanel, CytoPanelComponent.class, new Properties());
            // If the state of the cytoPanelWest is HIDE, show it
            if (this.cytoPanelWest.getState() == CytoPanelState.HIDE) {
                this.cytoPanelWest.setState(CytoPanelState.DOCK);
            }
            // Select my panel
            int index = this.cytoPanelWest.indexOfComponent(this.userPanel);
            if (index == -1) {
                return;
            }
            this.cytoPanelWest.setSelectedIndex(index);
            putValue(Action.NAME, "Hide Panel");
        } else if (currentName.trim().equalsIgnoreCase("Hide Panel")) {
            this.cyServiceRegistrarRef.unregisterService(this.userPanel, CytoPanelComponent.class);
            putValue(Action.NAME, "View Panel");
        }
    }

}
