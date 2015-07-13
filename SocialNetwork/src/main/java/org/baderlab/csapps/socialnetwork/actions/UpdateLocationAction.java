package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
public class UpdateLocationAction implements CyNodeViewContextMenuFactory, ActionListener {
    
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
        
        if (listOfInstitutions == null) {
            CytoscapeUtilities.notifyUser(String.format("%s is not assigned to an institution.", authorName));
        }
        
        String institution = listOfInstitutions.get(0);
        
        JComboBox<String> institutionComboBox = new JComboBox<String>();
        institutionComboBox.addItem(institution);
        JTextField locationTextField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        myPanel.add(new JLabel("Institution"));
        myPanel.add(institutionComboBox);
        myPanel.add(new JLabel("Location"));
        myPanel.add(locationTextField);

        int outcome = JOptionPane.OK_OPTION;
        String location = "N/A";
        while (outcome == JOptionPane.OK_OPTION) {
            // Display dialog box and get user's outcome.
            outcome = JOptionPane.showConfirmDialog(null, myPanel, "Update location of " + authorName, JOptionPane.OK_CANCEL_OPTION);
            if (outcome == JOptionPane.OK_OPTION) {
                institution = ((String)institutionComboBox.getSelectedItem()).trim();
                location = locationTextField.getText().trim();
                if (institution.trim().isEmpty() && location.trim().isEmpty()) {
                    CytoscapeUtilities.notifyUser("Please specify both an institution and a location");
                } else {
                    if (institution.trim().isEmpty()) {
                        CytoscapeUtilities.notifyUser("Please specify an institution");
                    } else if (location.trim().isEmpty()) {
                        CytoscapeUtilities.notifyUser("Please specify a location");
                    } else {
                        if (!CytoscapeUtilities.getLocationSet().contains(location.toLowerCase())) {
                            CytoscapeUtilities.notifyUser("Location does not exist. Please enter a valid location.");
                        } else {
                            institution = institution.toUpperCase();
                            // Format location (in case casing was done improperly)
                            // i.e. 'united states' becomes 'United States'
                            String[] words = location.split("\\s");
                            location = "";
                            for (String word : words) {
                                location += word.replaceAll("^\\w", word.substring(0, 1).toUpperCase()) + " ";
                            }
                            location = location.trim();
                            outcome = JOptionPane.CANCEL_OPTION;
                        }
                    }
                }
            }
        }
        
        CytoscapeUtilities.updateLocationMap(institution, location);
        CytoscapeUtilities.addLocationToNodeTable(this.cyNetwork, cyNode.getSUID(), location);
        this.taskManager.execute(this.applyVisualStyleTaskFactoryRef.createTaskIterator());

    }
    
    public UpdateLocationAction(TaskManager<?, ?> taskManager, 
            ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef) {
        this.taskManager = taskManager;
        this.applyVisualStyleTaskFactoryRef = applyVisualStyleTaskFactoryRef;
    }

    public CyMenuItem createMenuItem(CyNetworkView netView, View<CyNode> nodeView) {
        this.cyNetwork = netView.getModel();
        this.cyNode = nodeView.getModel();
        this.taskManager = taskManager;
        this.applyVisualStyleTaskFactoryRef = applyVisualStyleTaskFactoryRef;
        JMenuItem addInstitutionMenuItem = new JMenuItem("Update author location");
        addInstitutionMenuItem.addActionListener(this);
        CyMenuItem cyMenuItem = new CyMenuItem(addInstitutionMenuItem, 0);
        return cyMenuItem;
    }

}
