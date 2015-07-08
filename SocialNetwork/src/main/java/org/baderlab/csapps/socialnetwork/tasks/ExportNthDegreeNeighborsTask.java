package org.baderlab.csapps.socialnetwork.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * Task for exporting a csv file containing the nth degree neighbors of
 * every node in an existing Cytoscape network.
 * 
 * @author Victor Kofia
 */
public class ExportNthDegreeNeighborsTask extends AbstractTask {
    
    private static final Logger logger = Logger.getLogger(ExportNthDegreeNeighborsTask.class.getName());
    
    private SocialNetworkAppManager appManager = null;
    
    /**
     * Constructor for {@link ExportNthDegreeNeighborsTask}
     * 
     * @param SocialNetworkAppManager appManager
     */
    public ExportNthDegreeNeighborsTask(SocialNetworkAppManager appManager) {
        this.appManager = appManager;
    }

    /* (non-Javadoc)
     * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        CyNetwork cyNetwork = this.appManager.getNeighborListNetwork();
        int degree = this.appManager.getNeighborListDegree();
        File selectedFolder = this.appManager.getNeighborListFolder();
        String attr = this.appManager.getNeighborListAttribute();
        exportNeighborsToCSV(cyNetwork, degree, selectedFolder, attr);
    }
    
    /**
     * Export the nth degree neighbors of the specified network to CSV
     * 
     * @param CyNetwork cyNetwork
     * @param int degree
     */
    private void exportNeighborsToCSV(CyNetwork cyNetwork, int degree, File selectedFolder, String attr) {
        try {
            String fileName = "/neighbor_list.csv";
            FileWriter writer = new FileWriter(selectedFolder.getAbsolutePath() + fileName);
            writer.append("Author");
            writer.append(',');
            writer.append("Neighbors");
            writer.append('\n');
            if (!attr.equalsIgnoreCase("N/A")) {
                for (CyNode node : cyNetwork.getNodeList()) {
                    // column is assumed to be of type String
                    writer.append(cyNetwork.getDefaultNodeTable().getRow(node.getSUID()).get(attr, String.class));
                    writer.append(',');
                    writeNthDegreeNode(node, cyNetwork, writer, attr, degree);
                    writer.append('\n');
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
    
}
