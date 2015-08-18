package org.baderlab.csapps.socialnetwork.tasks;

import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkChartListener;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifierFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CreateChartTaskFactory extends AbstractTaskFactory {
    
    private CyApplicationManager cyApplicationManagerServiceRef = null;
    private SocialNetworkChartListener customChartManager = null;
    private VisualMappingManager vmmServiceRef = null;
    private CyColumnIdentifierFactory columnIdFactory = null;
    protected VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    protected VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    protected VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
    private VisualStyleFactory visualStyleFactoryServiceRef = null;
    private SocialNetworkAppManager socialNetworkAppManager = null;

    public CreateChartTaskFactory(CyApplicationManager cyApplicationManagerServiceRef, SocialNetworkChartListener customChartManager,
            VisualMappingManager vmmServiceRef, CyColumnIdentifierFactory columnIdFactory, SocialNetworkAppManager socialNetworkAppManager,
            VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef, VisualStyleFactory visualStyleFactoryServiceRef) {
        this.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
        this.customChartManager = customChartManager;
        this.vmmServiceRef = vmmServiceRef;
        this.columnIdFactory = columnIdFactory;
        this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
        this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
        this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
        this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
        this.socialNetworkAppManager = socialNetworkAppManager;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new CreateChartTask(this.cyApplicationManagerServiceRef, this.customChartManager, 
                this.vmmServiceRef, this.columnIdFactory, this.socialNetworkAppManager, this.passthroughMappingFactoryServiceRef,
                this.continuousMappingFactoryServiceRef, this.discreteMappingFactoryServiceRef, this.visualStyleFactoryServiceRef));
    }

}
