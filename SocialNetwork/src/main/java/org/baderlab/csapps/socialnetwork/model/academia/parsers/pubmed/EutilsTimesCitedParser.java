package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
     * Globally referenced index variable to retrieve publications from publist
     */
    private int index = 0;

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
            this.pubList = pubList;
            int totalPubs = pubList.size();
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            String url = null;
            while (retStart < totalPubs) {
                // Use newly discovered queryKey and webEnv to build a tag
                Tag tag = new Tag(queryKey, webEnv, retStart, retMax);
                // Load all publications at once
                url = String.format("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed%s", tag);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isPMID) {
            this.pmid.append(ch, start, length);
            this.isPMID = false;
        }
        if (this.isTimesCited) {
            this.timesCited.append(ch, start, length);
            this.isTimesCited = false;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("DocSum")) {
            Publication pub = this.pubList.get(this.index);
            pub.setTimesCited(this.timesCited.toString());
            this.index++;
            if (!pub.getPMID().equals(this.pmid.toString())) {
                logger.log(Level.WARNING, "SAX parser truncated pmid from " + pub.getPMID() + " to " + this.pmid.toString());
            }
            this.pmid = null;
            this.timesCited = null;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
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
