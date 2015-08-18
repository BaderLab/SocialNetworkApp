package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
        // TODO: Perhaps putting the below code into a task might be a good idea in the future.
        // Loading large files may cause the user interface to hang
        // --------------------------------------------------------------------------------------------------------------
        File inputFile = this.fileUtil.getFile(this.cySwingApp.getJFrame(), "Data File Selection", FileUtil.LOAD, filters);
        if (inputFile == null) {
            return;
        }
        try {
            logger.log(Level.INFO, String.format("Creating PubMed query from %s", inputFile.getAbsolutePath()));
            Scanner scan = new Scanner(inputFile);
            String author = null;
            ArrayList<String> authorList = new ArrayList<String>();
            while (scan.hasNextLine()) {
                author = scan.nextLine();
                authorList.add(author);
            }
            StringBuffer query = new StringBuffer();
            String author1 = null, author2 = null;
            for (int i = 0; i < authorList.size(); i++) {
                author1 = authorList.get(i);
                for (int j = i + 1; j < authorList.size(); j++) {
                    author2 = authorList.get(j);
                    query.append('(').append(author1).append("[au]").append(" OR ")
                    .append(author2).append("[au]").append(')');
                    if (j < authorList.size() - 1) {
                        query.append(" AND ");
                    }
                }
                if (i < authorList.size() - 2) {
                    query.append(" AND ");
                }
            }            
            File outputFile = this.fileUtil.getFile(this.cySwingApp.getJFrame(), "Save PubMed query", FileUtil.SAVE, filters);
            logger.log(Level.INFO, String.format("Saving PubMed query to %s", outputFile.getAbsolutePath()));
            // if file doesnt exists, then create it
            if (outputFile != null && !outputFile.exists()) {
                outputFile.createNewFile();
            } else {
                return;
            }
            FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(query.toString());
            bw.close();  
          // ------------------------------------------------------------------------------------------------------------
        } catch (FileNotFoundException e1) {
            logger.log(Level.SEVERE, e1.getMessage());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, e1.getMessage());
        }
        
        // TODO: Put code for generating PubMed query into a separate task to prevent hanging of UI
        // for large jobs.
        /*
        this.taskManagerServiceRef.execute(this.createPubMedQueryTaskFactory.createTaskIterator());
         */
        
    }

}
