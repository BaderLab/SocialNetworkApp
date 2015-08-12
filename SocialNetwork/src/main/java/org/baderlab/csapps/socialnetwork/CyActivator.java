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
import org.baderlab.csapps.socialnetwork.actions.ChangeAuthorInstitutionAction;
import org.baderlab.csapps.socialnetwork.actions.CreateChartAction;
import org.baderlab.csapps.socialnetwork.actions.ExportNthDegreeNeighborsAction;
import org.baderlab.csapps.socialnetwork.actions.ShowAboutPanelAction;
import org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import org.baderlab.csapps.socialnetwork.actions.UpdateAuthorLocationAction;
import org.baderlab.csapps.socialnetwork.listeners.RestoreSocialNetworksFromProp;
import org.baderlab.csapps.socialnetwork.listeners.SaveSocialNetworkToProp;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkAddedListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkChartListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkDestroyedListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkNameChangedListener;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkSelectedListener;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.CreateChartTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ExportNthDegreeNeighborsTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.HideAuthorsTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ParseIncitesXLSXTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ParsePubMedXMLTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ParseScopusCSVTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ParseSocialNetworkFileTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.SearchPubMedTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.RowsSetListener;
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
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics2Factory;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifierFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

    public CyActivator() {
        super();
    }

    public void start(BundleContext bc) {
                
        // Configuration properties
        PropsReader propsReader = new PropsReader("socialnetwork", "socialnetwork.props");
        Properties propsReaderServiceProps = new Properties();
        propsReaderServiceProps.setProperty("cyPropertyName", "socialnetwork.props");
        registerAllServices(bc, propsReader, propsReaderServiceProps);

        // Acquire services
        final CyApplicationManager cyApplicationManagerServiceRef = getService(bc, CyApplicationManager.class);

        final CySwingApplication cySwingApplicationServiceRef = getService(bc, CySwingApplication.class);

        final CyNetworkNaming cyNetworkNamingServiceRef = getService(bc, CyNetworkNaming.class);

        final CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc, CyNetworkFactory.class);

        final CyNetworkManager cyNetworkManagerServiceRef = getService(bc, CyNetworkManager.class);

        final CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc, CyNetworkViewFactory.class);

        final CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc, CyNetworkViewManager.class);

        final CyLayoutAlgorithmManager cyLayoutManagerServiceRef = getService(bc, CyLayoutAlgorithmManager.class);

        final VisualStyleFactory visualStyleFactoryServiceRef = getService(bc, VisualStyleFactory.class);

        final FileUtil fileUtil = getService(bc, FileUtil.class);
        
        final CyColumnIdentifierFactory columnIdFactory = getService(bc, CyColumnIdentifierFactory.class);

        // Open browser used by about panel,
        OpenBrowser openBrowserRef = getService(bc, OpenBrowser.class);

        VisualMappingFunctionFactory passthroughMappingFactoryServiceRef = getService(bc, VisualMappingFunctionFactory.class,
                "(mapping.type=passthrough)");

        VisualMappingFunctionFactory continuousMappingFactoryServiceRef = getService(bc, VisualMappingFunctionFactory.class,
                "(mapping.type=continuous)");

        VisualMappingFunctionFactory discreteMappingFactoryServiceRef = getService(bc, VisualMappingFunctionFactory.class, 
                "(mapping.type=discrete)");

        final VisualMappingManager vmmServiceRef = getService(bc, VisualMappingManager.class);

        TaskManager<?, ?> taskManager = getService(bc, TaskManager.class);

        CyServiceRegistrar cyServiceRegistrarRef = getService(bc, CyServiceRegistrar.class);

        // Create a new Cytoscape object to manage everything
        SocialNetworkAppManager appManager = new SocialNetworkAppManager();

        // Create & register new menu item (for opening /closing main app panel)
        UserPanel userPanel = new UserPanel(appManager, fileUtil, cySwingApplicationServiceRef);

        Map<String, String> serviceProperties = new HashMap<String, String>();
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Apps.Social Network");
        ShowUserPanelAction userPanelAction = new ShowUserPanelAction(serviceProperties, cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef, cySwingApplicationServiceRef, cyServiceRegistrarRef, userPanel);

        registerService(bc, userPanelAction, CyAction.class, new Properties());
        
        // Add panel and action to the manager
        appManager.setUserPanelRef(userPanel);
        appManager.setUserPanelAction(userPanelAction);

        // Create and register task factories
        ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = new ApplyVisualStyleTaskFactory(vmmServiceRef, appManager);
        registerService(bc, applyVisualStyleTaskFactoryRef, TaskFactory.class, new Properties());
        
        CreateNetworkTaskFactory networkTaskFactoryRef = new CreateNetworkTaskFactory(cyNetworkNamingServiceRef, cyNetworkFactoryServiceRef,
                cyNetworkManagerServiceRef, cyNetworkViewFactoryServiceRef, cyNetworkViewManagerServiceRef, cyLayoutManagerServiceRef, appManager);
        registerService(bc, networkTaskFactoryRef, TaskFactory.class, new Properties());

        DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = new DestroyNetworkTaskFactory(cyNetworkManagerServiceRef, appManager);
        registerService(bc, destroyNetworkTaskFactoryRef, TaskFactory.class, new Properties());
        
        ExportNthDegreeNeighborsTaskFactory exportNthDegreeNeighborsTaskFactoryRef = new ExportNthDegreeNeighborsTaskFactory(appManager);
        registerService(bc, exportNthDegreeNeighborsTaskFactoryRef, TaskFactory.class, new Properties());

        ParsePubMedXMLTaskFactory parsePubMedXMLTaskFactoryRef = new ParsePubMedXMLTaskFactory(appManager);
        registerService(bc, parsePubMedXMLTaskFactoryRef, TaskFactory.class, new Properties());

        ParseIncitesXLSXTaskFactory parseIncitesXLSXTaskFactoryRef = new ParseIncitesXLSXTaskFactory(appManager);
        registerService(bc, parseIncitesXLSXTaskFactoryRef, TaskFactory.class, new Properties());

        ParseScopusCSVTaskFactory parseScopusCSVTaskFactoryRef = new ParseScopusCSVTaskFactory(appManager);
        registerService(bc, parseScopusCSVTaskFactoryRef, TaskFactory.class, new Properties());
        
        ParseSocialNetworkFileTaskFactory parseSocialNetworkFileTaskFactoryRef = new ParseSocialNetworkFileTaskFactory(appManager, 
                taskManager, parseIncitesXLSXTaskFactoryRef, parsePubMedXMLTaskFactoryRef, parseScopusCSVTaskFactoryRef);
        registerService(bc, parseSocialNetworkFileTaskFactoryRef, TaskFactory.class, new Properties());
        
        final SocialNetworkChartListener customChartManager = new SocialNetworkChartListener();
        registerServiceListener(bc, customChartManager, "addCustomGraphicsFactory", "removeCustomGraphicsFactory", CyCustomGraphics2Factory.class);

        HideAuthorsTaskFactory hideAuthorsTaskFactoryRef = new HideAuthorsTaskFactory(cyApplicationManagerServiceRef);
        registerService(bc, hideAuthorsTaskFactoryRef, TaskFactory.class, new Properties());
        
        CreateChartTaskFactory createChartTaskFactory = new CreateChartTaskFactory(cyApplicationManagerServiceRef, customChartManager, 
                vmmServiceRef, columnIdFactory, taskManager, hideAuthorsTaskFactoryRef);
        registerService(bc, createChartTaskFactory, TaskFactory.class, new Properties());
        
        SearchPubMedTaskFactory searchPubMedTaskFactoryRef = new SearchPubMedTaskFactory(appManager);
        registerService(bc, searchPubMedTaskFactoryRef, TaskFactory.class, new Properties());
        
        
        // Create and register listeners
        SocialNetworkSelectedListener networkSelectedListener = new SocialNetworkSelectedListener(appManager, cyServiceRegistrarRef, taskManager, hideAuthorsTaskFactoryRef);
        registerService(bc, networkSelectedListener, SetSelectedNetworksListener.class, new Properties());

        SocialNetworkDestroyedListener networkDestroyedListener = new SocialNetworkDestroyedListener(cyNetworkManagerServiceRef, appManager);
        registerService(bc, networkDestroyedListener, NetworkAboutToBeDestroyedListener.class, new Properties());

        SocialNetworkAddedListener networkAddedListener = new SocialNetworkAddedListener(appManager, cyNetworkManagerServiceRef, vmmServiceRef,
                visualStyleFactoryServiceRef, passthroughMappingFactoryServiceRef, continuousMappingFactoryServiceRef, discreteMappingFactoryServiceRef,
                cyServiceRegistrarRef, cySwingApplicationServiceRef, taskManager, hideAuthorsTaskFactoryRef, cyApplicationManagerServiceRef);
        registerService(bc, networkAddedListener, NetworkAddedListener.class, new Properties());

        SocialNetworkNameChangedListener networkNameChangedListener = new SocialNetworkNameChangedListener(appManager, cyNetworkManagerServiceRef);
        registerService(bc, networkNameChangedListener, RowsSetListener.class, new Properties());

        SaveSocialNetworkToProp saveSession = new SaveSocialNetworkToProp(appManager);
        registerService(bc, saveSession, SessionAboutToBeSavedListener.class, new Properties());

        RestoreSocialNetworksFromProp restoreSession = new RestoreSocialNetworksFromProp(appManager, cyNetworkViewManagerServiceRef, cyServiceRegistrarRef,
                cySwingApplicationServiceRef, userPanelAction, userPanel, taskManager, hideAuthorsTaskFactoryRef);
        registerService(bc, restoreSession, SessionLoadedListener.class, new Properties());
                

        // Add dependencies to app manager
        // TODO:
        // NOTE: Using setters violates dependency injection
        appManager.setPropsReader(propsReader);
        
        appManager.setParseSocialNetworkFileTaskFactory(parseSocialNetworkFileTaskFactoryRef);

        appManager.setNetworkTaskFactoryRef(networkTaskFactoryRef);

        appManager.setServiceRegistrar(cyServiceRegistrarRef);

        appManager.setTaskManager(taskManager);

        appManager.setApplyVisualStyleTaskFactoryRef(applyVisualStyleTaskFactoryRef);

        appManager.setDestroyNetworkTaskFactoryRef(destroyNetworkTaskFactoryRef);

        appManager.setCyAppManagerServiceRef(cyApplicationManagerServiceRef);
        
        appManager.setSearchPubMedTaskFactoryRef(searchPubMedTaskFactoryRef);
        
        // Add dependencies to CytoscapeUtilities
        CytoscapeUtilities.setPropsReader(propsReader);

        // About Action
        serviceProperties = new HashMap<String, String>();
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Apps.Social Network");
        ShowAboutPanelAction aboutAction = new ShowAboutPanelAction(serviceProperties, cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef, cySwingApplicationServiceRef, openBrowserRef);

        registerService(bc, aboutAction, CyAction.class, new Properties());

        // InCites Action
        serviceProperties = new HashMap<String, String>();
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Tools.InCites");
        AddInstitutionAction incitesAction = new AddInstitutionAction(serviceProperties, cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef, taskManager, applyVisualStyleTaskFactoryRef);

        registerService(bc, incitesAction, CyAction.class, new Properties());

        // Export neighbors Action
        serviceProperties = new HashMap<String, String>();
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Tools.NetworkAnalyzer.Neighbor List");
        ExportNthDegreeNeighborsAction exportNeighborsAction = new ExportNthDegreeNeighborsAction(serviceProperties, cyApplicationManagerServiceRef,
                cyNetworkViewManagerServiceRef, cyNetworkManagerServiceRef, cySwingApplicationServiceRef, taskManager,
                exportNthDegreeNeighborsTaskFactoryRef, appManager);

        registerService(bc, exportNeighborsAction, CyAction.class, new Properties());
        
        // Create a task that is shown in the "Apps" menu.
        // TODO:
        serviceProperties.put("inMenuBar", "true");
        serviceProperties.put("preferredMenu", "Apps.Social Network");
        CreateChartAction createChartAction = new CreateChartAction(serviceProperties, cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef, 
                createChartTaskFactory, taskManager, createChartTaskFactory);
        registerService(bc, createChartAction, CyAction.class, new Properties());
        
        // Create & register new menu item (for updating the location that a specific author
        // has been assigned to)
        CyNodeViewContextMenuFactory updateAuthorLocationAction = new UpdateAuthorLocationAction(taskManager,
                applyVisualStyleTaskFactoryRef);
        Properties updateAuthorLocationActionProps = new Properties();
        updateAuthorLocationActionProps.put("preferredMenu", "Apps");
        registerAllServices(bc, updateAuthorLocationAction, updateAuthorLocationActionProps);

        // Menu item for changing the default institution of an author
        CyNodeViewContextMenuFactory changeAuthorInstitutionAction = new ChangeAuthorInstitutionAction(taskManager,
                applyVisualStyleTaskFactoryRef, propsReader);
        Properties changeAuthorInstitutionActionProps = new Properties();
        changeAuthorInstitutionActionProps.put("preferredMenu", "Apps");
        registerAllServices(bc, changeAuthorInstitutionAction, changeAuthorInstitutionActionProps);
    }
}