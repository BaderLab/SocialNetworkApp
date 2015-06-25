package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class EutilsSearchParser extends DefaultHandler {

    /**
     * XML Parsing variables. Used to temporarily store data.
     */
    boolean isQueryKey = false;

    boolean isWebEnv = false;
    boolean isTotalPubs = false;
    /**
     * Unique queryKey. Necessary for retrieving search results
     */
    private StringBuilder queryKey = null;
    /**
     * The total number of publications found in search
     */
    private StringBuilder totalPubs = null;
    /**
     * Unique WebEnv. Necessary for retrieving search results
     */
    private StringBuilder webEnv = null;
    
    // TODO: Add descriptions for retStart and retMax
    private int retStart = 0;
    private int retMax = 0;
    
    private static final Logger logger = Logger.getLogger(EutilsSearchParser.class.getName());
    
    /**
     * Create a new eUtils search parser
     * 
     * @param Query query
     */
    public EutilsSearchParser(Query query) {
        try {
            // Create new SAXParser
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            // Get Query Key & Web Env
            String url = String.format("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=%s", query);
            saxParser.parse(url, this);
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
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isTotalPubs) {
            this.totalPubs.append(ch, start, length);
            this.isTotalPubs = false;
        }
        if (this.isQueryKey) {
            this.queryKey.append(ch, start, length);
            this.isQueryKey = false;
        }
        if (this.isWebEnv) {
            this.webEnv.append(new String(ch, start, length));
            this.isWebEnv = false;
        }
    }

    /**
     * Get query key
     * 
     * @return String queryKey
     */
    public String getQueryKey() {
        return this.queryKey.toString();
    }
    
    /**
     * Get the retMax
     * 
     * @return int retMax
     */
    public int getRetMax() {
        int total = getTotalPubs();
        this.retMax = total > 400 ? 400 : total;
        return this.retMax;
    }

    /**
     * Get the retStart
     * 
     * @return int retStart
     */
    public int getRetStart() {
        return retStart; // retStart is always 0
    }

    /**
     * Get total pubs
     * 
     * @return int totalPubs
     */
    public int getTotalPubs() {
    	String total = this.totalPubs.toString();
        if (total != null && Pattern.matches("[0-9]+", total)) {
            return Integer.parseInt(total);
        } else {
            return 0;
        }
    }

    /**
     * Get web env
     * 
     * @return String webEnv
     */
    public String getWebEnv() {
        return webEnv.toString();
    }
    
    /* (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("Count") && this.totalPubs == null) {
            this.isTotalPubs = true;
            this.totalPubs = new StringBuilder();
        }
        if (qName.equalsIgnoreCase("QueryKey")) {
            this.isQueryKey = true;
            this.queryKey = new StringBuilder();
        }
        if (qName.equalsIgnoreCase("WebEnv")) {
            this.isWebEnv = true;
            this.webEnv = new StringBuilder();
        }
    }

}
