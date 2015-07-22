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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.AbstractNode;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NetworkAttribute;
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

    private int currentSteps = 0;
    private CyLayoutAlgorithmManager cyLayoutManagerServiceRef;
    private CyNetworkFactory cyNetworkFactoryServiceRef;
    private CyNetworkManager cyNetworkManagerServiceRef;
    private CyNetworkNaming cyNetworkNamingServiceRef;
    private CyNetworkViewFactory cyNetworkViewFactoryServiceRef;
    private CyNetworkViewManager cyNetworkViewManagerServiceRef;
    private SocialNetworkAppManager appManager;
    private double progress = 0.0;
    private int totalSteps = 0;

    /**
     *
     * Create a new <i>CreateNetworkTask</i> object
     *
     * @param {@link CyNetworkNaming} cyNetworkNamingServiceRef
     * @param {@link CyNetworkFactory} cyNetworkFactoryServiceRef
     * @param {@link CyNetworkManager} cyNetworkManagerServiceRef
     * @param {@link CyNetworkViewFactory} cyNetworkViewFactoryServiceRef
     * @param {@link CyNetworkViewManager} cyNetworkViewManagerServiceRef
     * @param {@link CyLayoutAlgorithmManager} cyLayoutManagerServiceRef
     * @param {@link SocialNetworkAppManager} appManager
     */
    public CreateNetworkTask(CyNetworkNaming cyNetworkNamingServiceRef, CyNetworkFactory cyNetworkFactoryServiceRef,
            CyNetworkManager cyNetworkManagerServiceRef, CyNetworkViewFactory cyNetworkViewFactoryServiceRef,
            CyNetworkViewManager cyNetworkViewManagerServiceRef, CyLayoutAlgorithmManager cyLayoutManagerServiceRef,
            SocialNetworkAppManager appManager) {
        this.cyNetworkNamingServiceRef = cyNetworkNamingServiceRef;
        this.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
        this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
        this.cyNetworkViewFactoryServiceRef = cyNetworkViewFactoryServiceRef;
        this.cyNetworkViewManagerServiceRef = cyNetworkViewManagerServiceRef;
        this.cyLayoutManagerServiceRef = cyLayoutManagerServiceRef;
        this.appManager = appManager;
    }

    /**
     * Return a network containing node's siblings and all corresponding edges
     *
     * @param Map map
     * @return CyNetwork network
     */
    public CyNetwork loadNetwork(Map<Collaboration, ArrayList<AbstractEdge>> map, TaskMonitor taskMonitor) {
        try {
            // Create an empty network
            CyNetwork myNet = this.cyNetworkFactoryServiceRef.createNetwork();

            // Get network node table
            CyTable nodeTable = null;
            // Get network edge table
            CyTable edgeTable = null;
            // get network table
            CyTable networkTable = null;

            // Add all columns to node table
            nodeTable = myNet.getDefaultNodeTable();
            Object[] keys = map.keySet().toArray();
            AbstractNode key = ((Collaboration) keys[0]).getNode1();
            for (Entry<String, Object> attr : key.getNodeAttrMap().entrySet()) {
                String attrName = attr.getKey();
                Object attrType = attr.getValue();
                if (attrType instanceof String) {
                    nodeTable.createColumn(attrName, String.class, false);
                } else if (attrType instanceof Integer) {
                    nodeTable.createColumn(attrName, Integer.class, false);
                } else if (attrType instanceof List) {
                    if (attrName.equals("Yearly Publications")) { // TODO: Find a better solution
                        nodeTable.createListColumn(attrName, Integer.class, false);
                    } else {
                        nodeTable.createListColumn(attrName, String.class, false);                        
                    }
                }
            }

            // Add all columns to edge table
            edgeTable = myNet.getDefaultEdgeTable();
            Object[] values = map.values().toArray();
            @SuppressWarnings("unchecked")
            ArrayList<AbstractEdge> values2 = (ArrayList<AbstractEdge>) values[0];
            AbstractEdge value = values2.get(0);
            for (Entry<String, Object> attr : value.getEdgeAttrMap().entrySet()) {
                String attrName = attr.getKey();
                Object attrType = attr.getValue();
                if (attrType instanceof String) {
                    edgeTable.createColumn(attrName, String.class, false);
                } else if (attrType instanceof Integer) {
                    edgeTable.createColumn(attrName, Integer.class, false);
                } else if (attrType instanceof List) {
                    edgeTable.createListColumn(attrName, String.class, false);
                }
            }

            // TODO: This code shouldn't be here. It will make it difficult to expand the app's functionality
            // to other types of social networks (not just academia).
            
            // Add all columns to network table
            networkTable = myNet.getDefaultNetworkTable();
            networkTable.createColumn(NetworkAttribute.TotalPublications.toString(), Integer.class, false);
            networkTable.createColumn(NetworkAttribute.TotalFaculty.toString(), Integer.class, false);
            networkTable.createColumn(NetworkAttribute.TotalUnidentifiedFaculty.toString(), Integer.class, false);
            networkTable.createColumn(NetworkAttribute.ListUnidentifiedFaculty.toString(), String.class, false);

            // Set network name
            myNet.getDefaultNetworkTable().getRow(myNet.getSUID())
                    .set("name", this.cyNetworkNamingServiceRef.getSuggestedNetworkTitle(this.appManager.getNetworkName()));

            // Add network attributes
            myNet.getDefaultNetworkTable()
                    .getRow(myNet.getSUID())
                    .set(NetworkAttribute.TotalPublications.toString(),
                            this.appManager.getSocialNetwork(this.appManager.getNetworkName()).getNum_publications());
            myNet.getDefaultNetworkTable().getRow(myNet.getSUID())
                    .set(NetworkAttribute.TotalFaculty.toString(), this.appManager.getSocialNetwork(this.appManager.getNetworkName()).getNum_faculty());
            myNet.getDefaultNetworkTable()
                    .getRow(myNet.getSUID())
                    .set(NetworkAttribute.TotalUnidentifiedFaculty.toString(),
                            this.appManager.getSocialNetwork(this.appManager.getNetworkName()).getNum_uniden_faculty());
            myNet.getDefaultNetworkTable()
                    .getRow(myNet.getSUID())
                    .set(NetworkAttribute.TotalPublications.toString(),
                            this.appManager.getSocialNetwork(this.appManager.getNetworkName()).getNum_publications());

            // Build network
            Collaboration collaboration = null;
            AbstractNode node1 = null;
            AbstractNode node2 = null;
            List<? extends AbstractEdge> edgeArray = null;
            CyEdge edgeRef = null;
            CyNode nodeRef = null;

            Map<AbstractNode, CyNode> nodeMap = new HashMap<AbstractNode, CyNode>();

            // Set 'Load Network' progress monitor
            this.setProgressMonitor(taskMonitor, "Loading Network View ...", map.size());

            // Get all collaborations and their corresponding edges
            for (Entry<Collaboration, ArrayList<AbstractEdge>> entry : map.entrySet()) {
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
                    for (Entry<String, Object> attr : node1.getNodeAttrMap().entrySet()) {
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
                    for (Entry<String, Object> attr : node2.getNodeAttrMap().entrySet()) {
                        nodeTable.getRow(nodeRef.getSUID()).set(attr.getKey(), attr.getValue());
                    }
                    nodeTable.getRow(nodeRef.getSUID()).set(CyNetwork.NAME, node2.getLabel());
                    nodeTable.getRow(nodeRef.getSUID()).set(CyRootNetwork.SHARED_NAME, node2.getLabel());

                    nodeMap.put(node2, nodeRef);
                }

                for (AbstractEdge edge : edgeArray) {
                    edgeRef = myNet.addEdge(nodeMap.get(node1), nodeMap.get(node2), false);
                    edge.setCyEdge(edgeRef);
                    // Add each edge attribute to it's respective column
                    for (Entry<String, Object> attr : edge.getEdgeAttrMap().entrySet()) {
                        edgeTable.getRow(edgeRef.getSUID()).set(attr.getKey(), attr.getValue());
                    }
                    edgeTable.getRow(edgeRef.getSUID()).set(CyNetwork.NAME, node1.getLabel() + "_" + node2.getLabel());

                }

                updateProgress(taskMonitor);

            }

            return myNet;
        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
            this.appManager.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            CytoscapeUtilities.notifyUser("Network could not be loaded. Array Index Out Of Bounds.");
            this.appManager.getSocialNetworkMap().remove(this.appManager.getNetworkName());
            this.appManager.setNetworkName(null);
            taskMonitor = null;
            return null;
        }
    }

    /**
     * Run network task
     *
     * @param TaskMonitor taskMonitor
     */
    @Override
    public void run(TaskMonitor monitor) throws Exception {
        CyNetworkManager networkManager = this.cyNetworkManagerServiceRef;
        CyNetworkViewManager networkViewManager = this.cyNetworkViewManagerServiceRef;

        // Get map
        Map<Collaboration, ArrayList<AbstractEdge>> map = this.appManager.getMap();

        if (map == null) {
            CytoscapeUtilities.notifyUser("Network could not be loaded. Cytoscape network map could not be accessed.");
        } else {

            // Load network
            CyNetwork network = loadNetwork(map, monitor);

            if (network == null) {
                return;
            }

            this.setProgressMonitor(monitor, "Loading Nework ...", -1);

            networkManager.addNetwork(network);

            final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(network);

            CyNetworkView networkView = null;
            if (views.size() != 0) {
                networkView = views.iterator().next();
            }
            if (networkView == null) {
                // Create a new view for my network
                networkView = this.cyNetworkViewFactoryServiceRef.createNetworkView(network);
                networkViewManager.addNetworkView(networkView); // TODO:
                                                                // NullPointerException
            } else {
                CytoscapeUtilities.notifyUser("Network already present");
            }

            // Set the variable destroyView to true, the following snippet of
            // code
            // will destroy a view
            boolean destroyView = false;
            if (destroyView) {
                networkViewManager.destroyNetworkView(networkView);
            }

            SocialNetwork socialNetwork = this.appManager.getSocialNetworkMap().get(this.appManager.getNetworkName());
            socialNetwork.setNetworkView(networkView);

            // Auto apply visual style and layout
            CyLayoutAlgorithm layout = this.cyLayoutManagerServiceRef.getLayout("force-directed");
            String layoutAttribute = null;
            TaskIterator layoutTaskIterator = layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS,
                    layoutAttribute);

            TaskIterator taskIterator = new TaskIterator();
            taskIterator.append(layoutTaskIterator);
            int visualStyleID = this.appManager.getCurrentlySelectedSocialNetwork().getDefaultVisualStyle();
            this.appManager.setVisualStyleID(visualStyleID);
            this.appManager.getUserPanelRef().setSelectedVisualStyle(visualStyleID);
            taskIterator.append(this.appManager.getApplyVisualStyleTaskFactoryRef().createTaskIterator());
            insertTasksAfterCurrentTask(taskIterator);

        }
    }

    /**
     * Set progress monitor
     *
     * @param TaskMonitor taskMonitor
     * @param String taskName
     * @param int totalSteps
     */
    private void setProgressMonitor(TaskMonitor taskMonitor, String taskName, int totalSteps) {
        taskMonitor.setTitle(taskName);
        taskMonitor.setProgress(0.0);
        this.currentSteps = 0;
        this.totalSteps = totalSteps;
    }

    /**
     * Return progress as a percentage
     *
     * @param Double progress
     * @return String percentage
     */
    private String toPercent(double progress) {
        progress = progress * 100;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(progress) + "%";
    }

    /**
     * Update progress monitor
     *
     * @param int currentSteps
     */
    private void updateProgress(TaskMonitor taskMonitor) {
        this.currentSteps += 1;
        this.progress = (double) this.currentSteps / this.totalSteps;
        taskMonitor.setStatusMessage("Complete: " + toPercent(this.progress));
        taskMonitor.setProgress(this.progress);
    }

}