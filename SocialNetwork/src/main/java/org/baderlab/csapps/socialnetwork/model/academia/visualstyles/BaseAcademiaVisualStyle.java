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
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

/**
 * Contains attributes present in every academia visual style used by 
 * the app. Every academia visual style extends this class.
 *
 * @author risserlin
 * @author Victor Kofia
 */
public class BaseAcademiaVisualStyle extends AbstractVisualStyle {

	protected VisualStyle visualStyle;
	
    protected final VisualMappingFunctionFactory continuousMappingFactory;
    protected final CyApplicationManager applicationManager;
    protected final VisualMappingFunctionFactory discreteMappingFactory;
    protected final CyNetwork network;
    protected final VisualMappingFunctionFactory passthroughMappingFactory;
    protected final SocialNetwork socialNetwork;

    public BaseAcademiaVisualStyle(
    		CyApplicationManager applicationManager,
    		CyNetwork network,
    		SocialNetwork socialNetwork, 
            VisualStyleFactory visualStyleFactory,
            VisualMappingFunctionFactory passthroughMappingFactory, 
            VisualMappingFunctionFactory continuousMappingFactory,
            VisualMappingFunctionFactory discreteMappingFactory, 
            boolean isChart
     ) {
        this.network = network;
        this.socialNetwork = socialNetwork;
        this.applicationManager = applicationManager;
        this.passthroughMappingFactory = passthroughMappingFactory;
        this.continuousMappingFactory = continuousMappingFactory;
        this.discreteMappingFactory = discreteMappingFactory;
        
        String networkName = null;
        
        switch(socialNetwork.getNetworkType()) {
            case Category.PUBMED:
                networkName = String.format("%s_PubMed", socialNetwork.getNetworkName());
                break;
            case Category.INCITES:
                networkName = String.format("%s_InCites", socialNetwork.getNetworkName());
                break;
            case Category.SCOPUS:
                networkName = String.format("%s_Scopus", socialNetwork.getNetworkName());
                break;
        }
        
        if (isChart)
            networkName = String.format("%s Chart", socialNetwork.getNetworkName());
        
        visualStyle = visualStyleFactory.createVisualStyle(networkName);
        applyVisualStyle(visualStyle);
    }

    @Override
    protected void applyEdgeStyle(VisualStyle visualStyle) {
        // Specify EDGE_WIDTH
        applyEdgeWidth(visualStyle);
        
        // Specify EDGE_TRANSPARENCY
        applyEdgeTransparency(visualStyle);
        
        // Specify EDGE_VISIBILITY
        applyEdgeVisibility(visualStyle);
    }

    @Override
    protected void applyEdgeTransparency(VisualStyle visualStyle) {
        // Edge table reference
        var edgeTable = network.getDefaultEdgeTable();
        
        // Edge width variables
        int min = 0;
        int max = 0;
        
        edgeTable = network.getDefaultEdgeTable();
        var copubColumn = edgeTable.getColumn(EdgeAttribute.COPUBLICATION_COUNT.toString());
        var copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        min = getSmallestInCutoff(copubList, 5.0);
        max = getLargestInCutoff(copubList, 100.0);   
        
        var mapping = (ContinuousMapping<Integer, ?>) continuousMappingFactory.createVisualMappingFunction(
                EdgeAttribute.COPUBLICATION_COUNT.toString(), Integer.class, BasicVisualLexicon.EDGE_TRANSPARENCY);
        // BRVs are used to set limits on edge transparency
        // (min edge transparency = 100; max edge transparency = 255)
        var bv0 = new BoundaryRangeValues(100, 100, 100);
        var bv1 = new BoundaryRangeValues(255, 255, 255);
        // Adjust handle position
        mapping.addPoint(1, bv0);
        mapping.addPoint(Math.round(max / 2.0f), bv1);
        visualStyle.addVisualMappingFunction(mapping);        
    }

    @Override
    protected void applyEdgeVisibility(VisualStyle visualStyle) {
        // TODO: Set default visibility
    }

    @Override
    protected void applyEdgeWidth(VisualStyle visualStyle) {
        // Edge table reference
    	var edgeTable = network.getDefaultEdgeTable();
        
        // Edge width variables
        int min = 0;
        int max = 0;
        
        edgeTable = network.getDefaultEdgeTable();
        var copubColumn = edgeTable.getColumn(EdgeAttribute.COPUBLICATION_COUNT.toString());
        var copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        min = getSmallestInCutoff(copubList, 5.0);
        max = getLargestInCutoff(copubList, 100.0);   
        
        // Specify EDGE_WIDTH
        var edgeWidthContinuousMapping = (ContinuousMapping<Integer, ?>) continuousMappingFactory.createVisualMappingFunction(
                EdgeAttribute.COPUBLICATION_COUNT.toString(), Integer.class, BasicVisualLexicon.EDGE_WIDTH);
        // BRVs are used to set limits on edge width (max edge width = 10; min edge width = 1)
        var bv0 = new BoundaryRangeValues(1.0, 1.0, 1.0);
        var bv1 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        // Adjust handle position
        edgeWidthContinuousMapping.addPoint(min + 1, bv0);
        edgeWidthContinuousMapping.addPoint(max, bv1);
        visualStyle.addVisualMappingFunction(edgeWidthContinuousMapping);    
    }

    @Override
    protected void applyNodeBorderPaint(VisualStyle visualStyle) {
        // TODO: Set default node border color
    }

    @Override
    protected void applyNodeBorderWidth(VisualStyle visualStyle) {
        // TODO: Set default node border width
    }

    @Override
    protected void applyNodeFillColor(VisualStyle visualStyle) {
        // TODO: Set default node fill color
    }

    @Override
    protected void applyNodeLabel(VisualStyle visualStyle) {
    	var labelPassthroughMapping = (PassthroughMapping<Integer, ?>) passthroughMappingFactory
                .createVisualMappingFunction(NodeAttribute.LABEL.toString(), Integer.class, BasicVisualLexicon.NODE_LABEL);
        visualStyle.addVisualMappingFunction(labelPassthroughMapping);        
    }

    @Override
    protected void applyNodeLabelFontFace(VisualStyle visualStyle) {
        // TODO: Set default node label font face
    }

    @Override
    protected void applyNodeLabelPosition(VisualStyle visualStyle) {
        // TODO: Set default node label position
    }

    @Override
    protected void applyNodeShape(VisualStyle visualStyle) {
        // TODO: Set default node shape
    }

    @Override
    protected void applyNodeSize(VisualStyle visualStyle) {
        var nodeTable = this.network.getDefaultNodeTable();
        var timesCitedColumn = nodeTable.getColumn(NodeAttribute.TIMES_CITED.toString());
        var timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
        int minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
        int maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
        var timesCitesContinuousMapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactory.createVisualMappingFunction(
                NodeAttribute.TIMES_CITED.toString(), Integer.class, BasicVisualLexicon.NODE_SIZE);
        var bv0 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        var bv1 = new BoundaryRangeValues(50.0, 50.0, 50.0);
        timesCitesContinuousMapping.addPoint(minNodeSize, bv0);
        timesCitesContinuousMapping.addPoint(maxNodeSize, bv1);
        visualStyle.addVisualMappingFunction(timesCitesContinuousMapping);
    }

    @Override
    protected void applyNodeStyle(VisualStyle visualStyle) {
        
        // Specify NODE_LABEL
        applyNodeLabel(visualStyle);
        
        // Specify NODE_LABEL_POSITION
        applyNodeLabelPosition(visualStyle);
        
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
        
        // Specify NODE_VISIBLE
        applyNodeVisibility(visualStyle);
    }

    @Override
    protected void applyNodeVisibility(VisualStyle visualStyle) {
        // TODO: Set default visibility 
    }

    @Override
    protected void applyVisualStyle(VisualStyle visualStyle) {
        applyNodeStyle(visualStyle);
        applyEdgeStyle(visualStyle);
    }

    @Override
    public boolean equals(Object obj) {
        BaseAcademiaVisualStyle other = (BaseAcademiaVisualStyle) obj;
        return this.visualStyle.getTitle().equals(other.getVisualStyle().getTitle());
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

    @Override
    public VisualStyle getVisualStyle() {
        return this.visualStyle;
    }

    @Override
    public int hashCode() {
        return this.visualStyle.getTitle().hashCode();
    }
}
