package org.baderlab.csapps.socialnetwork.model.visualstyles.academia;

import java.awt.Color;
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
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class IncitesChartVisualStyle extends BaseAcademiaVisualStyle {
    
    public IncitesChartVisualStyle(CyNetwork network, SocialNetwork socialNetwork) {
        super(network, socialNetwork);
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.visualstyles.academia.BaseAcademiaVisualStyle#applyNodeStyle()
     */
    @Override
    protected void applyNodeStyle() {
        CyTable nodeTable = null;
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
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
                new Object[] { NodeAttribute.TimesCited.toString(), minNodeSize + 1, maxNodeSize });
        
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
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_FILL_COLOR, new Object[] { colorAttrMap });
        
        // Specify NODE_SHAPE
        Map<String, HashMap<String, NodeShape>> shapeAttrMap = new HashMap<String, HashMap<String, NodeShape>>();
        HashMap<String, NodeShape> departmentMap = new HashMap<String, NodeShape>();
        departmentMap.put((String) (this.socialNetwork.getAttrMap().get(NodeAttribute.Department.toString())), NodeShapeVisualProperty.RECTANGLE);
        departmentMap.put("N/A", NodeShapeVisualProperty.RECTANGLE);
        shapeAttrMap.put(NodeAttribute.Department.toString(), departmentMap);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SHAPE, new Object[] { shapeAttrMap });
        
        // Specify NODE_BORDER_WIDTH
        // TODO: Is this really necessary?
        
        // Specify NODE_BORDER_FILL
        // TODO: Is this really necessary?
    }

}
