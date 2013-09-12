package org.baderlab.csapps.socialnetwork.tasks;


import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class ApplyVisualStyleTaskFactory extends AbstractTaskFactory {
	private VisualMappingManager vmmServiceRef;
	private VisualStyleFactory visualStyleFactoryServiceRef;
	private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
	private VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
	private VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
	private SocialNetworkAppManager appManager;
	
	public ApplyVisualStyleTaskFactory(VisualStyleFactory visualStyleFactoryServiceRef, 
			                           VisualMappingManager vmmServiceRef, 
			                           VisualMappingFunctionFactory passthroughMappingFactoryServiceRef,
			                           VisualMappingFunctionFactory continuousMappingFactoryServiceRef, 
			                           VisualMappingFunctionFactory discreteMappingFactoryServiceRef, SocialNetworkAppManager appManager)  {
		this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
		this.vmmServiceRef = vmmServiceRef;
		this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
		this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
		this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
		this.appManager = appManager;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ApplyVisualStyleTask(visualStyleFactoryServiceRef, 
				                                         vmmServiceRef, 
				                                         passthroughMappingFactoryServiceRef, 
				                                         continuousMappingFactoryServiceRef, 
				                                         discreteMappingFactoryServiceRef,appManager));
	}

}
