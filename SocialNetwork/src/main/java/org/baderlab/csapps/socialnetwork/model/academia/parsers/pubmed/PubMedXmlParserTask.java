package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.MonitoredFileInputStream;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PubMedXmlParserTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(PubMedXmlParserTask.class.getName());

    /**
     * The author of a specific publication. This variable is globally
     * referenced to allow for multiple additions in to a publication
     */
    private Author author;
    private StringBuilder lastName;
    private StringBuilder firstName;
    private StringBuilder middleInitials;
    private StringBuilder institution;
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
    
    private File xmlFile;
    private SocialNetwork socialNetwork;
    private SocialNetworkAppManager appManager;

	public PubMedXmlParserTask(SocialNetworkAppManager appManager, SocialNetwork socialNetwork) {
		this.appManager = appManager;
		this.socialNetwork = socialNetwork;

		this.xmlFile = appManager.getNetworkFile();

		this.lastName = new StringBuilder();
		this.firstName = new StringBuilder();
		this.middleInitials = new StringBuilder();
		this.institution = new StringBuilder();
		this.journal = new StringBuilder();
		this.pubDate = new StringBuilder();
		this.pmid = new StringBuilder();
		this.timesCited = new StringBuilder();
		this.title = new StringBuilder();
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		tm.setTitle("Social Network - PubMed Parser");
		
		try {
			var fileInputStream = new MonitoredFileInputStream(xmlFile, tm, "Parsing PubMed XML...");
			var saxParser = SAXParserFactory.newInstance().newSAXParser();
			// create instance of parser
			var xmlparser = new Parser();
			saxParser.parse(fileInputStream, xmlparser);

			var pubList = xmlparser.getPubList();
			
			if (pubList.size() < 1)
				return; // stop here if there are no publications
			
			var pubmed = new PubMed(pubList);
			socialNetwork.setPublications(pubmed.getPubList());
			
			if (pubList == null) {
				this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "Exception occurred", e);
			CytoscapeUtilities
					.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
		} catch (SAXException e) {
			logger.log(Level.SEVERE, "Exception occurred", e);
			CytoscapeUtilities
					.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception occurred", e);
			CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
		}
	}
    
    private class Parser extends DefaultHandler{
    
		/**
		 * XML Parsing variables. Used to temporarily store data.
		 */
		boolean isAuthor = false;
		boolean isFirstName = false;
		boolean isInstitution = false;
		boolean isJournal = false;
		boolean isLastName = false;
		boolean isMiddleInitial = false;
		boolean isPMID = false;
		boolean isPubDate = false;
		boolean isTimesCited = false;
		boolean isTitle = false;

		@Override
		public void characters(char ch[], int start, int length) throws SAXException {
			// Collect tag contents (if applicable)
			if (this.isPubDate) {
				pubDate.append(ch, start, length);
			}
			if (this.isAuthor) {
				author = new Author(new String(ch, start, length), Category.PUBMED);
			}
			if (this.isInstitution) {
				institution.append(ch, start, length);
			}
			if (this.isFirstName) {
				firstName.append(ch, start, length);
			}
			if (this.isLastName) {
				lastName.append(ch, start, length);
			}
			if (this.isMiddleInitial) {
				middleInitials.append(ch, start, length);
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
	            if (attributes.getValue(i).equalsIgnoreCase(text)) {
	                return true;
	            }
	        }
	        return false;
	    }
	
	    @Override
	    public void endElement(String uri, String localName, String qName) throws SAXException {
	        if (qName.equals("PubDate")) {
	            this.isPubDate = false;
	        }
	        if (qName.equals("Author")) {
	            this.isAuthor = false;
	        }
	        if (qName.equals("Affiliation")) {
	            this.isInstitution = false;
	        }
	        if (qName.equals("ForeName")) {
	            this.isFirstName = false;
	        }
	        if (qName.equals("LastName")) {
	            this.isLastName = false;
	        }
	        if (qName.equals("Initials")) {
	            this.isMiddleInitial = false;
	        }
	        if (qName.equals("Title")) {
	            this.isJournal = false;
	        }
	        if (qName.equals("ArticleTitle")) {
	            this.isTitle = false;
	        }
	        if (qName.equals("PmcRefCount")) {
	            this.isTimesCited = false;
	        }
	        if (qName.equals("ArticleId") && this.isPMID) {
	            this.isPMID = false;
	        }
	        if (qName.equalsIgnoreCase("PubmedArticle")) {
	            Publication pub = new Publication(title.toString(), pubDate.toString(), journal.toString(), 
	                    timesCited.toString(), null, pubAuthorList);
	            pub.setPMID(pmid.toString());
	            pubList.add(pub);
	            pubAuthorList.clear();
	        }
	        if (qName.equals("Author")) {
	            // add the firstname,lastname, initial to the author
	            author.setFirstName(firstName.toString());
	            author.setLastName(lastName.toString());
	            author.setMiddleInitial(middleInitials.toString());
	            author.setFirstInitial(firstName.substring(0, 1));
	            author.setLabel(author.getFirstInitial() + " " + author.getLastName());
	            author.addInstitution(institution.toString());
	            // Add author to publication author list
	            if (!pubAuthorList.contains(author)) {
	                pubAuthorList.add(author);
	            }
	        }
	    }
	
	    /**
	     * Get publication list
	     */
	    public ArrayList<Publication> getPubList() {
	        return pubList;
	    }
	
	    @Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	        if (qName.equals("Author")) {
	            isAuthor = true;
	        }
	        if (qName.equals("Affiliation")) {
	            isInstitution = true;
	            institution.setLength(0);
	        }
	        if (qName.equals("LastName")) {
	            isLastName = true;
	            lastName.setLength(0);
	        }
	        if (qName.equals("ForeName")) {
	            isFirstName = true;
	            firstName.setLength(0);
	        }
	        if (qName.equals("Initials")) {
	            isMiddleInitial = true;
	            middleInitials.setLength(0);
	        }
	        if (qName.equals("Title")) {
	            isJournal = true;
	            journal.setLength(0);
	        }
	        if (qName.equals("PubDate")) {
	            isPubDate = true;
	            pubDate.setLength(0);
	        }
	        if (qName.equals("ArticleTitle")) {
	            isTitle = true;
	            title.setLength(0);
	        }
	        if (qName.equals("PmcRefCount")) {
	            isTimesCited = true;
	            timesCited.setLength(0);
	        }
	        if (qName.equals("ArticleId") && contains(attributes, "pubmed")) {
	            isPMID = true;
	            pmid.setLength(0);
	        }
	    }
    }
}
