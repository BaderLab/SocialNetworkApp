package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
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
    
    private final String USER_AGENT = "esummary/1.0";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";

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
     * Raw text of an author of a specific publication
     */
    private StringBuilder rawAuthorText = null;
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
     * Send a POST request
     * 
     * @param HttpsURLConnection con
     * @param Tag parameters
     * 
     * @return int response code
     * 
     * @throws ProtocolException
     * @throws IOException
     */
    private int sendPOST(HttpsURLConnection con, Tag parameters) throws ProtocolException, IOException {
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", CONTENT_TYPE);
        
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters.toString());
        wr.flush();
        wr.close();

        return con.getResponseCode();
    }
    
    // TODO: Delete. Here for testing purposes.
    /*
    private void printContents(HttpsURLConnection con) {
    	try {
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			FileUtils.writeStringToFile(new File("C:\\Users\\SloGGy\\Desktop\\retrieval.txt"), response.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    */

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
        this.rawAuthorText = new StringBuilder();
        this.journal = new StringBuilder();
        this.pubDate = new StringBuilder();
        this.pmid = new StringBuilder();
        this.timesCited = new StringBuilder();
        this.title = new StringBuilder();
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            while (retStart < totalPubs) {
                // Use newly discovered queryKey and webEnv to build a tag
                Tag tag = new Tag(queryKey, webEnv, retStart, retMax);
                // Load all publications at once
                URL obj = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi");
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                int responseCode = sendPOST(con, tag);
                if (responseCode != 200) {
                	logger.log(Level.INFO, String.format("Eutils response code: %d", responseCode));
                    return;
                }
                //printContents(con);
                saxParser.parse(con.getInputStream(), this);
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
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isPubDate) {
            this.pubDate.append(ch, start, length);
        }
        if (this.isAuthor) {
            this.rawAuthorText.append(ch, start, length);
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
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("Item") && this.isAuthor) {
            this.isAuthor = false;
            this.author = new Author(this.rawAuthorText.toString(), Category.PUBMED);
            // Add author to publication author list
            if (!this.pubAuthorList.contains(this.author)) {
                this.pubAuthorList.add(this.author);
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
            Publication publication = new Publication(this.title.toString(), this.pubDate.toString(), 
                    this.journal.toString(), this.timesCited.toString(), null, this.pubAuthorList);
            publication.setPMID(this.pmid.toString()); // TODO: pass this value
                                                       // through the
                                                       // constructor?
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

    /**
     * Get total pubs
     * 
     * @return int totalPubs
     */
    public int getTotalPubs() {
        return pubList.size();
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (contains(attributes, "Author")) {
            this.isAuthor = true;
            this.rawAuthorText.setLength(0);
        }
        if (contains(attributes, "FullJournalName")) {
            this.isJournal = true;
            this.journal.setLength(0);
        }
        if (contains(attributes, "PubDate")) {
            this.isPubDate = true;
            this.pubDate.setLength(0);
        }
        if (contains(attributes, "Title")) {
            this.isTitle = true;
            this.title.setLength(0);
        }
        if (contains(attributes, "PmcRefCount")) {
            this.isTimesCited = true;
            this.timesCited.setLength(0);
        }
        if (qName.equals("Id")) {
            this.isPMID = true;
            this.pmid.setLength(0);
        }
    }

}
