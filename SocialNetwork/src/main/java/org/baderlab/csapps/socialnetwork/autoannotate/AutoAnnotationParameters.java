/**
 * Created by
 * User: arkadyark
 * Date: Jul 24, 2014
 * Time: 9:29:43 AM
 */
package org.baderlab.csapps.socialnetwork.autoannotate;

import java.util.ArrayList;
import java.util.HashMap;

import org.baderlab.csapps.socialnetwork.autoannotate.model.AnnotationSet;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.session.CySession;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.annotations.Annotation;

/* 
 * AutoAnnotation Parameters define all the variables that are needed to create, manipulate, explore
 * and save an individual AutoAnnotation session (potentially consisting of multiple annotation sets)
 * on a single network view.
 */
public class AutoAnnotationParameters {
	//network that belongs to this annotation
	private CyNetwork network;
	// network view that this belongs to
	private CyNetworkView networkView;
	
	//name of annotationset
	private String name;
	//column used to cluster
	private String clusterColumnName;
	//column used to calculateAnnotations
	private String annotateColumnName;
	//name of algorithm used to cluster
	private String algorithm;
	
	//does this annoatation set have groups defined
	private boolean groups;
	
	// network view ID (used for saving/loading)
	private long networkViewID;
	// used to select/deselect cluster
	private String selectedAnnotationSetName;
	// stores all of the annotation sets for this network view
	private HashMap<String, AnnotationSet> annotationSets;	
	
	
	public AutoAnnotationParameters() {
		annotationSets = new HashMap<String, AnnotationSet> ();
	}
	
	public AutoAnnotationParameters(CyNetwork network, CyNetworkView networkView) {
		annotationSets = new HashMap<String, AnnotationSet> ();
		this.network = network;
		this.networkView = networkView;
		this.networkViewID = networkView.getSUID();
	}
	
	public CyNetworkView getNetworkView() {
		return networkView;
	}
	
	public void setNetworkView(CyNetworkView networkView) {
		this.networkView = networkView;
		this.networkViewID = networkView.getSUID();
	}

	public CyNetwork getNetwork() {
		return network;
	}

	public void setNetwork(CyNetwork network) {
		this.network = network;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClusterColumnName() {
		return clusterColumnName;
	}

	public void setClusterColumnName(String clusterColumnName) {
		this.clusterColumnName = clusterColumnName;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public boolean isGroups() {
		return groups;
	}

	public void setGroups(boolean groups) {
		this.groups = groups;
	}

	public String getAnnotateColumnName() {
		return annotateColumnName;
	}

	public void setAnnotateColumnName(String annotateColumnName) {
		this.annotateColumnName = annotateColumnName;
	}

	public AnnotationSet getSelectedAnnotationSet() {
		return annotationSets.get(selectedAnnotationSetName);
	}

	public void setSelectedAnnotationSet(AnnotationSet selectedAnnotationSet) {
		this.selectedAnnotationSetName = selectedAnnotationSet.getName();
		selectedAnnotationSet.setSelected(true);
		for (AnnotationSet annotationSet : annotationSets.values()) {
			if (!annotationSet.equals(selectedAnnotationSet)) {
				annotationSet.setSelected(false);
			}
		}
	}

	public void setSelectedAnnotationSetName(String selectedAnnotationSetName) {
		this.selectedAnnotationSetName = selectedAnnotationSetName;
	}
	
	public HashMap<String, AnnotationSet> getAnnotationSets() {
		return annotationSets;
	}
	
	public void addAnnotationSet(AnnotationSet annotationSet) {
		this.annotationSets.put(annotationSet.getName(), annotationSet);
	}
	
	public void removeAnnotationSet(AnnotationSet annotationSet) {
		annotationSets.remove(annotationSet.getName());
	}

	public String nextAnnotationSetName(String algorithm,
			String clusterColumnName) {
		// Method to prevent overlap of annotation set names
		String originalAnnotationSetName = null;
		if (algorithm != null) {
			originalAnnotationSetName = algorithm + " Annotation Set";			
		} else {
			originalAnnotationSetName = clusterColumnName + " Column Annotation Set";
		}
		String annotationSetName = originalAnnotationSetName;
		int suffix = 2;
		while (annotationSets.keySet().contains(annotationSetName)) {
			annotationSetName = originalAnnotationSetName + " " + suffix;
			suffix++;
		}
		return annotationSetName;
	}
	
	public String nextClusterColumnName(String clusterMakerColumnName, CyTable nodeTable) {
		// Method to prevent overlap of cluster columns
		String originalClusterColumnName = clusterMakerColumnName;
		String clusterColumnName = originalClusterColumnName;
		int suffix = 2;
		while (nodeTable.getColumn(clusterColumnName) != null) {
			clusterColumnName = originalClusterColumnName + suffix;
			suffix++;
		}
		return clusterColumnName;
	}
	
	public void load(String fullText, CySession session) {
		String[] fileLines = fullText.split("\n");
		// Reload network view by ID
		CyNetworkView view = session.getObject(Long.parseLong(fileLines[0]), CyNetworkView.class);
		setNetworkView(view);
		setNetwork(view.getModel());
		
		// TODO
		// Reload annotations from ID, currently not possible
		try{
			for (Annotation annotation : AutoAnnotationManager.getInstance().getAnnotationManager().getAnnotations(view)) {
				annotation.removeAnnotation();
			}
		}catch(Exception e){
			System.out.println("There are no annotations in this view");
		}
		// Reload selected annotation set
		setSelectedAnnotationSetName(fileLines[1]);
		// The rest of the lines correspond to the annotation sets
		int lineNumber = 2;
		// Accumulator for annotation set lines in the session file
		ArrayList<String> annotationSetLines = new ArrayList<String>();
		while (lineNumber < fileLines.length) {
			String line = fileLines[lineNumber];
			if (!line.equals("End of annotation set")) {
				// Add to the growing list of lines for the annotation set
				annotationSetLines.add(line);
			} else {
				// Reached the end of this annotation set, load it
				AnnotationSet annotationSet = new AnnotationSet();
				annotationSet.setView(networkView);
				annotationSet.load(annotationSetLines, session);
				addAnnotationSet(annotationSet);
				// Reset accumulator for annotation set lines in the session file
				annotationSetLines = new ArrayList<String>();
			}
			lineNumber++;
		}
	}

	public String toSessionString() {
		String sessionString = "";
		sessionString += networkViewID + "\n";
		sessionString += selectedAnnotationSetName + "\n";
		for (AnnotationSet annotationSet : annotationSets.values()) {
			sessionString += annotationSet.toSessionString();
		}
		return sessionString;
	}
}
