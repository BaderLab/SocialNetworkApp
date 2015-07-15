package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JMenuItem;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskManager;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class UpdateAuthorLocationAction implements CyNodeViewContextMenuFactory, ActionListener {
    
    private CyNode cyNode = null;
    private CyNetwork cyNetwork = null;
    private TaskManager<?, ?> taskManager = null;
    private ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = null;

    public void actionPerformed(ActionEvent e) {
        // Get the institution of this author
        CyTable nodeTable = this.cyNetwork.getDefaultNodeTable();
        CyRow cyRow = nodeTable.getRow(this.cyNode.getSUID());
        String authorName = (String) cyRow.getAllValues().get("Label");
        @SuppressWarnings("unchecked")
        List<String> listOfInstitutions = (List<String>) cyRow.getAllValues().get("Institution");
        if (listOfInstitutions != null && listOfInstitutions.size() > 0) {
            String institution = listOfInstitutions.get(0);       
            CytoscapeUtilities.createDialogBox(String.format("Update location of %s", authorName), institution, 
                    this.cyNetwork, 
                    cyNode.getSUID());
            this.taskManager.execute(this.applyVisualStyleTaskFactoryRef.createTaskIterator());
        } else {
            CytoscapeUtilities.notifyUser(String.format("%s is not assigned to an institution.", authorName));            
        }
    }
    
    public UpdateAuthorLocationAction(TaskManager<?, ?> taskManager, 
            ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef) {
        this.taskManager = taskManager;
        this.applyVisualStyleTaskFactoryRef = applyVisualStyleTaskFactoryRef;
    }

    public CyMenuItem createMenuItem(CyNetworkView netView, View<CyNode> nodeView) {
        this.cyNetwork = netView.getModel();
        this.cyNode = nodeView.getModel();
        JMenuItem addInstitutionMenuItem = new JMenuItem("Update Author's Location");
        addInstitutionMenuItem.addActionListener(this);
        CyMenuItem cyMenuItem = new CyMenuItem(addInstitutionMenuItem, 0);
        return cyMenuItem;
    }

}
