package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * TaskFactory for {@link ParseIncitesXLSXTask}
 * 
 * @author Victor Kofia
 */
public class ParseIncitesXLSXTaskFactory extends AbstractTaskFactory {
    
    private SocialNetworkAppManager appManager = null;
    
    /**
     * Constructor for {@link ParseIncitesXLSXFactory}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParseIncitesXLSXTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new ParseIncitesXLSXTask(appManager));
    }

}
