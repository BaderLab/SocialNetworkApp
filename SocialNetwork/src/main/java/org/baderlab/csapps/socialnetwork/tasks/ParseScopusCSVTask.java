package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.ParseScopus;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Task for parsing an Scopus CSV file.
 * 
 * @author Victor Kofia
 */
public class ParseScopusCSVTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(ParseScopusCSVTask.class.getName());

    /**
     * A reference to the {@link SocialNetworkAppManager}. Used to
     * retrieve the name of the network (usually same as the Scopus CSV
     * filename), the network file and a reference to the user panel.
     */
    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link ParseScopusCSVTask}.
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParseScopusCSVTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Loading Scopus Network ...");
        TaskIterator taskIterator = new TaskIterator();
        String networkName = this.appManager.getNetworkName();
        SocialNetwork socialNetwork = new SocialNetwork(networkName, Category.SCOPUS);
        ParseScopus parseScopus = new ParseScopus(this.appManager.getNetworkFile(), socialNetwork, appManager);
        taskIterator.append(parseScopus);
        
        // Create interaction
        CreatePublicationNetworkFromPublications createnetwork = new CreatePublicationNetworkFromPublications(appManager,socialNetwork,networkName);
        taskIterator.append(createnetwork);       	
        
        taskIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());
        insertTasksAfterCurrentTask(taskIterator);
    }

}
