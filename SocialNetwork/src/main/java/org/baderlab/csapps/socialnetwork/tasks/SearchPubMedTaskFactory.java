package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.util.logging.Logger;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsRetrievalParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsSearchParser;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


/**
 * Task for performing a PubMed search.
 * 
 * @author Victor Kofia
 */
public class SearchPubMedTaskFactory extends AbstractTaskFactory {

    private static final Logger logger = Logger.getLogger(SearchPubMedTaskFactory.class.getName());

    /**
     * A reference to the {@link SocialNetworkAppManager}. Makes it possible to
     * retrieve the name of the network (usually same as the PubMed query).
     */
    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link SearchPubMedTaskFactory}.
     * 
     * @param SocialNetworkAppManager appManager
     */
    public SearchPubMedTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

   

	public TaskIterator createTaskIterator() {
        TaskIterator taskIterator = new TaskIterator();

	        String searchTerm = this.appManager.getSearchTerm();	        
	        
	        // Verify that network name is valid
	        if (!this.appManager.isNameValid(searchTerm)) {
	            String message = String.format("Network %s already exists in Cytoscape. Please enter a new name.", searchTerm);
	            CytoscapeUtilities.notifyUser(message);
	            return taskIterator;
	        }
	        SocialNetwork socialNetwork = new SocialNetwork(searchTerm, Category.PUBMED);
	        appManager.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));        

	        // Create new search session
	        Query query = new Query(searchTerm);
	        EutilsSearchParser eUtilsSearchParser = new EutilsSearchParser(query,socialNetwork);
	        taskIterator.append(eUtilsSearchParser);
	     	        
	        EutilsRetrievalParser eUtilsRetParser = new EutilsRetrievalParser(socialNetwork);
	        taskIterator.append(eUtilsRetParser);
	        
	        CreatePublicationNetworkFromPublications createnetwork = new CreatePublicationNetworkFromPublications(appManager,socialNetwork,searchTerm);
	        taskIterator.append(createnetwork);
	        	        
	        taskIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());
			return taskIterator;
     
	}

	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

}