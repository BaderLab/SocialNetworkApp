package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;


public class UpdateVisualStyleTaskFactory extends AbstractTaskFactory {
    
    private TaskManager<?, ?> taskManager = null;
    private VisualMappingManager visualMappingManager = null;
    private VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    private SocialNetworkAppManager appManager = null;
    private CyApplicationManager cyApplicationManagerServiceRef = null;

    
    public UpdateVisualStyleTaskFactory(TaskManager<?, ?> taskManager, SocialNetworkAppManager appManager,
            VisualMappingManager visualMappingManager, VisualMappingFunctionFactory discrete, 
            CyApplicationManager cyApplicationManagerServiceRef) {
        this.taskManager = taskManager;
        this.appManager = appManager;
        this.visualMappingManager = visualMappingManager;
        this.discreteMappingFactoryServiceRef = discrete;
        this.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new UpdateVisualStyleTask(taskManager, appManager, visualMappingManager, discreteMappingFactoryServiceRef,
                cyApplicationManagerServiceRef));
    }

}
