package main.java.org.baderlab.csapps.socialnetwork;

import main.java.org.baderlab.csapps.socialnetwork.actions.UserPanelAction;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;
import main.java.org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
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
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.service.util.CyServiceRegistrar;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		
		// ??
		DestroyedNetworkCleanUp destroyedNetworkCleanUp = new DestroyedNetworkCleanUp();
		registerService(bc, destroyedNetworkCleanUp, NetworkAboutToBeDestroyedListener.class, new Properties());
		
		// ??
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc, CyApplicationManager.class);
		
		// ??
		CySwingApplication cySwingApplicationServiceRef = getService(bc,CySwingApplication.class);
		
		// ??
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);
		
		// ??
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);
		
		// ??
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		
		// ??
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		
		// ??
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		
		// ??
		CyLayoutAlgorithmManager cyLayoutManagerServiceRef = getService(bc, CyLayoutAlgorithmManager.class);
		
		// ??
		VisualStyleFactory visualStyleFactoryServiceRef = getService(bc,VisualStyleFactory.class);
		
		// ??
		VisualMappingFunctionFactory passthroughMappingFactoryServiceRef = getService(bc,VisualMappingFunctionFactory.class,"(mapping.type=passthrough)");

		// ??
		VisualMappingFunctionFactory continuousMappingFactoryServiceRef = getService(bc,VisualMappingFunctionFactory.class,"(mapping.type=continuous)");
		
		// ??
		VisualMappingFunctionFactory discreteMappingFactoryServiceRef = getService(bc,VisualMappingFunctionFactory.class,"(mapping.type=discrete)");		
		
		// ??
		VisualMappingManager vmmServiceRef = getService(bc,VisualMappingManager.class);
		
		// ??
		TaskManager<?, ?> taskManager = getService(bc,TaskManager.class);
		
		// get the service registrar so we can register new services in different classes
		CyServiceRegistrar cyServiceRegistrarRef = getService(bc, CyServiceRegistrar.class);
		
		// Initialize & register apply visual style task factory
		ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = 
				new ApplyVisualStyleTaskFactory(visualStyleFactoryServiceRef, vmmServiceRef, passthroughMappingFactoryServiceRef, 
						                        continuousMappingFactoryServiceRef, discreteMappingFactoryServiceRef);
		registerService(bc, applyVisualStyleTaskFactoryRef, TaskFactory.class, new Properties());
			
		
		// Initialize & register network task factory
		CreateNetworkTaskFactory networkTaskFactoryRef = new CreateNetworkTaskFactory(cyNetworkNamingServiceRef, cyNetworkFactoryServiceRef, cyNetworkManagerServiceRef, 
																		cyNetworkViewFactoryServiceRef, cyNetworkViewManagerServiceRef, cyLayoutManagerServiceRef);
		registerService(bc,networkTaskFactoryRef,TaskFactory.class, new Properties());
		
		// Initialize & register destroy network task factory
		DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = new DestroyNetworkTaskFactory(cyNetworkManagerServiceRef);
		registerService(bc, destroyNetworkTaskFactoryRef, TaskFactory.class, new Properties());
		
		// Create user panel 
		UserPanel userPanel = new UserPanel();

		Map<String, String> serviceProperties;
		serviceProperties = new HashMap<String, String>();
		serviceProperties.put("inMenuBar", "true");
		serviceProperties.put("preferredMenu", "Apps.Social Network");
		UserPanelAction userPanelAction = new UserPanelAction(serviceProperties, cyApplicationManagerServiceRef, 
				                                              cyNetworkViewManagerServiceRef, cySwingApplicationServiceRef, 
				                                              cyServiceRegistrarRef, userPanel);		
		
		registerService(bc, userPanelAction, CyAction.class, new Properties());
		
		// Transfer a reference of network task factory to class Cytoscape
		// NOTE: Violates dependency injection
		Cytoscape.setNetworkTaskFactoryRef(networkTaskFactoryRef);
		
		// Transfer a reference of user panel to class Cytoscape
		// NOTE: Violates dependency injection
		Cytoscape.setUserPanelRef(userPanel);
		
		// Transfer a reference of user panel action to class Cytoscape
		// NOTE: Violates dependency injection
		Cytoscape.setUserPanelAction(userPanelAction);
		
		// Transfer a reference of service registrar to class Cytoscape
		// NOTE: Violates dependency injection
		Cytoscape.setServiceRegistrar(cyServiceRegistrarRef);

		// Transfer a reference of task manager service to class Cytoscape
		// FYI: Makes execution of tasks via form-field more flexible
		Cytoscape.setTaskManager(taskManager);
		
		// Transfer a reference of apply visual style task factory to class Cytoscape
		// NOTE: Violates Dependency Injection
		Cytoscape.setApplyVisualStyleTaskFactoryRef(applyVisualStyleTaskFactoryRef);
		
		// Transfer a reference of destroy network task factory to class Cytoscape
		// NOTE: Violates Dependency Injection
		Cytoscape.setDestroyNetworkTaskFactoryRef(destroyNetworkTaskFactoryRef);


	}
}