package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
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
public class EutilsTimesCitedParser extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(EutilsTimesCitedParser.class.getName());

    /**
     * XML Parsing variables. Used to temporarily store data.
     */
    boolean isTimesCited = false;
    boolean isPMID = false;

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
     * Number of pmids that the SAX parser was unable to resolve. Used for testing purposes.
     */
    private int numSaxConflicts = 0;
    // TODO: Write description
    private HashMap<String, Publication> pubMap = null;
    
    private final String USER_AGENT = "esummary/1.0";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    
    /**
     * Create PubMap
     * 
     * @param ArrayList pubList
     */
    private void createPubMap(ArrayList<Publication> pubList) {
        this.pubMap = new HashMap<String, Publication>();
        Iterator<Publication> it = pubList.iterator();
        Publication pub = null;
        while(it.hasNext()) {
            pub = it.next();
            this.pubMap.put(pub.getPMID(), pub);
        }
    }
    
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
			FileUtils.writeStringToFile(new File("C:\\Users\\SloGGy\\Desktop\\times_cited.txt"), response.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    */

    /**
     * Create a new eUtils times cited parser
     * 
     * @param ArrayList pubList
     * @param int startIndex
     * @param String queryKey
     * @param String webEnv
     * @param int retStart
     * @param int retMax
     * @param int totalPubs
     */
    public EutilsTimesCitedParser(ArrayList<Publication> pubList, String queryKey, String webEnv, int retStart, int retMax) {
        try {
            this.createPubMap(pubList);
            this.pubList = pubList;
            int totalPubs = pubList.size();
            this.pmid = new StringBuilder();
            this.timesCited = new StringBuilder();
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            while (retStart < totalPubs) {
                // Use newly discovered queryKey and webEnv to build a tag
                Tag tag = new Tag(queryKey, webEnv, retStart, retMax);
                URL obj = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi");
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                int responseCode = sendPOST(con, tag);
                if (responseCode != 200) {
                	logger.log(Level.INFO, String.format("Eutils response code: %d", responseCode));
                    return;
                }
                // printContents(con); TODO
                saxParser.parse(con.getInputStream(), this);                
                retStart += retMax;
            }
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please try again some other time.");
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please try again some other time.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your internet connection.");
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isPMID) {
            this.pmid.append(ch, start, length);
        }
        if (this.isTimesCited) {
            this.timesCited.append(ch, start, length);
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

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("Id")) {
            this.isPMID = false;
        }
        if (qName.equalsIgnoreCase("Item") && this.isTimesCited) {
            this.isTimesCited = false;
        }
        if (qName.equalsIgnoreCase("DocSum")) {
            Publication pub = this.pubMap.get(this.pmid.toString());
            if (pub != null) {
                pub.setTimesCited(this.timesCited.toString());
            } else {
                logger.log(Level.SEVERE, String.format("Times cited could not be retrieved for publication with the following pmid: %s", this.pmid));
            }
        }
    }

    /**
     * Get the publication list
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
        if (contains(attributes, "PmcRefCount")) {
            this.isTimesCited = true;
            this.timesCited.setLength(0);
        }
        if (qName.equals("Id")) {
            this.isPMID = true;
            this.pmid.setLength(0);
        }
    }

    /**
     * Get the number of times the SAX parser truncated
     * a pmid value. Used for testing.
     * 
     * @return int numSaxConflicts
     */
    public int getNumSaxConflicts() {
        return numSaxConflicts;
    }

}
