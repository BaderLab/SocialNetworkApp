package org.baderlab.csapps.socialnetwork.autoannotate.task;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTable;

import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationParameters;
import org.baderlab.csapps.socialnetwork.autoannotate.model.AnnotationSet;
import org.baderlab.csapps.socialnetwork.autoannotate.model.Cluster;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.DeleteClusterTask;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class RemoveAnnotationTask extends AbstractTask {

    private AutoAnnotationParameters params;


    public RemoveAnnotationTask(AutoAnnotationParameters params) {
        super();
        this.params = params;
    }

    public void removeAction() {

        AnnotationSet annotationSet = this.params.getSelectedAnnotationSet();
        CyNetwork selectedNetwork = annotationSet.getView().getModel();

        JTable clusterTable = annotationSet.getClusterTable();
        clusterTable.getParent().getParent().getParent().remove(clusterTable.getParent().getParent());
        // Delete all annotations
        Iterator<Cluster> clusterIterator = annotationSet.getClusterMap().values().iterator();
        // Iterate over a copy to prevent Concurrent Modification
        ArrayList<Cluster> clusterSetCopy = new ArrayList<Cluster>();
        while (clusterIterator.hasNext()){
            clusterSetCopy.add(clusterIterator.next());
        }
        // Delete each cluster (WordCloud)
        TaskIterator clusterDeletionTasks = new TaskIterator();
        for (Cluster cluster : clusterSetCopy) {
            clusterDeletionTasks.append(new DeleteClusterTask(this.params,cluster));
        }
        AutoAnnotationManager.getInstance().getDialogTaskManager().execute(clusterDeletionTasks);
        this.params.removeAnnotationSet(annotationSet);

    }

    @Override
    public void run(TaskMonitor arg0) throws Exception {
        removeAction();

    }

}
