package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
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
    boolean isRetStart = false;
    boolean isRetMax = false;
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
    private StringBuilder retStart = null;
    private StringBuilder retMax = null;

    private static final Logger logger = Logger.getLogger(EutilsSearchParser.class.getName());

    /**
     * Create a new eUtils search parser
     * 
     * @param Query query
     * @throws IOException 
     */
    public EutilsSearchParser(Query query) {
        this.queryKey = new StringBuilder();
        this.totalPubs = new StringBuilder();
        this.webEnv = new StringBuilder();
        this.retStart = new StringBuilder();
        this.retMax = new StringBuilder();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // Create new SAXParser
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            
            HttpPost httpPost = new HttpPost("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("db", "pubmed"));
            nvps.add(new BasicNameValuePair("term", query.toString()));
            nvps.add(new BasicNameValuePair("usehistory", "y"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            response = httpclient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
            	logger.log(Level.INFO, String.format("Eutils response code: %d", responseCode));
                return;
            }
            HttpEntity entity = response.getEntity();
            saxParser.parse(new InputSource(entity.getContent()), this);
            response.close();        		
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
        if (this.isTotalPubs) {
            this.totalPubs.append(ch, start, length);
        }
        if (this.isQueryKey) {
            this.queryKey.append(ch, start, length);
        }
        if (this.isWebEnv) {
            this.webEnv.append(ch, start, length);
        }
        if (this.isRetStart) {
            this.retStart.append(ch, start, length);
        }
        if (this.isRetMax) {
            this.retMax.append(ch, start, length);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(String, String, String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("Count")) {
            this.isTotalPubs = false;
        }
        if (qName.equalsIgnoreCase("QueryKey")) {
            this.isQueryKey = false;
        }
        if (qName.equalsIgnoreCase("WebEnv")) {
            this.isWebEnv = false;
        }
        if (qName.equalsIgnoreCase("RetStart")) {
            this.isRetStart = false;
        }
        if (qName.equalsIgnoreCase("RetMax")) {
            this.isRetMax = false;
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
        String total = this.retMax.toString();
        if (total != null && Pattern.matches("[0-9]+", total)) {
            return Integer.parseInt(total);
        } else {
            return 0;
        }
    }

    /**
     * Get the retStart
     * 
     * @return int retStart
     */
    public int getRetStart() {
        String total = this.retStart.toString();
        if (total != null && Pattern.matches("[0-9]+", total)) {
            return Integer.parseInt(total);
        } else {
            return 0;
        }
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
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("Count") && this.totalPubs.length() == 0) {
            this.isTotalPubs = true;
            this.totalPubs.setLength(0);
        }
        if (qName.equalsIgnoreCase("QueryKey")) {
            this.isQueryKey = true;
            this.queryKey.setLength(0);
        }
        if (qName.equalsIgnoreCase("WebEnv")) {
            this.isWebEnv = true;
            this.webEnv.setLength(0);
        }
        if (qName.equalsIgnoreCase("RetStart")) {
            this.isRetStart = true;
            this.retStart.setLength(0);
        }
        if (qName.equalsIgnoreCase("RetMax")) {
            this.isRetMax = true;
            this.retMax.setLength(0);
        }
    }

}
