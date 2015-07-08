package org.baderlab.csapps.socialnetwork.listeners;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.table.DefaultTableModel;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

/**
 * Used for updating the network summary panel when a social network's name is
 * modified.
 * 
 * @author Victor Kofia
 */
public class SocialNetworkNameChangedListener implements RowsSetListener {

    private SocialNetworkAppManager appManager = null;
    private CyNetworkManager cyNetworkManagerServiceRef = null;
    private UserPanel userPanel = null;

    public SocialNetworkNameChangedListener(SocialNetworkAppManager appManager, CyNetworkManager cyNetworkManagerServiceRef) {
        super();
        this.appManager = appManager;
        this.userPanel = appManager.getUserPanelRef();
        this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
    }

    /**
     * Get the row in table that contains the specified name. Assumes that each
     * row stores a unique name.
     *
     * @param {@link DefaultTableModel} model
     * @param String name
     * @return int row
     */
    private int getRow(DefaultTableModel model, String name) {
        for (int row = 0; row < model.getRowCount(); row++) {
            if (((String) model.getValueAt(row, 0)).equalsIgnoreCase(name)) {
                return row;
            }
        }
        return -1;
    }

    /**
     * Update network summary panel after a social network is renamed
     * 
     * @param RowsSetEvent rowsSetEvent
     */
    public void handleEvent(RowsSetEvent rowsSetEvent) {
        if (!rowsSetEvent.containsColumn(CyNetwork.NAME)) {
            return;
        }
        // Retrieve new network name
        CyRow row = rowsSetEvent.getSource().getAllRows().get(0); // only 1 row
        String updatedName = (String) row.getAllValues().get(CyNetwork.NAME);
        Long s = null, suid = (Long) row.getAllValues().get("SUID");

        SocialNetwork network = null;
        CyNetwork cyNetwork = null;
        Iterator<Entry<String, SocialNetwork>> it = this.appManager.getSocialNetworkMap().entrySet().iterator();
        Map.Entry<String, SocialNetwork> pair = null;
        while (it.hasNext()) {
            pair = (Map.Entry<String, SocialNetwork>) it.next();
            network = (SocialNetwork) pair.getValue();
            cyNetwork = network.getCyNetwork();
            if (cyNetwork != null) {
                s = cyNetwork.getSUID();
                if (suid.equals(s)) {
                    String oldName = network.getNetworkName();
                    // Update SocialNetwork map
                    network.setNetworkName(updatedName);
                    this.appManager.getSocialNetworkMap().remove(pair.getKey());
                    this.appManager.getSocialNetworkMap().put(updatedName, network);
                    // Update network summary panel
                    DefaultTableModel model = (DefaultTableModel) this.userPanel.getNetworkTableRef().getModel();
                    int rowIndex = getRow(model, oldName);
                    if (rowIndex > -1) {
                        model.setValueAt(updatedName, rowIndex, 0);
                    }
                }
            }
        }
    }

}
