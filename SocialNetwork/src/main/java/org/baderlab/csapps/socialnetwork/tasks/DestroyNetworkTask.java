package main.java.org.baderlab.csapps.socialnetwork.tasks;

import main.java.org.baderlab.csapps.socialnetwork.model.Cytoscape;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class DestroyNetworkTask extends AbstractTask {
	private CyNetworkManager cyNetworkManagerServiceRef = null;
	
	public DestroyNetworkTask(CyNetworkManager cyNetworkManagerServiceRef) {
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
	}

	/**
	 * Destroy a network
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		cyNetworkManagerServiceRef.destroyNetwork(Cytoscape.getNetworkToBeDestroyed());
	}

}
