package org.baderlab.csapps.socialnetwork.autoannotate;

import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.baderlab.csapps.socialnetwork.autoannotate.action.DisplayOptionsPanelAction;
import org.baderlab.csapps.socialnetwork.autoannotate.view.AutoAnnotationPanel;
import org.baderlab.csapps.socialnetwork.autoannotate.view.DisplayOptionsPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetSelectedNetworkViewsEvent;
import org.cytoscape.application.events.SetSelectedNetworkViewsListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.ColumnNameChangedEvent;
import org.cytoscape.model.events.ColumnNameChangedListener;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.annotations.AnnotationFactory;
import org.cytoscape.view.presentation.annotations.AnnotationManager;
import org.cytoscape.view.presentation.annotations.ShapeAnnotation;
import org.cytoscape.view.presentation.annotations.TextAnnotation;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.swing.DialogTaskManager;
/**
 * Created by
 * User: arkadyark
 * Date: July 9, 2014
 * Time: 9:46 AM
 */

public class AutoAnnotationManager implements
SetSelectedNetworkViewsListener, ColumnCreatedListener,
ColumnDeletedListener, ColumnNameChangedListener,
NetworkViewAboutToBeDestroyedListener {

    public static AutoAnnotationManager getInstance() {
        if (instance == null) {
            instance = new AutoAnnotationManager();
        }
        return instance;
    }
    // instance variable used to get the manager from other parts of the program
    private static AutoAnnotationManager instance = null;
    // used to control selected panels
    private CySwingApplication application;
    // reference to the panel that the user interacts with
    private AutoAnnotationPanel annotationPanel;
    // reference to the action to create the displayOptionsPanel
    private DisplayOptionsPanelAction displayOptionsPanelAction;
    // stores the annotation parameters (one for each network view)
    private HashMap<CyNetworkView, AutoAnnotationParameters> networkViewToAutoAnnotationParameters;
    // used to set clusterMaker default parameters
    public static final SortedMap<String, String> algorithmToColumnName;
    static {
        TreeMap<String, String> aMap = new TreeMap<String, String>();
        aMap.put("Affinity Propagation Cluster", "__APCluster");
        aMap.put("Cluster Fuzzifier", "__fuzzifierCluster");
        aMap.put("Community cluster (GLay)", "__glayCluster");
        aMap.put("ConnectedComponents Cluster", "__ccCluster");
        aMap.put("Fuzzy C-Means Cluster", "__fcmlCluster");
        aMap.put("MCL Cluster", "__mclCluster");
        aMap.put("SCPS Cluster", "__scpsCluster");
        algorithmToColumnName = Collections.unmodifiableSortedMap(aMap);
    }
    // used to read from the tables that WordCloud creates
    private CyTableManager tableManager;
    // used to execute command line commands
    private CommandExecutorTaskFactory commandExecutor;
    // used to execute annotation, WordCloud, and clusterMaker tasks
    private DialogTaskManager dialogTaskManager;
    // used to apply layouts
    private SynchronousTaskManager<?> syncTaskManager;
    //used for getting current network
    private CyApplicationManager applicationManager;
    // annotations are added to here
    private AnnotationManager annotationManager;
    // used to update the layout of the nodes
    private CyLayoutAlgorithmManager layoutManager;
    // creates ellipses
    private AnnotationFactory<ShapeAnnotation> shapeFactory;
    // creates text labels
    private AnnotationFactory<TextAnnotation> textFactory;
    // creates node groups stored in clusters
    private CyGroupFactory groupFactory;
    // used to destroy the groups
    private CyGroupManager groupManager;
    // used to force heatmap to update before selection
    private CyEventHelper eventHelper;

    private DisplayOptionsPanel displayOptionsPanel;

    //flag to indicate that currently in the middle of cluster table selection event
    private boolean clusterTableUpdating = false;

    public AutoAnnotationManager() {
        this.networkViewToAutoAnnotationParameters = new HashMap<CyNetworkView, AutoAnnotationParameters>();
    }

    public void flushPayloadEvents() {
        this.eventHelper.flushPayloadEvents();
    }

    public SortedMap<String, String> getAlgorithmToColumnName() {
        return algorithmToColumnName;
    }

    public AnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }

    public AutoAnnotationPanel getAnnotationPanel() {
        return this.annotationPanel;
    }

    public CySwingApplication getApplication() {
        return this.application;
    }

    public CyApplicationManager getApplicationManager() {
        return this.applicationManager;
    }

    public CommandExecutorTaskFactory getCommandExecutor() {
        return this.commandExecutor;
    }

    public DialogTaskManager getDialogTaskManager() {
        return this.dialogTaskManager;
    }

    public DisplayOptionsPanel getDisplayOptionsPanel() {
        return this.displayOptionsPanel;
    }

    public DisplayOptionsPanelAction getDisplayOptionsPanelAction() {
        return this.displayOptionsPanelAction;
    }

    public CyGroupFactory getGroupFactory() {
        return this.groupFactory;
    }

    public CyGroupManager getGroupManager() {
        return this.groupManager;
    }

    public CyLayoutAlgorithmManager getLayoutManager() {
        return this.layoutManager;
    }

    public HashMap<CyNetworkView, AutoAnnotationParameters> getNetworkViewToAutoAnnotationParameters() {
        return this.networkViewToAutoAnnotationParameters;
    }

    public AnnotationFactory<ShapeAnnotation> getShapeFactory() {
        return this.shapeFactory;
    }

    public CytoPanel getSouthPanel() {
        return this.application.getCytoPanel(CytoPanelName.SOUTH);
    }

    public SynchronousTaskManager<?> getSyncTaskManager() {
        return this.syncTaskManager;
    }

    public CyTableManager getTableManager() {
        return this.tableManager;
    }

    public AnnotationFactory<TextAnnotation> getTextFactory() {
        return this.textFactory;
    }

    public CytoPanel getWestPanel() {
        return this.application.getCytoPanel(CytoPanelName.WEST);
    }

    public void handleEvent(ColumnCreatedEvent e) {
        if (this.annotationPanel != null) {
            this.annotationPanel.columnCreated(e.getSource(), e.getColumnName());
        }
    }

    public void handleEvent(ColumnDeletedEvent e) {
        if (this.annotationPanel != null) {
            this.annotationPanel.columnDeleted(e.getSource(), e.getColumnName());
        }
    }

    public void handleEvent(ColumnNameChangedEvent e) {
        if (this.annotationPanel != null) {
            this.annotationPanel.updateColumnName(e.getSource(), e.getOldColumnName(), e.getNewColumnName());
        }
    }

    public void handleEvent(NetworkViewAboutToBeDestroyedEvent e) {
        if (this.annotationPanel != null) {
            this.annotationPanel.removeNetworkView(e.getNetworkView());
        }
    }

    public void handleEvent(SetSelectedNetworkViewsEvent e) {
        if (this.annotationPanel != null) {
            this.annotationPanel.updateSelectedView(e.getNetworkViews().get(0));
        }
    }

    // Initialize all of the services that will be needed for auto annotation
    public void initialize(CySwingApplication application, CyTableManager tableManager,
            CommandExecutorTaskFactory commandExecutor,	DialogTaskManager dialogTaskManager,
            SynchronousTaskManager<?> syncTaskManager, AnnotationManager annotationManager,
            CyLayoutAlgorithmManager layoutManager, AnnotationFactory<ShapeAnnotation> shapeFactory,
            AnnotationFactory<TextAnnotation> textFactory, CyGroupFactory groupFactory,
            CyGroupManager groupManager, CyEventHelper eventHelper,
            CyApplicationManager applicationManager) {

        this.application = application;
        this.tableManager = tableManager;
        this.commandExecutor = commandExecutor;
        this.dialogTaskManager = dialogTaskManager;
        this.syncTaskManager = syncTaskManager;
        this.annotationManager = annotationManager;
        this.layoutManager = layoutManager;
        this.shapeFactory = shapeFactory;
        this.textFactory = textFactory;
        this.groupFactory = groupFactory;
        this.groupManager = groupManager;
        this.eventHelper = eventHelper;
        this.applicationManager = applicationManager;
    }

    public boolean isClusterTableUpdating() {
        return this.clusterTableUpdating;
    }

    public void setAnnotationPanel(AutoAnnotationPanel inputPanel) {
        this.annotationPanel = inputPanel;
    }

    public void setClusterTableUpdating(boolean clusterTableUpdating) {
        this.clusterTableUpdating = clusterTableUpdating;
    }

    public void setDisplayOptionsPanel(DisplayOptionsPanel displayOptionsPanel) {
        this.displayOptionsPanel = displayOptionsPanel;
    }

    public void setDisplayOptionsPanelAction(DisplayOptionsPanelAction displayOptionsPanelAction) {
        this.displayOptionsPanelAction = displayOptionsPanelAction;
    }

    public void setNetworkViewToAutoAnnotation(
            HashMap<CyNetworkView, AutoAnnotationParameters> networkViewToAutoAnnotationParameters) {
        this.networkViewToAutoAnnotationParameters = networkViewToAutoAnnotationParameters;
    }


}
