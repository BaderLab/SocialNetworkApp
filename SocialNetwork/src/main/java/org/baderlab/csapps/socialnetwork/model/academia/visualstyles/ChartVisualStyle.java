package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

import java.awt.Color;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class ChartVisualStyle extends BaseAcademiaVisualStyle {
    
    public ChartVisualStyle(CyNetwork network, SocialNetwork socialNetwork, VisualStyleFactory visualStyleFactoryServiceRef,
            VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef, boolean isChart) {
        super(network, socialNetwork, visualStyleFactoryServiceRef, passthroughMappingFactoryServiceRef, continuousMappingFactoryServiceRef,
                discreteMappingFactoryServiceRef, isChart);
    }
    
    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeFillColor(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeFillColor(VisualStyle visualStyle) {
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.WHITE);
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeShape(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeShape(VisualStyle visualStyle) {
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.RECTANGLE);
    }

}
