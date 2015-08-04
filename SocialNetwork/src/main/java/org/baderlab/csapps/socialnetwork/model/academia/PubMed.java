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
import java.util.List;
import java.util.regex.Pattern;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsRetrievalParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsSearchParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsTimesCitedParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.PubMedXmlParser;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.cytoscape.work.TaskMonitor;

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
    public static HashMap<String, Object> constructPubMedAttrMap() {
        HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
        nodeAttrMap.put(NodeAttribute.LABEL.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.FIRST_NAME.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.LAST_NAME.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.MAIN_INSTITUTION.toString(), "N/A");
        nodeAttrMap.put(NodeAttribute.TIMES_CITED.toString(), 0);
        nodeAttrMap.put(NodeAttribute.PUBLICATION_COUNT.toString(), 0);
        nodeAttrMap.put(NodeAttribute.PUBLICATIONS.toString(), new ArrayList<String>());
        nodeAttrMap.put(NodeAttribute.INSTITUTION.toString(), new ArrayList<String>());
        nodeAttrMap.put(NodeAttribute.IS_SELECTED.toString(), false);
        List<Integer> pubsPerYearList = new ArrayList<Integer>();
        pubsPerYearList.add(0);
        nodeAttrMap.put(NodeAttribute.PUBS_PER_YEAR.toString(), pubsPerYearList);
        String startYearTxt = SocialNetworkAppManager.getStartDateTextFieldRef().getText().trim();
        String endYearTxt = SocialNetworkAppManager.getEndDateTextFieldRef().getText().trim();
        if (Pattern.matches("[0-9]+", startYearTxt) && Pattern.matches("[0-9]+", endYearTxt)) {
            int startYear = Integer.parseInt(startYearTxt), endYear = Integer.parseInt(endYearTxt);
            List<Integer> years = new ArrayList<Integer>();
            for (int i = startYear; i <= endYear; i++) {
                years.add(i);
            }
            nodeAttrMap.put(NodeAttribute.YEARS.toString(), years);
        }
        return nodeAttrMap;
    }
    
    /**
     * Use the pmids of the specified publications to construct a query (for
     * eUtils). The various pmids will be combined with an OR operator.
     * 
     * @param ArrayList pubList
     * @return String eUtilsPMIDs
     */
    public static String getEutilsPMIDs(ArrayList<Publication> pubList) {
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
     * A list containing all the results that search session has yielded
     */
    private ArrayList<Publication> pubList = null;

    /**
     * Create a new {@link PubMed} session from xmlFile
     *
     * @param File xmlFile
     * @param TaskMonitor taskMonitor
     */
    public PubMed(File xmlFile, TaskMonitor taskMonitor) {
        PubMedXmlParser xmlParser = new PubMedXmlParser(xmlFile, taskMonitor);
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
        totalPubs = eUtilsRetParser.getTotalPubs();
        setPubList(eUtilsRetParser.getPubList());
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
     * Return the total number of hits (total number of publications)
     * 
     * @return int totalHits
     */
    public int getTotalHits() {
        return this.pubList != null ? this.pubList.size() : 0;
    }

    /**
     * Set the PmcRefCount of the publications in the list.
     * 
     * @param ArrayList pubList
     */
    private void setPmcRefCount(ArrayList<Publication> pubList) {
        Query query = new Query(PubMed.getEutilsPMIDs(pubList));
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
