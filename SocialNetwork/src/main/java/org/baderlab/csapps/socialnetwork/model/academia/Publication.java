
package org.baderlab.csapps.socialnetwork.model.academia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.AbstractNode;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.cytoscape.model.CyEdge;


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
	/*
	 * Location that most of the authors are from
	 */
	private String location = "N/A";
	
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
		
		//calculate the most used location
		this.calculateLocation();
	}


	/**
	 * Construct edge attribute map for use in Cytoscape
	 * @param null
	 * @return null
	 */
	public void constructEdgeAttrMap() {
		edgeAttrMap = new HashMap<String, Object>();
		edgeAttrMap.put(BasicSocialNetworkVisualstyle.nodeattr_timescited, this.timesCited);
		edgeAttrMap.put("Pub Date", this.pubDate);
		edgeAttrMap.put("Journal", this.journal);
		edgeAttrMap.put("Title", this.title);
	}
	
	/*
	 * Go through all the authors on the paper and get the location that occurs the most on the paper
	 */
	
	public void calculateLocation(){
		String maxlocation = "N/A";
		Integer max = 0;
		HashMap<String, Integer> all_locations = new HashMap<String, Integer>();
		
		// Add a comma between each author
		for (Author author: authorList) {
			//get author locations
			String current_location = author.getLocation();
			
			if(all_locations.containsKey(current_location)){
				Integer count = all_locations.get(current_location) +1;
				all_locations.put(current_location, count);
				//only set the max count if it doesn't belong to "N/A" group
				if((count > max) && (!current_location.equalsIgnoreCase("N/A"))){
					max = count;
					maxlocation = current_location;
				}
			}
			else{
				all_locations.put(current_location, 1);
			}
		}
		
		this.location = maxlocation;
		
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


	/**
	 * Get CyEdge
	 * @param null
	 * @return CyEdge cyEdge
	 */
	public CyEdge getCyEdge() {
		return this.cyEdge;
	}


	/**
	 * Set CyEdge
	 * @param CyEdge cyEdge
	 * @return null
	 */
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


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public ArrayList<Author> getAuthorList() {
		return authorList;
	}


	public void setAuthorList(ArrayList<Author> authorList) {
		this.authorList = authorList;
	}
	
	
	
}
