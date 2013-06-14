package main.java.org.baderlab.csapps.socialnetwork.actions;
		
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Properties;

import main.java.org.baderlab.csapps.socialnetwork.UserPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
		
public class UserPanelAction extends AbstractCyAction {
	
	private CytoPanel cytoPanelWest = null;
	private CyServiceRegistrar cyServiceRegistrarRef = null;
	private UserPanel userPanel = null;
		
	public UserPanelAction(Map<String,String> configProps, CyApplicationManager cyApplicationManagerServiceRef, 
						  CyNetworkViewManager cyNetworkViewManagerServiceRef, CySwingApplication cySwingApplicationServiceRef,
						    CyServiceRegistrar cyServiceRegistrarRef, UserPanel userPanel) {
		
		super(configProps, cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef);
		
 		this.setName("Display Panel");		
 		
		this.cytoPanelWest = cySwingApplicationServiceRef.getCytoPanel(CytoPanelName.WEST);
		this.cyServiceRegistrarRef = cyServiceRegistrarRef;
		this.userPanel = userPanel;
	}
	
    public void actionPerformed(ActionEvent event) {
    	    	    	
    	if (this.getName().trim().equalsIgnoreCase("Display Panel")) {
    		this.cyServiceRegistrarRef.registerService(this.userPanel, CytoPanelComponent.class, new Properties());
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
    		this.setName("Close Panel");
    	} else if (this.getName().trim().equalsIgnoreCase("Close Panel")) {
    		this.cyServiceRegistrarRef.unregisterService(this.userPanel, CytoPanelComponent.class);
    		this.setName("Display Panel");
    	} 
    	
    }
 
}
