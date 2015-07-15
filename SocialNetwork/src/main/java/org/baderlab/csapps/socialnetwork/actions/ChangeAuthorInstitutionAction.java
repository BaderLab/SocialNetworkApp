package org.baderlab.csapps.socialnetwork.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
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
    // TODO: Write description
    public void actionPerformed(ActionEvent e) {
        Long SUID = this.cyNode.getSUID();
        // Get the institution of this author
        CyTable nodeTable = this.cyNetwork.getDefaultNodeTable();
        String authorName = (String) CytoscapeUtilities.getNodeAttribute(nodeTable, SUID, "Label");
        @SuppressWarnings("unchecked")
        List<String> listOfInstitutions = (List<String>) CytoscapeUtilities.getNodeAttribute(nodeTable, SUID, "Institution");
        if (listOfInstitutions != null && listOfInstitutions.size() > 0) {
            String title = String.format("Change main institution of %s", authorName);
            String listOfInstitArray[] = new String[listOfInstitutions.size()];
            JComboBox<String> institutionComboBox = new JComboBox<String>(listOfInstitutions.toArray(listOfInstitArray));
            JPanel dialogPanel = new JPanel();
            JPanel wrapperPanel = new JPanel();
            wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
            wrapperPanel.add(new JLabel("Select Main Institution"));
            wrapperPanel.add(Box.createHorizontalStrut(5));
            wrapperPanel.add(institutionComboBox);
            dialogPanel.add(wrapperPanel, BorderLayout.NORTH);
            int outcome = JOptionPane.OK_OPTION;
            String mainInstitution = "N/A";
            while (outcome == JOptionPane.OK_OPTION) {
                // Display dialog box and get user's outcome.
                outcome = JOptionPane.showConfirmDialog(null, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION);
                if (outcome == JOptionPane.OK_OPTION) {
                    mainInstitution = ((String) institutionComboBox.getSelectedItem()).trim();
                    if (mainInstitution.isEmpty()) {
                        CytoscapeUtilities.notifyUser("Please specify both an institution and a location");
                    } else {
                        if (!mainInstitution.equalsIgnoreCase("n/a")) {
                            mainInstitution = mainInstitution.toUpperCase();
                            CytoscapeUtilities.setNodeAttribute(nodeTable, SUID, "Main Institution", mainInstitution);
                            Map<String, String> locationMap = CytoscapeUtilities.getLocationMap();
                            String location = locationMap.get(mainInstitution);
                            if (location == null) {
                                location = "Other";
                                locationMap.put(mainInstitution, location);
                                CytoscapeUtilities.saveLocationMap(locationMap);
                            }
                            CytoscapeUtilities.setNodeAttribute(nodeTable, SUID, "Location", location);                                
                            outcome = JOptionPane.CANCEL_OPTION;                            
                        }
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
        JMenuItem addInstitutionMenuItem = new JMenuItem("Change Author's Main Institution");
        addInstitutionMenuItem.addActionListener(this);
        CyMenuItem cyMenuItem = new CyMenuItem(addInstitutionMenuItem, 0);
        return cyMenuItem;
    }

}