
package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;

import main.java.org.baderlab.csapps.socialnetwork.AbstractEdge;
import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;

/**
 * A publication (article, review, scientific paper)
 * @author Victor Kofia
 */
public class Publication extends AbstractEdge {
	/**
	 * A list of all authors who collaborated on Publication
	 */
	private ArrayList<Author> authorList = new ArrayList<Author>();
	/**
	 * The expected number of citations Publication expects to receive
	 */
	private String expectedCitations = null; 
	/**
	 * The journal to which Publication belongs
	 */
	private String journal = null;
	/**
	 * Publication's release date
	 */
	private String pubDate = null;
	/**
	 * The total amount of times Publication has been cited
	 */
	private int timesCited = 0;
	/**
	 * Publication's title
	 */
	private String title = null; 

	
	/**
	 * Create new publication
	 * @param String pubDate
	 * @param String title
	 * @param String journal
	 * @param String timesCited
	 * @param String expectedCitations
	 * @param List coauthorList
	 * @return null
	 */
	public Publication(String title, String pubDate, String journal, 
			           String timesCited, String expectedCitations, 
			           List<Author> coauthorList) {
		this.pubDate = pubDate;
		this.title = title;
		this.journal = journal;
		this.authorList.addAll(coauthorList);
		if (timesCited != null) {
			this.timesCited = Integer.parseInt(timesCited);
		}
		this.expectedCitations = expectedCitations;
		constructEdgeAttrMap();
	}


	/**
	 * Construct edge attribute map for use in Cytoscape
	 * @param null
	 * @return null
	 */
	public void constructEdgeAttrMap() {
		edgeAttrMap = new HashMap<String, Object>();
		edgeAttrMap.put("Times Cited", this.timesCited);
		edgeAttrMap.put("Pub Date", this.pubDate);
		edgeAttrMap.put("Journal", this.journal);
		edgeAttrMap.put("Title", this.title);
	}
	
	
	/**
	 * Return a text representation of all of publication's authors
	 * @paran null
	 * @return String authors
	 */
	public String getAuthors() {
		String allAuthors = "";
		// Add a comma between each author
		for (Author author: authorList) {
			allAuthors += author + ", ";
		}
		return allAuthors;
	}
	
	/**
	 * Get edge attribute map
	 * @param null
	 * @return Map edgeAttrMap
	 */
	public Map<String, Object> getEdgeAttrMap() {
		return this.edgeAttrMap;
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
	 * Get authors
	 * @return List authorList
	 */
	public List<? extends AbstractNode> getNodes() {
		return this.authorList;
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
	 * Get times cited
	 * @param null
	 * @return int timesCited
	 */
	public int getTimesCited() {
		return timesCited;
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
	 * Set publication authors
	 * @param ArrayList authors
	 * @return null
	 */
	public void setAuthors(ArrayList<Author> authors) {
		this.authorList = authors;
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
	 * Set publication pub date
	 * @param String date
	 * @return null
	 */
	public void setPubDate(String date) {
		this.pubDate = date;
	}

	/**
	 * Set times cited
	 * @param int timesCited
	 * @return null
	 */
	public void setTimesCited(int timesCited) {
		this.timesCited = timesCited;
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
	 * Return a string representation of the publication in the format:
	 * <br>Title: <i>title</i>
	 * <br>Pub-Date: <i>pubdate</i>
	 * <br>Authors: <i>author</i>
	 * @param null
	 * @return String publication
	 */
	public String toString() {
		return "Title: " + title
			+  "\nTimes Cited: " + timesCited;
	}


	@Override
	public CyEdge getCyEdge() {
		// TODO Auto-generated method stub
		return this.cyEdge;
	}


	@Override
	public void setCyEdge(CyEdge cyEdge) {
		this.cyEdge = cyEdge;
	}
	
	/**
	 * Return true iff publication was authored by a single
	 * individual
	 * @param null
	 * @return boolean bool
	 */
	public boolean isSingleAuthored() {
		return this.authorList.size() == 2 &&
			   this.authorList.get(0).equals(this.authorList.get(1));
	}
	
}
