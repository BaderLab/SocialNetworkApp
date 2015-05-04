package org.baderlab.csapps.socialnetwork.autoannotate.task;
import org.baderlab.csapps.socialnetwork.autoannotate.model.Cluster;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.DrawClusterEllipseTask;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.DrawClusterLabelTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class VisualizeClusterAnnotationTaskFactory implements TaskFactory{

    private Cluster cluster;

    public VisualizeClusterAnnotationTaskFactory(Cluster cluster) {
        super();
        this.cluster = cluster;
    }

    public TaskIterator createTaskIterator() {

        TaskIterator currentTasks = new TaskIterator();
        if(this.cluster != null){
            DrawClusterEllipseTask drawEllipseTask = new DrawClusterEllipseTask(this.cluster);
            currentTasks.append(drawEllipseTask);
            DrawClusterLabelTask drawLabelTask = new DrawClusterLabelTask(this.cluster);
            currentTasks.append(drawLabelTask);
        }

        return currentTasks;
    }

    public boolean isReady() {
        if(this.cluster != null) {
            return true;
        }
        return false;
    }
}
