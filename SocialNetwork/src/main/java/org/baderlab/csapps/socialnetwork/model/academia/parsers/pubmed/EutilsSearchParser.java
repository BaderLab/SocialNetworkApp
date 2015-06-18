package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
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
    private String queryKey = null;
    /**
     * The total number of publications found in search
     */
    private String totalPubs = null;
    /**
     * Unique WebEnv. Necessary for retrieving search results
     */
    private String webEnv = null;
    
    // TODO: Add descriptions for retStart and retMax
    private int retStart = 0;
    private int retMax = 0;
    
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
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
            // TODO: Add log message
        } catch (SAXException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
            // TODO: Add log message
        } catch (IOException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
            // TODO: Add log message
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
        if (this.isTotalPubs) {
            this.setTotalPubs(new String(ch, start, length));
            this.isTotalPubs = false;
        }
        if (this.isQueryKey) {
            this.setQueryKey(new String(ch, start, length));
            this.isQueryKey = false;
        }
        if (this.isWebEnv) {
            this.setWebEnv(new String(ch, start, length));
            this.isWebEnv = false;
        }
    }

    public void DefaultHandler() {
        this.setTotalPubs(null);
    }

    /**
     * Get query key
     * 
     * @return String queryKey
     */
    public String getQueryKey() {
        return this.queryKey;
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
        if (this.totalPubs != null && Pattern.matches("[0-9]+", this.totalPubs)) {
            return Integer.parseInt(this.totalPubs);
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
        return webEnv;
    }

    /**
     * Set query key
     * 
     * @param String queryKey
     */
    private void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }
    
    

    /**
     * Set total pubs
     * 
     * @param String totalPubs
     */
    private void setTotalPubs(String totalPubs) {
        this.totalPubs = totalPubs;
    }

    /**
     * Set web env
     * 
     * @param String webEnv
     */
    private void setWebEnv(String webEnv) {
        this.webEnv = webEnv;
    }
    
    // Reset XML variables
    @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("Count") && this.totalPubs == null) {
            this.isTotalPubs = true;
        }
        if (qName.equalsIgnoreCase("QueryKey")) {
            this.isQueryKey = true;
        }
        if (qName.equalsIgnoreCase("WebEnv")) {
            this.isWebEnv = true;
        }
    }

}
