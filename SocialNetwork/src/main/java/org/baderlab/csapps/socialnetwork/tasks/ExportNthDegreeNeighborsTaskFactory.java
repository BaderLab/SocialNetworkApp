package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * TaskFactory for {@link ExportNthDegreeNeighborsTask}
 * 
 * @author Victor Kofia
 */
public class ExportNthDegreeNeighborsTaskFactory extends AbstractTaskFactory {

    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link ExportNthDegreeNeighborsFactory}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ExportNthDegreeNeighborsTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new ExportNthDegreeNeighborsTask(appManager));
    }

}