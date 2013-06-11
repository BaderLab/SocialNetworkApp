package main.java.org.baderlab.csapps.socialnetwork;

import main.java.org.baderlab.csapps.socialnetwork.tasks.NetworkTaskFactory;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.model.CyNetworkFactory;
import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;

import java.util.Properties;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		
		//??
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);
		
		//??
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);
		
		//??
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		
		//??
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		
		//??
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		
		//??
		CyLayoutAlgorithmManager cyLayoutManagerServiceRef = getService(bc, CyLayoutAlgorithmManager.class);
		
		//??
		VisualStyleFactory visualStyleFactoryServiceRef = getService(bc,VisualStyleFactory.class);
		
		//??
		VisualMappingFunctionFactory passthroughMappingFactoryServiceRef = getService(bc,VisualMappingFunctionFactory.class,"(mapping.type=passthrough)");

		//??
		VisualMappingManager vmmServiceRef = getService(bc,VisualMappingManager.class);
		
		//??
		TaskManager taskManager = getService(bc,TaskManager.class);
		
		// Transfer a reference of task manager service to class Cytoscape
		// FYI: Makes execution of tasks via form-field more flexible
		Cytoscape.setTaskManager(taskManager);
		
		// Create a custom panel & register
		InputPanel socialNetworkPanel = new InputPanel();	
		registerService(bc,socialNetworkPanel,CytoPanelComponent.class, new Properties());
		
		// Initialize & register network task factory
		NetworkTaskFactory networkTaskFactory = new NetworkTaskFactory(cyNetworkNamingServiceRef, cyNetworkFactoryServiceRef, cyNetworkManagerServiceRef, 
																		cyNetworkViewFactoryServiceRef, cyNetworkViewManagerServiceRef, cyLayoutManagerServiceRef, 
																		visualStyleFactoryServiceRef, passthroughMappingFactoryServiceRef, vmmServiceRef);
		registerService(bc,networkTaskFactory,TaskFactory.class, new Properties());
		
		// Transfer a reference of network task factory to class Cytoscape
		Cytoscape.setNetworkTaskFactory(networkTaskFactory);
	}
}

