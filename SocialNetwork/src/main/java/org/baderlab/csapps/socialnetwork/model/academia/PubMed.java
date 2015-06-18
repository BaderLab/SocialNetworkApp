/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates,
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.model.academia;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsRetrievalParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsSearchParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsTimesCitedParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.PubMedXmlParser;

/**
 * Methods & fields for manipulating PubMed data
 *
 * @author Victor Kofia
 */
public class PubMed {

    /**
     * Construct PubMed attribute map
     *
     * @return Map nodeAttrMap
     */
    public static HashMap<String, Object> constructPubMedAttrMap(Author author) {
        HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
        String[] columns = new String[] { BasicSocialNetworkVisualstyle.nodeattr_label, BasicSocialNetworkVisualstyle.nodeattr_lname,
                BasicSocialNetworkVisualstyle.nodeattr_fname, BasicSocialNetworkVisualstyle.nodeattr_timescited,
                BasicSocialNetworkVisualstyle.nodeattr_inst, BasicSocialNetworkVisualstyle.nodeattr_numpub,
                BasicSocialNetworkVisualstyle.nodeattr_pub };
        int i = 0;
        for (i = 0; i < 5; i++) {
            nodeAttrMap.put(columns[i], "");
        }
        // Initialize the num publication attribute (~Integer)
        nodeAttrMap.put(columns[i], 0);
        i++;
        // Initialize Publications attribute (~ ArrayList)
        nodeAttrMap.put(columns[i], new ArrayList<String>());
        return nodeAttrMap;
    }

    /**
     * A list containing all the results that search session has yielded
     */
    private ArrayList<Publication> pubList = null;

    /**
     * Create a new {@link PubMed} session from xmlFile
     *
     * @param File xmlFile
     */
    public PubMed(File xmlFile) {
        PubMedXmlParser xmlParser = new PubMedXmlParser(xmlFile);
        ArrayList<Publication> pubList = xmlParser.getPubList();
        if (pubList.size() < 1) {
            return; // stop here if there are no publications
        }
        setPubList(pubList);
        setPmcRefCount(this.getPubList());
    }

    /**
     * Create a new {@link PubMed} search session
     *
     * @param String searchTerm
     */
    public PubMed(String searchTerm) {
        Query query = new Query(searchTerm);
        EutilsSearchParser eUtilsSearchParser = new EutilsSearchParser(query);
        int totalPubs = eUtilsSearchParser.getTotalPubs();
        if (totalPubs < 1) {
            return; // stop here if there are no results
        }
        int retStart = eUtilsSearchParser.getRetStart();
        int retMax = eUtilsSearchParser.getRetMax();
        String queryKey = eUtilsSearchParser.getQueryKey();
        String webEnv = eUtilsSearchParser.getWebEnv();
        EutilsRetrievalParser eUtilsRetParser = new EutilsRetrievalParser(queryKey, webEnv, retStart, retMax, totalPubs);
        setPubList(eUtilsRetParser.getPubList());
    }
    
    /**
     * Use the pmids of the specified publications to construct a query (for eUtils). 
     * The various pmids will be combined with an OR operator.
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
            pmids.append("[UID]");
            if (i < (retMax - 1)) {
                pmids.append(" OR ");
            }
        }
        return pmids.toString();
    }

    /**
     * Return a list of all the publications (& co-authors) found for User's
     * specified authorName, MeSH term or Institution name.
     *
     * @return ArrayList pubList
     */
    public ArrayList<Publication> getPubList() { // Return all results
        return this.pubList;
    }
    
    /**
     * Set the PmcRefCount of the publications in the list.
     * 
     * @param ArrayList pubList
     */
    private void setPmcRefCount(ArrayList<Publication> pubList) {
        Query query = new Query(getEutilsPMIDs(pubList));
        EutilsSearchParser eUtilsSearchParser = new EutilsSearchParser(query);
        int retStart = eUtilsSearchParser.getRetStart();
        int retMax = eUtilsSearchParser.getRetMax();
        String queryKey = eUtilsSearchParser.getQueryKey();
        String webEnv = eUtilsSearchParser.getWebEnv();
        EutilsTimesCitedParser eUtilsTimesCitedParser = new EutilsTimesCitedParser(pubList, queryKey, webEnv, retStart, retMax);
        setPubList(eUtilsTimesCitedParser.getPubList());
    }

    /**
     * Set the publication list
     * 
     * @param ArrayList pubList
     */
    private void setPubList(ArrayList<Publication> pubList) {
        this.pubList = pubList;
    }

}
