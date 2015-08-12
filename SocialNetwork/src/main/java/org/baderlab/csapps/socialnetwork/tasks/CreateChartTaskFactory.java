package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkChartListener;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifierFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public class CreateChartTaskFactory extends AbstractTaskFactory {
    
    private CyApplicationManager cyApplicationManagerServiceRef = null;
    private SocialNetworkChartListener customChartManager = null;
    private VisualMappingManager vmmServiceRef = null;
    private CyColumnIdentifierFactory columnIdFactory = null;
    private TaskManager<?, ?> taskManager = null;
    private HideAuthorsTaskFactory hideAuthorsTaskFactory = null;

    public CreateChartTaskFactory(CyApplicationManager cyApplicationManagerServiceRef, SocialNetworkChartListener customChartManager,
            VisualMappingManager vmmServiceRef, CyColumnIdentifierFactory columnIdFactory, TaskManager<?, ?> taskManager,
            HideAuthorsTaskFactory hideAuthorsTaskFactory) {
        this.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
        this.customChartManager = customChartManager;
        this.vmmServiceRef = vmmServiceRef;
        this.columnIdFactory = columnIdFactory;
        this.taskManager = taskManager;
        this.hideAuthorsTaskFactory = hideAuthorsTaskFactory;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new CreateChartTask(this.cyApplicationManagerServiceRef, this.customChartManager, 
                this.vmmServiceRef, this.columnIdFactory, this.taskManager, this.hideAuthorsTaskFactory));
    }

}
