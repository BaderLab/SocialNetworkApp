/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates,
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

/**
 * A visual style for InCites networks
 *
 * @author risserlin
 */
public class IncitesVisualStyle extends BaseAcademiaVisualStyle {
    
    /**
     * ??
     * 
     * @param CyNetwork network
     * @param SocialNetwork socialNetwork
     */
    public IncitesVisualStyle(CyNetwork network, SocialNetwork socialNetwork, VisualStyleFactory visualStyleFactoryServiceRef,
            VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef, boolean isChart) {
        super(network, socialNetwork, visualStyleFactoryServiceRef, passthroughMappingFactoryServiceRef, continuousMappingFactoryServiceRef,
                discreteMappingFactoryServiceRef, isChart);
    }
    
    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeBorderPaint(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeBorderPaint(VisualStyle visualStyle) {
        super.applyNodeBorderPaint(visualStyle);
        DiscreteMapping borderPaintDiscreteMapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(NodeAttribute.Department.toString(), 
                String.class, BasicVisualLexicon.NODE_BORDER_PAINT);
        borderPaintDiscreteMapping.putMapValue(this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString()), new Color(243, 243, 21));       
    }


    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeBorderWidth(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeBorderWidth(VisualStyle visualStyle) {
        super.applyNodeBorderWidth(visualStyle);
        DiscreteMapping borderWidthDiscreteMapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(NodeAttribute.Department.toString(), 
                String.class, BasicVisualLexicon.NODE_BORDER_WIDTH);
        borderWidthDiscreteMapping.putMapValue(this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString()), 10.0);       
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeFillColor(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeFillColor(VisualStyle visualStyle) {
        super.applyNodeFillColor(visualStyle);
        Map<String, HashMap<String, Color>> colorAttrMap = new HashMap<String, HashMap<String, Color>>();
        HashMap<String, Color> locationsMap = new HashMap<String, Color>();
        locationsMap.put(Location.Ontario.toString(), new Color(255, 137, 41));
        locationsMap.put(Location.Canada.toString(), new Color(204, 0, 51));
        locationsMap.put(Location.UnitedStates.toString(), new Color(0, 51, 153));
        locationsMap.put(Location.International.toString(), new Color(0, 204, 204));
        locationsMap.put(Location.Other.toString(), new Color(204, 0, 204));
        locationsMap.put(Location.UofT.toString(), new Color(0, 204, 0));
        locationsMap.put(Location.NA.toString(), new Color(153, 153, 153));
        colorAttrMap.put(NodeAttribute.Location.toString(), locationsMap);
        DiscreteMapping fillColorDiscreteMapping = null;
        for (Entry<String, HashMap<String, Color>> colorMapEntry : colorAttrMap.entrySet()) {
            fillColorDiscreteMapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colorMapEntry.getKey(), String.class,
                    BasicVisualLexicon.NODE_FILL_COLOR);
            for (Entry<String, Color> attrMapEntry : colorMapEntry.getValue().entrySet()) {
                fillColorDiscreteMapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(fillColorDiscreteMapping);
        }
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeLabelFontFace(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeLabelFontFace(VisualStyle visualStyle) {
        super.applyNodeLabelFontFace(visualStyle);
        DiscreteMapping fontDiscreteMapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(NodeAttribute.Department.toString(), 
                String.class, BasicVisualLexicon.NODE_LABEL_FONT_FACE);
        fontDiscreteMapping.putMapValue(Location.UofT.toString(), new Font("Verdana", Font.BOLD, 12));
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle#applyNodeShape(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeShape(VisualStyle visualStyle) {
        super.applyNodeShape(visualStyle);
        Map<String, HashMap<String, NodeShape>> shapeAttrMap = new HashMap<String, HashMap<String, NodeShape>>();
        HashMap<String, NodeShape> departmentMap = new HashMap<String, NodeShape>();
        departmentMap.put((String) (this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString())), NodeShapeVisualProperty.TRIANGLE);
        departmentMap.put("N/A", NodeShapeVisualProperty.RECTANGLE);
        shapeAttrMap.put(NodeAttribute.Department.toString(), departmentMap);
        DiscreteMapping shapeDiscreteMapping = null;
        for (Entry<String, HashMap<String, NodeShape>> nodeShapeMapEntry : shapeAttrMap.entrySet()) {
            shapeDiscreteMapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(nodeShapeMapEntry.getKey(), String.class,
                    BasicVisualLexicon.NODE_SHAPE);
            for (Entry<String, NodeShape> attrMapEntry : nodeShapeMapEntry.getValue().entrySet()) {
                shapeDiscreteMapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(shapeDiscreteMapping);
        }
    }

}
