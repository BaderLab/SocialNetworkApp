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

package org.baderlab.csapps.socialnetwork.model.visualstyles.academia;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;

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
    public IncitesVisualStyle(CyNetwork network, SocialNetwork socialNetwork) {
        super(network, socialNetwork);
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.visualstyles.academia.BaseAcademiaVisualStyle#applyNodeStyle()
     */
    @Override
    protected void applyNodeStyle() {
        // Node table reference
        CyTable nodeTable = null;
        // Node size variables
        int minNodeSize = 0;
        int maxNodeSize = 0;
        
        // Specify NODE_LABEL

        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, new Object[] { NodeAttribute.Label.toString() });

        // Specify NODE_SIZE
        nodeTable = this.network.getDefaultNodeTable();
        CyColumn timesCitedColumn = nodeTable.getColumn(NodeAttribute.TimesCited.toString());
        ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
        minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
        maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, new Object[] { NodeAttribute.TimesCited.toString(), minNodeSize + 1, maxNodeSize });

        // Specify NODE_FILL_COLOR
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
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_FILL_COLOR, new Object[] { colorAttrMap });
        
        // Specify NODE_SHAPE
        Map<String, HashMap<String, NodeShape>> shapeAttrMap = new HashMap<String, HashMap<String, NodeShape>>();
        HashMap<String, NodeShape> departmentMap = new HashMap<String, NodeShape>();
        departmentMap.put((String) (this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString())), NodeShapeVisualProperty.TRIANGLE);
        departmentMap.put("N/A", NodeShapeVisualProperty.RECTANGLE);
        shapeAttrMap.put(NodeAttribute.Department.toString(), departmentMap);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SHAPE, new Object[] { shapeAttrMap });

        // Specify NODE_BORDER_WIDTH and NODE_BORDER_FILL for faculty members
        // (i.e. department)
        Map<String, HashMap<String, Color>> borderpainAttrMap = new HashMap<String, HashMap<String, Color>>();
        HashMap<String, Color> departmentMap_borderpaint = new HashMap<String, Color>();
        departmentMap_borderpaint.put((String) (this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString())), new Color(243, 243, 21));
        borderpainAttrMap.put(NodeAttribute.Department.toString(), departmentMap_borderpaint);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_BORDER_PAINT, new Object[] { borderpainAttrMap });

        Map<String, HashMap<String, Double>> borderwidthAttrMap = new HashMap<String, HashMap<String, Double>>();
        HashMap<String, Double> departmentMap_borderwidth = new HashMap<String, Double>();
        departmentMap_borderwidth.put((String) (this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString())), 10.0);
        borderwidthAttrMap.put(NodeAttribute.Department.toString(), departmentMap_borderwidth);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_BORDER_WIDTH, new Object[] { borderwidthAttrMap });

        Map<String, HashMap<String, Font>> fontsizeAttrMap = new HashMap<String, HashMap<String, Font>>();
        HashMap<String, Font> departmentMap_fontsize = new HashMap<String, Font>();
        departmentMap_fontsize.put(Location.UofT.toString(), new Font("Verdana", Font.BOLD, 12));
        fontsizeAttrMap.put(NodeAttribute.Location.toString(), departmentMap_fontsize);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL_FONT_FACE, new Object[] { fontsizeAttrMap });
    }

}
