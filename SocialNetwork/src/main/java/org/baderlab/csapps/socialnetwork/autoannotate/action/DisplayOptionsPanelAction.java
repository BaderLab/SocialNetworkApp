package org.baderlab.csapps.socialnetwork.autoannotate.action;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.autoannotate.view.DisplayOptionsPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.annotations.AnnotationManager;


public class DisplayOptionsPanelAction extends AbstractCyAction {
    private static final long serialVersionUID = 3764130543697594367L;


    // track initialization of panel (so only one panel is ever created)
    private boolean initialized = false;
    private final CytoPanel cytoPanelEast;
    private CyApplicationManager applicationManager;
    private CySwingApplication application;
    private DisplayOptionsPanel displayOptionsPanel;
    // used to register the panel
    private CyServiceRegistrar registrar;

    public DisplayOptionsPanelAction(Map<String,String> configProps, CyApplicationManager applicationManager,
            CyNetworkViewManager networkViewManager, CySwingApplication application, AnnotationManager annotationManager,
            CyServiceRegistrar registrar){

        super( configProps,  applicationManager,  networkViewManager);

        this.cytoPanelEast = application.getCytoPanel(CytoPanelName.EAST);
        this.applicationManager = applicationManager;
        this.application = application;
        this.registrar = registrar;
    }

    public void actionPerformed(ActionEvent event) {
        if (this.applicationManager.getCurrentNetworkView() != null) {
            // Only registers one panel
            if(!this.initialized) {
                this.displayOptionsPanel = new DisplayOptionsPanel();
                this.registrar.registerService(this.displayOptionsPanel, CytoPanelComponent.class, new Properties());
                AutoAnnotationManager.getInstance().setDisplayOptionsPanel(this.displayOptionsPanel);
                this.initialized = true;
            }
            // If the state of the cytoPanelWest is HIDE, show it
            if (this.cytoPanelEast.getState() == CytoPanelState.HIDE) {
                this.cytoPanelEast.setState(CytoPanelState.DOCK);
            }

            // Select my panel
            int inputIndex = this.cytoPanelEast.indexOfComponent(this.displayOptionsPanel);
            if (inputIndex != -1) {
                this.cytoPanelEast.setSelectedIndex(inputIndex);
            }
        } else {
            // Don't create the panel if it can't be used
            JOptionPane.showMessageDialog(this.application.getJFrame(), "Please load an Enrichment Map.");
        }
    }
}
