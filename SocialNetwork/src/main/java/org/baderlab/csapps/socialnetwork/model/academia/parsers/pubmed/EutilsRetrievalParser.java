package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.IOException;
import java.util.ArrayList;
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
     * A publication's total number of citations
     */
    private String timesCited = null;
    /**
     * A publication's title
     */
    private String title = null;
    
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
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
            // TODO: add log message
        } catch (SAXException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
            // TODO: add log message
        } catch (IOException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
            // TODO: add log message
        }
    }

    // Collect tag contents (if applicable)
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isPubDate) {
            this.pubDate = new String(ch, start, length);
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
            this.journal = new String(ch, start, length);
            this.isJournal = false;
        }
        if (this.isTitle) {
            this.title = new String(ch, start, length);
            this.isTitle = false;
        }
        if (this.isTimesCited) {
            this.timesCited = new String(ch, start, length);
            this.isTimesCited = false;
        }
        if (this.isPMID) {
            this.pmid = new String(ch, start, length);
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
            Publication publication = new Publication(this.title, this.pubDate, this.journal, this.timesCited, null, this.pubAuthorList);
            publication.setPMID(this.pmid); // TODO: pass this value through the constructor?
            this.pubList.add(publication);
            this.pubAuthorList.clear();
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

}
