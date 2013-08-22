package main.java.org.baderlab.csapps.socialnetwork;

import main.java.org.baderlab.csapps.socialnetwork.actions.IncitesAction;
import main.java.org.baderlab.csapps.socialnetwork.actions.UserPanelAction;
import main.java.org.baderlab.csapps.socialnetwork.listeners.SocialNetworkAddedListener;
import main.java.org.baderlab.csapps.socialnetwork.listeners.SocialNetworkDestroyedListener;
import main.java.org.baderlab.csapps.socialnetwork.listeners.SocialNetworkSelectedListener;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;
import main.java.org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import main.java.org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetSelectedNetworksListener;
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
import org.cytoscape.model.events.NetworkAddedListener;
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
		
		// Acquire services
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc, CyApplicationManager.class);
		
		CySwingApplication cySwingApplicationServiceRef = getService(bc,CySwingApplication.class);
		
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);
		
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);
		
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		
		CyLayoutAlgorithmManager cyLayoutManagerServiceRef = getService(bc, CyLayoutAlgorithmManager.class);
		
		VisualStyleFactory visualStyleFactoryServiceRef = getService(bc,VisualStyleFactory.class);
		
		VisualMappingFunctionFactory passthroughMappingFactoryServiceRef = getService
				(bc,VisualMappingFunctionFactory.class,"(mapping.type=passthrough)");
		
		VisualMappingFunctionFactory continuousMappingFactoryServiceRef = getService
				(bc,VisualMappingFunctionFactory.class,"(mapping.type=continuous)");
		
		VisualMappingFunctionFactory discreteMappingFactoryServiceRef = getService
				(bc,VisualMappingFunctionFactory.class,"(mapping.type=discrete)");		
		
		VisualMappingManager vmmServiceRef = getService(bc,VisualMappingManager.class);
		
		TaskManager<?, ?> taskManager = getService(bc,TaskManager.class);
		
		CyServiceRegistrar cyServiceRegistrarRef = getService(bc, CyServiceRegistrar.class);
		
		
		// Create and register listeners
		SocialNetworkSelectedListener networkSelectedListener = new SocialNetworkSelectedListener();
		registerService(bc, networkSelectedListener, SetSelectedNetworksListener.class, new Properties());
		
		SocialNetworkDestroyedListener networkDestroyedListener = new SocialNetworkDestroyedListener(cyNetworkManagerServiceRef);
		registerService(bc, networkDestroyedListener, NetworkAboutToBeDestroyedListener.class, new Properties());
		
		SocialNetworkAddedListener networkAddedListener = new SocialNetworkAddedListener();
		registerService(bc, networkAddedListener, NetworkAddedListener.class, new Properties());		
		
		// Create and register task factories
		ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = 
				                      new ApplyVisualStyleTaskFactory(visualStyleFactoryServiceRef, 
				                    		                          vmmServiceRef, 
				                    		                          passthroughMappingFactoryServiceRef, 
						                                              continuousMappingFactoryServiceRef, 
						                                              discreteMappingFactoryServiceRef);
		registerService(bc, applyVisualStyleTaskFactoryRef, TaskFactory.class, new Properties());
		
		
		CreateNetworkTaskFactory networkTaskFactoryRef = new CreateNetworkTaskFactory(cyNetworkNamingServiceRef, 
				                                                                      cyNetworkFactoryServiceRef, 
				                                                                      cyNetworkManagerServiceRef, 
																                      cyNetworkViewFactoryServiceRef, 
																                      cyNetworkViewManagerServiceRef, 
																                      cyLayoutManagerServiceRef);
		registerService(bc,networkTaskFactoryRef,TaskFactory.class, new Properties());
		
		DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = new DestroyNetworkTaskFactory(cyNetworkManagerServiceRef);
		registerService(bc, destroyNetworkTaskFactoryRef, TaskFactory.class, new Properties());
		
		
		// Create & register new menu item (for opening /closing main app panel)
		UserPanel userPanel = new UserPanel();
		
		Map<String, String> serviceProperties = new HashMap<String, String>();
		serviceProperties.put("inMenuBar", "true");
		serviceProperties.put("preferredMenu", "Apps.Social Network");
		UserPanelAction userPanelAction = new UserPanelAction(serviceProperties, 
				                                              cyApplicationManagerServiceRef, 
				                                              cyNetworkViewManagerServiceRef, 
				                                              cySwingApplicationServiceRef, 
				                                              cyServiceRegistrarRef, userPanel);		
		
		registerService(bc, userPanelAction, CyAction.class, new Properties());	
		
		// Create and register new menu item (for adding institutions)
		serviceProperties = new HashMap<String, String>();
		serviceProperties.put("inMenuBar", "true");
		serviceProperties.put("preferredMenu", "Tools.Incites");
		IncitesAction incitesAction = new IncitesAction(serviceProperties, 
														cyApplicationManagerServiceRef, 
														cyNetworkViewManagerServiceRef);
		
		registerService(bc, incitesAction, CyAction.class, new Properties());	
		
		
		// Add dependencies to class Cytoscape
		// NOTE: Using setters violates dependency injection
		Cytoscape.setNetworkTaskFactoryRef(networkTaskFactoryRef);
		
		Cytoscape.setUserPanelRef(userPanel);
		
		Cytoscape.setUserPanelAction(userPanelAction);
		
		Cytoscape.setServiceRegistrar(cyServiceRegistrarRef);
		
		Cytoscape.setTaskManager(taskManager);
		
		Cytoscape.setApplyVisualStyleTaskFactoryRef(applyVisualStyleTaskFactoryRef);
		
		Cytoscape.setDestroyNetworkTaskFactoryRef(destroyNetworkTaskFactoryRef);

		Cytoscape.setCyAppManagerServiceRef(cyApplicationManagerServiceRef);
		
	}
}