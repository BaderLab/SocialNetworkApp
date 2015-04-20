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

import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * ??
 * 
 * @author Victor Kofia
 */
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
