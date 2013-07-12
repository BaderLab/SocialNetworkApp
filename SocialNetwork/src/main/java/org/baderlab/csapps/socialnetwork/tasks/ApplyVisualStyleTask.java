package main.java.org.baderlab.csapps.socialnetwork.tasks;

import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;

import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
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
	
	public ApplyVisualStyleTask(VisualStyleFactory visualStyleFactoryServiceRef, 
			                    VisualMappingManager vmmServiceRef, 
			                    VisualMappingFunctionFactory passthroughMappingFactoryServiceRef,
			                    VisualMappingFunctionFactory continuousMappingFactoryServiceRef, 
			                    VisualMappingFunctionFactory discreteMappingFactoryServiceRef)  {
		this.vmmServiceRef = vmmServiceRef;
		this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
		this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
		this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
		this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
	}
	
	/**
	 * Add a label to each node
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle addNodeLabels(VisualStyle visualStyle) {
//		System.out.println("This is the social network " + Cytoscape.getCurrentlySelectedSocialNetwork());
//		System.out.println("This is the visual style map " + Cytoscape.getCurrentlySelectedSocialNetwork()
//                                             .getVisualStyleMap());
//		System.out.println("This is the array I just got from the map " + Cytoscape.getCurrentlySelectedSocialNetwork()
//                .getVisualStyleMap()
//                .get(BasicVisualLexicon.NODE_SIZE));

		String attribute = (String) Cytoscape.getCurrentlySelectedSocialNetwork()
				                             .getVisualStyleMap()
				                             .get(BasicVisualLexicon.NODE_LABEL)[0];
		PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>)
			passthroughMappingFactoryServiceRef.createVisualMappingFunction(attribute, 
					                      Integer.class, BasicVisualLexicon.NODE_LABEL);
					
		visualStyle.addVisualMappingFunction(mapping);	
		
		return visualStyle;
	}
	
	/**
	 * Add a label to each edge
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle addEdgeLabels(VisualStyle visualStyle) {
		String attribute = (String) Cytoscape.getCurrentlySelectedSocialNetwork()
                                             .getVisualStyleMap()
                                             .get(BasicVisualLexicon.EDGE_LABEL)[0];
		PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>)
			passthroughMappingFactoryServiceRef.createVisualMappingFunction(attribute, 
					                   Integer.class, BasicVisualLexicon.EDGE_LABEL);
		visualStyle.addVisualMappingFunction(mapping);	
		return visualStyle;
	}
	
	/**
	 * Modify edge width
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle modifyEdgeWidth(VisualStyle visualStyle) {
		Object[] attributes = Cytoscape.getCurrentlySelectedSocialNetwork()
                .getVisualStyleMap()
                .get(BasicVisualLexicon.EDGE_WIDTH);
		String attributeName = (String) attributes[0];
		ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) 
				                                this.continuousMappingFactoryServiceRef
		.createVisualMappingFunction(attributeName, Integer.class, BasicVisualLexicon.EDGE_WIDTH);
		
		// Adjust handle position
		int min = 1;
        int max = 10;

        // BRVs are used to set limits on edge width
        BoundaryRangeValues bv0 = new BoundaryRangeValues(1, 1, 1);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(10, 10, 10);
        
        mapping.addPoint(min, bv0);
        mapping.addPoint(max, bv1);
        
		visualStyle.addVisualMappingFunction(mapping);
		return visualStyle;
	}
	
	/**
	 * Modify node size
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle modifyNodeSize(VisualStyle visualStyle) {
		Object[] attributes = Cytoscape.getCurrentlySelectedSocialNetwork()
                .getVisualStyleMap()
                .get(BasicVisualLexicon.NODE_SIZE);
		String attributeName = (String) attributes[0];
		ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) 
				                                this.continuousMappingFactoryServiceRef
		.createVisualMappingFunction(attributeName, Integer.class, BasicVisualLexicon.NODE_SIZE);
        
		// Adjust handle position
		int min = (Integer) attributes[1];
        int max = (Integer) attributes[2];

        // BRVs are used to set limits on node size
        BoundaryRangeValues bv0 = new BoundaryRangeValues(10, 10, 10);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(50, 50, 50);
        
        mapping.addPoint(min, bv0);
        mapping.addPoint(max, bv1);
        
		visualStyle.addVisualMappingFunction(mapping);
		return visualStyle;
	}
	
	/**
	 * Modify edge opacity
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle modifyEdgeOpacity(VisualStyle visualStyle) {
		String attribute = (String) Cytoscape.getCurrentlySelectedSocialNetwork()
                .getVisualStyleMap()
                .get(BasicVisualLexicon.EDGE_TRANSPARENCY)[0];
		ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) 
				                        this.continuousMappingFactoryServiceRef
		.createVisualMappingFunction(attribute, Integer.class, 
				                     BasicVisualLexicon.EDGE_TRANSPARENCY);
				
		Object[] attributes = Cytoscape.getCurrentlySelectedSocialNetwork()
                .getVisualStyleMap()
                .get(BasicVisualLexicon.EDGE_WIDTH);

		
		// Adjust handle position
		Integer min = (Integer) attributes[1];
        Integer max = (Integer) attributes[2];

        // BRVs are used to set limits on edge transparency
        BoundaryRangeValues bv0 = new BoundaryRangeValues(70, 70, 70);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(100, 100, 100);
        
        mapping.addPoint(min, bv0);
        mapping.addPoint(max, bv1);
        
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
		modifyEdgeWidth(defaultVisualStyle);
		modifyNodeSize(defaultVisualStyle);
		modifyEdgeOpacity(defaultVisualStyle);
		return defaultVisualStyle;
	}

	/**
	 * Apply selected network view
	 */
	public void run(TaskMonitor arg0) throws Exception {
		switch(Cytoscape.getVisualStyleID()) {
			case Category.DEFAULT:
				this.defaultVisualStyle = visualStyleFactoryServiceRef
				                          .createVisualStyle("Default");
				vmmServiceRef.setCurrentVisualStyle(defaultVisualStyle);
				break;
			case Category.CHIPPED:
				this.chippedVisualStyle = createChippedVisualStyle();
				vmmServiceRef.setCurrentVisualStyle(this.chippedVisualStyle);
				break;
		}
	}

}
