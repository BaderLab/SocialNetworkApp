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

package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Cursor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.AbstractNode;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.EdgeAttribute;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NetworkAttribute;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Extends AbstractTask and is used to generate networks
 *
 * @author Victor Kofia
 */
public class CreateNetworkTask extends AbstractTask {

	private int currentSteps;
	private double progress;
	private int totalSteps;

	private final CyLayoutAlgorithmManager layoutManager;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkManager networkManager;
	private final CyNetworkNaming networkNaming;
	private final CyNetworkViewFactory networkViewFactory;
	private final CyNetworkViewManager networkViewManager;
	private final SocialNetworkAppManager appManager;

	public CreateNetworkTask(
			CyNetworkNaming networkNaming,
			CyNetworkFactory networkFactory,
			CyNetworkManager networkManager,
			CyNetworkViewFactory networkViewFactory,
			CyNetworkViewManager networkViewManager,
			CyLayoutAlgorithmManager layoutManager,
			SocialNetworkAppManager appManager
	) {
		this.networkNaming = networkNaming;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.networkViewFactory = networkViewFactory;
		this.networkViewManager = networkViewManager;
		this.layoutManager = layoutManager;
		this.appManager = appManager;
	}

    /**
     * Return a network containing node's siblings and all corresponding edges
     */
    @SuppressWarnings("unchecked")
    public CyNetwork loadNetwork(Map<Collaboration, ArrayList<AbstractEdge>> map, TaskMonitor tm) {
        try {
            // Create an empty network
            var myNet = networkFactory.createNetwork();

            // Get network node table
            CyTable nodeTable = null;
            // Get network edge table
            CyTable edgeTable = null;
            // get network table
            CyTable networkTable = null;

            // Add all columns to node table
            nodeTable = myNet.getDefaultNodeTable();
            var keys = map.keySet().toArray();
            var key = ((Collaboration) keys[0]).getNode1();
            
            for (var attr : key.getNodeAttrMap().entrySet()) {
            	var attrName = attr.getKey();
            	var attrType = attr.getValue();
                
            	if (attrType instanceof String) {
                    nodeTable.createColumn(attrName, String.class, false);
                } else if (attrType instanceof Integer) {
                    nodeTable.createColumn(attrName, Integer.class, false);
                } else if (attrType instanceof Double) {
                    nodeTable.createColumn(attrName, Double.class, false);
                } else if (attrType instanceof Boolean) {
                    nodeTable.createColumn(attrName, Boolean.class, false);
                } else if (attrType instanceof List) {
                    // TODO: Find a better way to handle lists that have integer values
                    if (attrName.equals(NodeAttribute.PUBS_PER_YEAR.toString()) ||
                            attrName.equals(NodeAttribute.YEARS_ACTIVE.toString()) ||
                            attrName.equals(NodeAttribute.CITATIONS_PER_YEAR.toString())) { 
                        nodeTable.createListColumn(attrName, Integer.class, false);
                    } else {
                        nodeTable.createListColumn(attrName, String.class, false);                        
                    }
                }
            }

            // Add all columns to edge table
            edgeTable = myNet.getDefaultEdgeTable();
            var values = map.values().toArray();
            var values2 = (ArrayList<AbstractEdge>) values[0];
            var value = values2.get(0);
            
            for (var attr : value.getEdgeAttrMap().entrySet()) {
            	var attrName = attr.getKey();
            	var attrType = attr.getValue();
                
            	if (attrType instanceof String) {
                    edgeTable.createColumn(attrName, String.class, false);
                } else if (attrType instanceof Integer) {
                    edgeTable.createColumn(attrName, Integer.class, false);
                } else if (attrType instanceof Double) {
                    nodeTable.createColumn(attrName, Double.class, false);
                } else if (attrType instanceof Boolean) {
                    edgeTable.createColumn(attrName, Boolean.class, false);
                } else if (attrType instanceof List) {
                    // TODO: Find a better way to handle list attributes
                    if (attrName.equals(EdgeAttribute.PUBS_PER_YEAR.toString())) {
                        edgeTable.createListColumn(attrName, Integer.class, false);
                    } else {
                        edgeTable.createListColumn(attrName, String.class, false);                        
                    }
                }
            }

            // TODO: This code shouldn't be here. It will make it difficult to expand the app's functionality
            // to other types of social networks (not just academia).
            
            // Add all columns to network table
            networkTable = myNet.getDefaultNetworkTable();
            networkTable.createColumn(NetworkAttribute.TOTAL_PUBLICATIONS.toString(), Integer.class, false);
            networkTable.createColumn(NetworkAttribute.TOTAL_FACULTY.toString(), Integer.class, false);
            networkTable.createColumn(NetworkAttribute.TOTAL_UNIDENTIFIED_FACULTY.toString(), Integer.class, false);
            networkTable.createColumn(NetworkAttribute.UNIDENTIFIED_FACULTY_LIST.toString(), String.class, false);

            // Set network name
            myNet.getDefaultNetworkTable().getRow(myNet.getSUID())
                    .set("name", networkNaming.getSuggestedNetworkTitle(appManager.getNetworkName()));

            // TODO:
            // Add network attributes
            myNet.getDefaultNetworkTable()
                    .getRow(myNet.getSUID())
                    .set(NetworkAttribute.TOTAL_PUBLICATIONS.toString(),
                            appManager.getSocialNetwork(appManager.getNetworkName()).getNum_publications());
            myNet.getDefaultNetworkTable().getRow(myNet.getSUID())
                    .set(NetworkAttribute.TOTAL_FACULTY.toString(), appManager.getSocialNetwork(appManager.getNetworkName()).getNum_faculty());
            myNet.getDefaultNetworkTable()
                    .getRow(myNet.getSUID())
                    .set(NetworkAttribute.TOTAL_UNIDENTIFIED_FACULTY.toString(),
                            appManager.getSocialNetwork(appManager.getNetworkName()).getNum_uniden_faculty());
            myNet.getDefaultNetworkTable()
                    .getRow(myNet.getSUID())
                    .set(NetworkAttribute.TOTAL_PUBLICATIONS.toString(),
                            appManager.getSocialNetwork(appManager.getNetworkName()).getNum_publications());

            // Build network
            Collaboration collaboration = null;
            AbstractNode node1 = null;
            AbstractNode node2 = null;
            List<? extends AbstractEdge> edgeArray = null;
            CyEdge edgeRef = null;
            CyNode nodeRef = null;

            var nodeMap = new HashMap<AbstractNode, CyNode>();

            // Set 'Load Network' progress monitor
            setProgressMonitor(tm, "Loading Network View...", map.size());

            // Get all collaborations and their corresponding edges
            for (var entry : map.entrySet()) {
                collaboration = entry.getKey();
                edgeArray = entry.getValue();

                // Get nodes
                node1 = collaboration.getNode1();
                node2 = collaboration.getNode2();

                // Check for nodes in nodeMap
                if (!nodeMap.containsKey(node1)) {
                    nodeRef = myNet.addNode();
                    node1.setCyNode(nodeRef);
                    // Add each node attribute to its respective column
                    for (var attr : node1.getNodeAttrMap().entrySet()) {
                        nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), attr.getValue());
                    }
                    // add node label as name and shared name
                    nodeTable.getRow(nodeRef.getSUID()).set(CyNetwork.NAME, node1.getLabel());
                    nodeTable.getRow(nodeRef.getSUID()).set(CyRootNetwork.SHARED_NAME, node1.getLabel());
                    nodeMap.put(node1, nodeRef);
                }

                if (!nodeMap.containsKey(node2)) {
                    nodeRef = myNet.addNode();
                    node2.setCyNode(nodeRef);
                    // Add each node attribute to its respective column
                    for (var attr : node2.getNodeAttrMap().entrySet()) {
                        nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), attr.getValue());
                    }
                    nodeTable.getRow(nodeRef.getSUID()).set(CyNetwork.NAME, node2.getLabel());
                    nodeTable.getRow(nodeRef.getSUID()).set(CyRootNetwork.SHARED_NAME, node2.getLabel());

                    nodeMap.put(node2, nodeRef);
                }

                for (var edge : edgeArray) {
                    edgeRef = myNet.addEdge(nodeMap.get(node1), nodeMap.get(node2), false);
                    edge.setCyEdge(edgeRef);
                    // Add each edge attribute to it's respective column
                    for (var attr : edge.getEdgeAttrMap().entrySet()) {
                        edgeTable.getRow(edgeRef.getSUID()).set(attr.getKey(), attr.getValue());
                    }
                    edgeTable.getRow(edgeRef.getSUID()).set(CyNetwork.NAME, node1.getLabel() + "_" + node2.getLabel());

                }

                updateProgress(tm);
            }

            return myNet;
        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
            appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network could not be loaded. Array Index Out Of Bounds.");
            appManager.getSocialNetworkMap().remove(appManager.getNetworkName());
            appManager.setNetworkName(null);
            tm = null;
            return null;
        }
    }

    /**
     * Run network task
     *
     * @param TaskMonitor taskMonitor
     */
    @Override
    public void run(TaskMonitor tm) throws Exception {
    	tm.setTitle("Create Social Network");
    	
        // Get map
        var map = appManager.getMap();

        if (map == null) {
            CytoscapeUtilities.notifyUser("Network could not be loaded. Cytoscape network map could not be accessed.");
        } else {
			// Load network
			var network = loadNetwork(map, tm);

			if (network == null)
				return;

			setProgressMonitor(tm, "Loading Network...", -1);

			networkManager.addNetwork(network);

			var views = networkViewManager.getNetworkViews(network);

			CyNetworkView networkView = null;

			if (views.size() != 0)
				networkView = views.iterator().next();

			if (networkView == null) {
				// Create a new view for my network
				networkView = networkViewFactory.createNetworkView(network);
				networkViewManager.addNetworkView(networkView); // TODO: NullPointerException
			} else {
				CytoscapeUtilities.notifyUser("Network already present");
			}

			// Set the variable destroyView to true, the following snippet of code will destroy a view
			boolean destroyView = false;

			if (destroyView)
				networkViewManager.destroyNetworkView(networkView);

			var socialNetwork = appManager.getSocialNetworkMap().get(appManager.getNetworkName());
			socialNetwork.setNetworkView(networkView);

			// Auto apply visual style and layout
			var layout = layoutManager.getLayout("force-directed");
			String layoutAttribute = null;
			var layoutTaskIterator = layout.createTaskIterator(networkView, layout.createLayoutContext(),
					CyLayoutAlgorithm.ALL_NODE_VIEWS, layoutAttribute);

			var taskIterator = new TaskIterator();
			taskIterator.append(layoutTaskIterator);
			int visualStyleID = socialNetwork.getDefaultVisualStyle();
			// int visualStyleID =
			// appManager.getCurrentlySelectedSocialNetwork().getDefaultVisualStyle();
			appManager.setVisualStyleID(visualStyleID);
			taskIterator.append(appManager.getApplyVisualStyleTaskFactoryRef().createTaskIterator());
			insertTasksAfterCurrentTask(taskIterator);
        }
    }

    private void setProgressMonitor(TaskMonitor tm, String taskName, int totalSteps) {
        tm.setStatusMessage(taskName);
        tm.setProgress(0.0);
        this.currentSteps = 0;
        this.totalSteps = totalSteps;
    }

    /**
     * Return progress as a percentage
     */
    private String toPercent(double progress) {
        progress = progress * 100;
        var df = new DecimalFormat("00");
        
        return df.format(progress) + "%";
    }

    private void updateProgress(TaskMonitor tm) {
        currentSteps += 1;
        progress = (double) currentSteps / totalSteps;
        tm.setStatusMessage("Complete: " + toPercent(progress));
        tm.setProgress(progress);
    }
}