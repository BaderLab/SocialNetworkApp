package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.text.DecimalFormat;
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
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.baderlab.csapps.socialnetwork.model.visualstyles.IncitesVisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class ParseNetworkFileTask extends AbstractTask {
    
    private static final Logger logger = Logger.getLogger(ParseNetworkFileTask.class.getName());
    
    private SocialNetworkAppManager appManager = null;
    private int currentSteps = 0;
    private int totalSteps = 0;
    private double progress = 0.0;
    
    /**
     * ??
     * 
     * @param SocialNetworkAppManager appManager
     */
    // TODO: Write constructor description
    public ParseNetworkFileTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
    	taskMonitor.setTitle("Loading Network");
        String networkName = this.appManager.getNetworkName();
        
        SocialNetwork socialNetwork = null;
        ArrayList<Publication> pubList = null;
        
        switch (this.appManager.getAnalysis_type()) {
            case SocialNetworkAppManager.ANALYSISTYPE_INCITES:
                socialNetwork = new SocialNetwork(networkName, Category.INCITES);
                IncitesParser incitesParser = new IncitesParser(this.appManager.getNetworkFile(), taskMonitor);
                if (incitesParser.getIgnoredRows() >= 1) {
                    logger.log(Level.WARNING, "Some rows could not be parsed.");
                }
                if (incitesParser.getIdentifiedFacultyList().size() == 0) {
                    logger.log(Level.WARNING, "Unable to identify faculty. Please verify that InCites data file is valid");
                }
                pubList = incitesParser.getPubList();
                if (pubList == null) {
                    this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    logger.log(Level.WARNING, "Invalid file. This InCites file is corrupt.");
                    return;
                }
                // Add summary attributes
                socialNetwork.setPublications(incitesParser.getPubList());
                socialNetwork.setFaculty(incitesParser.getFacultySet());
                socialNetwork.setUnidentifiedFaculty(incitesParser.getUnidentifiedFacultyList());
                socialNetwork.setUnidentified_faculty(incitesParser.getUnidentifiedFacultyString());

                // Add info to social network map(s)
                socialNetwork.getAttrMap().put(IncitesVisualStyle.nodeattr_dept, incitesParser.getDepartmentName());
                break;
            case SocialNetworkAppManager.ANALYSISTYPE_PUBMED:
                socialNetwork = new SocialNetwork(networkName, Category.PUBMED);
                PubMed pubmed = new PubMed(this.appManager.getNetworkFile(), taskMonitor);
                pubList = pubmed.getPubList();
                socialNetwork.setPublications(pubmed.getPubList());
                if (pubList == null) {
                    this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
                break;
            case SocialNetworkAppManager.ANALYSISTYPE_SCOPUS:
                socialNetwork = new SocialNetwork(networkName, Category.SCOPUS);
                Scopus scopus = new Scopus(this.appManager.getNetworkFile(), taskMonitor);
                pubList = scopus.getPubList();
                socialNetwork.setPublications(scopus.getPubList());
                if (pubList == null) {
                    this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
                break;
        }

        // Create interaction
        Interaction interaction = new Interaction(pubList, Category.ACADEMIA, this.appManager.getMaxAuthorThreshold());
        if (interaction.getExcludedPublications().size() == pubList.size()) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network couldn't be loaded. Adjust max author threshold");
            return;
        }
        socialNetwork.setExcludedPubs(interaction.getExcludedPublications());
        // Create map
        Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
        if (map.size() == 0) {
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network couldn't be loaded. File is corrupt.");
            return;
        }
        this.appManager.setMap(map);
        this.appManager.getSocialNetworkMap().put(networkName, socialNetwork);
        
        TaskIterator taskIterator = new TaskIterator();
        taskIterator.append(this.appManager.getNetworkTaskFactoryRef().createTaskIterator());
        insertTasksAfterCurrentTask(taskIterator);
    }
    
    /**
     * Set progress monitor
     *
     * @param TaskMonitor taskMonitor
     * @param String taskName
     * @param int totalSteps
     */
    private void setProgressMonitor(TaskMonitor taskMonitor, String taskName, int totalSteps) {
        taskMonitor.setTitle(taskName);
        taskMonitor.setProgress(0.0);
        this.currentSteps = 0;
        this.totalSteps = totalSteps;
    }

    /**
     * Update progress monitor
     *
     * @param int currentSteps
     */
    private void updateProgress(TaskMonitor taskMonitor) {
        this.currentSteps += 1;
        this.progress = (double) this.currentSteps / this.totalSteps;
        taskMonitor.setStatusMessage("Complete: " + toPercent(this.progress));
        taskMonitor.setProgress(this.progress);
    }
    
    /**
     * Return progress as a percentage
     *
     * @param Double progress
     * @return String percentage
     */
    private String toPercent(double progress) {
        progress = progress * 100;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(progress) + "%";
    }

}
