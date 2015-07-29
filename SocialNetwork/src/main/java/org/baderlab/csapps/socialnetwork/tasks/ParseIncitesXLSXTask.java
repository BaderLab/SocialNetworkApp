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
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Task for parsing an InCites XLSX file.
 * 
 * @author Victor Kofia
 */
public class ParseIncitesXLSXTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(ParseIncitesXLSXTask.class.getName());

    /**
     * A reference to the {@link SocialNetworkAppManager}. Makes it possible to
     * retrieve the name of the network (usually same as the Incites XLSX
     * filename).
     */
    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link ParseIncitesXLSXTask}.
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParseIncitesXLSXTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Loading Network");
        String networkName = this.appManager.getNetworkName();

        SocialNetwork socialNetwork = new SocialNetwork(networkName, Category.INCITES);
        IncitesParser incitesParser = new IncitesParser(this.appManager.getNetworkFile(), taskMonitor);
        if (incitesParser.getIgnoredRows() >= 1) {
            logger.log(Level.WARNING, "Some rows could not be parsed.");
        }
        if (incitesParser.getIdentifiedFacultyList().size() == 0) {
            logger.log(Level.WARNING, "Unable to identify faculty. Please verify that InCites data file is valid");
        }
        ArrayList<Publication> pubList = incitesParser.getPubList();
        if (pubList == null) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            logger.log(Level.SEVERE, "Invalid file. This InCites file is corrupt.");
            return;
        }
        // Add summary attributes
        socialNetwork.setPublications(incitesParser.getPubList());
        socialNetwork.setFaculty(incitesParser.getFacultySet());
        socialNetwork.setUnidentifiedFaculty(incitesParser.getUnidentifiedFacultyList());
        socialNetwork.setUnidentified_faculty(incitesParser.getUnidentifiedFacultyString());

        // Add info to social network map(s)
        socialNetwork.getAttrMap().put(NodeAttribute.DEPARTMENT.toString(), incitesParser.getDepartmentName());

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
        this.appManager.getSocialNetworkMap().put(networkName, socialNetwork);

        TaskIterator taskIterator = new TaskIterator();
        taskIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());
        insertTasksAfterCurrentTask(taskIterator);
    }

}
