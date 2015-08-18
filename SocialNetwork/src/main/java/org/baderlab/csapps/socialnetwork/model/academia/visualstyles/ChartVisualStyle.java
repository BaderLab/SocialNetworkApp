package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

import java.awt.Color;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
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
    
    public ChartVisualStyle(CyApplicationManager cyApplicationManager, CyNetwork network, SocialNetwork socialNetwork, VisualStyleFactory visualStyleFactoryServiceRef,
            VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef, boolean isChart) {
        super(cyApplicationManager, network, socialNetwork, visualStyleFactoryServiceRef, passthroughMappingFactoryServiceRef, continuousMappingFactoryServiceRef,
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
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeLabelPosition(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeLabelPosition(VisualStyle visualStyle) {
        // TODO: Disabled temporarily
        // Get the current Visual Lexicon
        VisualLexicon lexicon = this.cyApplicationManagerServiceRef.getCurrentRenderingEngine().getVisualLexicon();
        // Try to get the label visual property by its ID
        VisualProperty vp = lexicon.lookup(CyNode.class, "NODE_LABEL_POSITION");
        if (vp != null) {
            // If the property is supported by this rendering engine,
            // use the serialization string value to create the actual property value
            Object position = vp.parseSerializableString("N,S,c,0.00,-5.00");
            // If the parsed value is ok, apply it to the visual style
            // as default value or a visual mapping
            if (position != null) {
                visualStyle.setDefaultValue(vp, position);                
            }
        }
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeShape(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeShape(VisualStyle visualStyle) {
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.RECTANGLE);
    }

}
