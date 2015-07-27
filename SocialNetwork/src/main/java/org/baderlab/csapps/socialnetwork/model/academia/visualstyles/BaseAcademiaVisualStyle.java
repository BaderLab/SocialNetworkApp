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

import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

/**
 * Contains attributes present in every academia visual style in use by 
 * the app. Every academia visual style extends this class.
 *
 * @author risserlin
 * @author Victor Kofia
 */
public class BaseAcademiaVisualStyle extends AbstractVisualStyle {

    protected VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    protected VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    protected CyNetwork network = null;
    protected VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
    protected SocialNetwork socialNetwork = null;
    protected VisualStyle visualStyle = null;

    /**
     * ??
     * 
     * @param CyNetwork network
     */
    public BaseAcademiaVisualStyle(CyNetwork network, SocialNetwork socialNetwork, VisualStyleFactory visualStyleFactoryServiceRef,
            VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef) {
        this.network = network;
        this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
        this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
        this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
        String networkName = null;
        switch(socialNetwork.getNetworkType()) {
            case Category.PUBMED:
                networkName = "PubMed";
                break;
            case Category.INCITES:
                networkName = "InCites";
                break;
            case Category.SCOPUS:
                networkName = "Scopus";
        }
        this.visualStyle = visualStyleFactoryServiceRef.createVisualStyle(networkName);
        applyVisualStyle(this.visualStyle);
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.visualstyles.AbstractVisualStyle#applyEdgeStyle(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyEdgeStyle(VisualStyle visualStyle) {
        
        // Edge table reference
        CyTable edgeTable = this.network.getDefaultEdgeTable();
        
        // Edge width variables
        int min = 0;
        int max = 0;
        
        edgeTable = this.network.getDefaultEdgeTable();
        CyColumn copubColumn = edgeTable.getColumn(EdgeAttribute.NumCopublications.toString());
        ArrayList<Integer> copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        min = getSmallestInCutoff(copubList, 5.0);
        max = getLargestInCutoff(copubList, 100.0);   
        
        // Specify EDGE_WIDTH
        ContinuousMapping<Integer, ?> edgeWidthContinuousMapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactoryServiceRef.createVisualMappingFunction(
                EdgeAttribute.NumCopublications.toString(), Integer.class, BasicVisualLexicon.EDGE_WIDTH);
        // BRVs are used to set limits on edge width (max edge width = 10; min edge width = 1)
        BoundaryRangeValues bv0 = new BoundaryRangeValues(1.0, 1.0, 1.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        // Adjust handle position
        edgeWidthContinuousMapping.addPoint(min + 1, bv0);
        edgeWidthContinuousMapping.addPoint(max, bv1);
        visualStyle.addVisualMappingFunction(edgeWidthContinuousMapping);
        
        // Specify EDGE_TRANSPARENCY
        ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactoryServiceRef.createVisualMappingFunction(
                EdgeAttribute.NumCopublications.toString(), Integer.class, BasicVisualLexicon.EDGE_TRANSPARENCY);
        // BRVs are used to set limits on edge transparency
        // (min edge transparency = 100; max edge transparency = 300)
        bv0 = new BoundaryRangeValues(100.0, 100.0, 100.0);
        bv1 = new BoundaryRangeValues(300.0, 300.0, 300.0);
        // Adjust handle position
        mapping.addPoint(1, bv0);
        mapping.addPoint(max / 2, bv1);
        visualStyle.addVisualMappingFunction(mapping);        
        
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyNodeBorderPaint(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeBorderPaint(VisualStyle visualStyle) {
        // TODO: Set default node border color
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyNodeBorderWidth(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeBorderWidth(VisualStyle visualStyle) {
        // TODO: Set default node border width
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyNodeFillColor(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeFillColor(VisualStyle visualStyle) {
        // TODO: Set default node fill color
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyNodeLabel(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeLabel(VisualStyle visualStyle) {
        PassthroughMapping<Integer, ?> labelPassthroughMapping = (PassthroughMapping<Integer, ?>) this.passthroughMappingFactoryServiceRef
                .createVisualMappingFunction(NodeAttribute.Label.toString(), Integer.class, BasicVisualLexicon.NODE_LABEL);
        visualStyle.addVisualMappingFunction(labelPassthroughMapping);        
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyNodeLabelFontFace(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeLabelFontFace(VisualStyle visualStyle) {
        // TODO: Set default node label font face
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyNodeShape(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeShape(VisualStyle visualStyle) {
        // TODO: Set default node shape
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyNodeSize(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeSize(VisualStyle visualStyle) {
        CyTable nodeTable = this.network.getDefaultNodeTable();
        CyColumn timesCitedColumn = nodeTable.getColumn(NodeAttribute.TimesCited.toString());
        ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
        int minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
        int maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
        ContinuousMapping<Integer, ?> timesCitesContinuousMapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactoryServiceRef.createVisualMappingFunction(
                NodeAttribute.TimesCited.toString(), Integer.class, BasicVisualLexicon.NODE_SIZE);
        BoundaryRangeValues bv0 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(50.0, 50.0, 50.0);
        timesCitesContinuousMapping.addPoint(minNodeSize, bv0);
        timesCitesContinuousMapping.addPoint(maxNodeSize, bv1);
        visualStyle.addVisualMappingFunction(timesCitesContinuousMapping);
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.visualstyles.AbstractVisualStyle#applyNodeStyle(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyNodeStyle(VisualStyle visualStyle) {
        
        // Specify NODE_LABEL
        applyNodeLabel(visualStyle);
        
        // Specify NODE_SIZE
        applyNodeSize(visualStyle);
        
        // Specify NODE_FILL_COLOR
        applyNodeFillColor(visualStyle);
        
        // Specify NODE_SHAPE
        applyNodeShape(visualStyle);
        
        // Specify NODE_BORDER_PAINT
        applyNodeBorderPaint(visualStyle);
        
        // Specify NODE_BORDER_WIDTH
        applyNodeBorderWidth(visualStyle);
        
        // Specify NODE_LABEL_FONT_FACE
        applyNodeLabelFontFace(visualStyle);
        
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#applyVisualStyle(org.cytoscape.view.vizmap.VisualStyle)
     */
    @Override
    protected void applyVisualStyle(VisualStyle visualStyle) {
        applyNodeStyle(visualStyle);
        applyEdgeStyle(visualStyle);
    }

    /**
     * Get largest value given cut-off point.
     *
     * @param List list
     * @param Double cutoff
     * @return Integer value
     */
    protected int getLargestInCutoff(ArrayList<Integer> list, Double cutoff) {
        Collections.sort(list);
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int value : list) {
            stats.addValue(value);
        }
        Double percentile = stats.getPercentile(cutoff);
        for (int i = list.size() - 1; i > -1; i--) {
            if (list.get(i) <= percentile) {
                return list.get(i);
            }
        }
        return list.get(list.size() - 1);
    }

    /**
     * Get smallest value given cutoff.
     *
     * @param List list
     * @param Double cutoff
     * @return Integer value
     */
    protected int getSmallestInCutoff(ArrayList<Integer> list, Double cutoff) {
        Collections.sort(list);
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int value : list) {
            stats.addValue(value);
        }
        Double percentile = stats.getPercentile(cutoff);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) >= percentile) {
                return list.get(i);
            }
        }
        return list.get(0);
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.AbstractVisualStyle#getVisualStyle()
     */
    @Override
    public VisualStyle getVisualStyle() {
        return this.visualStyle;
    }

}
