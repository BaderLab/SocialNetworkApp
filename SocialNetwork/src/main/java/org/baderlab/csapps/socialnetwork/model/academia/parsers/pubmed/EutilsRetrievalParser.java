package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Tag;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EutilsRetrievalParser extends AbstractTask {

    private static final Logger logger = Logger.getLogger(EutilsRetrievalParser.class.getName());

    private int publication_counter = 0;
    private TaskMonitor taskMonitor;

    /**
     * The author of a specific publication.
     */
    private Author author;
    /**
     * Raw text of an author of a specific publication
     */
    private StringBuilder rawAuthorText;
    /**
     * A publication's journal
     */
    private StringBuilder journal;
    /**
     * A list containing all authors found in a particular publication
     */
    private ArrayList<Author> pubAuthorList = new ArrayList<>();
    /**
     * A publication's date
     */
    private StringBuilder pubDate;
    /**
     * A list containing all the results that search session has yielded
     */
    private ArrayList<Publication> pubList = new ArrayList<>();
    /**
     * A publication's unique identifier
     */
    private StringBuilder pmid;
    /**
     * A publication's total number of citations
     */
    private StringBuilder timesCited;
    /**
     * A publication's title
     */
    private StringBuilder title;

    private String queryKey = "";
    private String webEnv = "";
    private int retStart = 0;
    private int retMax = 0;
    private int totalPubs = 0;
    private SocialNetwork socialNetwork;
    
	public EutilsRetrievalParser(SocialNetwork socialNetwork) {
		this.rawAuthorText = new StringBuilder();
		this.journal = new StringBuilder();
		this.pubDate = new StringBuilder();
		this.pmid = new StringBuilder();
		this.timesCited = new StringBuilder();
		this.title = new StringBuilder();
		this.socialNetwork = socialNetwork;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		tm.setTitle("Social Network - Retrieve PubMed Records");
		
		try {
			if (socialNetwork.getEutilsResults() != null) {
				queryKey = socialNetwork.getEutilsResults().getQueryKey();
				webEnv = socialNetwork.getEutilsResults().getWebEnv();
				retStart = socialNetwork.getEutilsResults().getRetStart();
				retMax = socialNetwork.getEutilsResults().getRetMax();
				totalPubs = socialNetwork.getEutilsResults().getTotalPubs();
			}

			tm.setStatusMessage("Retrieving " + totalPubs + " publication(s) from PubMed...");
			tm.setProgress(0);
			
			var saxParser = SAXParserFactory.newInstance().newSAXParser();
			var parser = new EutilsParser();
			this.taskMonitor = tm;
			
			while (retStart < totalPubs) {
				// Use newly discovered queryKey and webEnv to build a tag
				var tag = new Tag(queryKey, webEnv, retStart, retMax);
				// Load all publications at once
				Thread.sleep(500);
				var url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed%s", tag);
				saxParser.parse(url, parser);
				retStart += retMax;
				tm.setProgress((int) (((double) retStart / totalPubs) * 100));
			}
			
			socialNetwork.setPublications(parser.getPubList());
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "Exception occurred", e);
			CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
		} catch (SAXException e) {
			logger.log(Level.SEVERE, "Exception occurred", e);
			CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception occurred", e);
			CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
		}
	}

	private class EutilsParser extends DefaultHandler {
    	
        boolean isAuthor = false;
        boolean isJournal = false;
        boolean isPMID = false;
        boolean isPubDate = false;
        boolean isTimesCited = false;
        boolean isTitle = false;
        
		@Override
		public void characters(char ch[], int start, int length) throws SAXException {
			if (this.isPubDate) {
				pubDate.append(ch, start, length);
			}
			if (this.isAuthor) {
				rawAuthorText.append(ch, start, length);
			}
			if (this.isJournal) {
				journal.append(ch, start, length);
			}
			if (this.isTitle) {
				title.append(ch, start, length);
			}
			if (this.isTimesCited) {
				timesCited.append(ch, start, length);
			}
			if (this.isPMID) {
				pmid.append(ch, start, length);
			}
		}
	
		/**
		 * Returns true iff attributes contains the specified text
		 */
		public boolean contains(Attributes attributes, String text) {
			for (int i = 0; i < attributes.getLength(); i++) {
				if (attributes.getValue(i).equalsIgnoreCase(text))
					return true;
			}

			return false;
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("Item") && this.isAuthor) {
	            this.isAuthor = false;
	            author = new Author(rawAuthorText.toString(), Category.PUBMED);
	            // Add author to publication author list
	            if (!pubAuthorList.contains(author)) {
	                pubAuthorList.add(author);
	            }
	        }
	        if (qName.equals("Item") && this.isJournal) {
	            this.isJournal = false;
	        }
	        if (qName.equals("Item") && this.isPubDate) {
	            this.isPubDate = false;
	        }
	        if (qName.equals("Item") && this.isTitle) {
	            this.isTitle = false;
	        }
	        if (qName.equals("Item") && this.isTimesCited) {
	            this.isTimesCited = false;
	        }
	        if (qName.equals("Id")) {
	            this.isPMID = false;
	        }
	        if (qName.equalsIgnoreCase("DocSum")) {
	            Publication publication = new Publication(title.toString(), pubDate.toString(), 
	                    journal.toString(), timesCited.toString(), null, pubAuthorList);
	            publication.setPMID(pmid.toString()); // TODO: pass this value
	                                                       // through the
	                                                       // constructor?
	            pubList.add(publication);
	            pubAuthorList.clear();
	            publication_counter++;
				// Calculate Percentage. This must be a value between 0..100.
				int percentComplete = (int) (((double) publication_counter / totalPubs) * 100);
				
				if (taskMonitor != null)
					taskMonitor.setProgress(percentComplete);
			}
		}
	
		/**
		 * Get publication list
		 */
		public ArrayList<Publication> getPubList() {
			return pubList;
		}

		/**
		 * Get total pubs
		 */
		public int getTotalPubs() {
			return pubList.size();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (contains(attributes, "Author")) {
				this.isAuthor = true;
				rawAuthorText.setLength(0);
			}
			if (contains(attributes, "FullJournalName")) {
				this.isJournal = true;
				journal.setLength(0);
			}
			if (contains(attributes, "PubDate")) {
				this.isPubDate = true;
				pubDate.setLength(0);
			}
			if (contains(attributes, "Title")) {
				this.isTitle = true;
				title.setLength(0);
			}
			if (contains(attributes, "PmcRefCount")) {
				this.isTimesCited = true;
				timesCited.setLength(0);
			}
			if (qName.equals("Id")) {
				this.isPMID = true;
				pmid.setLength(0);
			}
		}
	}
}
