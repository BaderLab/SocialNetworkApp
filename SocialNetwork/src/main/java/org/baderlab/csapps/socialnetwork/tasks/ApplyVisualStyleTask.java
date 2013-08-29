package main.java.org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import main.java.org.baderlab.csapps.socialnetwork.model.Category;
import main.java.org.baderlab.csapps.socialnetwork.model.Cytoscape;

import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
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
	private VisualStyle vanueVisualStyle;
	private VisualStyle defaultVisualStyle;
	private TaskMonitor taskMonitor;
	
	/**
	 * Get task monitor
	 * @param null
	 * @return TaskMonitor taskMonitor
	 */
	private TaskMonitor getTaskMonitor() {
		return this.taskMonitor;
	}
	
	/**
	 * Set task monitor
	 * @param TaskMonitor taskMonitor
	 * @return null
	 */
	private void setTaskMonitor(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}
	
	/**
	 * Get Chipped visual style
	 * @param null
	 * @return VisualStyle chippedVisualStyle
	 */
	private VisualStyle getChippedVisualStyle() {
		if (this.chippedVisualStyle == null) {
			this.chippedVisualStyle = this.createChippedVisualStyle();
		}
		return this.chippedVisualStyle;
	}
	
	/**
	 * Get Vanue visual style
	 * @param null
	 * @return VisualStyle vanueVisualStyle
	 */
	private VisualStyle getVanueVisualStyle() {
		if (this.vanueVisualStyle == null) {
			this.vanueVisualStyle = this.createVanueVisualStyle();
		}
		return this.vanueVisualStyle;
	}
	
	/**
	 * Get Default visual style
	 * @param null
	 * @return VisualStyle defaultVisualStyle
	 */
	private VisualStyle getDefaultVisualStyle() {
		if (this.defaultVisualStyle == null) {
			this.defaultVisualStyle = visualStyleFactoryServiceRef
                                    .createVisualStyle("Default");
		}
		return this.defaultVisualStyle;
	}
	
	/**
	 * Create new apply visual style task
	 * @param visualStyleFactoryServiceRef
	 * @param vmmServiceRef
	 * @param passthroughMappingFactoryServiceRef
	 * @param continuousMappingFactoryServiceRef
	 * @param discreteMappingFactoryServiceRef
	 * @return null
	 */
	
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
		// Get column name
		String colName = (String) Cytoscape.getCurrentlySelectedSocialNetwork()
				                             .getVisualStyleMap()
				                             .get(BasicVisualLexicon.NODE_LABEL)[0];
		// Assign node label filter to column
		PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>)
			passthroughMappingFactoryServiceRef.createVisualMappingFunction(colName, 
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
		// Get column name
		String colName = (String) Cytoscape.getCurrentlySelectedSocialNetwork()
                                             .getVisualStyleMap()
                                             .get(BasicVisualLexicon.EDGE_LABEL)[0];
		// Assign edge label filter to column
		PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>)
			passthroughMappingFactoryServiceRef.createVisualMappingFunction(colName, 
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
		// Get column name
		String colName = (String) attributes[0];
		ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) 
				                                this.continuousMappingFactoryServiceRef
		.createVisualMappingFunction(colName, Integer.class, BasicVisualLexicon.EDGE_WIDTH);
        // BRVs are used to set limits on edge width (max edge width = 10; min edge width = 1)
        BoundaryRangeValues bv0 = new BoundaryRangeValues(1.0, 1.0, 1.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        // Adjust handle position
		int min = (Integer) attributes[1];
        int max = (Integer) attributes[2];
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
		// Get column name
		String colName = (String) attributes[0];
		ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) 
				                                this.continuousMappingFactoryServiceRef
		.createVisualMappingFunction(colName, Integer.class, BasicVisualLexicon.NODE_SIZE);
        // BRVs are used to set limits on node size (max size = 50; min size = 10)
        BoundaryRangeValues bv0 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(50.0, 50.0, 50.0);
		// Adjust handle position
		int min = (Integer) attributes[1];
        int max = (Integer) attributes[2];
        mapping.addPoint(min, bv0);
        mapping.addPoint(max, bv1);
		visualStyle.addVisualMappingFunction(mapping);
		return visualStyle;
	}
	
	/**
	 * Modify node color
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle modifyNodeColor(VisualStyle visualStyle) {
		DiscreteMapping mapping = null;
		Object[] tempVar = Cytoscape.getCurrentlySelectedSocialNetwork()
                                     .getVisualStyleMap()
                                     .get(BasicVisualLexicon.NODE_FILL_COLOR);
		Map<String, HashMap<String, Color>> colorMap = (Map<String, HashMap<String, Color>>) tempVar[0];
		for (Entry<String, HashMap<String, Color>> colorMapEntry : colorMap.entrySet()) {
			String colName = colorMapEntry.getKey();
			mapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction
                    (colName, String.class, BasicVisualLexicon.NODE_FILL_COLOR);
			for (Entry<String, Color> attrMapEntry : colorMapEntry.getValue().entrySet()) {
				mapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
			}
			visualStyle.addVisualMappingFunction(mapping);
		}
		return visualStyle;
	}
	
	/**
	 * Modify node shape
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle modifyNodeShape(VisualStyle visualStyle) {
		DiscreteMapping mapping = null;
		Object[] tempVar = Cytoscape.getCurrentlySelectedSocialNetwork()
                                     .getVisualStyleMap()
                                     .get(BasicVisualLexicon.NODE_SHAPE);
		Map<String, HashMap<String, NodeShape>> nodeShapeMap = (Map<String, HashMap<String, NodeShape>>) tempVar[0];
		for (Entry<String, HashMap<String, NodeShape>> colorMapEntry : nodeShapeMap.entrySet()) {
			String colName = colorMapEntry.getKey();
			mapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef
					.createVisualMappingFunction(colName, String.class, BasicVisualLexicon.NODE_SHAPE);
			for (Entry<String, NodeShape> attrMapEntry : colorMapEntry.getValue().entrySet()) {
				mapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
			}
			visualStyle.addVisualMappingFunction(mapping);
		}
		return visualStyle;
	}
	
	/**
	 * Modify edge opacity
	 * @param VisualStyle visualStyle
	 * @return VisualStyle visualStyle
	 */
	private VisualStyle modifyEdgeOpacity(VisualStyle visualStyle) {
		// Get column name
		String colName = (String) Cytoscape.getCurrentlySelectedSocialNetwork()
                .getVisualStyleMap()
                .get(BasicVisualLexicon.EDGE_TRANSPARENCY)[0];
		ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) 
				                        this.continuousMappingFactoryServiceRef
		.createVisualMappingFunction(colName, Integer.class, 
				                     BasicVisualLexicon.EDGE_TRANSPARENCY);
		Object[] attributes = Cytoscape.getCurrentlySelectedSocialNetwork()
                                       .getVisualStyleMap()
                                       .get(BasicVisualLexicon.EDGE_WIDTH);
        // BRVs are used to set limits on edge transparency 
		// (min edge transparency = 100; max edge transparency = 300)
        BoundaryRangeValues bv0 = new BoundaryRangeValues(100.0, 100.0, 100.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(300.0, 300.0, 300.0);
		// Adjust handle position
		Integer min = (Integer) attributes[1];
        Integer max = (Integer) attributes[2];
        mapping.addPoint(1, bv0);
        mapping.addPoint(max / 2, bv1);
		visualStyle.addVisualMappingFunction(mapping);
		return visualStyle;
	}
	
	/**
	 * Create Chipped visual style
	 * @param null
	 * @return VisualStyle chippedVisualStyle
	 */
	private VisualStyle createChippedVisualStyle() {
		VisualStyle chippedVisualStyle = visualStyleFactoryServiceRef
				                         .createVisualStyle("Chipped");
		addNodeLabels(chippedVisualStyle);
		modifyEdgeWidth(chippedVisualStyle);
		modifyNodeSize(chippedVisualStyle);
		modifyEdgeOpacity(chippedVisualStyle);
		modifyNodeColor(chippedVisualStyle);
		modifyNodeShape(chippedVisualStyle);
		return chippedVisualStyle;
	}
	
	/**
	 * Create Vanue visual style
	 * @param null
	 * @return VisualStyle vanueVisualStyle 
	 */
	private VisualStyle createVanueVisualStyle() {
		VisualStyle vanueVisualStyle = visualStyleFactoryServiceRef
                .createVisualStyle("Vanue");
		addNodeLabels(vanueVisualStyle);
		modifyNodeSize(vanueVisualStyle);
		modifyEdgeWidth(vanueVisualStyle);
		modifyEdgeOpacity(vanueVisualStyle);
		return vanueVisualStyle;
	}

	/**
	 * Apply selected network view
	 * @param TaskMonitor taskMonitor
	 * @return null
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.setTaskMonitor(taskMonitor);
		switch(Cytoscape.getVisualStyleID()) {
			case Category.DEFAULT:
				vmmServiceRef.setCurrentVisualStyle(this.getDefaultVisualStyle());
				break;
			case Category.CHIPPED:
				this.getTaskMonitor().setTitle("Loading Chipped Visual Style ... ");
				this.getTaskMonitor().setProgress(0.0);
				this.getTaskMonitor().setStatusMessage("");
				vmmServiceRef.setCurrentVisualStyle(this.getChippedVisualStyle());
				break;
			case Category.VANUE:
				this.getTaskMonitor().setTitle("Loading Vanue Visual Style ... ");
				this.getTaskMonitor().setProgress(0.0);
				this.getTaskMonitor().setStatusMessage("");
				vmmServiceRef.setCurrentVisualStyle(this.getVanueVisualStyle());
				break;
		}
		return;
	}

}
