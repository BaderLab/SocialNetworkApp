package main.java.org.baderlab.csapps.socialnetwork.tasks;


import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CreateNetworkTaskFactory extends AbstractTaskFactory {
		private CyNetworkFactory cyNetworkFactoryServiceRef;
		private CyNetworkViewFactory cyNetworkViewFactoryServiceRef;
		private CyNetworkViewManager cyNetworkViewManagerServiceRef;
		private CyNetworkManager cyNetworkManagerServiceRef;
		private CyNetworkNaming cyNetworkNamingServiceRef;
		private CyLayoutAlgorithmManager cyLayoutManagerServiceRef;
	
	public CreateNetworkTaskFactory(CyNetworkNaming cyNetworkNamingServiceRef, CyNetworkFactory cyNetworkFactoryServiceRef, CyNetworkManager cyNetworkManagerServiceRef, 
			CyNetworkViewFactory cyNetworkViewFactoryServiceRef, CyNetworkViewManager cyNetworkViewManagerServiceRef, CyLayoutAlgorithmManager cyLayoutManagerServiceRef) {
			this.cyNetworkNamingServiceRef = cyNetworkNamingServiceRef;
			this.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
			this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
			this.cyNetworkViewFactoryServiceRef = cyNetworkViewFactoryServiceRef;
			this.cyNetworkViewManagerServiceRef = cyNetworkViewManagerServiceRef;
			this.cyLayoutManagerServiceRef = cyLayoutManagerServiceRef;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new CreateNetworkTask(cyNetworkNamingServiceRef, cyNetworkFactoryServiceRef, cyNetworkManagerServiceRef, 
				cyNetworkViewFactoryServiceRef, cyNetworkViewManagerServiceRef, cyLayoutManagerServiceRef));
	}

}
