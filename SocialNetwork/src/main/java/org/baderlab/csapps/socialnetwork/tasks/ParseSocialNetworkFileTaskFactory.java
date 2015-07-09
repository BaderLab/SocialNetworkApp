package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 * TaskFactory for {@link ParseSocialNetworkFileTask}
 * 
 * @author Victor Kofia
 */
public class ParseSocialNetworkFileTaskFactory extends AbstractTaskFactory {
    private SocialNetworkAppManager appManager = null;
    private TaskManager<?, ?> taskManagerServiceRef = null;
    private ParseIncitesXLSXTaskFactory parseIncitesXLSXTaskFactoryRef = null;
    private ParsePubMedXMLTaskFactory parsePubMedXMLTaskFactoryRef = null;
    private ParseScopusCSVTaskFactory parseScopusCSVTaskFactoryRef = null;

    /**
     * Constructor for {@link ParseSocialNetworkFileFactory}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParseSocialNetworkFileTaskFactory(SocialNetworkAppManager appManager, 
            TaskManager<?, ?> taskManagerServiceRef,
            ParseIncitesXLSXTaskFactory parseIncitesXLSXTaskFactoryRef,
            ParsePubMedXMLTaskFactory parsePubMedXMLTaskFactoryRef, ParseScopusCSVTaskFactory parseScopusCSVTaskFactoryRef) {
        this.appManager = appManager;
        this.taskManagerServiceRef = taskManagerServiceRef;
        this.parseIncitesXLSXTaskFactoryRef = parseIncitesXLSXTaskFactoryRef;
        this.parsePubMedXMLTaskFactoryRef = parsePubMedXMLTaskFactoryRef;
        this.parseScopusCSVTaskFactoryRef = parseScopusCSVTaskFactoryRef;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new ParseSocialNetworkFileTask(appManager, taskManagerServiceRef, 
                parseIncitesXLSXTaskFactoryRef, parsePubMedXMLTaskFactoryRef, parseScopusCSVTaskFactoryRef));
    }

}