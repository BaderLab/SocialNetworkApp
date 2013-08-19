package main.java.org.baderlab.csapps.socialnetwork;

import java.awt.Cursor;
import java.util.List;

import main.java.org.baderlab.csapps.socialnetwork.academia.Pubmed;

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
	public Search(String searchTerm, int website) {
        Cytoscape.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		switch (website) {
			case Category.DEFAULT:
				Cytoscape.notifyUser("Click --SELECT CATEGORY-- to select a category");
				break;
			case Category.ACADEMIA:
				Pubmed pubmed = new Pubmed(searchTerm);
				this.results = pubmed.getListOfPublications();
				this.totalHits = pubmed.getTotalPubs();
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
