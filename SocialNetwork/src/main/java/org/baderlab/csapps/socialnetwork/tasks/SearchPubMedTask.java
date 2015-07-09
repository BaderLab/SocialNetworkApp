package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.Search;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Task for performing a PubMed search.
 * 
 * @author Victor Kofia
 */
public class SearchPubMedTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(SearchPubMedTask.class.getName());

    /**
     * A reference to the {@link SocialNetworkAppManager}. Makes it possible to
     * retrieve the name of the network (usually same as the PubMed query).
     */
    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link SearchPubMedTask}.
     * 
     * @param SocialNetworkAppManager appManager
     */
    public SearchPubMedTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Searching PubMed");
        String searchTerm = this.appManager.getSearchTerm();
        int maxAuthorThreshold = this.appManager.getMaxAuthorThreshold();
        
        // Verify that network name is valid
        if (!this.appManager.isNameValid(searchTerm)) {
            CytoscapeUtilities.notifyUser(String.format("Network %s already exists in Cytoscape. Please enter a new name.", searchTerm));
            return;
        }
        // Create new search session
        Search search = new Search(searchTerm, Category.ACADEMIA, this.appManager);
        // Get a list of the results that are going to serve as edges. Exact
        // result type may vary with website
        List<? extends AbstractEdge> results = search.getResults();
        if (results == null) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            String message = "Network could not be loaded";
            logger.log(Level.WARNING, message);
            CytoscapeUtilities.notifyUser(message);
            return;
        }
        if (results.size() == 0) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            String message = "Search did not yield any results";
            logger.log(Level.WARNING, message);
            CytoscapeUtilities.notifyUser(message);
            return;
        }
        
        Map<Collaboration, ArrayList<AbstractEdge>> map = null;
        Interaction interaction = null;
        SocialNetwork socialNetwork = null;
        
        interaction = new Interaction(results, Category.PUBMED, maxAuthorThreshold);
        map = interaction.getAbstractMap();
        if (map.size() == 0) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network couldn't be loaded. Adjust max author threshold.");
            return;
        }
        socialNetwork = new SocialNetwork(searchTerm, Category.PUBMED);
        ArrayList<Publication> pubList = (ArrayList<Publication>) results;
        socialNetwork.setPublications(pubList);
        socialNetwork.setExcludedPubs(interaction.getExcludedPublications());
        // TODO:figure out how to add publications from pubmed search
        // socialNetwork.setPublications(search.getTotalHits());

        // Create new map using results
        // Set social network attributes
        // ??

        this.appManager.setNetworkName(searchTerm);
        this.appManager.getSocialNetworkMap().put(searchTerm, socialNetwork);
        // Transfer map to Cytoscape's map variable
        this.appManager.setMap(map);
        
        TaskIterator taskIterator = new TaskIterator();
        taskIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());
        insertTasksAfterCurrentTask(taskIterator);        
    }

}