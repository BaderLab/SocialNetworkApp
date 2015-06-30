package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * TaskFactory for {@link ParsePubMedXMLTask}
 * 
 * @author Victor Kofia
 */
public class ParsePubMedXMLTaskFactory extends AbstractTaskFactory {
    
    private SocialNetworkAppManager appManager = null;
    
    /**
     * Constructor for {@link ParsePubMedXMLFactory}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParsePubMedXMLTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new ParsePubMedXMLTask(appManager));
    }

}
