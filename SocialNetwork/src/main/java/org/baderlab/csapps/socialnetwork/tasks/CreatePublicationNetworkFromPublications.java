package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Copublication;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class CreatePublicationNetworkFromPublications extends AbstractTask {


    private static final Logger logger = Logger.getLogger(SearchPubMedTaskFactory.class.getName());

    /**
     * A reference to the {@link SocialNetworkAppManager}. Makes it possible to
     * retrieve the name of the network (usually same as the PubMed query).
     */
    private SocialNetworkAppManager appManager = null;

    private SocialNetwork socialNetwork = null;
    
    private String name = null;
    
    private ArrayList<Publication> excludedPublications = null;
    
    private TaskMonitor taskMonitor = null;
    
    
	
	public CreatePublicationNetworkFromPublications(
			SocialNetworkAppManager appManager, SocialNetwork socialNetwork, String searchTerm) {
		super();
		this.appManager = appManager;
		this.socialNetwork = socialNetwork;
		this.name = searchTerm;
		this.excludedPublications = new ArrayList<Publication>();
	}



	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		this.taskMonitor = taskMonitor;
		taskMonitor.setStatusMessage("Creating Co-publication network from retrieved Pubmed results");
		
		int maxAuthorThreshold = this.appManager.getMaxAuthorThreshold();
		
		// Get a list of the results that are going to serve as edges. Exact
        // result type may vary with website
        List<? extends AbstractEdge> results = socialNetwork.getPublications();
        if (results == null) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            String message = "No results found";
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
        
        //parse the publications
        Map<Collaboration, ArrayList<AbstractEdge>> academiamap = loadAcademiaMap(results,maxAuthorThreshold);
                
        Interaction interaction = new Interaction(academiamap, Category.PUBMED);
        
        map = interaction.getAbstractMap();
        if (map.size() == 0) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network couldn't be loaded. Adjust max author threshold.");
            return ;
        }
        
        ArrayList<Publication> pubList = (ArrayList<Publication>) results;
        socialNetwork.setPublications(pubList);
        socialNetwork.setExcludedPubs(interaction.getExcludedPublications());
        // TODO:figure out how to add publications from pubmed search
        // socialNetwork.setPublications(search.getTotalHits());

        // Create new map using results
        // Set social network attributes
        // ??

        this.appManager.setNetworkName(name);
        this.appManager.getSocialNetworkMap().put(name, socialNetwork);
        // Transfer map to Cytoscape's map variable
        this.appManager.setMap(map);
		
	}
	
    /**
     * Create new Academia hash-map
     *
     * @param ArrayList abstractEdgeList
     * @param int maxAuthor
     * @return Map academiaMap
     */
    private Map<Collaboration, ArrayList<AbstractEdge>> loadAcademiaMap(List<? extends AbstractEdge> results, int maxThreshold) {
        // Create new academia map
        Map<Collaboration, ArrayList<AbstractEdge>> academiaMap = new HashMap<Collaboration, ArrayList<AbstractEdge>>();
        // Create new author map
        // Key: author's facsimile
        // Value: actual author
        Map<Author, Author> authorMap = new HashMap<Author, Author>();
        int h = 0, i = 0, j = 0;
        Collaboration collaboration = null;
        Author author1 = null, author2 = null;
        Copublication copublications = null;
        Publication publication = null;
        List<Author> listOfNodes = null;
        HashSet<Publication> pubSet = new HashSet<Publication>();
        // Iterate through each publication
        while (h <= results.size() - 1) {
            publication = (Publication) results.get(h);
            if (pubSet.contains(publication)) {
                h++;
                continue;
            }
            // Include publication only if the # of authors does not exceed the
            // threshold
            if ((maxThreshold < 0) || (publication.getNodes().size() <= maxThreshold)) {
                i = 0;
                j = 0;
                collaboration = null;
                author1 = null;
                author2 = null;
                copublications = null;
                listOfNodes = null;
                listOfNodes = (List<Author>) publication.getNodes();
                while (i < listOfNodes.size()) {
                    // Add author#1 to map if he / she is not present
                    author1 = listOfNodes.get(i);
                    if (authorMap.get(author1) == null) {
                        authorMap.put(author1, author1);
                    }
                    // Add current publication to author's total list
                    // of publications
                    // NOTE: Author's time cited value will be updated
                    // automatically
                    authorMap.get(author1).addPublication(publication);
                    j = i + 1;
                    while (j < listOfNodes.size()) {
                        // Add author#2 to map if he / she is not present
                        author2 = listOfNodes.get(j);
                        if (authorMap.get(author2) == null) {
                            authorMap.put(author2, author2);
                        }
                        // Create collaboration out of both authors
                        collaboration = new Collaboration(authorMap.get(author1), authorMap.get(author2));
                        // Check for collaboration's existence before it's
                        // entered
                        // into map
                        if (!academiaMap.containsKey(collaboration)) {
                            copublications = new Copublication(collaboration, publication);
                            ArrayList<AbstractEdge> edgeList = new ArrayList<AbstractEdge>();
                            edgeList.add(copublications);
                            academiaMap.put(collaboration, edgeList);
                        } else {
                            ArrayList<AbstractEdge> array = academiaMap.get(collaboration);
                            copublications = (Copublication) array.get(0);
                            copublications.addPublication(publication);
                        }
                        j++;
                    }
                    i++;
                }
            } else {
                this.excludedPublications.add(publication);
            }
            pubSet.add(publication);
            h++;
            taskMonitor.setProgress((int) (((double) h/results.size()) * 100));
        }
        return academiaMap;
    }

}
