package main.java.org.baderlab.csapps.socialnetwork;

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
		switch (website) {
			case Category.DEFAULT:
				Cytoscape.notifyUser("Click --SELECT CATEGORY-- to select a category");
				break;
			case Category.ACADEMIA:
				this.results = Pubmed.getListOfPublications(searchTerm);
				this.totalHits = Pubmed.getTotalPubs();
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
