package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class UpdateLocationContextMenuFactory implements CyNodeViewContextMenuFactory, ActionListener {

    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "Feature is still a work in progress ...");
    }

    public CyMenuItem createMenuItem(CyNetworkView netView, View<CyNode> nodeView) {
        JMenuItem addInstitutionMenuItem = new JMenuItem("Update author location");
        addInstitutionMenuItem.addActionListener(this);
        CyMenuItem cyMenuItem = new CyMenuItem(addInstitutionMenuItem, 0);
        return cyMenuItem;
    }

}
