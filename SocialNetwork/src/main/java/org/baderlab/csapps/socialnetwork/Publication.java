package main.java.org.baderlab.csapps.socialnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A publication (article, review, scientific paper)
 * @author Victor Kofia
 */
public class Publication extends AbstractEdge {
	/**
	 * The publication's release date
	 */
	private String pubDate = null;
	/**
	 * A list of all authors who played a part in the creation of this publication
	 */
	private ArrayList<Author> authors = new ArrayList<Author>(); 
	/**
	 * The journal to which this publication belongs
	 */
	private String journal = null;
	/**
	 * The publication's title
	 */
	private String title = null;
	/**
	 * The total amount of times this publication has been cited
	 */
	private String timesCited = "0";
	/**
	 * The expected number of citations this publication expects to receive
	 */
	private String expectedCitations = null; 

	
	/**
	 * Create new publication with specified title, pubdate and list of authors
	 * @param pubDate
	 * @param title
	 * @param coauthorList
	 */
	public Publication(String title, String pubDate, String journal, List<Author> coauthorList) {
		this.pubDate = pubDate;
		this.title = title;
		this.journal = journal;
		this.authors.addAll(coauthorList);
		constructAttrMap();
	}


	/**
	 * Return a text representation of all of this publication's authors
	 * @paran null
	 * @return String authors
	 */
	public String getAuthors() {
		String allAuthors = "";
		// Add a comma between each author
		for (Author author: authors) {
			allAuthors += author + ", ";
		}
		return allAuthors;
	}
	
	
	/**
	 * Return arraylist containing publication authors
	 * @return
	 */
	public List<? extends AbstractNode> getNodes() {
		return this.authors;
	}
	
	/**
	 * Set publication title
	 * @param String title
	 * @return null
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	/**
	 * Get publication title
	 * @param null
	 * @return String title
	 */
	public String getTitle() {
		return this.title;
	}
	
	
	/**
	 * Set publication pub date
	 * @param String date
	 * @return null
	 */
	public void setPubDate(String date) {
		this.pubDate = date;
	}
	
	
	/**
	 * Get publication date
	 * @param null 
	 * @return String pubDate
	 */
	public String getPubDate() {
		return this.pubDate;
	}
	
	
	/**
	 * Set publication authors
	 * @param ArrayList authors
	 * @return null
	 */
	public void setAuthors(ArrayList<Author> authors) {
		this.authors = authors;
	}
	
	
	/**
	 * Return a string representation of the publication in the format: ??
	 * Title: title
	 * PubDate: pubdate
	 * Authors: author
	 * @param null
	 * @return String publication
	 */
	public String toString() {
		// removing period (for cosmetic purposes)
		return title.substring(0,title.length() - 1);
	}

	
	/**
	 * Return times cited
	 * @param null
	 * @return String timesCited
	 */
	public String getTimesCited() {
		return timesCited;
	}


	/**
	 * Set times cited
	 * @param String timesCited
	 * @return null
	 */
	public void setTimesCited(String timesCited) {
		this.timesCited = timesCited;
	}

	/**
	 * Get expected citations
	 * @param null
	 * @return String expectedCitations
	 */
	public String getExpectedCitations() {
		return expectedCitations;
	}

	/**
	 * Set expected citations
	 * @param String expectedCitations
	 * @return null
	 */
	public void setExpectedCitations(String expectedCitations) {
		this.expectedCitations = expectedCitations;
	}
	
	/**
	 * Construct attribute map for use in Cytoscape
	 * @param null
	 * @return null
	 */
	public void constructAttrMap() {
		attrMap = new HashMap<String, String>();
		attrMap.put("Title", this.title);
		attrMap.put("Times Cited", this.timesCited);
		attrMap.put("Expected Citations", this.expectedCitations);
		attrMap.put("Journal", this.journal);
		attrMap.put("Pub Date", this.pubDate);
	}
	
	/**
	 * Get attribute map
	 * @param null
	 * @return Map attrMap
	 */
	public Map<String, String> getAttrMap() {
		return this.attrMap;
	}
}
