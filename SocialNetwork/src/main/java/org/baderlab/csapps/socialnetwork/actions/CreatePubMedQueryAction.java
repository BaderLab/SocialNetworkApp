package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;


public class CreatePubMedQueryAction extends AbstractCyAction {
    
    private static final Logger logger = Logger.getLogger(CreatePubMedQueryAction.class.getName());
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /*
    private TaskManager<?, ?> taskManagerServiceRef = null;
    private CreatePubMedQueryTaskFactory createPubMedQueryTaskFactory = null;
    */
    private FileUtil fileUtil = null;
    private CySwingApplication cySwingApp = null;
    
    public CreatePubMedQueryAction(Map<String, String> configProps, CyApplicationManager applicationManager, CyNetworkViewManager networkViewManager,
            TaskManager<?, ?> taskManagerServiceRef, FileUtil fileUtil, CySwingApplication cySwingApp) {
        super(configProps, applicationManager, networkViewManager);
        /*
        this.createPubMedQueryTaskFactory = createChartTaskFactory;
        this.taskManagerServiceRef = taskManagerServiceRef;
        */
        this.fileUtil = fileUtil;
        this.cySwingApp = cySwingApp;
        putValue(NAME, "Create PubMed Query");
    }

    public void actionPerformed(ActionEvent e) {
        FileChooserFilter filter1 = new FileChooserFilter("text file", "txt");
        FileChooserFilter filter2 = new FileChooserFilter("text file", "TXT");
        HashSet<FileChooserFilter> filters = new HashSet<FileChooserFilter>();
        filters.add(filter1);
        filters.add(filter2);
        File textFile = this.fileUtil.getFile(this.cySwingApp.getJFrame(), "Data File Selection",FileUtil.LOAD, filters);
        try {
            Scanner scan = new Scanner(textFile);
            String author = null;
            ArrayList<String> authorList = new ArrayList<String>();
            while (scan.hasNextLine()) {
                author = scan.nextLine();
                authorList.add(author);
            }
            StringBuffer query = new StringBuffer();
            // TODO:
        } catch (FileNotFoundException e1) {
            logger.log(Level.SEVERE, e1.getMessage());
        }
        
        
        // TODO: Put code for generating PubMed query into a separate task to prevent hanging of UI
        // for large jobs.
        /*
        this.taskManagerServiceRef.execute(this.createPubMedQueryTaskFactory.createTaskIterator());
        */
        
    }

}
