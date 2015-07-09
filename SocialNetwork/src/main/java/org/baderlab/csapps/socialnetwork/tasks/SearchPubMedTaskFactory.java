package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * TaskFactory for {@link SearchPubMedTask}
 * 
 * @author Victor Kofia
 */
public class SearchPubMedTaskFactory extends AbstractTaskFactory {

    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link SearchPubMedFactory}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public SearchPubMedTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new SearchPubMedTask(appManager));
    }

}