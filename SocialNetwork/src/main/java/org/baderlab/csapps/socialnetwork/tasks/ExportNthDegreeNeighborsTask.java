package org.baderlab.csapps.socialnetwork.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * Task for exporting a csv file containing the nth degree neighbors of every
 * node in an existing Cytoscape network.
 * 
 * @author Victor Kofia
 */
public class ExportNthDegreeNeighborsTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(ExportNthDegreeNeighborsTask.class.getName());

    private SocialNetworkAppManager appManager = null;
    private TaskMonitor taskMonitor = null;
    private int currentSteps = 0;
    private int totalSteps = 0;
    private double progress = 0.0;

    /**
     * Constructor for {@link ExportNthDegreeNeighborsTask}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ExportNthDegreeNeighborsTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    /**
     * Return the ordinal for an integer value
     * 
     * @param int value
     * @return String ordinal
     */
    public static String getOrdinalFor(int value) {
        int hundredRemainder = value % 100;
        int tenRemainder = value % 10;
        if (hundredRemainder - tenRemainder == 10) {
            return "th";
        }
        switch (tenRemainder) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        this.taskMonitor = taskMonitor;
        CyNetwork cyNetwork = this.appManager.getNeighborListNetwork();
        int degree = this.appManager.getNeighborListDegree();
        String message = String.format("Exporting %d%s degree neighbors ...", degree, getOrdinalFor(degree));
        setProgressMonitor(message, cyNetwork.getNodeCount());
        File selectedFolder = this.appManager.getNeighborListFolder();
        String attr = this.appManager.getNeighborListAttribute();
        exportNeighborsToCSV(cyNetwork, degree, selectedFolder, attr);
    }

    /**
     * Export the nth degree neighbors of the specified network to CSV
     * 
     * @param CyNetwork cyNetwork
     * @param int degree
     * @param File selectedFolder
     * @param String attr
     */
    private void exportNeighborsToCSV(CyNetwork cyNetwork, int degree, File selectedFolder, String attr) {
        try {
            String fileName = "/neighbor_list.csv", attrValue = null;
            FileWriter writer = new FileWriter(selectedFolder.getAbsolutePath() + fileName);
            writer.append("Author");
            writer.append(',');
            writer.append("Neighbors");
            writer.append('\n');
            if (!attr.equalsIgnoreCase("N/A")) {
                for (CyNode node : cyNetwork.getNodeList()) {
                    // column is assumed to be of type String
                    attrValue = cyNetwork.getDefaultNodeTable().getRow(node.getSUID()).get(attr, String.class);
                    if (attrValue != null) {
                        writer.append(attrValue);
                        writer.append(',');
                        writeNthDegreeNode(node, cyNetwork, writer, attr, degree);
                        writer.append('\n');
                        updateProgress();
                    }
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser("IOException. Unable to save csv file");
        }
    }

    /**
     * Write the nth degree neighbors of {@code node} into a text file
     * 
     * @param CyNode node
     * @param CyNetwork network
     * @param FileWriter writer
     * @param String attr
     * @param {@code int} depth
     */
    private void writeNthDegreeNode(CyNode node, CyNetwork network, FileWriter writer, String attr, int depth) {
        if (depth == 0) {
            try {
                writer.append("(" + network.getDefaultNodeTable().getRow(node.getSUID()).get(attr, String.class) + ") ");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Exception occurred", e);
                CytoscapeUtilities.notifyUser("IOException. Unable to save csv file");
            }
            return;
        }
        for (CyNode neighbour : network.getNeighborList(node, CyEdge.Type.ANY)) {
            writeNthDegreeNode(neighbour, network, writer, attr, depth - 1);
        }
    }

    /**
     * Set progress monitor
     *
     * @param TaskMonitor taskMonitor
     * @param String taskName
     * @param int totalSteps
     */
    private void setProgressMonitor(String taskName, int totalSteps) {
        this.taskMonitor.setTitle(taskName);
        this.taskMonitor.setProgress(0.0);
        this.currentSteps = 0;
        this.totalSteps = totalSteps;
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

    /**
     * Update progress monitor
     *
     * @param int currentSteps
     */
    private void updateProgress() {
        this.currentSteps += 1;
        this.progress = (double) this.currentSteps / this.totalSteps;
        this.taskMonitor.setStatusMessage("Complete: " + toPercent(this.progress));
        this.taskMonitor.setProgress(this.progress);
    }

}
