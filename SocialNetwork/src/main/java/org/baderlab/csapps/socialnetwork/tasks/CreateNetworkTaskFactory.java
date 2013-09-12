package org.baderlab.csapps.socialnetwork.tasks;



import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.group.CyGroupFactory;
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
		private SocialNetworkAppManager appManager;
	
	public CreateNetworkTaskFactory(CyNetworkNaming cyNetworkNamingServiceRef, 
			                        CyNetworkFactory cyNetworkFactoryServiceRef, 
			                        CyNetworkManager cyNetworkManagerServiceRef, 
			                        CyNetworkViewFactory cyNetworkViewFactoryServiceRef, 
			                        CyNetworkViewManager cyNetworkViewManagerServiceRef, 
			                        CyLayoutAlgorithmManager cyLayoutManagerServiceRef, SocialNetworkAppManager appManager) {
			this.cyNetworkNamingServiceRef = cyNetworkNamingServiceRef;
			this.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
			this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
			this.cyNetworkViewFactoryServiceRef = cyNetworkViewFactoryServiceRef;
			this.cyNetworkViewManagerServiceRef = cyNetworkViewManagerServiceRef;
			this.cyLayoutManagerServiceRef = cyLayoutManagerServiceRef;
			this.appManager = appManager;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new CreateNetworkTask(cyNetworkNamingServiceRef, 
				                                      cyNetworkFactoryServiceRef, 
				                                      cyNetworkManagerServiceRef, 
				                                      cyNetworkViewFactoryServiceRef, 
				                                      cyNetworkViewManagerServiceRef, 
				                                      cyLayoutManagerServiceRef,appManager));
	}

}
