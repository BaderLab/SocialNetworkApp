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

package org.baderlab.csapps.socialnetwork.listeners;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.BaseAcademiaVisualStyle;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.IncitesVisualStyle;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.Location;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

/**
 * Adds relevant network information to network table once Cytoscape generates a
 * network
 *
 * @author Victor Kofia
 *
 */
public class SocialNetworkAddedListener implements NetworkAddedListener {
    
    /*
    VisualStyle incitesChartVisualStyle = this.getVisualStyle("InCites Chart");
    if (incitesChartVisualStyle == null) {
        incitesChartVisualStyle = this.createIncitesChartVisualStyle();
        this.vmmServiceRef.addVisualStyle(incitesChartVisualStyle); // TODO:
    }
     */

    private SocialNetworkAppManager appManager = null;
    private CyNetworkManager cyNetworkManagerServiceRef = null;
    private VisualMappingManager vmmServiceRef = null;
    private VisualStyleFactory visualStyleFactoryServiceRef;
    private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
    private VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    private VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    private CyNetwork network = null;
    private SocialNetwork socialNetwork = null;
    
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

    /**
     * Create a new {@link SocialNetworkAddedListener} object
     *
     * @param {@link SocialNetworkAppManager} appManager
     */
    public SocialNetworkAddedListener(SocialNetworkAppManager appManager, CyNetworkManager cyNetworkManagerServiceRef,
            VisualMappingManager vmmServiceRef, VisualStyleFactory visualStyleFactoryServiceRef, VisualMappingFunctionFactory passthroughMappingFactoryServiceRef,
            VisualMappingFunctionFactory continuousMappingFactoryServiceRef, VisualMappingFunctionFactory discreteMappingFactoryServiceRef) {
        super();
        this.appManager = appManager;
        this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
        this.vmmServiceRef = vmmServiceRef;
        this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
        this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
        this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
    }
    
    /**
     * ??
     * 
     * @return VisualStyle incitesChartVisualStyle
     */
    private VisualStyle createIncitesChartVisualStyle() {
        VisualStyle visualStyle = this.visualStyleFactoryServiceRef.createVisualStyle("InCites Chart");
        
        // Specify NODE_LABEL
        String colName = NodeAttribute.Label.toString();
        PassthroughMapping<Integer, ?> passMapping = (PassthroughMapping<Integer, ?>) this.passthroughMappingFactoryServiceRef
                .createVisualMappingFunction(colName, Integer.class, BasicVisualLexicon.NODE_LABEL);
        visualStyle.addVisualMappingFunction(passMapping);
        
        // Specify NODE_SIZE
        CyTable nodeTable = this.network.getDefaultNodeTable();
        CyColumn timesCitedColumn = nodeTable.getColumn(NodeAttribute.TimesCited.toString());
        ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
        int min = getSmallestInCutoff(timesCitedList, 10.0);
        int max = getLargestInCutoff(timesCitedList, 95.0);
        colName = NodeAttribute.TimesCited.toString();
        ContinuousMapping<Integer, ?> contMapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactoryServiceRef.createVisualMappingFunction(
                colName, Integer.class, BasicVisualLexicon.NODE_SIZE);
        BoundaryRangeValues bv0 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(50.0, 50.0, 50.0);
        contMapping.addPoint(min, bv0);
        contMapping.addPoint(max, bv1);
        visualStyle.addVisualMappingFunction(contMapping);

        // Specify NODE_FILL_COLOR
        Map<String, HashMap<String, Color>> colorAttrMap = new HashMap<String, HashMap<String, Color>>();
        HashMap<String, Color> locationsMap = new HashMap<String, Color>();
        locationsMap.put(Location.Ontario.toString(), Color.WHITE);
        locationsMap.put(Location.Canada.toString(), Color.WHITE);
        locationsMap.put(Location.UnitedStates.toString(), Color.WHITE);
        locationsMap.put(Location.International.toString(), Color.WHITE);
        locationsMap.put(Location.Other.toString(), Color.WHITE);
        locationsMap.put(Location.UofT.toString(), Color.WHITE);
        locationsMap.put(Location.NA.toString(), Color.WHITE);
        colorAttrMap.put(NodeAttribute.Location.toString(), locationsMap);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, new Color(0, 204, 204));
        DiscreteMapping locMapping = null;
        for (Entry<String, HashMap<String, Color>> colorMapEntry : colorAttrMap.entrySet()) {
            colName = colorMapEntry.getKey();
            locMapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colName, String.class,
                    BasicVisualLexicon.NODE_FILL_COLOR);
            for (Entry<String, Color> attrMapEntry : colorMapEntry.getValue().entrySet()) {
                locMapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(locMapping);
        }

        // Specify NODE_SHAPE
        Map<String, HashMap<String, NodeShape>> shapeAttrMap = new HashMap<String, HashMap<String, NodeShape>>();
        HashMap<String, NodeShape> departmentMap = new HashMap<String, NodeShape>();
        departmentMap.put((String) (this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString())), NodeShapeVisualProperty.RECTANGLE);
        departmentMap.put("N/A", NodeShapeVisualProperty.RECTANGLE);
        shapeAttrMap.put(NodeAttribute.Department.toString(), departmentMap);
        
        DiscreteMapping shapeMapping = null;
        for (Entry<String, HashMap<String, NodeShape>> nodeShapeMapEntry : shapeAttrMap.entrySet()) {
            colName = nodeShapeMapEntry.getKey();
            shapeMapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colName, String.class,
                    BasicVisualLexicon.NODE_SHAPE);
            for (Entry<String, NodeShape> attrMapEntry : nodeShapeMapEntry.getValue().entrySet()) {
                shapeMapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(shapeMapping);
        }
        
        return visualStyle;
    }
    
    /**
     * ??
     * 
     * @return VisualStyle basicChartVisualStyle
     */
    private VisualStyle createBasicChartVisualStyle() {
        return null;
    }

    /**
     * Adds network to network table and configures visual styles if necessary.
     *
     * @param {@link NetworkAddedEvent} event
     */
    public void handleEvent(NetworkAddedEvent event) {
        this.network = event.getNetwork();
        // Set mouse cursor to default (network's already been loaded)
        this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        String name = SocialNetworkAppManager.getNetworkName(event.getNetwork());
        // If the network being added is a social network, then
        // add it to network table
        if (this.appManager.getSocialNetworkMap().containsKey(name)) {
            // Add to network table
            this.socialNetwork = this.appManager.getSocialNetworkMap().get(name);
            this.socialNetwork.setCyNetwork(event.getNetwork());
            this.appManager.getUserPanelRef().addNetworkToNetworkPanel(socialNetwork);
            int networkID = socialNetwork.getNetworkType();
            // Specify the default visual styles
            switch (networkID) {
                case Category.INCITES:
                    // Add a standard InCites visual style to the visual style map in the social network
                    IncitesVisualStyle incitesVisualStyle = new IncitesVisualStyle(event.getNetwork(), socialNetwork);
                    incitesVisualStyle.applyVisualStyle();
                    // Add new visual style called 'InCites Charts' that users can select to visualize charts
                    // TODO:
                    this.vmmServiceRef.addVisualStyle(this.createIncitesChartVisualStyle());
                    break;
                case Category.SCOPUS:
                case Category.PUBMED:
                    // Add a standard InCites visual style to the visual style map in the social network
                    BaseAcademiaVisualStyle basicVisualStyle = new BaseAcademiaVisualStyle(event.getNetwork(), socialNetwork);
                    basicVisualStyle.applyVisualStyle();
                    // Add new visual style called 'Basic Charts' that users can select to visualize charts
                    // TODO:
                    this.vmmServiceRef.addVisualStyle(this.createBasicChartVisualStyle());
                    break;
            }
            this.appManager.setCurrentlySelectedSocialNetwork(socialNetwork);
        }
    }
}