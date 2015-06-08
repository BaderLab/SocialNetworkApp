package org.baderlab.csapps.socialnetwork.autoannotate.task;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationParameters;
import org.baderlab.csapps.socialnetwork.autoannotate.model.AnnotationSet;
import org.baderlab.csapps.socialnetwork.autoannotate.model.Cluster;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class RunWordCloudForClustersTaskFactory implements TaskFactory{

    private AnnotationSet annotationSet;
    private AutoAnnotationParameters params;
    private CyNetwork network;

    private CommandExecutorTaskFactory executor;



    public RunWordCloudForClustersTaskFactory(AnnotationSet annotationSet,
            AutoAnnotationParameters params) {
        super();
        this.annotationSet = annotationSet;
        this.params = params;
        this.network = params.getNetwork();

        this.executor = AutoAnnotationManager.getInstance().getCommandExecutor();
    }

    public TaskIterator createTaskIterator() {
        TreeMap<Integer, Cluster> clusterMap = this.annotationSet.getClusterMap();
        ArrayList<String> commands = new ArrayList<String>();
        for (int clusterNumber : clusterMap.keySet()) {

            //ArrayList<String> commands = new ArrayList<String>();

            Cluster cluster = clusterMap.get(clusterNumber);

            Set<CyNode> current_nodes = cluster.getNodes();
            String names = "";

            for(CyNode node : current_nodes){
                names = names +  "SUID:" + this.network.getRow(node).get(CyNetwork.SUID,  Long.class) + ",";
            }
            String command = "wordcloud create wordColumnName=\"" + this.params.getAnnotateColumnName() + "\"" +
                    " cloudName=\"" + this.params.getName()+ " Cloud " +  clusterNumber + "\""
                    + " cloudGroupTableName=\"" + this.params.getName() + "\"" + " nodelist=\"" + names + "\"";

            commands.add(command);
        }

        return this.executor.createTaskIterator(commands, null);

    }

    public boolean isReady() {
        // TODO Auto-generated method stub
        return false;
    }




}
