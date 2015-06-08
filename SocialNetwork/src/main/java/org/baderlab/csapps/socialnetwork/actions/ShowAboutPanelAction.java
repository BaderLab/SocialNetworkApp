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

import javax.swing.WindowConstants;

import org.baderlab.csapps.socialnetwork.panels.AboutPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 * Enables users to view the Social Network Cytoscape app <i>About</i> panel
 *
 * @author risserlin
 */
public class ShowAboutPanelAction extends AbstractCyAction {

    /**
     *
     */
    private CySwingApplication application;
    /**
     *
     */
    private OpenBrowser browser;
    /**
     *
     */
    private static final long serialVersionUID = 1341062331014243704L;

    /**
     * Constructor for {@link ShowAboutPanelAction}
     *
     * @param Map configProps
     * @param CyApplicationManager applicationManager
     * @param CyNetworkViewManager networkViewManager
     * @param CySwingApplication application
     * @param OpenBrowser openBrowserRef
     */
    public ShowAboutPanelAction(Map<String, String> configProps,
            CyApplicationManager applicationManager,
            CyNetworkViewManager networkViewManager,
            CySwingApplication application,
            OpenBrowser openBrowserRef) {
        super(configProps, applicationManager, networkViewManager);
        putValue(NAME, "About ...");
        this.application = application;
        this.browser = openBrowserRef;
    }

    /**
     * Invoked when an action is performed
     *
     * @param {@link ActionEvent} event
     */
    public void actionPerformed(ActionEvent event) {
        // Open new dialog
        AboutPanel aboutPanel = new AboutPanel(this.application, this.browser);
        aboutPanel.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        aboutPanel.pack();
        aboutPanel.setLocationRelativeTo(this.application.getJFrame());
        aboutPanel.setVisible(true);
    }

}
