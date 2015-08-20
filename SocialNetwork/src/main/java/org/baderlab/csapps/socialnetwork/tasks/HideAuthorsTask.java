package org.baderlab.csapps.socialnetwork.tasks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write task description
public class HideAuthorsTask extends AbstractTask {
    
    private static final Logger logger = Logger.getLogger(HideAuthorsTask.class.getName());

    private CyNetworkView cyNetworkView = null;
    private CyApplicationManager cyApplicationManager = null;
    private CyNetwork cyNetwork = null;
    private int startYear = -1, endYear = -1;

    public HideAuthorsTask(CyApplicationManager cyApplicationManagerServiceRef) {
        this.cyApplicationManager = cyApplicationManagerServiceRef;
        this.cyNetwork = cyApplicationManagerServiceRef.getCurrentNetwork();
        this.cyNetworkView = cyApplicationManagerServiceRef.getCurrentNetworkView();
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        SocialNetwork socialNetwork = SocialNetworkAppManager.getSelectedSocialNetwork();
        this.cyNetwork = socialNetwork.getCyNetwork();
        this.cyNetworkView = socialNetwork.getNetworkView();

        if (this.cyNetworkView == null) {
            return;
        }
                
        taskMonitor.setTitle(String.format("Loading %s Visual Style ... ", 
                VisualStyles.toString(socialNetwork.getVisualStyleId())));

        int year = SocialNetworkAppManager.getSelectedYear();

        this.startYear = socialNetwork.getStartYear();
        this.endYear = socialNetwork.getEndYear();

        Iterator<CyEdge> edgeIt = this.cyNetwork.getEdgeList().iterator();
        HashSet<CyNode> selectedNodes = new HashSet<CyNode>();
        CyEdge edge = null;
        View<CyEdge> edgeView = null;
        View<CyNode> sourceView = null, targetView = null;
        CyTable defaultEdgeTable = this.cyNetwork.getDefaultEdgeTable();
        while (edgeIt.hasNext()) {
            edge = edgeIt.next();
            edgeView = this.cyNetworkView.getEdgeView(edge);
            List<Integer> pubsPerYear = (List<Integer>) CytoscapeUtilities.getCyTableAttribute(defaultEdgeTable, edge.getSUID(), 
                    EdgeAttribute.PUBS_PER_YEAR.toString());
            if (pubsPerYear.get(year - this.startYear) > 0) { // TODO:
                selectedNodes.add(edge.getSource());
                selectedNodes.add(edge.getTarget());
                sourceView = this.cyNetworkView.getNodeView(edge.getSource());
                targetView = this.cyNetworkView.getNodeView(edge.getTarget());
                sourceView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, true);
                targetView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, true);
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);
            } else {
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);
            }
        }

        Iterator<CyNode> nodeIt = this.cyNetwork.getNodeList().iterator();
        CyNode node = null;
        View<CyNode > nodeView = null;
        CyTable defaultNodeTable = this.cyNetwork.getDefaultNodeTable();
        while (nodeIt.hasNext()) {
            try {
                node = nodeIt.next();
                nodeView = this.cyNetworkView.getNodeView(node);
                if (!selectedNodes.contains(node)) {
                    nodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, false);
                } 
            } catch (Exception e) {
                String label = (String) CytoscapeUtilities.getCyTableAttribute(defaultNodeTable, node.getSUID(), 
                        NodeAttribute.LABEL.toString());
                logger.log(Level.WARNING, String.format("Unable to hide \"%s\"", label.trim()));
            }

        }

        this.cyNetworkView.updateView();
        
    }

}
