package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

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
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.MonitoredFileInputStream;
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
public class PubMedXmlParser extends DefaultHandler {
    
    private static final Logger logger = Logger.getLogger(PubMedXmlParser.class.getName());

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
    
    /**
     * Create a new PubMed xml parser
     * 
     * @param File xml
     */
    public PubMedXmlParser(File xml, TaskMonitor taskMonitor) {
        try {
        	MonitoredFileInputStream fileInputStream = new MonitoredFileInputStream(xml, taskMonitor, "Parsing PubMed XML ...");
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(fileInputStream, this);
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
    
    // Collect tag contents (if applicable)
    @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isPubDate) {
            this.pubDate.append(ch, start, length);
            this.isPubDate = false;
        }
        if (this.isAuthor) {
            this.author = new Author(new String(ch, start, length), Category.PUBMED);
            this.isAuthor = false;
        }
        if (this.isInstitution) {
            this.institution.append(ch, start, length);
            this.isInstitution = false;
        }
        if (this.isFirstName) {
            this.firstName.append(ch, start, length);
            this.isFirstName = false;
        }
        if (this.isLastName) {
            this.lastName.append(ch, start, length);
            this.isLastName = false;
        }
        if (this.isMiddleInitial) {
            this.middleInitials.append(ch, start, length);
            this.isMiddleInitial = false;
        }
        if (this.isJournal) {
            this.journal.append(ch, start, length);
            this.isJournal = false;
        }
        if (this.isTitle) {
            this.title.append(ch, start, length);
            this.isTitle = false;
        }
        if (this.isTimesCited) {
            this.timesCited.append(ch, start, length);
            this.isTimesCited = false;
        }
        if (this.isPMID) {
            this.pmid.append(ch, start, length);
            this.isPMID = false;
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
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("PubmedArticle")) {
            Publication pub = new Publication(this.title != null ? this.title.toString() : null, 
            		                          this.pubDate != null ? this.pubDate.toString() : null, 
            		                          this.journal != null ? this.journal.toString() : null, 
            		                          this.timesCited != null ? this.timesCited.toString() : null, 
            		                          null, 
            		                          this.pubAuthorList);
            pub.setPMID(this.pmid != null ? this.pmid.toString() : null);
            this.pubList.add(pub);
            this.pubAuthorList.clear();
        }
        if (qName.equals("Author")) {
            // add the firstname,lastname, initial to the author
            this.author.setFirstName(this.firstName != null ? this.firstName.toString() : null);
            this.author.setLastName(this.lastName != null ? this.lastName.toString() : null);
            this.author.setMiddleInitial(this.middleInitials != null ? this.middleInitials.toString() : null);
            this.author.setFirstInitial(this.firstName.substring(0, 1));
            this.author.setLabel(this.author.getFirstInitial() + " " + this.author.getLastName());
            this.author.setInstitution(this.institution != null ? this.institution.toString() : null);

            // Add author to publication author list
            if (!this.pubAuthorList.contains(this.author)) {
                this.pubAuthorList.add(this.author);
            }

        }
    }

    /**
     * Get publication list
     * 
     * @return ArrayList pubList
     */
    public ArrayList<Publication> getPubList() {
        return this.pubList;
    }

    /* (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("Author")) {
            this.isAuthor = true;
        }
        if (qName.equals("Affiliation")) {
            this.isInstitution = true;
            this.institution = new StringBuilder();
        }
        if (qName.equals("LastName")) {
            this.isLastName = true;
            this.lastName = new StringBuilder();
        }
        if (qName.equals("ForeName")) {
            this.isFirstName = true;
            this.firstName = new StringBuilder();
        }
        if (qName.equals("Initials")) {
            this.isMiddleInitial = true;
            this.middleInitials = new StringBuilder();
        }
        if (qName.equals("Title")) {
            this.isJournal = true;
            this.journal = new StringBuilder();
        }
        if (qName.equals("PubDate")) {
            this.isPubDate = true;
            this.pubDate = new StringBuilder();
        }
        if (qName.equals("ArticleTitle")) {
            this.isTitle = true;
            this.title = new StringBuilder();
        }
        if (qName.equals("PmcRefCount")) {
            this.isTimesCited = true;
            this.timesCited = new StringBuilder();
        }
        if (qName.equals("ArticleId") && contains(attributes, "pubmed")) {
            this.isPMID = true;
            this.pmid = new StringBuilder();
        }
    }

}
