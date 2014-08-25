package org.baderlab.csapps.socialnetwork;



import org.baderlab.csapps.socialnetwork.actions.AddInstitutionAction;
import org.baderlab.csapps.socialnetwork.actions.ShowAboutPanelAction;
import org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkAddedListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkDestroyedListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkSelectedListener;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.util.swing.OpenBrowser;
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
		
		FileUtil fileUtil = getService(bc, FileUtil.class);
		
		//open browser used by about  panel,
		OpenBrowser openBrowserRef = getService(bc, OpenBrowser.class);

		
		VisualMappingFunctionFactory passthroughMappingFactoryServiceRef = getService
				(bc,VisualMappingFunctionFactory.class,"(mapping.type=passthrough)");
		
		VisualMappingFunctionFactory continuousMappingFactoryServiceRef = getService
				(bc,VisualMappingFunctionFactory.class,"(mapping.type=continuous)");
		
		VisualMappingFunctionFactory discreteMappingFactoryServiceRef = getService
				(bc,VisualMappingFunctionFactory.class,"(mapping.type=discrete)");		
		
		VisualMappingManager vmmServiceRef = getService(bc,VisualMappingManager.class);
		
		TaskManager<?, ?> taskManager = getService(bc,TaskManager.class);
		
		CyServiceRegistrar cyServiceRegistrarRef = getService(bc, CyServiceRegistrar.class);
		
		//Create a new Cytoscape object to manage everything
		//TODO:Change name of class 
		SocialNetworkAppManager appManager = new SocialNetworkAppManager();
		
		// Create & register new menu item (for opening /closing main app panel)
		UserPanel userPanel = new UserPanel(appManager,fileUtil,cySwingApplicationServiceRef);
		
		Map<String, String> serviceProperties = new HashMap<String, String>();
		serviceProperties.put("inMenuBar", "true");
		serviceProperties.put("preferredMenu", "Apps.Social Network");
		ShowUserPanelAction userPanelAction = new ShowUserPanelAction(serviceProperties, 
		  cyApplicationManagerServiceRef, 
		  cyNetworkViewManagerServiceRef, 
		  cySwingApplicationServiceRef, 
		  cyServiceRegistrarRef, userPanel);		
		
		registerService(bc, userPanelAction, CyAction.class, new Properties());	
		
		//add panel and action to the manager
		appManager.setUserPanelRef(userPanel);		
		appManager.setUserPanelAction(userPanelAction);
		
		//instantiate an instance of CytoscapeUtilities (to populate static fields with version information)
		CytoscapeUtilities utils = new CytoscapeUtilities();
		
		// Create and register listeners
		SocialNetworkSelectedListener networkSelectedListener = new SocialNetworkSelectedListener(appManager);
		registerService(bc, networkSelectedListener, SetSelectedNetworksListener.class, new Properties());
		
		SocialNetworkDestroyedListener networkDestroyedListener = new SocialNetworkDestroyedListener(cyNetworkManagerServiceRef,appManager);
		registerService(bc, networkDestroyedListener, NetworkAboutToBeDestroyedListener.class, new Properties());
		
		SocialNetworkAddedListener networkAddedListener = new SocialNetworkAddedListener(appManager);
		registerService(bc, networkAddedListener, NetworkAddedListener.class, new Properties());		
		
		// Create and register task factories
		ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = 
				                      new ApplyVisualStyleTaskFactory(visualStyleFactoryServiceRef, 
				                    		                          vmmServiceRef, 
				                    		                          passthroughMappingFactoryServiceRef, 
						                                              continuousMappingFactoryServiceRef, 
						                                              discreteMappingFactoryServiceRef,appManager);
		registerService(bc, applyVisualStyleTaskFactoryRef, TaskFactory.class, new Properties());
		
		
		CreateNetworkTaskFactory networkTaskFactoryRef = new CreateNetworkTaskFactory(cyNetworkNamingServiceRef, 
				                                                                      cyNetworkFactoryServiceRef, 
				                                                                      cyNetworkManagerServiceRef, 
																                      cyNetworkViewFactoryServiceRef, 
																                      cyNetworkViewManagerServiceRef, 
																                      cyLayoutManagerServiceRef,appManager);
		registerService(bc,networkTaskFactoryRef,TaskFactory.class, new Properties());
		
		DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = new DestroyNetworkTaskFactory(cyNetworkManagerServiceRef,appManager);
		registerService(bc, destroyNetworkTaskFactoryRef, TaskFactory.class, new Properties());
		
		// Add dependencies to app manager
		// NOTE: Using setters violates dependency injection		
				
		appManager.setNetworkTaskFactoryRef(networkTaskFactoryRef);
								
		appManager.setServiceRegistrar(cyServiceRegistrarRef);
				
		appManager.setTaskManager(taskManager);
				
		appManager.setApplyVisualStyleTaskFactoryRef(applyVisualStyleTaskFactoryRef);
				
		appManager.setDestroyNetworkTaskFactoryRef(destroyNetworkTaskFactoryRef);

		appManager.setCyAppManagerServiceRef(cyApplicationManagerServiceRef);
		
		//About Action
		serviceProperties = new HashMap<String, String>();
		serviceProperties.put("inMenuBar", "true");
		serviceProperties.put("preferredMenu", "Apps.Social Network");
		ShowAboutPanelAction aboutAction = new ShowAboutPanelAction(serviceProperties,cyApplicationManagerServiceRef ,cyNetworkViewManagerServiceRef, cySwingApplicationServiceRef, openBrowserRef);		

		//register the services
		registerService(bc, aboutAction, CyAction.class,new Properties());
		
		// Create and register new menu item (for adding institutions)
		serviceProperties = new HashMap<String, String>();
		serviceProperties.put("inMenuBar", "true");
		serviceProperties.put("preferredMenu", "Tools.Incites");
		AddInstitutionAction incitesAction = new AddInstitutionAction(serviceProperties, 
														cyApplicationManagerServiceRef, 
														cyNetworkViewManagerServiceRef);
		
		registerService(bc, incitesAction, CyAction.class, new Properties());	
		
		
		
				
	}
}