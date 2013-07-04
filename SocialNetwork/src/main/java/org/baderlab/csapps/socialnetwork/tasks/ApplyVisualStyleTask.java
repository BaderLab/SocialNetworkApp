package main.java.org.baderlab.csapps.socialnetwork.tasks;

import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;

import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ApplyVisualStyleTask extends AbstractTask {
	private VisualMappingManager vmmServiceRef;
	private VisualStyleFactory visualStyleFactoryServiceRef;
	private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
	private VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
	private VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
	private VisualStyle chippedVisualStyle;
	private VisualStyle defaultVisualStyle;
	
	public ApplyVisualStyleTask(VisualStyleFactory visualStyleFactoryServiceRef, VisualMappingManager vmmServiceRef, VisualMappingFunctionFactory passthroughMappingFactoryServiceRef,
			VisualMappingFunctionFactory continuousMappingFactoryServiceRef, VisualMappingFunctionFactory discreteMappingFactoryServiceRef)  {
		this.vmmServiceRef = vmmServiceRef;
		this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
		this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
		this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
		this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
		this.chippedVisualStyle = createChippedVisualStyle();
		this.defaultVisualStyle = visualStyleFactoryServiceRef.createVisualStyle("Default");
	}
	
	/**
	 * Add a label to each node
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	public VisualStyle addNodeLabels(VisualStyle visualStyle) {
		// Set node color map to attribute ???
		PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>)
			passthroughMappingFactoryServiceRef.createVisualMappingFunction("Last Name", 
					                      Integer.class, BasicVisualLexicon.NODE_LABEL);
					
		visualStyle.addVisualMappingFunction(mapping);	
		
		return visualStyle;
	}
	
	/**
	 * Add a label to each edge
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	public VisualStyle addEdgeLabels(VisualStyle visualStyle) {
		// Set node color map to attribute ???
		PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>)
			passthroughMappingFactoryServiceRef.createVisualMappingFunction("Title", 
					                   Integer.class, BasicVisualLexicon.EDGE_LABEL);
					
		visualStyle.addVisualMappingFunction(mapping);	
		
		return visualStyle;
	}
	
	/**
	 * Create chipped visual style
	 * @param null
	 * @return VisualStyle chippedVisualStyle
	 */
	private VisualStyle createChippedVisualStyle() {
		VisualStyle defaultVisualStyle = visualStyleFactoryServiceRef
				                         .createVisualStyle("Chipped");
		addNodeLabels(defaultVisualStyle);
		addEdgeLabels(defaultVisualStyle);
		return defaultVisualStyle;
	}

	/**
	 * Apply selected network view
	 */
	public void run(TaskMonitor arg0) throws Exception {
		switch(Cytoscape.getVisualStyleID()) {
			case Category.DEFAULT:
				vmmServiceRef.setCurrentVisualStyle(defaultVisualStyle);
				break;
			case Category.CHIPPED:
				vmmServiceRef.setCurrentVisualStyle(this.chippedVisualStyle);
				break;
		}
	}

}
