package org.baderlab.csapps.socialnetwork.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


public class HideAuthorsTaskFactory extends AbstractTaskFactory {
    
    private CyApplicationManager cyApplicationManagerServiceRef = null;
    
    public HideAuthorsTaskFactory(CyApplicationManager cyApplicationManagerServiceRef) {
        this.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new HideAuthorsTask(this.cyApplicationManagerServiceRef));
    }

}
