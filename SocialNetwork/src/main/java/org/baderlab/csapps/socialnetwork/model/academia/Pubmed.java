/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular 
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates, 
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.  
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.model.academia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Methods & fields for manipulating PubMed data
 * @author Victor Kofia
 */
public class Pubmed {
	/**
	 * The author of a specific publication. This variable is globally referenced to allow for 
	 * multiple additions in to a publication
	 */
	private Author author = null;
	private String lastName = null;
	private String firstName = null;
	private String middleInitials = null;
	/**
 	 * A publication's journal
 	 */
 	private String journal = null;
	/**
	 * A list containing all authors found in a particular publication
	 */
	private ArrayList<Author> pubAuthorList = new ArrayList<Author>();
	/**
	 * A publication's date
	 */
 	private String pubDate = null;
 	/**
	 * A list containing all the results that search session has yielded
	 */
	private List<Publication> pubList = new ArrayList<Publication>();
	/**
	 * Unique queryKey. Necessary for retrieving search results
	 */
	private String queryKey = null;
	/**
	 * The number of UIDs returned in search at one go
	 */
	private String retMax = null;
	/**
	 * The index of the first record returned in search
	 */
	private String retStart = null;
	/**
 	 * A publication's total number of citations
 	 */
 	private String timesCited = null;
	/**
 	 * A publication's title
 	 */
 	private String title = null;
 	/**
	 * The total number of publications found in search
	 */
	private String totalPubs = null;
 	/**
	 * Unique WebEnv. Necessary for retrieving search results
	 */
	private String webEnv = null;
	
	/**
	 * Variable to indicate that pubmed search should be done.  
	 */
	private boolean search = true;
	private String filename = "";
	
	/**
	 * Create a new Pubmed search session
	 * @param String searchTerm
	 * @return null
	 */
	public Pubmed(String searchTerm) {
		
		if(searchTerm.endsWith(".xml")){
			search = false;
			filename = searchTerm;
		}
		if(search){
		
			//Query
			Query query = new Query(searchTerm);
			try {
				// Create new SAXParser
				SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
				// Get Query Key & Web Env
				String url = String.format
						("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=%s", query);
				saxParser.parse(url, getSearchHandler());
				// Once all required fields have been filled commit to search
				commitPubMedSearch();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " +
		             "try again some other time.");
			} catch (SAXException e) {
				e.printStackTrace();
				CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " +
		             "try again some other time.");
			} catch (IOException e) {
				e.printStackTrace();
				CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " +
		             "internet connection.");
			}
		}
		else{
			commitPubMedSearch();
		}
	}
 	
 	/**
	 * Commit search using: (queryKey, webEnv, retStart and retMax)
	 * @param null
	 * @return null
	 */
	private void commitPubMedSearch() {
		try {
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			if(search){
				if (Integer.parseInt(totalPubs) > 500) {
					// WIP (Work In Progress)
					// On the event that a search yields 500+ publications, these publications will
					// need to be accessed incrementally as pubmed places sanctions on users with
					// extremely large requests
				}
				else {
					// Use newly discovered queryKey and webEnv to build a tag
					Tag tag = new Tag(queryKey, webEnv, retStart, retMax);
					// Load all publications at once
					String url = String.format
							("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed%s", tag);
					saxParser.parse(url, getPublicationHandler());
				}
			}
			else{
				File XmlFile = new File(filename);
				saxParser.parse(XmlFile, getPublicationHandlerFile());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " +
					             "try again some other time.");
		} catch (SAXException e) {
			e.printStackTrace();
			CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " +
					             "try again some other time.");
		} catch (IOException e) {
			e.printStackTrace();
			CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " +
					             "internet connection.");
		}
	}
 	
 	/**
 	 * Return a list of all the publications (& co-authors) found for User's specified 
 	 * authorName, MeSH term or Institution name.
 	 * @param null
 	 * @return List pubList
 	 */
 	public List<Publication> getListOfPublications() {	// Return all results
		return pubList;
 	}
 	
	/**
	 * Get publication handler
	 * @param null
	 * @return DefaultHandler publicationHandler
	 */
	private DefaultHandler getPublicationHandler() {
		DefaultHandler publicationHandler = new DefaultHandler() {
			
			/**
			 * XML Parsing variables. Used to temporarily store data.
			 */
			boolean isPubDate = false, isAuthor = false, isTitle = false, isJournal = false, 
					isTimesCited = false;
			
			// Reset variable contents
			public void startElement(String uri, String localName, String qName, Attributes attributes) 
					                                                              throws SAXException {
				if (contains(attributes, "Author")) {
					isAuthor = true;
				}
				if (contains(attributes, "FullJournalName")) {
					isJournal = true;
				}
				if (contains(attributes, "PubDate")) {
					isPubDate = true;
				}
				if (contains(attributes, "Title")) {
					isTitle = true;
				}
				if (contains(attributes, "PmcRefCount")) {
					isTimesCited = true;
				}
			}
			
			// Collect tag contents (if applicable)
			public void characters(char ch[], int start, int length) throws SAXException {
				if (isPubDate) {
					pubDate = new String(ch, start, length);
					isPubDate = false;
				}
				if (isAuthor) {
					author = new Author(new String(ch, start, length), Category.PUBMED);
					// Add author to publication author list
					if (! pubAuthorList.contains(author)) {						
						pubAuthorList.add(author);
					}
					isAuthor = false;
				}
				if (isJournal) {
					journal = new String(ch, start, length);
					isJournal = false;
				}
				if (isTitle) {
					title = new String(ch, start, length);
					isTitle = false;
				}
				if (isTimesCited) {
					timesCited = new String(ch, start, length);
					isTimesCited = false;
				}
			}
			
			// Create new publication and add it to overall publist
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (qName.equalsIgnoreCase("DocSum")) {
					pubList.add(new Publication(title, pubDate, journal, timesCited, null, pubAuthorList));
					pubAuthorList.clear();
				}
			}
			
			/**
			 * Returns true iff attributes contains the specified  text
			 * @param Attribute attributes
			 * @param String text
			 * @return Boolean bool
			 */
			public boolean contains(Attributes attributes, String text) {
				for (int i = 0; i < attributes.getLength(); i++) {
					if(attributes.getValue(i).equalsIgnoreCase(text)) {
						return true;
					}
				}
				return false;
			}
		};
		
		return publicationHandler;
		
	}
	
	/**
	 * Get publication handler
	 * @param null
	 * @return DefaultHandler publicationHandler
	 */
	private DefaultHandler getPublicationHandlerFile() {
		DefaultHandler publicationHandler = new DefaultHandler() {
			
			/**
			 * XML Parsing variables. Used to temporarily store data.
			 */
			boolean isPubDate = false, isAuthor = false, isTitle = false, isJournal = false, 
					isTimesCited = false,isFirstName = false, isLastName=false,isMiddleInitial = false;
			
			// Reset variable contents
			public void startElement(String uri, String localName, String qName, Attributes attributes) 
					                                                              throws SAXException {
				if (qName.equals( "Author")) {
					isAuthor = true;
				}
				if (qName.equals( "LastName")) {
					isLastName = true;
				}
				if (qName.equals( "ForeName")) {
					isFirstName = true;
				}
				if (qName.equals( "Initials")) {
					isMiddleInitial = true;
				}
				if (qName.equals("Title")) {
					isJournal = true;
				}
				if (qName.equals( "PubDate")) {
					isPubDate = true;
				}
				if (qName.equals( "ArticleTitle")) {
					isTitle = true;
				}
				if (qName.equals( "PmcRefCount")) {
					isTimesCited = true;
				}
			}
			
			// Collect tag contents (if applicable)
			public void characters(char ch[], int start, int length) throws SAXException {
				if (isPubDate) {
					pubDate = new String(ch, start, length);
					isPubDate = false;
				}
				if (isAuthor) {
					author = new Author(new String(ch, start, length), Category.PUBMED);
					isAuthor = false;
				}
				if (isFirstName) {
					firstName = new String(ch, start, length);
					isFirstName = false;					
				}
				if (isLastName) {
					lastName = new String(ch, start, length);
					isLastName = false;					
				}
				if (isMiddleInitial) {
					middleInitials = new String(ch, start, length);
					isMiddleInitial = false;					
				}
				if (isJournal) {
					journal = new String(ch, start, length);
					isJournal = false;
				}
				if (isTitle) {
					title = new String(ch, start, length);
					isTitle = false;
				}
				if (isTimesCited) {
					timesCited = new String(ch, start, length);
					isTimesCited = false;
				}
			}
			
			// Create new publication and add it to overall publist
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (qName.equalsIgnoreCase("PubmedArticle")) {
					//only add the publication if it has less than 30 authors
					if(pubAuthorList.size()<=270){
						pubList.add(new Publication(title, pubDate, journal, timesCited, null, pubAuthorList));
						pubAuthorList.clear();
					}
					else{
						System.out.println(title + ";; with ;;" + pubAuthorList.size() + " authors");
						pubAuthorList.clear();
					}
				}
				if(qName.equals("Author")){
					//add the firstname,lastname, initial to the author
					author.setFirstName(firstName);
					author.setLastName(lastName);
					author.setMiddleInitial(middleInitials);
					author.setFirstInitial(firstName.substring(0,1));
					author.setLabel(author.getFirstInitial() + " " + author.getLastName());
					
					// Add author to publication author list
					if (! pubAuthorList.contains(author)) {						
						pubAuthorList.add(author);
					}
					
				}
			}
			
			/**
			 * Returns true iff attributes contains the specified  text
			 * @param Attribute attributes
			 * @param String text
			 * @return Boolean bool
			 */
			public boolean contains(Attributes attributes, String text) {
				for (int i = 0; i < attributes.getLength(); i++) {
					if(attributes.getValue(i).equalsIgnoreCase(text)) {
						return true;
					}
				}
				return false;
			}
		};
		
		return publicationHandler;
		
	}
	
	/**
	 * Get search handler
	 * @param null
	 * @return DefaultHandler searchHandler
	 */
	private DefaultHandler getSearchHandler() throws SAXException, 
	                                                IOException, 
	                                                ParserConfigurationException {
		DefaultHandler searchHandler = new DefaultHandler() {
			
			/**
			 * XML Parsing variables. Used to temporarily store data. 
			 */
			boolean isQueryKey = false, isWebEnv = false, isTotalPubs = false;
			
			// Reset XML variables
			public void startElement(String uri, 
									 String localName, 
									 String qName, 
					                 Attributes attributes) throws SAXException {
				if (qName.equalsIgnoreCase("Count")) {
					isTotalPubs = true;
				}
				if (qName.equalsIgnoreCase("QueryKey")) {
					isQueryKey = true;
				}
				if (qName.equalsIgnoreCase("WebEnv")) {
					isWebEnv = true;
				}
			}

			// Collect tag contents (if applicable)
			public void characters(char ch[], int start, int length) 
					                                         throws SAXException {
				if (isTotalPubs) {
					totalPubs = new String(ch, start, length);
					isTotalPubs = false;
				}
				if (isQueryKey) {
					queryKey = new String(ch, start, length);
					isQueryKey = false;
				}
				if (isWebEnv) {
					webEnv = new String(ch, start, length);
					isWebEnv = false;
				}
			}
			
			public void endElement(String uri, 
					               String localName, 
					               String qName) throws SAXException {
			
			}
			
		};
		
		return searchHandler;
		
	}
	
	/**
 	 * Return total # of publications yielded from search.
 	 * @param null
 	 * @return int totalPubs
 	 */
 	public int getTotalPubs() {
 		return totalPubs != null ? Integer.parseInt(this.totalPubs) : -1;
 	}
 	
}
