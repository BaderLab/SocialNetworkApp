package org.baderlab.csapps.socialnetwork.tasks;


import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class DestroyNetworkTaskFactory extends AbstractTaskFactory {
	private CyNetworkManager cyNetworkManagerServiceRef = null;
	private SocialNetworkAppManager appManager;
	
	public DestroyNetworkTaskFactory(CyNetworkManager cyNetworkManagerServiceRef, SocialNetworkAppManager appManager) {
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
		this.appManager = appManager;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new DestroyNetworkTask(this.cyNetworkManagerServiceRef,this.appManager));
	}

}