package org.baderlab.csapps.socialnetwork.autoannotate.task;

import java.util.ArrayList;
import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationParameters;
import org.baderlab.csapps.socialnetwork.autoannotate.model.AnnotationSet;
import org.baderlab.csapps.socialnetwork.autoannotate.model.Cluster;
import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class CreateGroupsTask extends AbstractTask{

	private AnnotationSet annotationSet;
	private AutoAnnotationParameters params;
	
	private TaskMonitor taskMonitor;
	
	public CreateGroupsTask(AnnotationSet annotationSet,
			AutoAnnotationParameters params) {
		super();
		this.annotationSet = annotationSet;
		this.params = params;
	}


	private void createGroups(){
		AutoAnnotationManager autoAnnotationManager = AutoAnnotationManager.getInstance();
		CyGroupManager groupManager = autoAnnotationManager.getGroupManager();
		CyGroupFactory groupFactory =autoAnnotationManager.getGroupFactory();		
		
		for (Cluster cluster : annotationSet.getClusterMap().values()) {
						
				//Create a Node with the Annotation Label to represent the group
				CyNode groupNode = this.params.getNetwork().addNode();
				 this.params.getNetwork().getRow(groupNode).set(CyNetwork.NAME, cluster.getLabel());
				autoAnnotationManager.flushPayloadEvents();
				
				CyGroup group = groupFactory.createGroup( this.params.getNetwork(), groupNode,new ArrayList<CyNode>(cluster.getNodes()),null, true);							
				cluster.setGroup(group);
			}
	}


	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;
		
		this.taskMonitor.setTitle("Creating Groups");
		
		createGroups();
		
	}
	
}
