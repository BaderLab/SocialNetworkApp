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
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.academia.EutilsKeys;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
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
public class EutilsSearchParser extends AbstractTask {

   
    /**
     * querytranslation. just to see what ncbi translates a query into
     */
    private StringBuilder queryTranslation = null;
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

    private Query query = null;
    private SocialNetwork socialNetwork = null;
    
    private static final Logger logger = Logger.getLogger(EutilsSearchParser.class.getName());

    /**
     * Create a new eUtils search parser
     * 
     * @param Query query
     * @throws IOException 

     */
    public EutilsSearchParser(Query query,SocialNetwork socialNetwork) {
        this.queryKey = new StringBuilder();
        this.totalPubs = new StringBuilder();
        this.webEnv = new StringBuilder();
        this.retStart = new StringBuilder();
        this.retMax = new StringBuilder();
        this.queryTranslation = new StringBuilder();
        
        this.query = query;
        this.socialNetwork = socialNetwork;
       
    }

    /**
     * Use the pmids of the specified publications to construct a query (for
     * eUtils). The various pmids will be combined with an OR operator.
     * 
     * @param ArrayList pubList
     * @return String eUtilsPMIDs
     */
    private String getEutilsPMIDs(ArrayList<Publication> pubList) {
        Publication pub = null;
        int retStart = 0;
        int totalPubs = pubList.size();
        int retMax = totalPubs > 400 ? 400 : totalPubs;
        StringBuilder pmids = new StringBuilder();
        for (int i = retStart; i < retMax; i++) {
            pub = pubList.get(i);
            pmids.append(pub.getPMID());
            //pmids.append("[UID]");
            if (i < (retMax - 1)) {
                pmids.append(",");
            }
        }
        return pmids.toString();
    }
    
    @Override
	public void run(TaskMonitor taskMonitor) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        taskMonitor.setStatusMessage("Searching PubMed ...");   
        
        
        //if query is null then we should try to build the query from the publications object
        if(query == null)
        	query = new Query(getEutilsPMIDs(socialNetwork.getPublications()));
        
        taskMonitor.setProgress(0);

        try {
            // Create new SAXParser
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            SearchParser parser = new SearchParser();           
            HttpPost httpPost = new HttpPost("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("db", "pubmed"));
            nvps.add(new BasicNameValuePair("term", query.toString()));
            nvps.add(new BasicNameValuePair("usehistory", "y"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
            httpPost.addHeader("User-Agent","elink/1.0");
            response = httpclient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
            	logger.log(Level.INFO, String.format("Eutils response code: %d", responseCode));
                return;
            }
            HttpEntity entity = response.getEntity();

            saxParser.parse(new InputSource(entity.getContent()), parser);
            
            //set all the parsed info in the socialnetwork instance of Eutil keys. 
            socialNetwork.setEutilsResults(new EutilsKeys(parser.getQueryKey(),parser.getWebEnv(),parser.getRetStart(), parser.getRetMax(), parser.getTotalPubs()));
            socialNetwork.setQueryTranslation(parser.getQueryTranslation());
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
    private class SearchParser extends DefaultHandler{
    
    	 /**
         * XML Parsing variables. Used to temporarily store data.
         */
        boolean isQueryKey = false;
        boolean isWebEnv = false;
        boolean isTotalPubs = false;
        boolean isRetStart = false;
        boolean isRetMax = false;
        boolean isQuerytranslation = false;
    	
	    	/* (non-Javadoc)
	     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	     */
	    @Override
	    public void characters(char ch[], int start, int length) throws SAXException {
	        if (this.isTotalPubs) {
	            totalPubs.append(ch, start, length);
	        }
	        if (this.isQueryKey) {
	            queryKey.append(ch, start, length);
	        }
	        if (this.isWebEnv) {
	            webEnv.append(ch, start, length);
	        }
	        if (this.isRetStart) {
	            retStart.append(ch, start, length);
	        }
	        if (this.isRetMax) {
	            retMax.append(ch, start, length);
	        }
	        if (this.isQuerytranslation) {
	            queryTranslation.append(ch, start, length);
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
	        if (qName.equalsIgnoreCase("QueryTranslation")) {
	            this.isQuerytranslation = false;
	        }
	    }
	
	    /**
	     * Get query key
	     * 
	     * @return String queryKey
	     */
	    public String getQueryKey() {
	        return queryKey.toString();
	    }
	
	    /**
	     * Get the retMax
	     * 
	     * @return int retMax
	     */
	    public int getRetMax() {
	        String total = retMax.toString();
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
	        String total = retStart.toString();
	        if (total != null && Pattern.matches("[0-9]+", total)) {
	            return Integer.parseInt(total);
	        } else {
	            return 0;
	        }
	    }
	
		public String getQueryTranslation(){
			if(queryTranslation != null)
				return queryTranslation.toString();
	
			else
				return "";
		}
	
	    /**
	     * Get total pubs
	     * 
	     * @return int totalPubs
	     */
	    public int getTotalPubs() {
	        String total = totalPubs.toString();
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
	        if (qName.equalsIgnoreCase("Count") && totalPubs.length() == 0) {
	            isTotalPubs = true;
	            totalPubs.setLength(0);
	        }
	        if (qName.equalsIgnoreCase("QueryKey")) {
	            isQueryKey = true;
	            queryKey.setLength(0);
	        }
	        if (qName.equalsIgnoreCase("WebEnv")) {
	            isWebEnv = true;
	            webEnv.setLength(0);
	        }
	        if (qName.equalsIgnoreCase("RetStart")) {
	            isRetStart = true;
	            retStart.setLength(0);
	        }
	        if (qName.equalsIgnoreCase("RetMax")) {
	            isRetMax = true;
	            retMax.setLength(0);
	        }
	        if (qName.equalsIgnoreCase("QueryTranslation")) {
	            isQuerytranslation = true;
	            queryTranslation.setLength(0);
	        }
	    }
    }


	

}
