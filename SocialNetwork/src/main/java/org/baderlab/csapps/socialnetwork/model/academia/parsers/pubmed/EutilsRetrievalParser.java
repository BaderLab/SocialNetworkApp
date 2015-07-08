package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

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
import org.baderlab.csapps.socialnetwork.model.academia.Tag;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class EutilsRetrievalParser extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(EutilsRetrievalParser.class.getName());

    /**
     * XML Parsing variables. Used to temporarily store data.
     */
    boolean isAuthor = false;
    boolean isJournal = false;
    boolean isPMID = false;
    boolean isPubDate = false;
    boolean isTimesCited = false;
    boolean isTitle = false;

    /**
     * The author of a specific publication.
     */
    private Author author = null;
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
     * Create a new eUtils retrieval parser
     * 
     * @param String queryKey
     * @param String webEnv
     * @param int retStart
     * @param int retMax
     * @param int totalPubs
     */
    public EutilsRetrievalParser(String queryKey, String webEnv, int retStart, int retMax, int totalPubs) {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            while (retStart < totalPubs) {
                // Use newly discovered queryKey and webEnv to build a tag
                Tag tag = new Tag(queryKey, webEnv, retStart, retMax);
                // Load all publications at once
                String url = String.format("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed%s", tag);
                saxParser.parse(url, this);
                retStart += retMax;
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

    // Collect tag contents (if applicable)
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isPubDate) {
            this.pubDate.append(ch, start, length);
            this.isPubDate = false;
        }
        if (this.isAuthor) {
            this.author = new Author(new String(ch, start, length), Category.PUBMED);
            // Add author to publication author list
            if (!this.pubAuthorList.contains(this.author)) {
                this.pubAuthorList.add(this.author);
            }
            this.isAuthor = false;
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
            Publication publication = new Publication(this.title != null ? this.title.toString() : null,
                    this.pubDate != null ? this.pubDate.toString() : null, this.journal != null ? this.journal.toString() : null,
                    this.timesCited != null ? this.timesCited.toString() : null, null, this.pubAuthorList);
            publication.setPMID(this.pmid.toString()); // TODO: pass this value
                                                       // through the
                                                       // constructor?
            this.pubList.add(publication);
            this.pubAuthorList.clear();
            this.journal = null;
            this.pubDate = null;
            this.title = null;
            this.timesCited = null;
            this.pmid = null;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (contains(attributes, "Author")) {
            this.isAuthor = true;
        }
        if (contains(attributes, "FullJournalName")) {
            this.isJournal = true;
            this.journal = new StringBuilder();
        }
        if (contains(attributes, "PubDate")) {
            this.isPubDate = true;
            this.pubDate = new StringBuilder();
        }
        if (contains(attributes, "Title")) {
            this.isTitle = true;
            this.title = new StringBuilder();
        }
        if (contains(attributes, "PmcRefCount")) {
            this.isTimesCited = true;
            this.timesCited = new StringBuilder();
        }
        if (qName.equals("Id")) {
            this.isPMID = true;
            this.pmid = new StringBuilder();
        }
    }

}
