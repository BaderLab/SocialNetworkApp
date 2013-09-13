package org.baderlab.csapps.socialnetwork.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;

public class IncitesVisualStyle extends BasicSocialNetworkVisualstyle {
	
	public static final String nodeattr_location = "Location";
	public static final String nodeattr_inst = "Institution";
	public static final String nodeattr_dept = "Department";

	
	private CyNetwork network;
	private SocialNetwork socialNetwork;
	
	public void applyVisualStyle(CyNetwork network, SocialNetwork socialNetwork){
		this.network = network;
		this.socialNetwork = socialNetwork;
		applyNodeStyle(this.network, this.socialNetwork);
		applyIncitesNodeStyle();
		applyEdgeStyle(this.network, this.socialNetwork);
	}
	
	private void applyIncitesNodeStyle(){
		// Node table reference
		CyTable nodeTable = null;
		// Node size variables
		int minNodeSize = 0;
		int maxNodeSize = 0;
		
		
		// Specify Node_SIZE
		nodeTable = this.network.getDefaultNodeTable();
		CyColumn timesCitedColumn = nodeTable.getColumn(nodeattr_timescited);
		ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
		minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
		maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, 
				new Object[] {nodeattr_timescited, minNodeSize + 1, maxNodeSize});
		
		// Specify NODE_FILL_COLOR
		Map<String, HashMap<String, Color>> colorAttrMap = new HashMap<String, HashMap<String, Color>>();
		HashMap<String, Color> locationsMap = new HashMap<String, Color>();
		locationsMap.put("Ontario", new Color(255,137,41));
		//locationsMap.put("Canada", new Color(235,235,52));
		locationsMap.put("Canada", new Color(204,0,51));
		locationsMap.put("United States", new Color(0,51,153));
		locationsMap.put("International", new Color(0,204,204));
		locationsMap.put("Other", new Color(204, 0, 204));
		locationsMap.put("UNIV TORONTO", new Color(0, 204, 0));
		locationsMap.put("N/A", new Color(153, 153, 153));
		colorAttrMap.put(nodeattr_location, locationsMap);
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_FILL_COLOR, 
			                              new Object[] {colorAttrMap});
		// Specify NODE_SHAPE
		Map<String, HashMap<String, NodeShape>> shapeAttrMap = new HashMap<String, HashMap<String, NodeShape>>();
		HashMap<String, NodeShape> departmentMap = new HashMap<String, NodeShape>();
		departmentMap.put((String) (socialNetwork.getAttrMap().get(nodeattr_dept)), NodeShapeVisualProperty.TRIANGLE);
		departmentMap.put("N/A", NodeShapeVisualProperty.RECTANGLE);
		shapeAttrMap.put(nodeattr_dept, departmentMap);
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SHAPE, 
	            new Object[] {shapeAttrMap});
		
		//Specify Node_border_width and Node_Border_fill for faculty memebers (i.e. department)
		Map<String, HashMap<String, Color>> borderpainAttrMap = new HashMap<String, HashMap<String, Color>>();
		HashMap<String, Color> departmentMap_borderpaint = new HashMap<String, Color>();
		departmentMap_borderpaint.put((String) (socialNetwork.getAttrMap().get(nodeattr_dept)), new Color(243,243,21));
		borderpainAttrMap.put(nodeattr_dept, departmentMap_borderpaint);
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_BORDER_PAINT, 
	            new Object[] {borderpainAttrMap});

		Map<String, HashMap<String, Integer>> borderwidthAttrMap = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String,Integer> departmentMap_borderwidth = new HashMap<String, Integer>();
		departmentMap_borderwidth.put((String) (socialNetwork.getAttrMap().get(nodeattr_dept)), 10);
		borderwidthAttrMap.put(nodeattr_dept, departmentMap_borderwidth);
		socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_BORDER_WIDTH, 
			            new Object[] {borderwidthAttrMap});
		
		
	}
	
		
}
