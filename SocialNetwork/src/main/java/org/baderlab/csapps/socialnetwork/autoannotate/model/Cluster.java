package org.baderlab.csapps.socialnetwork.autoannotate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.cytoscape.group.CyGroup;
import org.cytoscape.model.CyNode;
import org.cytoscape.session.CySession;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.annotations.ShapeAnnotation;
import org.cytoscape.view.presentation.annotations.TextAnnotation;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Created by:
 * @author arkadyark
 * <p>
 * Date   Jun 18, 2014<br>
 * Time   12:47 PM<br>
 * <p>
 * Class to store the relevant sets of data corresponding to each cluster
 */

public class Cluster implements Comparable<Cluster> {

    private int clusterNumber;
    private String cloudName;
    private CyGroup group;
    private HashMap<CyNode, double[]> nodesToCoordinates;
    private int size = 0;
    private String label;
    private TextAnnotation textAnnotation;
    private ShapeAnnotation ellipse;
    private AnnotationSet parent;
    private boolean selected = false;
    private ArrayList<WordInfo> wordInfos;
    private HashMap<CyNode, Double> nodesToRadii;
    private HashMap<CyNode, Double> nodesToCentralities;
    private String mostCentralNodeLabel;
    private boolean coordinatesChanged = true;
    private double[] bounds = new double[4];

    private Set<CyNode> nodes;

    // Used when initializing from a session file
    public Cluster() {
        this.wordInfos = new ArrayList<WordInfo>();
        this.nodesToCoordinates = new HashMap<CyNode, double[]>();
        this.nodesToRadii = new HashMap<CyNode, Double>();
        this.nodesToCentralities = new HashMap<CyNode, Double>();
        this.nodes = new HashSet<CyNode> ();
    }

    public Cluster(int clusterNumber, AnnotationSet parent) {
        this.clusterNumber = clusterNumber;
        this.parent = parent;
        this.cloudName = parent.getName() + " Cloud " + clusterNumber;
        this.wordInfos = new ArrayList<WordInfo>();
        this.nodesToCoordinates = new HashMap<CyNode, double[]>();
        this.nodesToRadii = new HashMap<CyNode, Double>();
        this.nodesToCentralities = new HashMap<CyNode, Double>();
        this.nodes = new HashSet<CyNode> ();
    }

    // Used when creating clusters in the task
    public Cluster(int clusterNumber, AnnotationSet parent, CyGroup group) {
        this(clusterNumber, parent);
        this.group = group;
    }

    public void addNodeCoordinates(CyNode node, double[] coordinates) {
        if (!this.nodesToCoordinates.containsKey(node)) {
            this.size++;
            if (this.group != null && this.group.getGroupNode() != node) {
                ArrayList<CyNode> nodeToAdd = new ArrayList<CyNode>();
                nodeToAdd.add(node);
                this.group.addNodes(nodeToAdd);
            }

            this.nodes.add(node);
            this.nodesToCoordinates.put(node, coordinates);
        }
        else if(coordinatesHaveChanged(this.nodesToCoordinates.get(node),coordinates)){
            this.nodesToCoordinates.put(node, coordinates);
            this.coordinatesChanged = true;
        }
    }

    public void addNodeRadius(CyNode node, double radius) {
        this.nodesToRadii.put(node, radius);
    }

    public int compareTo(Cluster cluster2) {
        return this.label.compareTo(cluster2.getLabel());
    }

    public boolean coordinatesChanged() {
        return this.coordinatesChanged ;
    }

    private boolean coordinatesHaveChanged(double[] oldCoordinates, double[] newCoordinates) {
        return (oldCoordinates == null ||
                (Math.abs(oldCoordinates[0] - newCoordinates[0]) > 0.01 &&
                        Math.abs(oldCoordinates[1] - newCoordinates[1]) > 0.01));
    }

    public void erase() {
        eraseText();
        eraseEllipse();
    }

    public void eraseEllipse() {
        if(this.ellipse!= null) {
            this.ellipse.removeAnnotation();
        }
    }

    public void eraseText() {
        if(this.textAnnotation !=null) {
            this.textAnnotation.removeAnnotation();
        }
    }

    public double[] getBounds() {
        //Only updates the bounds of cluster where the coordinates have changed.
        if (this.coordinatesChanged) {
            // Find the edges of the cluster
            double xmin = 100000000;
            double ymin = 100000000;
            double xmax = -100000000;
            double ymax = -100000000;
            for (double[] coordinates : this.nodesToCoordinates.values()) {
                xmin = coordinates[0] < xmin ? coordinates[0] : xmin;
                xmax = coordinates[0] > xmax ? coordinates[0] : xmax;
                ymin = coordinates[1] < ymin ? coordinates[1] : ymin;
                ymax = coordinates[1] > ymax ? coordinates[1] : ymax;
            }
            this.bounds[0] = xmin;
            this.bounds[1] = xmax;
            this.bounds[2] = ymin;
            this.bounds[3] = ymax;

            this.coordinatesChanged = false;
        }
        return this.bounds;
    }

    public String getCloudName() {
        return this.cloudName;
    }

    public int getClusterNumber() {
        return this.clusterNumber;
    }

    public ShapeAnnotation getEllipse() {
        return this.ellipse;
    }

    public CyGroup getGroup() {
        return this.group;
    }

    public CyNode getGroupNode() {
        if (this.group != null) {
            return this.group.getGroupNode();
        }
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    public String getMostCentralNodeLabel() {
        if (this.mostCentralNodeLabel == null) {
            CyNode maxNode = this.nodesToCoordinates.keySet().iterator().next();
            double maxCentrality = -1;
            for (CyNode node : this.nodesToCentralities.keySet()) {
                double centrality = this.nodesToCentralities.get(node);
                if (centrality > maxCentrality) {
                    maxNode = node;
                    maxCentrality = centrality;
                }
            }
            String columnName = this.parent.getNameColumnName();
            Class<? extends Object> unknownClass = this.parent.getView().getModel().getRow(maxNode).getAllValues().get(columnName).getClass();
            // Construct a label out of a list by concatenating the list elements
            if (unknownClass.getCanonicalName().equals("org.cytoscape.model.internal.CyListImpl")) {
                List<?> list = this.parent.getView().getModel().getRow(maxNode).get(columnName, List.class);
                StringBuffer buffer = new StringBuffer();
                for (Object s : list) {
                    buffer.append(s.toString());
                    buffer.append(" ");
                }
                this.mostCentralNodeLabel = buffer.toString();
            } else if (unknownClass.getCanonicalName().equals("java.lang.String")) {
                this.mostCentralNodeLabel = this.parent.getView().getModel().getRow(maxNode).get(columnName, String.class);
            } else if (unknownClass.getCanonicalName().equals("java.lang.Boolean")) {
                Boolean unknownBoolean = this.parent.getView().getModel().getRow(maxNode).get(columnName, Boolean.class);
                this.mostCentralNodeLabel = unknownBoolean.toString();
            } else if (unknownClass.getCanonicalName().equals("java.lang.Float")) {
                Float unknownFloat = this.parent.getView().getModel().getRow(maxNode).get(columnName, Float.class);
                this.mostCentralNodeLabel = unknownFloat.toString();
            } else if (unknownClass.getCanonicalName().equals("java.lang.Integer")) {
                Integer unknownInt = this.parent.getView().getModel().getRow(maxNode).get(columnName, Integer.class);
                this.mostCentralNodeLabel = unknownInt.toString();
            } else if (unknownClass.getCanonicalName().equals("java.lang.Long")) {
                Long unknownLong = this.parent.getView().getModel().getRow(maxNode).get(columnName, Long.class);
                this.mostCentralNodeLabel = unknownLong.toString();
            }
        }
        return this.mostCentralNodeLabel;
    }

    public Set<CyNode> getNodes() {
        return this.nodes;
    }

    public HashMap<CyNode, Double> getNodesToCentralities() {
        return this.nodesToCentralities;
    }

    public HashMap<CyNode, double[]> getNodesToCoordinates() {
        return this.nodesToCoordinates;
    }

    public HashMap<CyNode, Double> getNodesToRadii() {
        return this.nodesToRadii;
    }

    public AnnotationSet getParent() {
        return this.parent;
    }

    public int getSize() {
        if(this.nodes != null) {
            return this.nodes.size();
        }
        return 0;
    }

    public TextAnnotation getTextAnnotation() {
        return this.textAnnotation;
    }

    public ArrayList<WordInfo> getWordInfos() {
        return this.wordInfos;
    }

    public boolean isCollapsed() {
        if (this.group != null) {
            return this.group.isCollapsed(this.parent.getView().getModel());
        } return false;
    }

    public boolean isSelected() {
        return this.selected;
    }

    // Connects strings together, separated by separator
    private String join(List<WordInfo> stringList, String separator) {
        String joined = "";
        for (int index = 0; index < stringList.size(); index++) {
            if (index == 0) {
                joined += stringList.get(index).getWord();
            } else {
                joined += separator + stringList.get(index).getWord();
            }
        }
        return joined;
    }

    public void load(ArrayList<String> text, CySession session) {
        this.clusterNumber = Integer.valueOf(text.get(0));
        this.cloudName = this.parent.getName() + " Cloud " + this.clusterNumber;
        this.label = text.get(1);
        this.selected = Boolean.valueOf(text.get(2));

        // Load the parameters of the annotations
        int lineNumber = 3;
        String line = text.get(lineNumber);
        Map<String, String> ellipseMap = new HashMap<String, String>();
        while (!line.equals("Text Annotations")) {
            String[] splitLine = line.split("\t");
            ellipseMap.put(splitLine[0], splitLine[1]);
            lineNumber++;
            line = text.get(lineNumber);
        }
        this.ellipse = AutoAnnotationManager.getInstance().getShapeFactory().createAnnotation(ShapeAnnotation.class,
                this.parent.getView(), ellipseMap);
        lineNumber++;
        line = text.get(lineNumber);

        Map<String, String> textMap = new HashMap<String, String>();
        while (!line.equals("End of annotations")) {
            String[] splitLine = line.split("\t");
            textMap.put(splitLine[0], splitLine[1]);
            lineNumber++;
            line = text.get(lineNumber);
        }
        this.textAnnotation = AutoAnnotationManager.getInstance().getTextFactory().createAnnotation(TextAnnotation.class,
                this.parent.getView(), textMap);
        lineNumber++;
        line = text.get(lineNumber);

        // Reload nodes
        while (!line.equals("End of nodes")) {
            String[] splitLine = line.split("\t");
            CyNode node = session.getObject(Long.valueOf(splitLine[0]), CyNode.class);
            double[] nodeCoordinates = {Double.valueOf(splitLine[1]), Double.valueOf(splitLine[2])};
            addNodeCoordinates(node, nodeCoordinates);
            double nodeRadius = Double.valueOf(splitLine[3]);
            addNodeRadius(node, nodeRadius);
            double nodeCentrality = Double.valueOf(splitLine[4]);
            this.nodesToCentralities.put(node, nodeCentrality);
            lineNumber++;
            line = text.get(lineNumber);
        }
    }

    public String makeLabel(ArrayList<WordInfo> wordInfos, String mostCentralNodeLabel,
            double sameClusterBonus, double centralityBonus, List<Integer> wordSizeThresholds,
            int maxWords) {
        // Work with a copy so as to not mess up the order for comparisons
        ArrayList<WordInfo> wordInfosCopy = new ArrayList<WordInfo>();
        for (WordInfo wordInfo : wordInfos) {
            wordInfosCopy.add(wordInfo.clone());
        }
        // Empty WordClouds are given an empty label
        if (wordInfosCopy.size() == 0) {
            return "";
        }
        // Sorts by size descending
        Collections.sort(wordInfosCopy);
        // Gets the biggest word in the cloud
        WordInfo biggestWord = wordInfosCopy.get(0);
        ArrayList<WordInfo> label = new ArrayList<WordInfo>();
        label.add(biggestWord);
        int numWords = 1;
        WordInfo nextWord = biggestWord;
        wordInfosCopy.remove(0);
        for (WordInfo word : wordInfosCopy) {
            if (mostCentralNodeLabel.toLowerCase().contains(word.getWord())) {
                word.setSize(word.getSize() + centralityBonus);
            }
        }
        while (numWords < maxWords && wordInfosCopy.size() > 0) {
            for (WordInfo word : wordInfosCopy) {
                if (word.getCluster() == nextWord.getCluster()) {
                    word.setSize(word.getSize() + sameClusterBonus);
                }
            }
            Collections.sort(wordInfosCopy); // Sizes have changed, re-sort
            double wordSizeThreshold = nextWord.getSize()*wordSizeThresholds.get(numWords - 1)/100.0;
            nextWord = wordInfosCopy.get(0);
            wordInfosCopy.remove(0);
            if (nextWord.getSize() > wordSizeThreshold) {
                label.add(nextWord);
                numWords++;
            } else {
                break;
            }
        }
        // Sort first by size, then by WordCloud 'number', tries to preserve original word order
        Collections.sort(label, WordInfoNumberComparator.getInstance());
        // Create a string label from the word infos
        return join(label, " ");
    }

    public void removeGroup() {
        this.group = null;
    }

    public void removeNode(CyNode nodeToRemove) {
        if (this.nodesToCoordinates.containsKey(nodeToRemove) || this.nodes.contains(nodeToRemove) || this.nodesToRadii.containsKey(nodeToRemove)) {
            if (this.nodes.contains(nodeToRemove) ) {
                this.nodes.remove(nodeToRemove);
            }
            if (this.nodesToCoordinates.containsKey(nodeToRemove)) {
                this.nodesToCoordinates.remove(nodeToRemove);
            }
            if(this.nodesToRadii.containsKey(nodeToRemove)) {
                this.nodesToRadii.remove(nodeToRemove);
            }
            //if there is a group defined remove the node from the group
            if (this.group != null) {
                //group remove expects a list of nodes so put the solitary node into a list.
                ArrayList<CyNode> node = new ArrayList<CyNode>();
                node.add(nodeToRemove);
                this.group.removeNodes(new ArrayList<CyNode>(node));
            }
            this.size--;
        }
    }

    public void setCoordinatesChanged(boolean b) {
        this.coordinatesChanged = b;
    }

    public void setEllipse(ShapeAnnotation ellipse) {
        this.ellipse = ellipse;
    }

    public void setGroup(CyGroup group) {
        this.group = group;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setNodesToCentralities(HashMap<CyNode, Double> nodesToCentralities) {
        this.nodesToCentralities = nodesToCentralities;
    }

    public void setParent(AnnotationSet annotationSet) {
        this.parent = annotationSet;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTextAnnotation(TextAnnotation textAnnotation) {
        this.textAnnotation = textAnnotation;
    }


    public void setWordInfos(ArrayList<WordInfo> wordInfos) {
        this.wordInfos = wordInfos;
    }

    public void swallow(Cluster cluster2) {
        // Add all of the nodes and coordinates from the second cluster
        HashMap<CyNode, double[]> cluster2NodesToCoordinates = cluster2.getNodesToCoordinates();
        HashMap<CyNode, Double> cluster2NodesToRadii = cluster2.getNodesToRadii();
        for (CyNode node : cluster2NodesToCoordinates.keySet()) {
            addNodeCoordinates(node, cluster2NodesToCoordinates.get(node));
            addNodeRadius(node, cluster2NodesToRadii.get(node));
        }
        for (Entry<CyNode, double[]> entry : cluster2.getNodesToCoordinates().entrySet()) {
            addNodeCoordinates(entry.getKey(), entry.getValue());
        }

    }

    public String toSessionString() {
        /* Each cluster is stored in the format:
         *  		1 - Cluster number
         *  		2 - Cluster label
         *  		3 - Selected (0/1)
         *  		4 - Use groups or not
         *  		5... - Nodes
         *  		6... - Node coordinates
         *  		-1 - End of cluster
         */

        String sessionString = "";
        // Write parameters of the cluster
        sessionString += this.clusterNumber + "\n";
        sessionString += this.label + "\n";
        sessionString += this.selected + "\n";
        // Write parameters of the annotations to recreate them after
        // Ellipse
        if(this.ellipse != null){
            Map<String, String> ellipseArgs = this.ellipse.getArgMap();
            for (String property : ellipseArgs.keySet()) {
                sessionString += property + "\t" + ellipseArgs.get(property) + "\n";
            }
        }
        // Text
        if(this.textAnnotation != null){
            sessionString += "Text Annotations\n";
            Map<String, String> textArgs = this.textAnnotation.getArgMap();
            for (String property : textArgs.keySet()) {
                sessionString += property + "\t" + textArgs.get(property) + "\n";
            }
            sessionString += "End of annotations\n";
        }
        //expand the groups
        if (this.group != null) {
            if (this.group.isCollapsed(this.parent.getView().getModel())) {
                this.group.expand(this.parent.getView().getModel());
            }
        }
        //update the coordinates based on the expansion
        this.updateCoordinates();

        // Write each node and its coordinates
        for (CyNode node : this.nodesToCoordinates.keySet()) {
            long nodeID = node.getSUID();
            double[] coordinates = this.nodesToCoordinates.get(node);
            double nodeX = coordinates[0];
            double nodeY = coordinates[1];
            double nodeRadius = this.nodesToRadii.get(node);
            double nodeCentrality = this.nodesToCentralities.get(node);
            sessionString += nodeID + "\t" + nodeX + "\t" + nodeY + "\t" + nodeRadius + "\t" + nodeCentrality + "\n";
        }
        sessionString += "End of nodes\n";
        sessionString += "End of cluster\n";

        // Destroy group (causes problems with session loading)
        //if (group != null) {
        //	if (group.isCollapsed(parent.getView().getModel())) {
        //		group.expand(parent.getView().getModel());
        //	}
        //	group.removeGroupFromNetwork(parent.getView().getModel());
        //	AutoAnnotationManager.getInstance().getGroupManager().destroyGroup(group);
        //}

        return sessionString;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public void updateCoordinates() {

        boolean hasNodeViews = false;
        for (CyNode node : this.getNodes()) {
            View<CyNode> nodeView = this.parent.getView().getNodeView(node);
            // nodeView can be null when group is collapsed
            if (nodeView != null) {
                hasNodeViews = true;
                //the new coordinates
                double[] coordinates = {nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION),
                        nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)};
                double nodeRadius = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);

                double[] default_coordinates = {0.0,0.0};
                //check to make sure the node has coordinates.  - it is possilbe that it was just expanded from the group
                //and not coordinates are registered
                double[] previous_coordinates = (this.nodesToCoordinates.containsKey(node)) ? this.nodesToCoordinates.get(node) : default_coordinates;

                if (coordinatesHaveChanged(coordinates, previous_coordinates)) {
                    // Coordinates have changed, redrawing necessary
                    this.setCoordinatesChanged(true);
                    this.addNodeCoordinates(node, coordinates);
                    this.addNodeRadius(node, nodeRadius);
                }

            }
            else{
                if(this.nodesToCoordinates.containsKey(node)) {
                    this.nodesToCoordinates.remove(node);
                }
                if(this.nodesToRadii.containsKey(node)) {
                    this.nodesToRadii.remove(node);
                }
            }
        }//end of for CyNode
        if(!hasNodeViews) {
            View<CyNode> nodeView = this.parent.getView().getNodeView(this.getGroupNode());
            if (nodeView != null) {
                hasNodeViews = true;
                // nodeView can be null when group is collapsed
                double[] coordinates = {nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION),
                        nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)};
                double nodeRadius = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
                // Coordinates have changed, redrawing necessary
                this.setCoordinatesChanged(true);
                // Draw the annotation as if all nodes were where the groupNode is
                this.addNodeCoordinates(this.getGroupNode(), coordinates);
                this.addNodeRadius(this.getGroupNode(), nodeRadius);
            }
        }
    }
}