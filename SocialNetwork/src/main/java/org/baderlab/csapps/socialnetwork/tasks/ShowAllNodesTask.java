package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.InfoPanel;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public class ShowAllNodesTask extends AbstractTask {

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Showing all nodes and edges ...");
        SocialNetwork socialNetwork = SocialNetworkAppManager.getSelectedSocialNetwork();
        InfoPanel infoPanel = SocialNetworkAppManager.getInfoPanel();
        // All nodes and edges have to be made visible 
        for (final CyNode node : socialNetwork.getCyNetwork().getNodeList()) {
            socialNetwork.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_VISIBLE, true);                 
        }

        for (final CyEdge edge : socialNetwork.getCyNetwork().getEdgeList()) {
            socialNetwork.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);                    
        }
        socialNetwork.getNetworkView().updateView();
        infoPanel.getTextField().setText("ALL");
        infoPanel.getTextField().repaint();
    }

}
