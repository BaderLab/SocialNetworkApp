package org.baderlab.csapps.socialnetwork.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 *
 * @author
 */
public class RestoreStateFile implements SessionLoadedListener {

    private SocialNetworkAppManager appManager = null;
    private CyNetworkViewManager viewManager = null;

    public RestoreStateFile(SocialNetworkAppManager appManager, CyNetworkViewManager viewManager) {
        super();
        this.appManager = appManager;
        this.viewManager = viewManager;
    }

    // restore app state from a file
    public void handleEvent(SessionLoadedEvent e) {

        if (e.getLoadedSession().getAppFileListMap() == null || e.getLoadedSession().getAppFileListMap().size() ==0){
            return;
        }

        List<File> files = e.getLoadedSession().getAppFileListMap().get("socialnetwork");

        if (files == null || files.size() == 0){
            return;
        }

        try {
            File propFile = files.get(0);

            BufferedReader in = new BufferedReader(new FileReader(propFile));

            String socialNetworks = in.readLine(), networkTypes = in.readLine();

            ArrayList<String> listOfSocialNetworks = new ArrayList<String>(Arrays.asList(socialNetworks.split("\\?")));
            ArrayList<String> listOfTypes = new ArrayList<String>(Arrays.asList(networkTypes.split("\\?")));

            SocialNetwork socialNetwork = null;
            CyNetworkView networkView = null;
            Collection<CyNetworkView> views = null;
            
            int index = -1;

            // Identify the social networks in the loaded session
            for (CyNetwork n : e.getLoadedSession().getNetworks()) {
            	index = listOfSocialNetworks.indexOf(n.toString());
                if (index > -1) {
                    socialNetwork = new SocialNetwork(n.toString(), Category.toCategory(listOfTypes.get(index)));
                    socialNetwork.setCyNetwork(n);
                    views = this.viewManager.getNetworkViews(n);
                    if (views.size() != 0) {
                        networkView = views.iterator().next();
                    }
                    socialNetwork.setNetworkView(networkView);
                    this.appManager.getSocialNetworkMap().put(n.toString(), socialNetwork);
                    this.appManager.getUserPanelRef().addNetworkToNetworkPanel(socialNetwork);
                }
            }

            in.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
