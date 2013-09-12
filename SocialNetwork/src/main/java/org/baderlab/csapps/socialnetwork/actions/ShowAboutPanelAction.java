package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.JFrame;


import org.baderlab.csapps.socialnetwork.panels.AboutPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.CyNetworkViewManager;

public class ShowAboutPanelAction extends AbstractCyAction {

	private CySwingApplication application;
	private OpenBrowser browser;
	/**
     * 
     */
    private static final long serialVersionUID = 1341062331014243704L;

    public ShowAboutPanelAction(Map<String,String> configProps, CyApplicationManager applicationManager, 
    		CyNetworkViewManager networkViewManager,CySwingApplication application, OpenBrowser openBrowserRef) {
    		super( configProps,  applicationManager,  networkViewManager);
		putValue(NAME, "About...");
		this.application = application;
		this.browser = openBrowserRef;
	}

	public void actionPerformed(ActionEvent event) {

		// open new dialog
		AboutPanel aboutPanel = new AboutPanel(application, browser);				
		aboutPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		aboutPanel.pack();
		aboutPanel.setLocationRelativeTo(application.getJFrame());
		aboutPanel.setVisible(true);

	}

}
