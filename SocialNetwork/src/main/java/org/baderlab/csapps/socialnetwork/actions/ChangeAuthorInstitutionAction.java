package org.baderlab.csapps.socialnetwork.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
public class ChangeAuthorInstitutionAction implements CyNodeViewContextMenuFactory, ActionListener {

    private CyNode cyNode = null;
    private CyNetwork cyNetwork = null;
    private TaskManager<?, ?> taskManager = null;
    private ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = null;

    /**
     * 
     * @param TaskManager taskManager
     * @param ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef
     */
    public ChangeAuthorInstitutionAction(TaskManager<?, ?> taskManager, ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef) {
        this.taskManager = taskManager;
        this.applyVisualStyleTaskFactoryRef = applyVisualStyleTaskFactoryRef;
    }

    /**
     * ??
     */
    public void actionPerformed(ActionEvent e) {

        // Get the institution of this author
        CyTable nodeTable = this.cyNetwork.getDefaultNodeTable();
        CyRow cyRow = nodeTable.getRow(this.cyNode.getSUID());
        String authorName = (String) cyRow.getAllValues().get("Label");
        @SuppressWarnings("unchecked")
        List<String> listOfInstitutions = (List<String>) cyRow.getAllValues().get("Institution");
        if (listOfInstitutions != null && listOfInstitutions.size() > 0) {

            String title = String.format("Change institution of %s", authorName); // TODO:

            String listOfInstitArray[] = new String[listOfInstitutions.size()];
            JComboBox<String> institutionComboBox = new JComboBox<String>(listOfInstitutions.toArray(listOfInstitArray));

            JPanel dialogPanel = new JPanel();
            JPanel wrapperPanel = new JPanel();
            wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
            wrapperPanel.add(new JLabel("Institution"));
            wrapperPanel.add(Box.createHorizontalStrut(5));
            wrapperPanel.add(institutionComboBox);
            dialogPanel.add(wrapperPanel, BorderLayout.NORTH);
            int outcome = JOptionPane.OK_OPTION;

            String institution = "N/A";
            while (outcome == JOptionPane.OK_OPTION) {
                // Display dialog box and get user's outcome.
                outcome = JOptionPane.showConfirmDialog(null, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION);
                if (outcome == JOptionPane.OK_OPTION) {
                    institution = ((String) institutionComboBox.getSelectedItem()).trim();
                    if (institution.isEmpty()) {
                        CytoscapeUtilities.notifyUser("Please specify both an institution and a location");
                    } else {
                        institution = institution.toUpperCase();
                        outcome = JOptionPane.CANCEL_OPTION;
                    }
                }
            }

            this.taskManager.execute(this.applyVisualStyleTaskFactoryRef.createTaskIterator());

        } else {
            CytoscapeUtilities.notifyUser(String.format("%s is not assigned to an institution.", authorName));
        }
    }

    /**
     * 
     */
    public CyMenuItem createMenuItem(CyNetworkView netView, View<CyNode> nodeView) {
        this.cyNetwork = netView.getModel();
        this.cyNode = nodeView.getModel();
        JMenuItem addInstitutionMenuItem = new JMenuItem("Change Author's Institution");
        addInstitutionMenuItem.addActionListener(this);
        CyMenuItem cyMenuItem = new CyMenuItem(addInstitutionMenuItem, 0);
        return cyMenuItem;
    }

}
