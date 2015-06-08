package org.baderlab.csapps.socialnetwork.autoannotate.task;

import java.util.ArrayList;

import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationParameters;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class RunClustermakerTaskFactory implements TaskFactory {


    private CyNetwork network;
    private CyNetworkView view;
    private String clusterColumnName;
    private String algorithm;


    private TaskMonitor taskMonitor = null;


    public RunClustermakerTaskFactory(AutoAnnotationParameters params) {
        super();

        this.network = params.getNetwork();
        this.view = params.getNetworkView();
        this.clusterColumnName = params.getClusterColumnName();
        this.algorithm = params.getAlgorithm();

    }

    public TaskIterator createTaskIterator() {
        // Delete potential existing columns - sometimes clusterMaker doesn't do this
        if (this.network.getDefaultNodeTable().getColumn(this.clusterColumnName) != null) {
            this.network.getDefaultNodeTable().deleteColumn(this.clusterColumnName);
        }

        // Cluster based on similarity coefficient if possible
        String edgeAttribute = "--None--";

        //select all the nodes to cluster
        for (View<CyNode> nodeView : this.view.getNodeViews()) {
            if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE)) {
                this.network.getRow(nodeView.getModel()).set(CyNetwork.SELECTED, true);
            }
        }

        // Executes the task inside of clusterMaker
        ArrayList<String> commands = new ArrayList<String>();
        commands.add(getCommand(this.algorithm, edgeAttribute, this.network.toString()));
        TaskIterator taskIterator = AutoAnnotationManager.getInstance().getCommandExecutor().createTaskIterator(commands,null);

        return taskIterator;
    }

    private String getCommand(String algorithm, String edgeAttribute, String networkName) {
        String command = "";
        if (algorithm == "Affinity Propagation Cluster") {
            command = "cluster ap attribute=\"" + edgeAttribute + "\" clusterAttribute=\"" + this.clusterColumnName + "\" selectedOnly=true";
        } else if (algorithm == "Cluster Fuzzifier") {
            command = "cluster fuzzifier attribute=\"" + edgeAttribute + "\" clusterAttribute=\"" + this.clusterColumnName + "\" selectedOnly=true";
        } else if (algorithm == "Community cluster (GLay)") {
            command = "cluster glay clusterAttribute=\"" + this.clusterColumnName + "\" selectedOnly=true";
        } else if (algorithm == "ConnectedComponents Cluster") {
            command = "cluster connectedcomponents attribute=\"" + edgeAttribute + "\" clusterAttribute=\"" + this.clusterColumnName + "\" selectedOnly=true";
        } else if (algorithm == "Fuzzy C-Means Cluster") {
            command = "cluster fcml attribute=\"" + edgeAttribute + "\" clusterAttribute=\"" + this.clusterColumnName + "\" selectedOnly=true";
        } else if (algorithm == "MCL Cluster") {
            command = "cluster mcl attribute=\"" + edgeAttribute + "\" clusterAttribute=\"" + this.clusterColumnName + "\" selectedOnly=true";
        } else if (algorithm == "SCPS Cluster") {
            command = "cluster scps attribute=\"" + edgeAttribute + "\" clusterAttribute=\"" + this.clusterColumnName + "\" selectedOnly=true";
        }
        return command;
    }


    public boolean isReady() {
        // TODO Auto-generated method stub
        return false;
    }
}
