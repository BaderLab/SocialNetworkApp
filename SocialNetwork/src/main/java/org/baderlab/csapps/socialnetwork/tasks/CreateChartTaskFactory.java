package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkChartListener;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifierFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CreateChartTaskFactory extends AbstractTaskFactory {
    
    private CyApplicationManager cyApplicationManagerServiceRef = null;
    private SocialNetworkChartListener customChartManager = null;
    private VisualMappingManager vmmServiceRef = null;
    private CyColumnIdentifierFactory columnIdFactory = null;

    public CreateChartTaskFactory(CyApplicationManager cyApplicationManagerServiceRef, SocialNetworkChartListener customChartManager,
            VisualMappingManager vmmServiceRef, CyColumnIdentifierFactory columnIdFactory) {
        this.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
        this.customChartManager = customChartManager;
        this.vmmServiceRef = vmmServiceRef;
        this.columnIdFactory = columnIdFactory;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new CreateChartTask(this.cyApplicationManagerServiceRef, this.customChartManager, 
                this.vmmServiceRef, this.columnIdFactory));
    }

}
