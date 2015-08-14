package org.baderlab.csapps.socialnetwork.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


public class ShowAllNodesTaskFactory extends AbstractTaskFactory {

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new ShowAllNodesTask());
    }

}
