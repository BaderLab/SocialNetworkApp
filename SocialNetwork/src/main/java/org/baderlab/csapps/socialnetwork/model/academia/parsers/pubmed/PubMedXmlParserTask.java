package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
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

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class PubMedXmlParserTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(PubMedXmlParserTask.class.getName());

    

    /**
     * The author of a specific publication. This variable is globally
     * referenced to allow for multiple additions in to a publication
     */
    private Author author = null;
    private StringBuilder lastName = null;
    private StringBuilder firstName = null;
    private StringBuilder middleInitials = null;
    private StringBuilder institution = null;
    /**
     * A publication's journal
     */
    private StringBuilder journal = null;
    /**
     * A list containing all authors found in a particular publication
     */
    private ArrayList<Author> pubAuthorList = new ArrayList<Author>();
    /**
     * A publication's date
     */
    private StringBuilder pubDate = null;
    /**
     * A list containing all the results that search session has yielded
     */
    private ArrayList<Publication> pubList = new ArrayList<Publication>();
    /**
     * A publication's unique identifier
     */
    private StringBuilder pmid = null;

    /**
     * A publication's total number of citations
     */
    private StringBuilder timesCited = null;
    /**
     * A publication's title
     */
    private StringBuilder title = null;
    
    private File xmlFile = null;
    private SocialNetwork socialNetwork = null;
    private SocialNetworkAppManager appManager = null;

    /**
     * Create a new PubMed xml parser
     * 
     * @param File xml
     */
    public PubMedXmlParserTask(SocialNetworkAppManager appManager,SocialNetwork socialNetwork) {
    	this.appManager = appManager;
        this.socialNetwork = socialNetwork;
    	
    	this.xmlFile = this.appManager.getNetworkFile();
    	
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
	public void run(TaskMonitor taskMonitor) throws Exception {
    	try {
            MonitoredFileInputStream fileInputStream = new MonitoredFileInputStream(xmlFile, taskMonitor, "Parsing PubMed XML ...");
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            //create instance of parser
            Parser xmlparser = new Parser();            
            saxParser.parse(fileInputStream, xmlparser);
            
            ArrayList<Publication> pubList = xmlparser.getPubList();
       	 	if (pubList.size() < 1) {
                return; // stop here if there are no publications
            }
            PubMed pubmed = new PubMed(pubList);
            
            socialNetwork.setPublications(pubmed.getPubList());
            if (pubList == null) {
                this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
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
    	/* (non-Javadoc)
	     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	     */
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
	     *
	     * @param Attribute attributes
	     * @param String text
	     * 
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
	
	    /* (non-Javadoc)
	     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	     * java.lang.String, java.lang.String)
	     */
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
	     * 
	     * @return ArrayList pubList
	     */
	    public ArrayList<Publication> getPubList() {
	        return pubList;
	    }
	
	    /* (non-Javadoc)
	     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	     */
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
