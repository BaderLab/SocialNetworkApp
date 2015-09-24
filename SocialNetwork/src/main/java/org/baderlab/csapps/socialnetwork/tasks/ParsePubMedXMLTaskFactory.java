package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsSearchParser;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.EutilsTimesCitedTask;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.PubMedXmlParserTask;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * TaskFactory for {@link ParsePubMedXMLTask}
 * 
 * @author Victor Kofia
 */
public class ParsePubMedXMLTaskFactory extends AbstractTaskFactory {

    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link ParsePubMedXMLFactory}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParsePubMedXMLTaskFactory(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    public TaskIterator createTaskIterator() {
    	
    	TaskIterator pubmedxmlIterator = new TaskIterator();
    	String networkName = this.appManager.getNetworkName();
    	SocialNetwork socialNetwork = new SocialNetwork(networkName, Category.PUBMED);
    	
    	//parse pubmed task
    	PubMedXmlParserTask parsePubmed = new PubMedXmlParserTask(appManager,socialNetwork);
    	pubmedxmlIterator.append(parsePubmed);
    	
    	//query pubmed for additional citations from pubmed (which aren't found in the xml file)
        EutilsSearchParser addCitations = new EutilsSearchParser(null,socialNetwork);
        pubmedxmlIterator.append(addCitations);
   	
    	//QueryPubmedForCitationsTask addcitations = new QueryPubmedForCitationsTask(socialNetwork);
    	//pubmedxmlIterator.append(addcitations);
    	
    	//parse pubmed results and add citation data.
    	EutilsTimesCitedTask parsecitations = new EutilsTimesCitedTask(socialNetwork);
    	pubmedxmlIterator.append(parsecitations);
    	
    	//createNetwork
    	CheckParametersTask createNetwork = new CheckParametersTask(appManager,socialNetwork);
    	pubmedxmlIterator.append(createNetwork);
    	
    	pubmedxmlIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());

    	
        return pubmedxmlIterator;
    }

}
