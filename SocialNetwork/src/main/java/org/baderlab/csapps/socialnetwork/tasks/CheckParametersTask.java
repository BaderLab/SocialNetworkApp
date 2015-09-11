package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class CheckParametersTask extends AbstractTask{
	
	 private static final Logger logger = Logger.getLogger(CheckParametersTask .class.getName());
	
	private SocialNetwork socialNetwork = null;
	private SocialNetworkAppManager appManager = null;
	
	

	public CheckParametersTask(SocialNetworkAppManager appManager,SocialNetwork socialNetwork) {
		super();
		this.socialNetwork = socialNetwork;
		this.appManager = appManager;
	}



	@Override
	public void run(TaskMonitor arg0) throws Exception {
		
		ArrayList<Publication> pubList = this.socialNetwork.getPublications();
		
        // Create interaction
        Interaction interaction = new Interaction(pubList, Category.ACADEMIA, this.appManager.getMaxAuthorThreshold());
        if (interaction.getExcludedPublications().size() == pubList.size()) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            String message = "Network couldn't be loaded. Adjust max author threshold";
            logger.log(Level.SEVERE, message);
            CytoscapeUtilities.notifyUser(message);
            return;
        }
        socialNetwork.setExcludedPubs(interaction.getExcludedPublications());
        
        // Create map
        Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
        if (map.size() == 0) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            String message = "Network couldn't be loaded. File is corrupt.";
            logger.log(Level.SEVERE, message);
            CytoscapeUtilities.notifyUser(message);
            return;
        }
        this.appManager.setMap(map);
        this.appManager.getSocialNetworkMap().put(socialNetwork.getNetworkName(), socialNetwork);

		
	}

}
