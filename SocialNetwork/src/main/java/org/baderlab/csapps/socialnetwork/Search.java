package main.java.org.baderlab.csapps.socialnetwork;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import main.java.org.baderlab.csapps.socialnetwork.pubmed.Pubmed;

import org.xml.sax.SAXException;

/**
 * A search session
 * @author Victor Kofia
 */
public class Search {
	/**
	 * PubMed (IP = 130.14.29.110)
	 */
	final public static int PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;
	
	/**
	 * Search results
	 */
	public List<? extends AbstractEdge> results = null;
	
	/**
	 * Total hits
	 */
	public int hits = 0;
	
	/**
	 * Create a new search session 
	 * @param String searchTerm
	 * @param int website
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Search(String searchTerm, int website) {
		if (website == Search.PUBMED) {
			this.results = Pubmed.getListOfPublications(searchTerm);
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
	
}
