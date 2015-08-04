package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
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

    
    public UpdateVisualStyleTaskFactory(TaskManager<?, ?> taskManager, SocialNetworkAppManager appManager,
            VisualMappingManager visualMappingManager, VisualMappingFunctionFactory discrete) {
        this.taskManager = taskManager;
        this.appManager = appManager;
        this.visualMappingManager = visualMappingManager;
        this.discreteMappingFactoryServiceRef = discrete;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new UpdateVisualStyleTask(taskManager, appManager, visualMappingManager, discreteMappingFactoryServiceRef));
    }

}
