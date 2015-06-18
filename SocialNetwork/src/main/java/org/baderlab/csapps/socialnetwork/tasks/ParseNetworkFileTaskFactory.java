package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class ParseNetworkFileTaskFactory extends AbstractTaskFactory {
    
    private SocialNetworkAppManager appManager = null;
    
    /**
     * ??
     * 
     * @param SocialNetworkAppManager appManager
     */
    // TODO: Write constructor description
    public ParseNetworkFileTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new ParseNetworkFileTask(appManager));
    }

}
