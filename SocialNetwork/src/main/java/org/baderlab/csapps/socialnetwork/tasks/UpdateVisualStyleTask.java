package org.baderlab.csapps.socialnetwork.tasks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.VisualStyles;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.EdgeAttribute;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: ??
public class UpdateVisualStyleTask extends AbstractTask {

    private ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = null;
    private TaskManager<?, ?> taskManager = null;
    private VisualMappingManager visualMappingManager = null;
    private VisualMappingFunctionFactory discreteMappingFactoryServiceRef = null;
    private CyNetworkView cyNetworkView = null;
    private SocialNetworkAppManager appManager = null;
    private CyApplicationManager cyApplicationManager = null;
    private SocialNetwork socialNetwork = null;
    private CyNetwork cyNetwork = null;
    private int startYear = -1, endYear = -1;

    public UpdateVisualStyleTask(TaskManager<?, ?> taskManager, SocialNetworkAppManager appManager,
            VisualMappingManager visualMappingManager, VisualMappingFunctionFactory discrete,
            CyApplicationManager cyApplicationManagerServiceRef, ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef) {
        this.taskManager = taskManager;
        this.appManager = appManager;
        this.cyApplicationManager = cyApplicationManagerServiceRef;
        this.cyNetwork = cyApplicationManagerServiceRef.getCurrentNetwork();
        this.cyNetworkView = cyApplicationManagerServiceRef.getCurrentNetworkView();
        this.visualMappingManager = visualMappingManager;
        this.discreteMappingFactoryServiceRef = discrete;
        this.applyVisualStyleTaskFactoryRef = applyVisualStyleTaskFactoryRef;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        SocialNetwork socialNetwork = SocialNetworkAppManager.getSelectedSocialNetwork();
        this.cyNetwork = this.cyApplicationManager.getCurrentNetwork();
        this.cyNetworkView = this.cyApplicationManager.getCurrentNetworkView();

        if (this.cyNetworkView == null) {
            return;
        }
                
        taskMonitor.setTitle(String.format("Loading %s Visual Style ... ", 
                VisualStyles.toString(socialNetwork.getVisualStyleId())));

        int year = SocialNetworkAppManager.getSelectedYear();

        this.startYear = socialNetwork.getStartYear();
        this.endYear = socialNetwork.getEndYear();

        Iterator<CyEdge> edgeIt = this.cyNetwork.getEdgeList().iterator();
        //HashSet<CyEdge> selectedEdges = new HashSet<CyEdge>();
        //HashSet<CyEdge> deselectedEdges = new HashSet<CyEdge>();
        HashSet<CyNode> selectedNodes = new HashSet<CyNode>();
        CyEdge edge = null;
        View<CyEdge> edgeView = null;
        CyTable defaultEdgeTable = this.cyNetwork.getDefaultEdgeTable();
        while (edgeIt.hasNext()) {
            edge = edgeIt.next();
            edgeView = this.cyNetworkView.getEdgeView(edge);
            List<Integer> pubsPerYear = (List<Integer>) CytoscapeUtilities.getCyTableAttribute(defaultEdgeTable, edge.getSUID(), 
                    EdgeAttribute.PUBS_PER_YEAR.toString());
            if (pubsPerYear.get(year - startYear) == 1) {
                selectedNodes.add(edge.getSource());
                selectedNodes.add(edge.getTarget());
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);
                CytoscapeUtilities.setCyTableAttribute(defaultEdgeTable, edge.getSUID(), 
                        EdgeAttribute.IS_SELECTED.toString(), true);
            } else {
                //deselectedEdges.add(edge);
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);
                CytoscapeUtilities.setCyTableAttribute(defaultEdgeTable, edge.getSUID(), 
                       EdgeAttribute.IS_SELECTED.toString(), false);
             }
        }
        
        /*
        Iterator<CyNode> selectedNodeIt = selectedNodes.iterator();
        CyNode selectedNode = null;
        View<CyNode> selectedNodeView = null;
        CyTable defaultNodeTable = this.cyNetwork.getDefaultNodeTable();
        while (selectedNodeIt.hasNext()) {
            selectedNode = selectedNodeIt.next();
            //selectedNodeView = this.cyNetworkView.getNodeView(selectedNode);
            //selectedNodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, true);
            CytoscapeUtilities.setCyTableAttribute(defaultNodeTable, selectedNode.getSUID(), 
                    NodeAttribute.IS_SELECTED.toString(), true);
        }
        */
        
        
        Iterator<CyNode> nodeIt = this.cyNetwork.getNodeList().iterator();
        CyNode node = null;
        View<CyNode > nodeView = null;
        CyTable defaultNodeTable = this.cyNetwork.getDefaultNodeTable();
        // HashSet<CyNode> deselectedNodes = new HashSet<CyNode>();
        while (nodeIt.hasNext()) {
            node = nodeIt.next();
            nodeView = this.cyNetworkView.getNodeView(node);
            if (!selectedNodes.contains(node)) {
                //deselectedNodes.add(node);
                // TODO: change opacity to 0
                nodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, false);
                CytoscapeUtilities.setCyTableAttribute(defaultNodeTable, node.getSUID(), 
                        NodeAttribute.IS_SELECTED.toString(), false);
            } else {
                nodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, true);
                CytoscapeUtilities.setCyTableAttribute(defaultNodeTable, node.getSUID(), 
                        NodeAttribute.IS_SELECTED.toString(), true);
            }
        }

        //this.cyNetworkView.updateView();
        
        /*
        // Set opacity of deselected nodes and deselected edges to 0
        VisualStyle vs = CytoscapeUtilities.getVisualStyle
                (VisualStyles.toString(socialNetwork.getVisualStyleId()), this.visualMappingManager);

        // Node visual style
        DiscreteMapping nodeOpacityMapping = (DiscreteMapping<Boolean, ?>) this.discreteMappingFactoryServiceRef
                .createVisualMappingFunction(NodeAttribute.IS_SELECTED.toString(), Boolean.class, 
                        BasicVisualLexicon.NODE_TRANSPARENCY);

        nodeOpacityMapping.putMapValue(false, 0.0);
        nodeOpacityMapping.putMapValue(true, 255.0);

        // Edge visual style
        DiscreteMapping edgeOpacityMapping = (DiscreteMapping<Boolean, ?>) this.discreteMappingFactoryServiceRef
                .createVisualMappingFunction(EdgeAttribute.IS_SELECTED.toString(), Boolean.class, 
                        BasicVisualLexicon.EDGE_TRANSPARENCY);

        edgeOpacityMapping.putMapValue(false, 0.0);
        edgeOpacityMapping.putMapValue(true, 255.0);

        vs.addVisualMappingFunction(nodeOpacityMapping);

        // Set current visual style to new modified visual style
        this.visualMappingManager.setCurrentVisualStyle(vs);
         */
        
        this.taskManager.execute(this.applyVisualStyleTaskFactoryRef.createTaskIterator());

    }

}
