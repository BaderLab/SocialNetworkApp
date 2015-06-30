package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * TaskFactory for {@link ParseScopusCSVTask}
 * 
 * @author Victor Kofia
 */
public class ParseScopusCSVTaskFactory extends AbstractTaskFactory {
    
    private SocialNetworkAppManager appManager = null;
    
    /**
     * Constructor for {@link ParseScopusCSVFactory}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParseScopusCSVTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new ParseScopusCSVTask(appManager));
    }

}
