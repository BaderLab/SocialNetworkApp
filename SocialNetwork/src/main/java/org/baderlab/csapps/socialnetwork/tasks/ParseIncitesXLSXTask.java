package org.baderlab.csapps.socialnetwork.tasks;


import java.util.logging.Logger;

import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Task for parsing an InCites XLSX file.
 * 
 * @author Victor Kofia
 */
public class ParseIncitesXLSXTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(ParseIncitesXLSXTask.class.getName());

    /**
     * A reference to the {@link SocialNetworkAppManager}. Makes it possible to
     * retrieve the name of the network (usually same as the Incites XLSX
     * filename).
     */
    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link ParseIncitesXLSXTask}.
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParseIncitesXLSXTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {


        TaskIterator taskIterator = new TaskIterator();
        
        String networkName = this.appManager.getNetworkName();

        SocialNetwork socialNetwork = new SocialNetwork(networkName, Category.INCITES);
        
        //parse incites
        IncitesParser incitesParser = new IncitesParser(this.appManager.getNetworkFile(),socialNetwork);
        taskIterator.append(incitesParser);
        
        CreatePublicationNetworkFromPublications createnetwork = new CreatePublicationNetworkFromPublications(appManager,socialNetwork,networkName);
        taskIterator.append(createnetwork);
       
        taskIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());
        insertTasksAfterCurrentTask(taskIterator);
    }

}
