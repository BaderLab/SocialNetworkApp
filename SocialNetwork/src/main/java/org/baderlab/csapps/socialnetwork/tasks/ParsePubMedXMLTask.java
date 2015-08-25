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
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Task for parsing a PubMed XML file.
 * 
 * @author Victor Kofia
 */
public class ParsePubMedXMLTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(ParsePubMedXMLTask.class.getName());

    /**
     * A reference to the {@link SocialNetworkAppManager}. Makes it possible to
     * retrieve the name of the network (usually same as the query used to
     * retrieve the PubMed XML)
     */
    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link ParsePubMedXMLTask}.
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParsePubMedXMLTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Loading PubMed Network ...");
        String networkName = this.appManager.getNetworkName();

        SocialNetwork socialNetwork = new SocialNetwork(networkName, Category.PUBMED);
        PubMed pubmed = new PubMed(this.appManager.getNetworkFile(), taskMonitor);
        ArrayList<Publication> pubList = pubmed.getPubList();
        socialNetwork.setPublications(pubmed.getPubList());
        if (pubList == null) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        // Create interaction
        Interaction interaction = new Interaction(pubList, Category.ACADEMIA, this.appManager.getMaxAuthorThreshold());
        if (interaction.getExcludedPublications().size() == pubList.size()) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            String message = "Network couldn't be loaded. Adjust max author threshold";
            logger.log(Level.SEVERE, message);
            CytoscapeUtilities.notifyUser(message);
            return;
        }
        socialNetwork.setExcludedPubs(interaction.getExcludedPublications());
        
        // Create map
        Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
        if (map.size() == 0) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            String message = "Network couldn't be loaded. File is corrupt.";
            logger.log(Level.SEVERE, message);
            CytoscapeUtilities.notifyUser(message);
            return;
        }
        this.appManager.setMap(map);
        this.appManager.getSocialNetworkMap().put(networkName, socialNetwork);

        TaskIterator taskIterator = new TaskIterator();
        taskIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());
        insertTasksAfterCurrentTask(taskIterator);
    }

}
