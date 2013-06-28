package main.java.org.baderlab.csapps.socialnetwork.tasks;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class DestroyNetworkTaskFactory extends AbstractTaskFactory {
	private CyNetworkManager cyNetworkManagerServiceRef = null;
	
	public DestroyNetworkTaskFactory(CyNetworkManager cyNetworkManagerServiceRef) {
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new DestroyNetworkTask(this.cyNetworkManagerServiceRef));
	}

}