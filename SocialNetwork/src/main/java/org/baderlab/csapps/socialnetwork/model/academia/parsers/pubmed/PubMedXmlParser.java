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
        this.lastName = new StringBuilder();
        this.firstName = new StringBuilder();
        this.middleInitials = new StringBuilder();
        this.institution = new StringBuilder();
        this.journal = new StringBuilder();
        this.pubDate = new StringBuilder();
        this.pmid = new StringBuilder();
        this.timesCited = new StringBuilder();
        this.title = new StringBuilder();
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

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        // Collect tag contents (if applicable)
        if (this.isPubDate) {
            this.pubDate.append(ch, start, length);
        }
        if (this.isAuthor) {
            this.author = new Author(new String(ch, start, length), Category.PUBMED);
        }
        if (this.isInstitution) {
            this.institution.append(ch, start, length);
        }
        if (this.isFirstName) {
            this.firstName.append(ch, start, length);
        }
        if (this.isLastName) {
            this.lastName.append(ch, start, length);
        }
        if (this.isMiddleInitial) {
            this.middleInitials.append(ch, start, length);
        }
        if (this.isJournal) {
            this.journal.append(ch, start, length);
        }
        if (this.isTitle) {
            this.title.append(ch, start, length);
        }
        if (this.isTimesCited) {
            this.timesCited.append(ch, start, length);
        }
        if (this.isPMID) {
            this.pmid.append(ch, start, length);
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
            Publication pub = new Publication(this.title.toString(), this.pubDate.toString(), this.journal.toString(), 
                    this.timesCited.toString(), null, this.pubAuthorList);
            pub.setPMID(this.pmid.toString());
            this.pubList.add(pub);
            this.pubAuthorList.clear();
        }
        if (qName.equals("Author")) {
            // add the firstname,lastname, initial to the author
            this.author.setFirstName(this.firstName.toString());
            this.author.setLastName(this.lastName.toString());
            this.author.setMiddleInitial(this.middleInitials.toString());
            this.author.setFirstInitial(this.firstName.substring(0, 1));
            this.author.setLabel(this.author.getFirstInitial() + " " + this.author.getLastName());
            this.author.addInstitution(this.institution.toString());
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
            this.institution.setLength(0);
        }
        if (qName.equals("LastName")) {
            this.isLastName = true;
            this.lastName.setLength(0);
        }
        if (qName.equals("ForeName")) {
            this.isFirstName = true;
            this.firstName.setLength(0);
        }
        if (qName.equals("Initials")) {
            this.isMiddleInitial = true;
            this.middleInitials.setLength(0);
        }
        if (qName.equals("Title")) {
            this.isJournal = true;
            this.journal.setLength(0);
        }
        if (qName.equals("PubDate")) {
            this.isPubDate = true;
            this.pubDate.setLength(0);
        }
        if (qName.equals("ArticleTitle")) {
            this.isTitle = true;
            this.title.setLength(0);
        }
        if (qName.equals("PmcRefCount")) {
            this.isTimesCited = true;
            this.timesCited.setLength(0);
        }
        if (qName.equals("ArticleId") && contains(attributes, "pubmed")) {
            this.isPMID = true;
            this.pmid.setLength(0);
        }
    }

}
