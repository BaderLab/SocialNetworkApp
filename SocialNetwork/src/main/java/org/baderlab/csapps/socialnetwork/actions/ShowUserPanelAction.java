package main.java.org.baderlab.csapps.socialnetwork.actions;
		
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Properties;

import javax.swing.Action;

import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

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
 * @author Victor Kofia
 */
public class ShowUserPanelAction extends AbstractCyAction {
	
	private static final long serialVersionUID = -4717114252027573487L;
	private CyServiceRegistrar cyServiceRegistrarRef = null;
	private CytoPanel cytoPanelWest = null;
	private UserPanel userPanel = null;
	
	/**
	 * Create new Cytoscape action.
	 * @param Map configProps
	 * @param CyApplicationManager cyApplicationManagerServiceRef
	 * @param CyNetworkViewManager cyNetworkViewManagerServiceRef
	 * @param CySwingApplication cySwingApplicationServiceRef
	 * @param CyServiceRegistrar cyServiceRegistrarRef
	 * @param UserPanel userPanel
	 * @return null
	 */
	public ShowUserPanelAction(Map<String,String> configProps, CyApplicationManager cyApplicationManagerServiceRef, 
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
	 * @param ActionEvent event
	 * @return null
	 */
    public void actionPerformed(ActionEvent event) {	
		String currentName = (String) getValue(Action.NAME);
    	if (currentName.trim().equalsIgnoreCase("View Panel")) {
    		this.cyServiceRegistrarRef.registerService
    		  (this.userPanel, CytoPanelComponent.class, new Properties());
    		// If the state of the cytoPanelWest is HIDE, show it
    		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
    			cytoPanelWest.setState(CytoPanelState.DOCK);
    		}
    		// Select my panel
    		int index = cytoPanelWest.indexOfComponent(this.userPanel);
    		if (index == -1) {
    			return;
    		}
    		cytoPanelWest.setSelectedIndex(index);
    		putValue(Action.NAME, "Hide Panel");
    	} else if (currentName.trim().equalsIgnoreCase("Hide Panel")) {
    		this.cyServiceRegistrarRef.unregisterService 
    		       (this.userPanel, CytoPanelComponent.class);
    		putValue(Action.NAME, "View Panel");
    	} 
    }
 
}
