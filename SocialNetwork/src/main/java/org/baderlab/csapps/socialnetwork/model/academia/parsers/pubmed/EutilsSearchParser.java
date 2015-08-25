package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
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
    
    private final String USER_AGENT = "esearch/1.0";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    
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
     * Send a POST request
     * 
     * @param HttpsURLConnection con
     * @param String parameters
     * 
     * @return int response code
     * 
     * @throws ProtocolException
     * @throws IOException
     */
    private int sendPOST(HttpsURLConnection con, String parameters) throws ProtocolException, IOException {
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", CONTENT_TYPE);
        
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters);
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
			FileUtils.writeStringToFile(new File("C:\\Users\\SloGGy\\Desktop\\search.txt"), response.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    */

    /**
     * Create a new eUtils search parser
     * 
     * @param Query query
     */
    public EutilsSearchParser(Query query) {
        this.queryKey = new StringBuilder();
        this.totalPubs = new StringBuilder();
        this.webEnv = new StringBuilder();
        this.retStart = new StringBuilder();
        this.retMax = new StringBuilder();
        try {
            // Create new SAXParser
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            
            URL obj = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi");
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            int responseCode = sendPOST(con, String.format("db=pubmed&term=%s", query));
            if (responseCode != 200) {
            	logger.log(Level.INFO, String.format("Entrez utilities response code: %d", responseCode));
                return;
            }
            // printContents(con); TODO
            saxParser.parse(new InputSource(con.getInputStream()), this);
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
