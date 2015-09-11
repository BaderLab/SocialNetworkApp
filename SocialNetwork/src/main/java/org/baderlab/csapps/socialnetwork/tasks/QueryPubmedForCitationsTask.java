package org.baderlab.csapps.socialnetwork.tasks;

import java.util.ArrayList;

import org.baderlab.csapps.socialnetwork.model.EutilsSearchResults;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsSearchParser;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class QueryPubmedForCitationsTask extends AbstractTask{

	private SocialNetwork socialNetwork = null;
	
	public QueryPubmedForCitationsTask(SocialNetwork socialNetwork) {
		super();
		this.socialNetwork = socialNetwork;
	}


	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Adding Pubmed citations ...");
		
		Query query = new Query(getEutilsPMIDs(this.socialNetwork.getPublications()));
        EutilsSearchParser eUtilsSearchParser = new EutilsSearchParser(query);
        this.socialNetwork.setQueryTranslation(eUtilsSearchParser.getQueryTranslation());
        System.out.println(eUtilsSearchParser.getQueryTranslation());
        EutilsSearchResults results = new EutilsSearchResults(eUtilsSearchParser.getRetStart(),eUtilsSearchParser.getRetMax(),eUtilsSearchParser.getQueryKey(),eUtilsSearchParser.getWebEnv());
        socialNetwork.setEutilsSearchResults(results);
        
        //EutilsTimesCitedParser eUtilsTimesCitedParser = new EutilsTimesCitedParser(this.socialNetwork, queryKey, webEnv, retStart, retMax);
        //this.socialNetwork.setPublications(eUtilsTimesCitedParser.getPubList());
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

}
