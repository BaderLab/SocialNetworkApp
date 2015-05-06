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

package org.baderlab.csapps.socialnetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.baderlab.csapps.socialnetwork.actions.AddInstitutionAction;
import org.baderlab.csapps.socialnetwork.actions.ShowAboutPanelAction;
import org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.listeners.RestoreStateFile;
import org.baderlab.csapps.socialnetwork.listeners.SaveStateFile;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkAddedListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkDestroyedListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkSelectedListener;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetSelectedNetworkViewsListener;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.ColumnNameChangedListener;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.annotations.AnnotationFactory;
import org.cytoscape.view.presentation.annotations.AnnotationManager;
import org.cytoscape.view.presentation.annotations.ShapeAnnotation;
import org.cytoscape.view.presentation.annotations.TextAnnotation;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

    public CyActivator() {
        super();
    }

    public void start(BundleContext bc) {

        // Acquire services
        CyApplicationManager cyApplicationManagerServiceRef = getService(bc, CyApplicationManager.class);

        CySwingApplication cySwingApplicationServiceRef = getService(bc,CySwingApplication.class);

        CyServiceRegistrar cyServiceRegistrar = getService(bc, CyServiceRegistrar.class);

        CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);

        CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);

        CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);

        CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);

        CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);

        CyLayoutAlgorithmManager cyLayoutManagerServiceRef = getService(bc, CyLayoutAlgorithmManager.class);

        CyTableManager cyTableManager = getService(bc, CyTableManager.class);

        CyLayoutAlgorithmManager cyLayoutManager = getService(bc, CyLayoutAlgorithmManager.class);

        CyEventHelper eventHelper = getService(bc,CyEventHelper.class);

        AnnotationFactory<ShapeAnnotation> shapeFactory = getService(bc, AnnotationFactory.class, "(type=ShapeAnnotation.class)");

        AnnotationFactory<TextAnnotation> textFactory = getService(bc, AnnotationFactory.class, "(type=TextAnnotation.class)");

        CommandExecutorTaskFactory commandExecutor = getService(bc, CommandExecutorTaskFactory.class);

        VisualStyleFactory visualStyleFactoryServiceRef = getService(bc,VisualStyleFactory.class);

        CyGroupFactory groupFactory = getService(bc, CyGroupFactory.class);

        CyGroupManager groupManager = getService(bc, CyGroupManager.class);

        SynchronousTaskManager<?> syncTaskManager = getService(bc, SynchronousTaskManager.class);

        DialogTaskManager dialogTaskManager = getService(bc, DialogTaskManager.class);

        FileUtil fileUtil = getService(bc, FileUtil.class);

        AnnotationManager annotationManager = getService(bc, AnnotationManager.class);

        // Set up autoAnnotationManager, hand it all of the services it needs
        AutoAnnotationManager autoAnnotationManager = AutoAnnotationManager.getInstance();
        autoAnnotationManager.initialize(cySwingApplicationServiceRef,
                cyTableManager,
                commandExecutor,
                dialogTaskManager,
                syncTaskManager,
                annotationManager,
                cyLayoutManager,
                shapeFactory,
                textFactory,
                groupFactory,
                groupManager,
                eventHelper,
                cyApplicationManagerServiceRef);
        // Register network/table events to autoAnnotationManager
        registerService(bc, autoAnnotationManager, SetSelectedNetworkViewsListener.class, new Properties());
        registerService(bc, autoAnnotationManager, NetworkViewAboutToBeDestroyedListener.class, new Properties());
        registerService(bc, autoAnnotationManager, ColumnCreatedListener.class, new Properties());
        registerService(bc, autoAnnotationManager, ColumnDeletedListener.class, new Properties());
        registerService(bc, autoAnnotationManager, ColumnNameChangedListener.class, new Properties());

        // Open browser used by about  panel,
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

        // Create a new Cytoscape object to manage everything
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

        // Add panel and action to the manager
        appManager.setUserPanelRef(userPanel);
        appManager.setUserPanelAction(userPanelAction);

        // Instantiate an instance of CytoscapeUtilities (to populate static fields with version information)
        CytoscapeUtilities utils = new CytoscapeUtilities();

        // Create and register listeners
        SocialNetworkSelectedListener networkSelectedListener = new SocialNetworkSelectedListener(appManager);
        registerService(bc, networkSelectedListener, SetSelectedNetworksListener.class, new Properties());

        SocialNetworkDestroyedListener networkDestroyedListener = new SocialNetworkDestroyedListener(cyNetworkManagerServiceRef,appManager);
        registerService(bc, networkDestroyedListener, NetworkAboutToBeDestroyedListener.class, new Properties());

        SocialNetworkAddedListener networkAddedListener = new SocialNetworkAddedListener(appManager);
        registerService(bc, networkAddedListener, NetworkAddedListener.class, new Properties());

        SaveStateFile saveSession = new SaveStateFile(appManager);
        registerService(bc, saveSession, SessionAboutToBeSavedListener.class, new Properties());

        RestoreStateFile restoreSession = new RestoreStateFile(appManager, cyNetworkViewManagerServiceRef);
        registerService(bc, restoreSession, SessionLoadedListener.class, new Properties());

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
                cyLayoutManagerServiceRef,
                appManager);
        registerService(bc,networkTaskFactoryRef,TaskFactory.class, new Properties());

        DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = new DestroyNetworkTaskFactory(cyNetworkManagerServiceRef,appManager);
        registerService(bc, destroyNetworkTaskFactoryRef, TaskFactory.class, new Properties());

        // Add dependencies to app manager
        // TODO:
        // NOTE: Using setters violates dependency injection
        appManager.setNetworkTaskFactoryRef(networkTaskFactoryRef);

        appManager.setServiceRegistrar(cyServiceRegistrarRef);

        appManager.setTaskManager(taskManager);

        appManager.setApplyVisualStyleTaskFactoryRef(applyVisualStyleTaskFactoryRef);

        appManager.setDestroyNetworkTaskFactoryRef(destroyNetworkTaskFactoryRef);

        appManager.setCyAppManagerServiceRef(cyApplicationManagerServiceRef);

        // About Action
        serviceProperties = new HashMap<String, String>();
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Apps.Social Network");
        ShowAboutPanelAction aboutAction = new ShowAboutPanelAction(serviceProperties,
                cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef,
                cySwingApplicationServiceRef,
                openBrowserRef);

        registerService(bc, aboutAction, CyAction.class,new Properties());

        // Incites Action
        serviceProperties = new HashMap<String, String>();
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Tools.Incites");
        AddInstitutionAction incitesAction = new AddInstitutionAction(serviceProperties,
                cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef);

        registerService(bc, incitesAction, CyAction.class, new Properties());

        // Auto-annotate Panel Action - opens Annotation panel
        /*
        serviceProperties = new HashMap<String, String>();
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Apps.Social Network");
        AutoAnnotationPanelAction autoAnnotationPanelAction = new AutoAnnotationPanelAction(serviceProperties,
                cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef,
                cySwingApplicationServiceRef,
                annotationManager,
                cyServiceRegistrar);
        registerService(bc, autoAnnotationPanelAction, CyAction.class, new Properties());
         */

        //Auto-annotate Display Options Panel Action - opens display options panel
        /*
        serviceProperties = new HashMap<String, String>();
        DisplayOptionsPanelAction displayOptionsPanelAction = new DisplayOptionsPanelAction(serviceProperties,
                cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef,
                cySwingApplicationServiceRef,
                annotationManager,
                cyServiceRegistrar);
        autoAnnotationManager.setDisplayOptionsPanelAction(displayOptionsPanelAction);
         */

    }
}