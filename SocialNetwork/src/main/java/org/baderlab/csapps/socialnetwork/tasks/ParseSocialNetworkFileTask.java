package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

/**
 * Task for parsing a social network file.
 * 
 * @author Victor Kofia
 */
public class ParseSocialNetworkFileTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(ParseSocialNetworkFileTask.class.getName());
    
    /**
     * A reference to the cytoscape task manager. As the name suggests the task
     * manager is used for executing tasks.
     */
    private TaskManager<?, ?> taskManagerServiceRef = null;

    /**
     * A reference to the {@link SocialNetworkAppManager}.
     */
    private SocialNetworkAppManager appManager = null;
    
    /**
     * A reference to the task factory for <i>ParseIncitesXLSXTask</i>
     */
    private ParseIncitesXLSXTaskFactory parseIncitesXLSXTaskFactoryRef = null;
    
    /**
     * A reference to the task factory for <i>ParsePubMedXMLTask</i>
     */
    private ParsePubMedXMLTaskFactory parsePubMedXMLTaskFactoryRef = null;
    
    /**
     * A reference to the task factory for <i>ParseScopusCSVTask</i>
     */
    private ParseScopusCSVTaskFactory parseScopusCSVTaskFactoryRef = null;

    /**
     * Constructor for {@link ParseSocialNetworkFileTask}.
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ParseSocialNetworkFileTask(SocialNetworkAppManager appManager, TaskManager<?, ?> taskManagerServiceRef,
            ParseIncitesXLSXTaskFactory parseIncitesXLSXTaskFactoryRef,
            ParsePubMedXMLTaskFactory parsePubMedXMLTaskFactoryRef, ParseScopusCSVTaskFactory parseScopusCSVTaskFactoryRef) {
        this.appManager = appManager;
        this.taskManagerServiceRef = taskManagerServiceRef;
        this.parseIncitesXLSXTaskFactoryRef = parseIncitesXLSXTaskFactoryRef;
        this.parsePubMedXMLTaskFactoryRef = parsePubMedXMLTaskFactoryRef;
        this.parseScopusCSVTaskFactoryRef = parseScopusCSVTaskFactoryRef;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Loading Network ...");
        File networkFile = this.appManager.getNetworkFile();
        String networkName = this.appManager.getNetworkName();
        if (!this.appManager.isNameValid(networkName)) {
            String message = "Network " + networkName + " already exists in Cytoscape." + " Please enter a new name.";
            logger.log(Level.WARNING, message);
            CytoscapeUtilities.notifyUser(message);
            return;
        }
        // Change mouse cursor
        this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // Initialize variables
        String extension = FilenameUtils.getExtension(networkFile.getPath()).trim();
        // Create network out of InCites data
        switch(this.appManager.getAnalysis_type()) {
            case SocialNetworkAppManager.ANALYSISTYPE_INCITES:
            	String message1 = "Incites interface has changed significantly since the release of this app.  Use at your discretion.";
            	CytoscapeUtilities.notifyUser(message1);
            	// Load data from text file
                if (!extension.equalsIgnoreCase("xlsx")) {
                    this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    String message = "Invalid file. InCites data files either have to be excel spreadsheets or text files.";
                    logger.log(Level.WARNING, message);
                    CytoscapeUtilities.notifyUser(message);
                    return;
                }
                this.visualizeIncitesXLSX();
                break;
            case SocialNetworkAppManager.ANALYSISTYPE_PUBMED:
            	String message2 = "Pubmed interface no longer allows direct download of XML files but we can only read Pubmed XML data.";
            	CytoscapeUtilities.notifyUser(message2);
                if (!extension.equalsIgnoreCase("xml")) {
                    this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    String message = "Invalid file. PubMed data files have to be in xml format.";
                    logger.log(Level.WARNING, message);
                    CytoscapeUtilities.notifyUser(message);
                    return;
                }
                this.visualizePubMedXML();
                break;
            case SocialNetworkAppManager.ANALYSISTYPE_SCOPUS:
                if (!extension.equalsIgnoreCase("csv")) {
                    this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    String message = "Invalid file. Scopus data files have to be csv spreadsheets";
                    logger.log(Level.WARNING, message);
                    CytoscapeUtilities.notifyUser(message);
                    return;
                }
                this.visualizeScopusCSV();
                break;
            default:
                break;
        }
    }
    
    /**
     * Get Cytoscape task manager
     *
     * @return TaskManager taskManager
     */
    public TaskManager<?, ?> getTaskManager() {
        return this.taskManagerServiceRef;
    }
    
    /**
     * Get the task factory for <i>ParseIncitesXLSXTask</i>
     * 
     * @return ParseIncitesXLSXTaskFactory parseIncitesXLSXTaskFactoryRef
     */
    public ParseIncitesXLSXTaskFactory getParseIncitesXLSXTaskFactoryRef() {
        return parseIncitesXLSXTaskFactoryRef;
    }

    /**
     * Get the task factory for <i>ParsePubMedXMLTask</i>
     * 
     * @return ParsePubMedXMLTaskFactory parsePubMedXMLTaskFactoryRef
     */
    public ParsePubMedXMLTaskFactory getParsePubMedXMLTaskFactoryRef() {
        return this.parsePubMedXMLTaskFactoryRef;
    }

    /**
     * Get the task factory for <i>ParseScopusTask</i>
     * 
     * @return ParseScopusCSVTaskFactory parseScopusCSVTaskFactoryRef
     */
    public ParseScopusCSVTaskFactory getParseScopusCSVTaskFactoryRef() {
        return parseScopusCSVTaskFactoryRef;
    }
    
    /**
     * Visualize a Incites XLSX file. Should not be executed directly.
     */
    private void visualizeIncitesXLSX() {
        this.getTaskManager().execute(this.getParseIncitesXLSXTaskFactoryRef().createTaskIterator());
    }

    /**
     * Visualize a PubMed XML file. Should not be executed directly.
     */
    private void visualizePubMedXML() {
        this.getTaskManager().execute(this.getParsePubMedXMLTaskFactoryRef().createTaskIterator());
    }

    /**
     * Visualize a Scopus CSV file. Should not be executed directly.
     */
    private void visualizeScopusCSV() {
        this.getTaskManager().execute(this.getParseScopusCSVTaskFactoryRef().createTaskIterator());
    }

}
