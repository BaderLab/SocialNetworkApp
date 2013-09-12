package org.baderlab.csapps.socialnetwork.tasks;


import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class DestroyNetworkTask extends AbstractTask {
	private CyNetworkManager cyNetworkManagerServiceRef = null;
	private SocialNetworkAppManager appManager;
	
	public DestroyNetworkTask(CyNetworkManager cyNetworkManagerServiceRef, SocialNetworkAppManager appManager) {
		this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
		this.appManager = appManager;
	}

	/**
	 * Destroy a network
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		cyNetworkManagerServiceRef.destroyNetwork(this.appManager.getNetworkToBeDestroyed());
	}

}
