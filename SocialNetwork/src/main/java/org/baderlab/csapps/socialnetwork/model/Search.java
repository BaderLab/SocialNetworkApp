package org.baderlab.csapps.socialnetwork.model;

import java.awt.Cursor;
import java.util.List;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.academia.Pubmed;


/**
 * A search session
 * @author Victor Kofia
 */
public class Search {
	/**
	 * Search results
	 */
	public List<? extends AbstractEdge> results = null;
	/**
	 * Total hits
	 */
	public int totalHits = 0;
	
	/**
	 * Create a new search session 
	 * @param String searchTerm
	 * @param int website
	 * @return null
	 */
	public Search(String searchTerm, int website, SocialNetworkAppManager appManager) {
        appManager.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		switch (website) {
			case Category.DEFAULT:
				CytoscapeUtilities.notifyUser("Click --SELECT CATEGORY-- to select a category");
				break;
			case Category.ACADEMIA:
				Pubmed pubmed = new Pubmed(searchTerm);
				this.results = pubmed.getListOfPublications();
				this.totalHits = pubmed.getListOfPublications().size();
				break;
		}
	}

	/**
	 * Get results
	 * @param null
	 * @return List results
	 */
	public List<? extends AbstractEdge> getResults() {
		return this.results;
	}
	
	/**
	 * Get total number of hits
	 * @param null
	 * @return in totalHits
	 */
	public int getTotalHits() {
		return this.totalHits;
	}
	
}
