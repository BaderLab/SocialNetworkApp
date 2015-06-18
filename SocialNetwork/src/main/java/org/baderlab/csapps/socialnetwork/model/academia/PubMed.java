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
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Methods & fields for manipulating PubMed data
 *
 * @author Victor Kofia
 */
public class PubMed {

    /**
     * Construct PubMed attribute map
     *
     * @return Map nodeAttrMap
     */
    public static HashMap<String, Object> constructPubMedAttrMap(Author author) {
        HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
        String[] columns = new String[] { BasicSocialNetworkVisualstyle.nodeattr_label, BasicSocialNetworkVisualstyle.nodeattr_lname,
                BasicSocialNetworkVisualstyle.nodeattr_fname, BasicSocialNetworkVisualstyle.nodeattr_timescited,
                BasicSocialNetworkVisualstyle.nodeattr_inst, BasicSocialNetworkVisualstyle.nodeattr_numpub,
                BasicSocialNetworkVisualstyle.nodeattr_pub };
        int i = 0;
        for (i = 0; i < 5; i++) {
            nodeAttrMap.put(columns[i], "");
        }
        // Initialize the num publication attribute (~Integer)
        nodeAttrMap.put(columns[i], 0);
        i++;
        // Initialize Publications attribute (~ ArrayList)
        nodeAttrMap.put(columns[i], new ArrayList<String>());
        return nodeAttrMap;
    }
    
    /**
     * The author of a specific publication. This variable is globally
     * referenced to allow for multiple additions in to a publication
     */
    private Author author = null;
    private String lastName = null;
    private String firstName = null;
    private String middleInitials = null;
    private String institution = null;
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
    private ArrayList<Publication> pubList = new ArrayList<Publication>();
    /**
     * A publication's unique identifier
     */
    private String pmid = null;
    /**
     * Unique queryKey. Necessary for retrieving search results
     */
    private String queryKey = null;
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
     * Create a new {@link PubMed} session
     */
    public PubMed() {
    	
    }
    
    /**
     * Create a new {@link PubMed} session from xmlFile
     *
     * @param File xmlFile
     */
    public PubMed(File xmlFile) {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(xmlFile, getXmlExportHandler());
            setPmcRefCount(this.pubList);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (SAXException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (IOException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
        }
    }
    
    /**
     * Set the PmcRefCount of the publications in the list.
     * 
     * @param ArrayList pubList
     */
    private void setPmcRefCount(ArrayList<Publication> pubList) {

    	Publication pub = null;
    	int retStart = 0;
    	int total = pubList.size();
    	int retMax = total > 400 ? 400 : total;
    	while (retStart < total) {
        	StringBuilder query = new StringBuilder();
    		for(int i =retStart;i<retMax;i++) {    			
    			pub = pubList.get(i);
    			query.append(pub.getPMID());
    			query.append("[UID]");
    			if (i<(retMax-1)) {
    				query.append(" OR ");    			
    			}
    		}
    		try {
    			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
    			String url = String.format("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=%s", new Query(query.toString()));
    			saxParser.parse(url, getSearchHandler());
    			saxParser = SAXParserFactory.newInstance().newSAXParser();
                      	
            	// Use newly discovered queryKey and webEnv to build a tag
            	Tag tag = new Tag(this.queryKey, this.webEnv, retStart, retMax);
            	// Load all publications at once
            	url = String.format("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed%s", tag);
            	saxParser.parse(url, getTimesCitedHandler(pubList,retStart));
            	retStart = retMax;
            	retMax = retMax+400 <= total ? (retMax + 400) : total;
            	
            
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (SAXException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (IOException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
        }
    }
   }
    /**
     * Create a new {@link PubMed} search session
     *
     * @param String searchTerm
     */
    public PubMed(String searchTerm) {
        // Query
        Query query = new Query(searchTerm);
        try {
            // Create new SAXParser
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            // Get Query Key & Web Env
            String url = String.format("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=%s", query);
            saxParser.parse(url, getSearchHandler());
            // Once all required fields have been filled commit to search
            commitPubMedSearch();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (SAXException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (IOException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
        }
    }

    /**
     * Commit search using: (queryKey, webEnv, retStart and retMax)
     */
    private void commitPubMedSearch() {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            if (this.totalPubs != null && Pattern.matches("[0-9]+", this.totalPubs)) {
            	int total = Integer.parseInt(this.totalPubs);
            	int retStart = 0;
            	int retMax = total > 500 ? 500 : total;
            	while (retStart < total) {
            		// Use newly discovered queryKey and webEnv to build a tag
            		Tag tag = new Tag(this.queryKey, this.webEnv, retStart, retMax);
            		// Load all publications at once
            		String url = String.format("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed%s", tag);
            		saxParser.parse(url, getEntrezUtilitiesHandler());
            		retStart += retMax;
            	}
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (SAXException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
        } catch (IOException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
        }
    }

    /**
     * Returns handler for parsing eUtils output
     *
     * @return DefaultHandler eUtilsHandler
     */
    private DefaultHandler getEntrezUtilitiesHandler() {
        DefaultHandler publicationHandler = new DefaultHandler() {

            /**
             * XML Parsing variables. Used to temporarily store data.
             */
            boolean isPubDate = false, isAuthor = false, isTitle = false, isJournal = false, isTimesCited = false,
            		isPMID = false;

            // Collect tag contents (if applicable)
            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                if (this.isPubDate) {
                    PubMed.this.pubDate = new String(ch, start, length);
                    this.isPubDate = false;
                }
                if (this.isAuthor) {
                    PubMed.this.author = new Author(new String(ch, start, length), Category.PUBMED);
                    // Add author to publication author list
                    if (!PubMed.this.pubAuthorList.contains(PubMed.this.author)) {
                        PubMed.this.pubAuthorList.add(PubMed.this.author);
                    }
                    this.isAuthor = false;
                }
                if (this.isJournal) {
                    PubMed.this.journal = new String(ch, start, length);
                    this.isJournal = false;
                }
                if (this.isTitle) {
                    PubMed.this.title = new String(ch, start, length);
                    this.isTitle = false;
                }
                if (this.isTimesCited) {
                    PubMed.this.timesCited = new String(ch, start, length);
                    this.isTimesCited = false;
                }
                if (this.isPMID) {
                	PubMed.this.pmid = new String(ch, start, length);
                	this.isPMID = false;
                }
            }

            /**
             * Returns true iff attributes contains the specified text
             *
             * @param Attribute attributes
             * @param String text
             * @return Boolean bool
             */
            public boolean contains(Attributes attributes, String text) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.getValue(i).equalsIgnoreCase(text)) {
                        return true;
                    }
                }
                return false;
            }

            // Create new publication and add it to overall publist
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (qName.equalsIgnoreCase("DocSum")) {
                	Publication publication = new Publication(PubMed.this.title, PubMed.this.pubDate, PubMed.this.journal, 
                    		PubMed.this.timesCited, null, PubMed.this.pubAuthorList);
                	publication.setPMID(PubMed.this.pmid);
                    PubMed.this.pubList.add(publication);
                    PubMed.this.pubAuthorList.clear();
                }
            }

            // Reset variable contents
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (contains(attributes, "Author")) {
                    this.isAuthor = true;
                }
                if (contains(attributes, "FullJournalName")) {
                    this.isJournal = true;
                }
                if (contains(attributes, "PubDate")) {
                    this.isPubDate = true;
                }
                if (contains(attributes, "Title")) {
                    this.isTitle = true;
                }
                if (contains(attributes, "PmcRefCount")) {
                    this.isTimesCited = true;
                }
                if (qName.equals("Id")) {
                	this.isPMID = true;
                }
             }
        };

        return publicationHandler;

    }
    
    /**
     * Returns handler for getting the times cited value of a publication
     *
     * @param String publicationTitle
     * @return DefaultHandler timesCitedHandler
     */
    private DefaultHandler getTimesCitedHandler(final ArrayList<Publication> pubList, final int start_index) {
        DefaultHandler publicationHandler = new DefaultHandler() {

            /**
             * XML Parsing variables. Used to temporarily store data.
             */
            boolean isTimesCited = false, isPMID = false;
            int index = start_index;

            // Collect tag contents (if applicable)
            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
            	if (this.isPMID) {
            		PubMed.this.pmid = new String(ch, start, length);
            		this.isPMID = false;
            	}
                if (this.isTimesCited) {
                	if (PubMed.this.pmid.equals(pubList.get(index).getPMID())) {
                		PubMed.this.timesCited = new String(ch, start, length);
                		this.isTimesCited = false;
                	} else {
                		// TODO: Store ids of ignored publications in a log file             		
                	}
                }
            }

            /**
             * Returns true iff attributes contains the specified text
             *
             * @param Attribute attributes
             * @param String text
             * @return Boolean bool
             */
            public boolean contains(Attributes attributes, String text) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.getValue(i).equalsIgnoreCase(text)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (qName.equalsIgnoreCase("DocSum")) {
                	// TODO:
                	pubList.get(index).setTimesCited(PubMed.this.timesCited);
                	index++;
                }
            }

            // Reset variable contents
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (contains(attributes, "PmcRefCount")) {
                    this.isTimesCited = true;
                }
                if (qName.equals("Id")) {
                	this.isPMID = true;
                }
            }
        };

        return publicationHandler;

    }

    /**
     * Return a list of all the publications (& co-authors) found for User's
     * specified authorName, MeSH term or Institution name.
     *
     * @return ArrayList pubList
     */
    public ArrayList<Publication> getPubList() { // Return all results
        return this.pubList;
    }

    /**
     * Return handler for parsing data exported directly from PubMed 
     *
     * @return DefaultHandler xmlExportHandler
     */
    private DefaultHandler getXmlExportHandler() {
        DefaultHandler publicationHandler = new DefaultHandler() {

            /**
             * XML Parsing variables. Used to temporarily store data.
             */
            boolean isPubDate = false, isAuthor = false, isTitle = false, isJournal = false, isTimesCited = false, 
            		isFirstName = false, isLastName = false, isMiddleInitial = false, isInstitution = false, isPMID = false;

            // Collect tag contents (if applicable)
            @Override
            /* (non-Javadoc)
             * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
             */
            public void characters(char ch[], int start, int length) throws SAXException {
                if (this.isPubDate) {
                    PubMed.this.pubDate = new String(ch, start, length);
                    this.isPubDate = false;
                }
                if (this.isAuthor) {
                    PubMed.this.author = new Author(new String(ch, start, length), Category.PUBMED);
                    this.isAuthor = false;
                }
                if (this.isInstitution) {
                	PubMed.this.institution = new String(ch, start, length);
                	this.isInstitution = false;
                }
                if (this.isFirstName) {
                    PubMed.this.firstName = new String(ch, start, length);
                    this.isFirstName = false;
                }
                if (this.isLastName) {
                    PubMed.this.lastName = new String(ch, start, length);
                    this.isLastName = false;
                }
                if (this.isMiddleInitial) {
                    PubMed.this.middleInitials = new String(ch, start, length);
                    this.isMiddleInitial = false;
                }
                if (this.isJournal) {
                    PubMed.this.journal = new String(ch, start, length);
                    this.isJournal = false;
                }
                if (this.isTitle) {
                    PubMed.this.title = new String(ch, start, length);
                    this.isTitle = false;
                }
                if (this.isTimesCited) {
                    PubMed.this.timesCited = new String(ch, start, length);
                    this.isTimesCited = false;
                }
                if (this.isPMID && PubMed.this.pmid == null) {
                	PubMed.this.pmid = new String(ch, start, length);
                	this.isPMID = false;
                }
            }

            /**
             * Returns true iff attributes contains the specified text
             *
             * @param Attribute attributes
             * @param String text
             * @return Boolean bool
             */
            public boolean contains(Attributes attributes, String text) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.getValue(i).equalsIgnoreCase(text)) {
                        return true;
                    }
                }
                return false;
            }

            // Create new publication and add it to overall publist
            @Override
            /* (non-Javadoc)
             * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
             */
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (qName.equalsIgnoreCase("PubmedArticle")) {
                	Publication pub = new Publication(PubMed.this.title, PubMed.this.pubDate, PubMed.this.journal, 
                    		PubMed.this.timesCited, null, PubMed.this.pubAuthorList);
                	pub.setPMID(PubMed.this.pmid);
                	PubMed.this.pmid = null;
                    PubMed.this.pubList.add(pub);
                    PubMed.this.pubAuthorList.clear();
                }
                if (qName.equals("Author")) {
                    // add the firstname,lastname, initial to the author
                    PubMed.this.author.setFirstName(PubMed.this.firstName);
                    PubMed.this.author.setLastName(PubMed.this.lastName);
                    PubMed.this.author.setMiddleInitial(PubMed.this.middleInitials);
                    PubMed.this.author.setFirstInitial(PubMed.this.firstName.substring(0, 1));
                    PubMed.this.author.setLabel(PubMed.this.author.getFirstInitial() + " " + PubMed.this.author.getLastName());
                    PubMed.this.author.setInstitution(PubMed.this.institution);

                    // Add author to publication author list
                    if (!PubMed.this.pubAuthorList.contains(PubMed.this.author)) {
                        PubMed.this.pubAuthorList.add(PubMed.this.author);
                    }

                }
            }

            // Reset variable contents
            @Override
            /* (non-Javadoc)
             * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
             */
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equals("Author")) {
                    this.isAuthor = true;
                }
                if (qName.equals("Affiliation")) {
                	this.isInstitution = true;
                }
                if (qName.equals("LastName")) {
                    this.isLastName = true;
                }
                if (qName.equals("ForeName")) {
                    this.isFirstName = true;
                }
                if (qName.equals("Initials")) {
                    this.isMiddleInitial = true;
                }
                if (qName.equals("Title")) {
                    this.isJournal = true;
                }
                if (qName.equals("PubDate")) {
                    this.isPubDate = true;
                }
                if (qName.equals("ArticleTitle")) {
                    this.isTitle = true;
                }
                if (qName.equals("PmcRefCount")) {
                    this.isTimesCited = true;
                }
                if (qName.equals("PMID")) {
                	this.isPMID = true;
                }
            }
        };

        return publicationHandler;

    }

    /**
     * Get search handler
     *
     * @return DefaultHandler searchHandler
     */
    private DefaultHandler getSearchHandler() throws SAXException, IOException, ParserConfigurationException {
        DefaultHandler searchHandler = new DefaultHandler() {
        	
        	public void DefaultHandler() {
        		PubMed.this.totalPubs = null;
        	}
 
            /**
             * XML Parsing variables. Used to temporarily store data.
             */
            boolean isQueryKey = false, isWebEnv = false, isTotalPubs = false;

            // Collect tag contents (if applicable)
            @Override
            /* (non-Javadoc)
             * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
             */
            public void characters(char ch[], int start, int length) throws SAXException {
                if (this.isTotalPubs) {
                    PubMed.this.totalPubs = new String(ch, start, length);
                    this.isTotalPubs = false;
                }
                if (this.isQueryKey) {
                    PubMed.this.queryKey = new String(ch, start, length);
                    this.isQueryKey = false;
                }
                if (this.isWebEnv) {
                    PubMed.this.webEnv = new String(ch, start, length);
                    this.isWebEnv = false;
                }
            }

            @Override
            /* (non-Javadoc)
             * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
             */
            public void endElement(String uri, String localName, String qName) throws SAXException {

            }

            // Reset XML variables
            @Override
            /* (non-Javadoc)
             * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
             */
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("Count") && PubMed.this.totalPubs == null) {
                    this.isTotalPubs = true;
                }
                if (qName.equalsIgnoreCase("QueryKey")) {
                    this.isQueryKey = true;
                }
                if (qName.equalsIgnoreCase("WebEnv")) {
                    this.isWebEnv = true;
                }
            }

        };

        return searchHandler;

    }

    /**
     * Return total # of publications yielded from search.
     *
     * @return int totalPubs
     */
    public int getTotalPubs() {
        return this.totalPubs != null ? Integer.parseInt(this.totalPubs) : -1;
    }

}
