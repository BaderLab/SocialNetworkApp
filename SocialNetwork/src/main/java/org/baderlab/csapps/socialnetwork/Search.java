package main.java.org.baderlab.csapps.socialnetwork;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import main.java.org.baderlab.csapps.socialnetwork.academia.Pubmed;

import org.xml.sax.SAXException;

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
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
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
	 * Get list of all search options granted to user by default
	 * @param null
	 * @return List siteList
	 */
	public static String[] getDefaultOptionList() {
		String[] defaultOptionList = {"--SELECT--"};
		return defaultOptionList;
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

	/**
	 * Get list of all search options granted to users 
	 * searching academic records
	 * @param null
	 * @return List academiaOptionList
	 */
	public static String[] getAcademiaOptionList() {
		String[] academiaOptionList = { "--SELECT--", "Author", "Institution", "MeSH"};
		return academiaOptionList;
	}
	
}
