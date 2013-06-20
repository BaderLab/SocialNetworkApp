package main.java.org.baderlab.csapps.socialnetwork;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import main.java.org.baderlab.csapps.socialnetwork.pubmed.Pubmed;

import org.xml.sax.SAXException;

/**
 * A search session
 * @author Victor Kofia
 */
public class Search {
	/**
	 * Default website 
	 */
	final public static int DEFAULT = 0;
	/**
	 * PubMed (IP = 130.14.29.110)
	 */
	final public static int PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;
	/**
	 * Incites (IP = 167.68.24.112)
	 */
	final static public int INCITES = (167 << 24) + (68 << 16) + (24 << 8) + 112;
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
			case DEFAULT:
				Cytoscape.notifyUser("Click --SELECT-- to select a website");
				break;
			case PUBMED: 
				this.results = Pubmed.getListOfPublications(searchTerm);
				this.totalHits = Pubmed.getTotalPubs();
				break;
		}
	}

	/**
	 * Get list of all websites currently supported by search
	 * @param null
	 * @return ListsiteList
	 */
	public static String[] getSiteList() {
		String[] siteList = { "--SELECT--", "PubMed"};
		return siteList;
	}
	
	/**
	 * Construct site map. Keys are string representations of each website
	 * and values are the actual websites themselves
	 * @param null
	 * @return Map<String, int> academiaMap
	 */
	public static Map<String, Integer> getSiteMap() {
		Map<String, Integer> siteMap = new HashMap<String, Integer>();
		siteMap.put("--SELECT--", Search.DEFAULT);
		siteMap.put("PubMed", Search.PUBMED);
		return siteMap;
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
