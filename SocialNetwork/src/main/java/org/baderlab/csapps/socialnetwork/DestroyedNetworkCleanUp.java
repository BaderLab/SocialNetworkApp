package main.java.org.baderlab.csapps.socialnetwork;

import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

/**
 * Clean-up activities done after network has been destroyed
 * @author Victor Kofia
 */
public class DestroyedNetworkCleanUp implements NetworkAboutToBeDestroyedListener {
	
	public DestroyedNetworkCleanUp() {
		
	}

	public void handleEvent(NetworkAboutToBeDestroyedEvent event) {
		
//		CyNetwork networkAboutToBeDestroyed = event.getNetwork();
//		Long networkSUID = networkAboutToBeDestroyed.getSUID();
//		
//		if (Cytoscape.getNetworkMap() != null) {
//			Network[] networksAlreadyLoaded = (Network[]) Cytoscape.getNetworkMap().values().toArray();
//
//			boolean isPresentInNetworkTable = false;
//
//			for (Network network : networksAlreadyLoaded) {
//				if (networkSUID == network.getNetworkRef().getSUID()) {
//					isPresentInNetworkTable = true;
//					break;
//				}
//			}
//
//			if (isPresentInNetworkTable) {
//				DefaultTableModel model = (DefaultTableModel) UserPanel.getNetworkTableRef().getModel();
//				model.removeRow(UserPanel.getSelectedRowInNetworkTable());
//				UserPanel.setSelectedNetwork(null);
//				UserPanel.setSelectedRowInNetworkTable(-1);
//				UserPanel.getNetworkTableRef().validate();
//				UserPanel.getNetworkTableRef().repaint();
//
//				TitledBorder visualStylePanelBorder = (TitledBorder) UserPanel.getVisualStylePanel().getBorder();
//				visualStylePanelBorder.setTitle("Visual Styles");
//				UserPanel.getVisualStylePanel().revalidate();
//				UserPanel.getVisualStylePanel().repaint();
//
//				UserPanel.swapVisualStyleSelector(UserPanel.getSelectedCategory());
//			}
//
//		}

	}

}
