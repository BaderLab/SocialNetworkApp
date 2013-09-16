package org.baderlab.csapps.socialnetwork.model;

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import org.apache.commons.io.FilenameUtils;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.actions.ShowUserPanelAction;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.academia.Incites_InstitutionLocationMap;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.baderlab.csapps.socialnetwork.panels.AcademiaPanel;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
import org.baderlab.csapps.socialnetwork.tasks.ApplyVisualStyleTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.CreateNetworkTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.DestroyNetworkTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskManager;

/**
 * Cytoscape
 * @author Victor Kofia
 */
public class SocialNetworkAppManager {
	/**
	 * A reference to the 'apply visual style' task factory
	 */
	private static ApplyVisualStyleTaskFactory applyVisualStyleTaskFactoryRef = null;
	/**
	 * Currently selected social network
	 */
	private  SocialNetwork currentlySelectedSocialNetwork = null;
	/**
	 * A reference to the cytoscape application manager. Necessary for
	 * changing network views.
	 */
	private  CyApplicationManager cyAppManagerServiceRef = null;
	/**
	 * A reference to the cytoscape service registrar. Necessary for 
	 * unregistering services at user's convenience.
	 */
	private  CyServiceRegistrar cyServiceRegistrarRef = null;
	/**
	 * A reference to the 'destroy network' task factory 
	 */
	private  DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef = null;
	/**
	 * A map containing the nodes and edges that will eventually be used
	 * to form a network
	 */
	private  Map<Collaboration, ArrayList<AbstractEdge>> map = null;
	/**
	 * Name of network
	 */
	private  String networkName = null;
	/**
	 * A reference to the 'create network' task factory
	 */
	private  CreateNetworkTaskFactory networkTaskFactoryRef = null;
	/**
	 * A network that's just about to be destroyed
	 */
	private  CyNetwork networkToBeDestroyed = null;
	/**
	 *Social network map. 
	 *<br>Key: network name 
	 *<br>Value: social network
	 */
	private  Map<String, SocialNetwork> socialNetworkMap = null;
	/**
	 * A reference to the cytoscape task manager. As the name suggests
	 * the task manager is used for executing tasks.
	 */
	private  TaskManager<?, ?> taskManagerServiceRef = null;
	/**
	 * A reference to user panel action. Controls panel actions (viewing,
	 * closing ... etc)
	 */
	private  ShowUserPanelAction userPanelAction = null;
	/**
	 * A reference to the app's user panel. User will interact with app primarily through
	 * this panel.
	 */
	private  UserPanel userPanelRef = null;
	/**
	 * Currently selected visual style ID
	 */
	private int visualStyleID = VisualStyles.DEFAULT_VISUAL_STYLE;
	/**
	 * Set of all visual styles currently supported by app
	 */
	private HashSet<String> visualStyleSet = null;
	
	public static final int ANALYSISTYPE_INCITES = 0;
	public static final int ANALYSISTYPE_SCOPUS = 1;
	private int analysis_type = SocialNetworkAppManager.ANALYSISTYPE_INCITES;
	
	/**
	 * Return true iff visual style is currently supported
	 * by app
	 * @param String visualStyleName
	 * @return boolean bool
	 */
	public boolean isValidVisualStyle(String visualStyleName) {
		if (visualStyleSet == null) {
			visualStyleSet = new HashSet<String>();
			visualStyleSet.add("Chipped");
			visualStyleSet.add("Vanue");
		}
		return visualStyleSet.contains(visualStyleName);
	}
	
	/**
	 * Apply visual style to network
	 * @param String visualStyle
	 * @return null
	 */
	public void applyVisualStyle(String visualStyle) {
		this.setVisualStyleID(new VisualStyles().getVisualStyleID(visualStyle));
		this.getTaskManager().execute(this.getApplyVisualStyleTaskFactoryRef()
				                                    .createTaskIterator());
	}
	
	/**
	 * Close user panel. Method will do nothing if user panel
	 * has not been registered prior to it's execution.
	 * @param null
	 * @return null
	 */
	public void closeUserPanel() {
		this.getServiceRegistrar().unregisterService
		(this.getUserPanelRef(), CytoPanelComponent.class);
		this.getUserPanelAction().putValue(Action.NAME, "View Panel");
	}
	
	/**
	 * Create a network. Method marked private in order to prevent users from inadvertently
	 * creating a network before all pertinent edge and node info is set.
	 * @param null
	 * @return null
	 */
	private void createNetwork() {
		
		// Execute network task. 
		// NOTE: Relevant node & edge info is not directly coupled with task execution. It is
		// acquired later on through Cytoscape.getMap()
		// This method is a blackbox and should NOT be directly executed under ANY circumstances
		this.getTaskManager().execute(this.getNetworkTaskFactoryRef()
				 .createTaskIterator());
	
	}
	
	/**
	 * Create a network from file
	 * @param File networkFile
	 * @return null
	 */	
	public void createNetwork(File networkFile) throws FileNotFoundException {
		// Verify that network name is valid
		String networkName = this.userPanelRef.getAcademiaPanel().getFacultyTextFieldRef().getText().trim();
		if (! this.isNameValid(networkName)) {
			CytoscapeUtilities.notifyUser("Network " + networkName + " already exists in Cytoscape."
					           + " Please enter a new name.");
			return;
		}
		// Change mouse cursor
		this.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		// Initialize variables
		List<? extends Publication> pubList = null;
		String extension = null;
		SocialNetwork socialNetwork = null;
		String departmentName = null;
		// Create network out of Incites data
		if (this.analysis_type == this.ANALYSISTYPE_INCITES) {
			extension = FilenameUtils.getExtension(networkFile.getPath());
			IncitesParser incitesParser = null;
			// Load data from text file
			if (extension.trim().equalsIgnoreCase("xlsx")) {
				socialNetwork = new SocialNetwork(networkName, Category.INCITES);
				incitesParser = new IncitesParser(networkFile);
				if (incitesParser.getIgnoredRows() >= 1) {
					CytoscapeUtilities.notifyUser("Some rows could not be parsed.");
				}
				if (incitesParser.getIdentifiedFacultyList().size() == 0) {
					CytoscapeUtilities.notifyUser("Unable to identify faculty." 
							          +  "Please verify that Incites data file is valid");
				}
				pubList = incitesParser.getPubList();
				departmentName = incitesParser.getDepartmentName();
				if (pubList == null) {
					this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					CytoscapeUtilities.notifyUser("Invalid file. This Incites file is corrupt.");
					return;
				}
			// Notify user of inappropriate file type
			} else {
				this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				CytoscapeUtilities.notifyUser("Invalid file. Incite data files either have to be excel " +
						             "spreadsheets or text files.");
				return;
			}
			// Add summary attributes
			socialNetwork.setNum_publications(incitesParser.getPubList().size());
			socialNetwork.setNum_faculty(incitesParser.getFacultySet().size());
			socialNetwork.setNum_uniden_faculty(incitesParser.getUnidentifiedFacultyList().size());
			socialNetwork.setUnidentified_faculty(incitesParser.getUnidentifiedFacultyString());
			/*socialNetwork.getSummaryList().add(new String[] {"Total # of publications: ", Integer.toString(incitesParser.getPubList().size())});
			socialNetwork.getSummaryList().add(new String[] {"Total # of faculty: ", Integer.toString(incitesParser.getFacultySet().size())});
			socialNetwork.getSummaryList().add(new String[] {"Total # of unidentified faculty: ", Integer.toString(incitesParser.getUnidentifiedFacultyList().size())});
			socialNetwork.getSummaryList().add(new String[] {"<hr><br>UNIDENTIFIED FACULTY", incitesParser.getUnidentifiedFacultyString()});
			*/
			// Add info to social network map(s)
			socialNetwork.getAttrMap().put(IncitesVisualStyle.nodeattr_dept, departmentName);
		// Create network out of Scopus data
		} else if (this.analysis_type == this.ANALYSISTYPE_SCOPUS) {
			extension = FilenameUtils.getExtension(networkFile.getPath());
			Scopus scopus = null;
			if (extension.trim().equalsIgnoreCase("csv")) {
				socialNetwork = new SocialNetwork(networkName, Category.SCOPUS);
				scopus = new Scopus(networkFile);
				pubList = scopus.getPubList();
				socialNetwork.setNum_publications(scopus.getPubList().size());
				if (pubList == null) {
					this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return;
				}
			} else {
				this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				CytoscapeUtilities.notifyUser("Invalid file. Scopus data files have to be csv " +
						             "spreadsheets");
				return;
			}
		}
		// Create interaction
		Interaction interaction = new Interaction(pubList, Category.ACADEMIA);
		// Create map
		Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
		if (map.size() == 0) {
			this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			CytoscapeUtilities.notifyUser("Network couldn't be loaded. File is corrupt.");
			return;
		}
		this.setMap(map);
		this.setNetworkName(networkName);
		this.getSocialNetworkMap().put(networkName, socialNetwork);
		// Create network using map
		this.createNetwork();
	}

	/**
	 * Create a network from search term
	 * @param String searchTerm
	 * @param int category
	 * @return null
	 */
	public void createNetwork(String searchTerm, int category) {
		// Verify that network name is valid
		if (! this.isNameValid(searchTerm)) {
			CytoscapeUtilities.notifyUser("Network " + networkName + " already exists in Cytoscape."
					+ " Please enter a new name.");
			return;
		}
		// Create new search session
		Search search = new Search(searchTerm, category,this);
		// Get a list of the results that are going to serve as edges. Exact result type
		// may vary with website
		List<? extends AbstractEdge> results = (List<? extends AbstractEdge>) search.getResults();
		if (results == null) {
			this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			CytoscapeUtilities.notifyUser("Network could not be loaded");
			return;
		} 
		if (results.size() == 0) {
			this.getUserPanelRef().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			CytoscapeUtilities.notifyUser("Search did not yield any results");
			return;
		}
		Map<Collaboration, ArrayList<AbstractEdge>> map = null;
		Interaction interaction = null;
		SocialNetwork socialNetwork = null;
		switch (category) {
			case Category.ACADEMIA:
				// Change category (to Pubmed)
				// This is only temporary ~
				category = Category.PUBMED;
				interaction = new Interaction(results, category);
				socialNetwork = new SocialNetwork(searchTerm, category);
				socialNetwork.setNum_publications(search.getTotalHits());
				
				// Create new map using results
				map = interaction.getAbstractMap();
				// Set social network attributes
				// ??
				break;
		}
		this.setNetworkName(searchTerm);
		this.getSocialNetworkMap().put(searchTerm, socialNetwork);
		// Transfer map to Cytoscape's map variable
		this.setMap(map);
		// Create network using map
		this.createNetwork();			
	}

	/**
	 * Destroy a network
	 * @param CyNetwork network
	 * @return null
	 */
	public void destroyNetwork(CyNetwork network) {
		this.setNetworkToBeDestroyed(network);
		this.getTaskManager().execute(this.getDestroyNetworkTaskFactoryRef()
				                                    .createTaskIterator());
	}

	/**
	 * Get apply view task factory
	 * @param null
	 * @return ApplyViewTaskFactory applyVisualStyleTaskFactoryRef
	 */
	public static ApplyVisualStyleTaskFactory getApplyVisualStyleTaskFactoryRef() {
		return SocialNetworkAppManager.applyVisualStyleTaskFactoryRef;
	}

	/**
	 * Get currently selected social network
	 * @param null
	 * @return SocialNetwork currentlySelectedSocialNetwork
	 */
	public SocialNetwork getCurrentlySelectedSocialNetwork() {
		return currentlySelectedSocialNetwork;
	}

	/**
	 * Get Cytoscape application manager
	 * @param null
	 * @return CyApplicationManager cyAppManagerServiceRef
	 */
	public  CyApplicationManager getCyAppManagerServiceRef() {
		return cyAppManagerServiceRef;
	}

	/**
	 * Get destroy network task factory
	 * @param null
	 * @return DestroyNetworkTaskFactory destroyNetworkTaskFactoryRef 
	 */
	public  DestroyNetworkTaskFactory getDestroyNetworkTaskFactoryRef() {
		return destroyNetworkTaskFactoryRef;
	}

	/**
	 * Get network map. Contains edge and node information.
	 * @param null
	 * @return Map map
	 */
	public  Map<Collaboration, ArrayList<AbstractEdge>> getMap() {
		return map;
	}

	/**
	 * Get network name.
	 * @param null
	 * @return String networkName
	 */
	public  String getNetworkName() {
		return networkName;
	}

	/**
	 * Get the name associated with a particular CyNetwork object
	 * @param CyNetwork network
	 * @return String networkName
	 */
	public  String getNetworkName(CyNetwork network) {
		ArrayList<CyRow> rowList = (ArrayList<CyRow>) network.getDefaultNetworkTable()
				                                             .getAllRows();
		CyRow row = rowList.get(0);
		String networkName = (String) row.getAllValues().get("name");
		return networkName;
	}

	/**
	 * Get network table column names
	 * @param null
	 * @return String[] networkTableColumnNames
	 * @return
	 */
	public  String[] getNetworkTableColumnNames() {
		return new String[] {"Name", "Node Count", "Edge Count", "Category"};
	}

	/**
	 * Get network task factory
	 * @param null
	 * @return NetworkTaskFactory networkTaskFactory
	 */
	public CreateNetworkTaskFactory getNetworkTaskFactoryRef() {
		return networkTaskFactoryRef;
	}

	/**
	 * Get network to be destroyed
	 * @param null
	 * @return CyNetwork networkToBeDestroyed
	 */
	public  CyNetwork getNetworkToBeDestroyed() {
		return networkToBeDestroyed;
	}

	/**
	 * Get Cytoscape service registrar
	 * @param null
	 * @return CyServiceRegistrar cyServiceRegistrarRef
	 */
	public  CyServiceRegistrar getServiceRegistrar() {
		return cyServiceRegistrarRef;
	}

	/**
	 * Get social network map
	 * @param null
	 * @return Map social networks
	 * <br><i>key: network name</i> 
	 * <br><i>value: {CyNetwork, Category, CyNetworkView}</i>
	 */
	public  Map<String, SocialNetwork> getSocialNetworkMap() {
		if (socialNetworkMap == null) {
			setSocialNetworkMap(new HashMap<String, SocialNetwork>());
			socialNetworkMap.put("DEFAULT", 
					                       new SocialNetwork("DEFAULT", Category.DEFAULT));
		}
		return socialNetworkMap;
	}
	
	/*
	 * Given the name of a network
	 * returns the SocialNetwork object associated with that network name
	 * returns null otherwise.
	 */
	
	public SocialNetwork getSocialNetwork(String name){
		if(socialNetworkMap != null && socialNetworkMap.containsKey(name))
			return socialNetworkMap.get(name);
		else 
			return null;
	}
	
	
	/**
	 * Get Cytoscape task manager
	 * @param null
	 * @return TaskManager taskManager
	 */
	public  TaskManager<?, ?> getTaskManager() {
		return taskManagerServiceRef;
	}

	/**
	 * Get user panel action
	 * @param null
	 * @return UserPanelAction userPanelAction
	 */
	public ShowUserPanelAction getUserPanelAction() {
		return userPanelAction;
	}

	/**
	 * Get user panel reference
	 * @param null
	 * @return UserPanel userPanelRef
	 */
	public  UserPanel getUserPanelRef() {
		return userPanelRef;
	}

	/**
	 * Get visual style ID
	 * @param null
	 * @return int visualStyleID
	 */
	public  int getVisualStyleID() {
		return visualStyleID;
	}
	
	/**
	 * Return true iff a network with a similar name is 
	 * <i>not</i> already present in Cytoscape
	 * @param String networkName
	 * @return boolean
	 */
	public  Boolean isNameValid(String name) {
		return ! getSocialNetworkMap().containsKey(name);
	}
	
	
	
	/**
	 * Set 'apply view' task factory
	 * @param ApplyViewTaskFactory applyViewTaskFactoryRef
	 * @return null
	 */
	public  void setApplyVisualStyleTaskFactoryRef
	                   (ApplyVisualStyleTaskFactory applyViewTaskFactoryRef) {
		applyVisualStyleTaskFactoryRef = applyViewTaskFactoryRef;
	}

	/**
	 * Set currently selected social network
	 * @param SocialNetwork currentlySelectedSocialNetwork
	 * @return null
	 */
	public  void setCurrentlySelectedSocialNetwork(
			SocialNetwork currentlySelectedSocialNetwork) {
		this.currentlySelectedSocialNetwork = currentlySelectedSocialNetwork;
	}
	
	/**
	 * Set network's view as the current view
	 * @param String networkName
	 * @return null
	 */
	public void setCurrentNetworkView(String networkName) {
		CyNetworkView networkView = (CyNetworkView) 
				                    getSocialNetworkMap()
				                    .get(networkName).getNetworkView();
		getCyAppManagerServiceRef().setCurrentNetworkView(networkView);
	}
	
	/**
	 * Set Cytoscape application manager
	 * @param CyApplicationManager cyAppManagerServiceRef
	 * @return null
	 */
	public  void setCyAppManagerServiceRef
	              (CyApplicationManager cyAppManagerServiceRef) {
		this.cyAppManagerServiceRef = cyAppManagerServiceRef;
	}
	
	/**
	 * Set 'destroy network' task factory
	 * @param DestroyNetworkTaskFActory destroyNetworkTaskFactory
	 * @return null
	 */
	public  void setDestroyNetworkTaskFactoryRef(DestroyNetworkTaskFactory 
			                                       destroyNetworkTaskFactory) {
		destroyNetworkTaskFactoryRef = destroyNetworkTaskFactory;
	}
	
	/**
	 * Set map
	 * @param Map map
	 * @return null
	 */
	public  void setMap(Map<Collaboration, ArrayList<AbstractEdge>> map) {
		this.map = map;
	}
	
	/**
	 * Set network name
	 * @param String networkName
	 * @return null
	 */
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	
	/**
	 * Set network task factory
	 * @param CreateNetworkTaskFactory networkTaskFactory
	 * @return null
	 */
	public void setNetworkTaskFactoryRef
	                   (CreateNetworkTaskFactory networkTaskFactoryRef) {
		this.networkTaskFactoryRef = networkTaskFactoryRef;
	}
	
	/**
	 * Set network to be destroyed
	 * @param CyNetwork networkToBeDestroyed
	 * @return null
	 */
	public void setNetworkToBeDestroyed(CyNetwork networkToBeDestroyed) {
		this.networkToBeDestroyed = networkToBeDestroyed;
	}
	
	/**
	 * Set Cytoscape service registrar
	 * @param CyServiceRegistrar cyServiceRegistrarRef
	 * @return null
	 */
	public void setServiceRegistrar
	                   (CyServiceRegistrar cyServiceRegistrarRef) {
		this.cyServiceRegistrarRef = cyServiceRegistrarRef;
	}
	
	/**
	 * Set social network map
	 * @param Map socialNetworkMap
	 * @return null
	 */
	public void setSocialNetworkMap
	                   (Map<String, SocialNetwork> socialNetwork) {
		this.socialNetworkMap = socialNetwork;
	}
	
	/**
	 * Set task manager
	 * @param TaskManager taskManager
	 * @return null
	 */
	public  void setTaskManager(TaskManager<?, ?> taskManager) {
		this.taskManagerServiceRef = taskManager;
	}

	/**
	 * Set user panel action
	 * @param ShowUserPanelAction userPanelAction
	 * @return null
	 */
	public  void setUserPanelAction(ShowUserPanelAction userPanelAction) {
		this.userPanelAction = userPanelAction;
	}

	/**
	 * Set user panel reference
	 * @param UserPanel userPanelRef
	 * @return null
	 */
	public  void setUserPanelRef(UserPanel userPanelRef) {
		this.userPanelRef = userPanelRef;
	}

	/**
	 * Set visual style ID
	 * @param int visualStyleID
	 * @return null
	 */
	public void setVisualStyleID(int visualStyleID) {
		this.visualStyleID = visualStyleID;
	}

	public int getAnalysis_type() {
		return analysis_type;
	}

	public void setAnalysis_type(int analysis_type) {
		this.analysis_type = analysis_type;
	}
	
	
}