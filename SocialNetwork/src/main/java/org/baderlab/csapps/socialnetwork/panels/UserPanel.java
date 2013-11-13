package org.baderlab.csapps.socialnetwork.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;


import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.VisualStyles;
import org.baderlab.csapps.socialnetwork.util.GenerateReports;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.util.swing.FileUtil;

import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
	
/**
 * Main panel for Social Network App
 * @author Victor Kofia
 */
public class UserPanel extends JPanel implements CytoPanelComponent {
	/**
	 * Reference to control panel
	 */
	private  JPanel controlPanelRef = null;
	/**
	 * Reference to the currently displayed info panel
	 */
	private  JPanel infoPanelRef = null;
	/**
	 * Reference to network panel
	 */
	private  JPanel networkPanelRef = null;
	/**
	 * Reference to network table
	 */
	private  JTable networkTableRef = null;
	/**
	 * Reference to the search box. Necessary for extracting queries.
	 */
	private  JTextField searchBoxRef = null;
	/**
	 * Search filter
	 */
	private  JComboBox searchFilter = null;
	/**
	 * Category that user has selected
	 */
	private  int selectedCategory = Category.DEFAULT;
	/**
	 * Network that user has selected
	 */
	private  String selectedNetwork = null;
	/**
	 * Visual style that user has selected
	 */
	private  int selectedVisualStyle = Category.DEFAULT;
	private  final long serialVersionUID = 8292806967891823933L;
	/**
	 * Reference to top panel
	 */
	private  JPanel topPanelRef = null;
	/**
	 * Reference to help button
	 */
	private  JButton helpButton = null;
	/**
	 * Reference to visual style panel
	 */
	private  JPanel visualStylePanel = null;
	/**
	 * Visual style selector
	 */
	private  JComboBox visualStyleSelector = null;
	/**
	 * The visual style selector type currently being displayed to the user
	 */
	private  int visualStyleSelectorType = Category.DEFAULT;
	/**
	 * Reference to network summary panel
	 */
	private  JPanel networkSummaryPanelRef = null;
	/**
	 * Reference to network summary pane
	 */
	private  JTextPane networkSummaryPaneRef = null;
	
	/**
	 * Reference to network summary pane
	 */
	private  JTextPane fileSummaryPaneRef = null;
	
	
	/**
	 * Reference to the main appManager
	 */
	private SocialNetworkAppManager appManager = null;
	private FileUtil fileUtil = null;
	private CySwingApplication cySwingAppRef = null;
	
	/*
	 * instance of academia panel
	 */
	private AcademiaPanel academiaPanel = null;
	
	/**
	 * Create a new panel
	 * @param null
	 * @return null
	 */
	public UserPanel(SocialNetworkAppManager appManager, FileUtil fileUtil, CySwingApplication cySwingAppRef ) {
		
		// Save a reference to this panel object
		this.appManager = appManager;
		this.fileUtil = fileUtil;
		this.cySwingAppRef = cySwingAppRef;
		this.appManager.setUserPanelRef(this);
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400,200));
		
		// NOTE: An 'Academia' flavored UI has been set as the default
		this.setSelectedCategory(Category.ACADEMIA);
			
		// Add top panel
		this.topPanelRef = this.createTopPanel();
		this.add(this.topPanelRef, BorderLayout.NORTH);
		
		// Add the default info panel (Academia)
		this.academiaPanel = new AcademiaPanel(appManager,this.fileUtil, this.cySwingAppRef);
		this.setSelectedInfoPanel(this.academiaPanel.createAcademiaInfoPanel());
		this.add(this.infoPanelRef, BorderLayout.CENTER);
		
		// Add bottom panel
		this.add(this.createBottomPanel(), BorderLayout.SOUTH);
		
		
		
	}

	/**
	 * Add network's visual style to user panel
	 * @param Network network
	 * @return null
	 */
	public  void addNetworkVisualStyle(SocialNetwork socialNetwork) {
//		int networkType = (socialNetwork == null) ? Category.DEFAULT : socialNetwork.getNetworkType();
//		int visualStyleType = (socialNetwork == null) ? VisualStyles.DEFAULT_VISUAL_STYLE : socialNetwork.getDefaultVisualStyle();
//		String networkName = (socialNetwork == null) ? "DEFAULT" : socialNetwork.getNetworkName();
		int visualStyleType = VisualStyles.DEFAULT_VISUAL_STYLE;
		String networkName = "DEFAULT";
		if (socialNetwork != null) {
			visualStyleType = socialNetwork.getDefaultVisualStyle();
			networkName = socialNetwork.getNetworkName();
		}
		if (this.getVisualStylePanel()  == null) {
			// Create new visual style panel
			this.setVisualStylePanel(this.createVisualStylePanel(networkName));
			// It is imperative that visual selector type be set before the visual
			// selector. Not doing this will cause random & seemingly untraceable
			// errors to occur.
			this.setVisualStyleSelectorType(visualStyleType);
			this.setVisualStyleSelector(this.createVisualStyleSelector
					                        (visualStyleType));
			this.getVisualStylePanel().add(this.getVisualStyleSelector());
			this.setVisualStyleHelpButton(this.createHelpButton());
			this.getVisualStylePanel().add(this.getVisualStyleHelpButton());
			this.getNetworkPanelRef().add(this.getVisualStylePanel(), 
			BorderLayout.SOUTH);
		} else {
			this.changeNetworkVisualStyle(networkName);
		}	
	}
	
	/**
	 * Present help information to user in a pop-up dialog
	 * box
	 * @param String dialogTitle
	 * @param String helpInfo
	 * @return null
	 */
	private  void help(String dialogTitle, String helpInfo) {
		String formatting = "<html><body style='width: 300px'>";
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame, formatting + helpInfo, dialogTitle, JOptionPane.QUESTION_MESSAGE);
	}
	
	/**
	 * Create help button
	 * @param null
	 * @return JButton helpButton
	 */
	private  JButton createHelpButton() {
		//URL iconURL = Thread.currentThread().getContextClassLoader().getResource("help.png");
		URL iconURL =this.getClass().getClassLoader().getResource("help.png");
		ImageIcon iconSearch = new ImageIcon(iconURL);
		JButton helpButton = new JButton(iconSearch);
	    helpButton.setBorder(null);
	    helpButton.setContentAreaFilled(false);
		helpButton.setToolTipText("Visual Style Help");
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println(getVisualStyleSelectorType());
				switch (getVisualStyleSelectorType()) {
					case VisualStyles.DEFAULT_VISUAL_STYLE:
						help("Default visual style", 
								       VisualStyles.DEFAULT_VISUAL_STYLE_HELP);
						break;
					case VisualStyles.INCITES_LITE_VISUAL_STYLE:
						help("Incites 'Lite' Visual Style",
								       VisualStyles.INCITES_LITE_VISUAL_STYLE_HELP);						
							break;
					case VisualStyles.PUBMED_LITE_VISUAL_STYLE:
						help("Pubmed 'Lite' Visual Style",
								       VisualStyles.PUBMED_LITE_VISUAL_STYLE_HELP);
						break;
					case VisualStyles.SCOPUS_LITE_VISUAL_STYLE:
						help("Pubmed 'Lite' Visual Style",
								       VisualStyles.SCOPUS_LITE_VISUAL_STYLE_HELP);
						break;
				}
			}
		});
		return helpButton;
	}

	/**
	 * Add a network to app's network panel
	 * @param CyNetwork network
	 * @return null
	 */
	public  void addNetworkToNetworkPanel(SocialNetwork socialNetwork) {
		
		CyNetwork network = socialNetwork.getCyNetwork();
		String networkName = socialNetwork.getNetworkName();
		int networkType = socialNetwork.getNetworkType();
	
		// Create a new table and add network's info if there's none
		if (getNetworkTableRef() ==  null) {		
			
			DefaultTableModel model = new DefaultTableModel(); 
			JTable networkTable = new JTable(model);
			networkTable.setEnabled(true);
			
			// Add columns to table
			for (String columnName : this.appManager.getNetworkTableColumnNames()) {
				model.addColumn((String) columnName);
			}
			
			// Add row to table
			model.addRow(new Object[] {(String) networkName, (Integer) network.getNodeCount(), 
					         (Integer) network.getEdgeCount(), Category.toString(networkType)});
					
			// Set table reference
			this.setNetworkTableRef(networkTable);
			
			// Context menu. Displayed to user on the event of a click (on the network table)
			final JPopupMenu destroyNetworkContextMenu = new JPopupMenu();
			destroyNetworkContextMenu.add(this.createDestroyNetworkMenuItem());
		    
			networkTable.addMouseListener(new MouseAdapter() {
		        public void mousePressed(MouseEvent e) {
		        
		            // Clicking on a network once causes it to be selected
		            if (e.getClickCount() == 1) {
		            	
		                JTable target = (JTable) e.getSource();		                
		                int row = target.getSelectedRow();
		                
		                // Get selected network name
		                String networkName = (String) getNetworkTableRef().getModel()
		                		                        .getValueAt
		                		                        (row, 0);
		                setSelectedNetwork(networkName);
		    			// Change network visual style		                
			    		changeNetworkVisualStyle(networkName);
			    		// Show network view
			    		appManager.setCurrentNetworkView(networkName);
		            }
					// Right clicking on a network will bring up
		        	// the destroy network context menu
					if (e.getButton() == MouseEvent.BUTTON3) {
						// Display context menu to user with all associated options.
						destroyNetworkContextMenu.show(getNetworkTableRef(), e.getX(), e.getY());
					} 
		        }
		    });
	
			JScrollPane networkTablePane = new JScrollPane(this.getNetworkTableRef());
			networkTablePane.setPreferredSize(new Dimension(200, 100));
			this.getNetworkPanelRef().add(networkTablePane, BorderLayout.NORTH);
			
			this.setNetworkSummaryPanelRef(this.createNetworkSummaryPanel(socialNetwork));
			this.getNetworkPanelRef().add(this.getNetworkSummaryPanelRef(), BorderLayout.CENTER);
	
		// Add network info to table
		} else {
			DefaultTableModel networkTableModel = (DefaultTableModel) getNetworkTableRef()
					                                                           .getModel();
			networkTableModel.addRow(new Object[] {networkName, (Integer) network.getNodeCount(), 
					         (Integer) network.getEdgeCount(), Category.toString(networkType)});
			
		}
		
		this.updateNetworkSummaryPanel(socialNetwork);		
		this.addNetworkVisualStyle(socialNetwork);
		
		this.getNetworkPanelRef().revalidate();
		this.getNetworkPanelRef().repaint();
	
	}
	
	/**
	 * Change network visual style to one more suited to
	 * the specified network. Assumes network is already
	 * loaded into Cytoscape.
	 * @param String networkName
	 * @return null
	 */
	public  void changeNetworkVisualStyle(String networkName) {
		int visualStyleType = (Integer) this.appManager.getSocialNetworkMap().get(networkName)
				                             .getDefaultVisualStyle();
		TitledBorder visualStylePanelBorder = (TitledBorder) this.getVisualStylePanel()
				                                                      .getBorder();
		if (networkName.length() >= 35) {
			networkName = networkName.substring(0, 34) + "...";
		}
		visualStylePanelBorder.setTitle(networkName + " Visual Styles");
		this.swapVisualStyleSelector(visualStyleType);
		this.getVisualStylePanel().revalidate();
		this.getVisualStylePanel().repaint();
	}

	/**
	 * Create bottom panel. Bottom panel will contain main
	 * panel controls (reset, close). As well as a network
	 * panel that will allow the user to change network
	 * parameters at their convenience.
	 * @param null
	 * @return JPanel bottomPanel
	 */
	private  JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		this.setNetworkPanelRef(this.createNetworkPanel());
		bottomPanel.add(this.getNetworkPanelRef(), BorderLayout.NORTH);
		this.setControlPanelRef(this.createControlPanel());
		bottomPanel.add(this.getControlPanelRef(), BorderLayout.SOUTH);
		return bottomPanel;
	}

	/**
	 * Create category option selector.
	 * @param null 
	 * @return JComboBox optionSelector
	 */
	private  JComboBox createCategoryOptionSelector() {
		//Create new JComboBox
		JComboBox categoryOptionSelector = new JComboBox(Category.getCategoryList());
		categoryOptionSelector.setEditable(false);
		categoryOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		categoryOptionSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String category = (String) jcmbType.getSelectedItem();
				// Set selected category before performing switcharoo
				// NOTE: This step is imperative. Not doing this will
				// result in a null pointer exception being thrown.
				setSelectedCategory(Category.getCategoryID(category));
				// Perform switcharoo iff the panel being switched to is 
				// distinct from the current panel
				if (! category.trim().equalsIgnoreCase(appManager
					.getUserPanelRef().getSelectedInfoPanel().getName())) {
					appManager.getUserPanelRef().performSwitcharoo();
				}
				//Create new search filter
				getSearchFilter()
				.setModel(new DefaultComboBoxModel(Category.getSearchFilterList(getSelectedCategory())));
				
			}
	
		});
		return categoryOptionSelector;
	}

	/**
	 * Create new category panel; will allow user to select a particular search category
	 * @param null
	 * @return JPanel categoryPanel
	 */
	private  JPanel createCategoryPanel() {
		JPanel categoryPanel = new JPanel();
		categoryPanel.setBorder(BorderFactory.createTitledBorder("Category"));
		categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));
		categoryPanel.add(this.createCategoryOptionSelector());
		return categoryPanel;
	}

	/**
	 *Create close button. Close button closes current panel
	 *@param null
	 *@return JButton closeButton
	 */
	private  JButton createCloseButton() {
		JButton closeButton = new JButton("Close");
		closeButton.setToolTipText("Close Social Network Panel");
		// Clicking of button results in the closing of current panel
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				appManager.closeUserPanel();
			}
		});
		return closeButton;
	}

	/**
	 * Create new control panel for use in main app panel; 
	 * will allow users to reset or close the main app panel.
	 * @param null
	 * @return JPanel control panel
	 */
	private  JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();
			
		controlPanel
		.setLayout(new FlowLayout());
	
		controlPanel.add(this.createResetButton());
		controlPanel.add(this.createCloseButton());
		
		return controlPanel;
	}

	/**
	 * Create 'destroy menu item'. Function is self-evident.
	 * @param null
	 * @return JMenuItem destroyNetworkMenuItem
	 */
	public  JMenuItem createDestroyNetworkMenuItem() {
		// Create new menu item.
		JMenuItem destroyNetworkMenuItem = new JMenuItem("Destroy Network");
		// Clicking of menu item results in the destruction of a network
		destroyNetworkMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Destroy currently selected network
				String selectedNetwork = getSelectedNetwork();
				appManager.destroyNetwork(appManager.getSocialNetworkMap().get(selectedNetwork)
						                          .getCyNetwork());
			}
		});
		return destroyNetworkMenuItem;
	}

	/**
	 * Create new network panel; will allow lay users to easily modify network
	 * parameters
	 * @param null
	 * @return JPanel networkPanel
	 */
	private  JPanel createNetworkPanel() {
		JPanel networkPanel = new JPanel();
		networkPanel.setBorder(BorderFactory.createTitledBorder("Social Network"));
		networkPanel.setLayout(new BorderLayout());
		return networkPanel;
	}

	/**
	 * Create reset button. Reset button resets info panel. All form 
	 * fills will be cleared and info panel will revert to its natural
	 * undisturbed state
	 * @param null
	 * @return JButton resetButton
	 */
	private  JButton createResetButton() {
		JButton resetButton = new JButton("Reset");
		resetButton.setToolTipText("Reset");
		// 'Reset' is achieved by creating a new panel
		// and replacing the current panel with the new
		// (Memory cost is trivial)
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				appManager.getUserPanelRef().performSwitcharoo();
			}
		});
		return resetButton;
	}

	/**
	 * Create new search box. Will allow user to search 
	 * any social website
	 * @param null
	 * @return JTextField searchBox
	 */
	private  JTextField createSearchBox() {
		// Create searchbox. Save a reference to it, and add
		JTextField searchBox = new JTextField();
		searchBox.setEditable(true);
		// Tapping enter results in the automatic generation of a network
		searchBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (getSearchBox().getText().trim().isEmpty()) {
					CytoscapeUtilities.notifyUser("Please enter a search term into the search box");
				} else if (! isValidInput(getSearchBox().getText().trim())) {
					CytoscapeUtilities.notifyUser("Illegal characters present. Please enter a valid search term.");
				} else {
					appManager.createNetwork(getSearchBox().getText(), getSelectedCategory());
				}
			}
		});
		return searchBox;
	}

	/**
	 *Create search button. Search button allows user to commit search.
	 *NOTE: Button not 100% necessary. Its function can be duplicated 
	 *by a simple tap on the return key
	 *@param null
	 *@return JButton search
	 */
	private  JButton createSearchButton() {
		//URL iconURL = Thread.currentThread().getContextClassLoader().getResource("search.png");
		URL iconURL = this.getClass().getClassLoader().getResource("search.png");
		ImageIcon iconSearch = new ImageIcon(iconURL);
		JButton searchButton = new JButton(iconSearch);
		searchButton.setToolTipText("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (getSearchBox().getText().trim().isEmpty()) {
					CytoscapeUtilities.notifyUser("Please enter a search term");
				} else if (! isValidInput(getSearchBox()
						   .getText().trim())) {
					CytoscapeUtilities.notifyUser("Illegal characters present. " +
							             "Please enter a valid search term.");
				} else {
					// Create a network 
					appManager.createNetwork(getSearchBox().getText()
					, getSelectedCategory());
				}
			}
		});
		return searchButton;
	}
	
	/**
	 * Create search option selector.
	 * @param null 
	 * @return JComboBox optionSelector
	 */
	private  JComboBox createSearchFilter() {
		JComboBox searchOptionSelector = new JComboBox(Category.getSearchFilterList 
				                                      (this.getSelectedCategory()));
		searchOptionSelector.setEditable(false);
		searchOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		searchOptionSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// SEARCH FILTERS
				// WIP (Work In Progress)
			}
		});
		return searchOptionSelector;
	}

	/**
	 * Create new search panel. Will allow user to search for a particular
	 * network. A default search filter will also be made available to the user.
	 * @param null
	 * @return JPanel searchPanel
	 */
	private  JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel();
		
		// Organize panel horizontally.
		searchPanel
		.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		
		searchPanel.setBorder(BorderFactory.createTitledBorder("Pubmed Search"));
		
		// Add search box to panel
		this.setSearchBox(this.createSearchBox());
		searchPanel.add(this.getSearchBox());
		
		// Add search button to panel
		searchPanel.add(this.createSearchButton());
		
		// Add search filter to panel
		/**
		 * NOTE: DISABLED TEMPORARILY
		 */
//		UserPanel.setSearchFilter(UserPanel.createSearchFilter());
//		searchPanel.add(UserPanel.getSearchFilter());
		
		return searchPanel;
	}

	/**
	 * Create new top panel for use in main app panel. Top panel
	 * will contain search box and category option seelctor.
	 * 
	 * @param null
	 * @return JPanel topPanel
	 */
	private  JPanel createTopPanel() {
		
		JPanel topPanel = new JPanel();
		
		topPanel.setLayout(new BorderLayout());
		/**
		 * NOTE: DISABLED TEMPORARILY
		 */
//		topPanel.add(UserPanel.createCategoryPanel(), BorderLayout.NORTH);
		topPanel.add(this.createSearchPanel(), BorderLayout.SOUTH);
		
		return topPanel;
	}

	/**
	 * Create visual style panel. Will allow the user to switch visual styles for 
	 * a particular network.
	 * @param String networkName
	 * @return JPanel visualStylePanel
	 */
	public  JPanel createVisualStylePanel(String networkName) {
		JPanel visualStylePanel = new JPanel();
		visualStylePanel.setBorder(BorderFactory.createTitledBorder(networkName + " Visual Styles"));
		visualStylePanel.setLayout(new BoxLayout(visualStylePanel, BoxLayout.X_AXIS));
		return visualStylePanel;
	}

	/**
	 * Create visual style selector. The type of selector created is dependent on
	 * the network's type. Different networks have different custom visual styles.
	 * 
	 * @param int visualStyleSelectorType
	 * @return JComboBox visualStyleSelector
	 */
	public  JComboBox createVisualStyleSelector(int visualStyleSelectorType) {
		JComboBox visualStyleSelector = new JComboBox(new VisualStyles().getVisualStyleList(visualStyleSelectorType));
		visualStyleSelector.setEditable(false);
		visualStyleSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		visualStyleSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String visualStyle = (String) jcmbType.getSelectedItem();
				int visualStyleID = new VisualStyles().getVisualStyleID(visualStyle);
				setSelectedVisualStyle(visualStyleID);
				appManager.applyVisualStyle(visualStyle);
			}
		});
		return visualStyleSelector;
	}

	/**
	 * Get control panel reference
	 * @param null
	 * @return JPanel controlPanelRef
	 */
	private  JPanel getControlPanelRef() {
		return this.controlPanelRef;
	}

	/**
	 * Get network panel reference
	 * @param null
	 * @return JPanel networkPanelRef
	 */
	public  JPanel getNetworkPanelRef() {
		return this.networkPanelRef;
	}

	/**
	 * Get network table reference
	 * @param null
	 * @return JTable networkTableRef
	 */
	public  JTable getNetworkTableRef() {
		return this.networkTableRef;
	}

	/**
	 * Get user panel searchbox
	 * @param null
	 * @return JTextField searchBox
	 */
	public  JTextField getSearchBox() {
		return this.searchBoxRef;
	}

	/**
	 * Get search filter
	 * @param null
	 * @return JComboBox searchFilter
	 */
	private  JComboBox getSearchFilter() {
		return this.searchFilter;
	}

	/**
	 * Get selected category
	 * @param null
	 * @return int selectedCategory
	 */
	public  int getSelectedCategory() {
		return this.selectedCategory;
	}

	/**
	 * Get selected network
	 * @param null
	 * @return String network
	 */
	public  String getSelectedNetwork() {
		return this.selectedNetwork;
	}

	/**
	 * Get selected visual style
	 * @param null
	 * @return int visualStyle
	 */
	private  int getSelectedVisualStyle() {
		return this.selectedVisualStyle;
	}

	/**
	 * Get visual style panel
	 * @param null
	 * @return JPanel visualStylePanel
	 */
	public  JPanel getVisualStylePanel() {
		return this.visualStylePanel;
	}

	/**
	 * Get visual style selector
	 * @param null
	 * @return JComboBox visualStyleSelector
	 */
	public  JComboBox getVisualStyleSelector() {
		return this.visualStyleSelector;
	}

	/**
	 * Get visual style type
	 * @param null
	 * @return int visualStyleType
	 */
	public  int getVisualStyleSelectorType() {
		return this.visualStyleSelectorType;
	}

	/**
	 * Return true iff input does not contain illegal characters i.e. (!@#$%^&*)
	 * @param String input
	 * @return boolean
	 */
	private  boolean isValidInput(String input) {
		Pattern pattern = Pattern.compile("[!@#$%^&*~]+?");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return false;
		}
		return true;
	}

	/**
	 * Set control panel reference
	 * @param JPanel controlPanelRef
	 * @return null
	 */
	private  void setControlPanelRef(JPanel controlPanelRef) {
		this.controlPanelRef = controlPanelRef;
	}	
	
	/**
	 * Set network panel reference
	 * @param JPanel networkPanelRef
	 * @return null
	 */
	private  void setNetworkPanelRef(JPanel networkPanelRef) {
		this.networkPanelRef = networkPanelRef;
	}

	/**
	 * Set network table reference
	 * @param JTable networkTableRef
	 * @return null
	 */
	public  void setNetworkTableRef(JTable networkTableRef) {
		this.networkTableRef = networkTableRef;
	}

	/**
	 * Set user panel searchbox
	 * @param JTextField searchBox
	 * @return null
	 */
	public  void setSearchBox(JTextField searchBox) {
		this.searchBoxRef = searchBox;
	}

	/**
	 * Set search filter
	 * @param JComboBox searchFilter
	 * @return null
	 */
	private  void setSearchFilter(JComboBox searchOptionSelector) {
		this.searchFilter = searchOptionSelector;
	}

	/**
	 * Set selected category
	 * @param int category
	 * @return null
	 */
	private  void setSelectedCategory(int category) {
		this.selectedCategory = category;
	}

	/**
	 * Set selected network
	 * @param String network
	 * @return null
	 */
	public  void setSelectedNetwork(String network) {
		this.selectedNetwork = network;
	}

	/**
	 * Set selected visual style 
	 * @param int visualStyle
	 * @return null
	 */
	public  void setSelectedVisualStyle(int visualStyle) {
		this.selectedVisualStyle = visualStyle;
	}

	/**
	 * Set visual style panel
	 * @param JPanel visualStylePanel
	 * @return null
	 */
	public  void setVisualStylePanel(JPanel visualStylePanel) {
		this.visualStylePanel = visualStylePanel;
	}

	/**
	 * Set visual style selector
	 * @param JComboBox visualStyleSelector
	 * @return null
	 */
	public  void setVisualStyleSelector(JComboBox visualStyleSelector) {
		this.visualStyleSelector = visualStyleSelector;
	}

	/**
	 * Set visual style selector type. 
	 * @param int visualStyleType
	 * @return null
	 */
	public  void setVisualStyleSelectorType(int visualStyleType) {
		this.visualStyleSelectorType = visualStyleType;
	}

	/**
	 * Swap visual style selector to given type
	 * @param int type
	 * @return null
	 */
	public  void swapVisualStyleSelector(int visualStyleSelectorType) {
		if (this.getVisualStyleSelectorType() != visualStyleSelectorType) {
			this.getVisualStylePanel().remove(this.getVisualStyleSelector());
			this.getVisualStylePanel().remove(this.getVisualStyleHelpButton());
			this.setVisualStyleSelectorType(visualStyleSelectorType);
			this.setVisualStyleSelector(this.createVisualStyleSelector(this
					                                  .getVisualStyleSelectorType()));
			this.getVisualStylePanel().add(this.getVisualStyleSelector());
			this.getVisualStylePanel().add(this.getVisualStyleHelpButton());
		}
	}

	public Component getComponent() {
		return this;
	}

	
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}
	
	
	/**
	 * Get panel icon
	 * @param null
	 * @return Icon panelIcon
	 */
	public Icon getIcon() {
		return null;
	}

	/**
	 * Get current info panel
	 * @param null
	 * @return JPanel currentInfoPanel
	 */
	private JPanel getSelectedInfoPanel() {
		return this.infoPanelRef;
	}

	/**
	 * Get panel title
	 * @param null
	 * @return String panelTitle
	 */
	public String getTitle() {
		return "Social Network";
	}

	/**
	 * Switch info panel to the new one
	 * that user's selected
	 * @param null
	 * @return null
	 */
	private void performSwitcharoo() {
			// Remove current info panel
			this.remove(this.getSelectedInfoPanel());
			switch (this.getSelectedCategory()) {
				case Category.DEFAULT:
					this.setSelectedInfoPanel(Category.createDefaultInfoPanel());
					break;
				case Category.ACADEMIA: 
					this.setSelectedInfoPanel(this.academiaPanel.createAcademiaInfoPanel());
					break;
				case Category.TWITTER:
					this.setSelectedInfoPanel(Category.createTwitterInfoPanel());
					break;
				case Category.LINKEDIN:
					this.setSelectedInfoPanel(Category.createLinkedInInfoPanel());
					break;
				case Category.YOUTUBE:
					this.setSelectedInfoPanel(Category.createYoutubeInfoPanel());
					break;
			}
			// Add selected panel
			this.add(this.getSelectedInfoPanel(), BorderLayout.CENTER);	
			// Refresh user panel to reflect update
			this.revalidate();
			this.repaint();
	}

	/**
	 * Set current info panel
	 * @param JPanel infoPanel
	 * @return null
	 */
	private void setSelectedInfoPanel(JPanel infoPanel) {
		this.infoPanelRef = infoPanel;
	}

	/**
	 * Create network summary panel
	 * @param SocialNetwork socialNetwork
	 * @return JPanel networkSummaryPanel
	 */
	public  JPanel createNetworkSummaryPanel(SocialNetwork socialNetwork) {
		// Create network summary panel
		JPanel networkSummaryPanel = new JPanel();
	    networkSummaryPanel.setLayout(new BorderLayout());
	    
	    //Create network summary pane
	    JTextPane networkSummaryPane = new JTextPane();
		networkSummaryPane.setContentType("text/html");
		networkSummaryPane.setEditable(false);
		networkSummaryPane.setAutoscrolls(true);
		this.setNetworkSummaryPaneRef(networkSummaryPane);
		JTextPane fileSummaryPane = new JTextPane();
		fileSummaryPane.setContentType("text/html");
		fileSummaryPane.setEditable(false);
		fileSummaryPane.setAutoscrolls(true);
	    this.setFileSummaryPaneRef(fileSummaryPane);
	    
	    //add both textPanes to a Panel
	    JPanel summaryPanel = new JPanel();
	    summaryPanel.setLayout(new BorderLayout());
	    summaryPanel.add(networkSummaryPane, BorderLayout.NORTH);
	    summaryPanel.add(fileSummaryPane, BorderLayout.SOUTH);
	    
	    JScrollPane wrapperPane = new JScrollPane();
		wrapperPane.getViewport().add(summaryPanel);
		wrapperPane.setPreferredSize(new Dimension(50,100));
	    networkSummaryPanel.add(wrapperPane, BorderLayout.NORTH);
	    
		return networkSummaryPanel;
	}

	/**
	 * Update network summary panel (for Academia networks only)
	 * @param SocialNetwork socialNetwork
	 * @return null
	 */
	public  void updateNetworkSummaryPanel(SocialNetwork socialNetwork){
		String networkName = "DEFAULT", networkSummary = "N/A";
		if (socialNetwork != null) {
			networkName = socialNetwork.getNetworkName();
			if (networkName.length() >= 10) {
				networkName = networkName.substring(0, 9) + " ...";
			}
			networkSummary = socialNetwork.getSummary();
		}	
		this.getNetworkSummaryPanelRef().setBorder(BorderFactory.createTitledBorder(networkName + " Summary"));
		this.getNetworkSummaryPaneRef().setText(networkSummary);
		
        
        		//if this is an incites network add the summaries
        		if(socialNetwork.getNetworkType() == Category.INCITES){
        			GenerateReports gr = new GenerateReports(socialNetwork.getPublications(),socialNetwork.getNetworkName());
        			HashMap<String,String> files = gr.createReports();
		
        			Object[] keys = files.keySet().toArray();
        			Arrays.sort(keys);
        			
        			StyledDocument doc = this.getFileSummaryPaneRef().getStyledDocument();
        	        SimpleAttributeSet attr = new SimpleAttributeSet();
        	        	
        	        //Add a heading label
        	        JLabel header = new JLabel("List of summary files:");
        	        this.getFileSummaryPaneRef().insertComponent(header);
        	        try {
						doc.insertString(doc.getLength(), "\n", attr );
					} catch (BadLocationException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
        	        
        			//for each file add a clickable label to launch file in browser.
        			for(Object key:keys){
        				JLabel websiteLabel = new JLabel();
        				final File url = new File(files.get(key));
        				websiteLabel.setText("<html><a href=\"\">"+key+"</a><br></html>");
        				websiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        				websiteLabel.addMouseListener(new MouseAdapter() {
			            @Override
			            public void mouseClicked(MouseEvent e) {
			                    try {
			                            Desktop.getDesktop().open(url);
			                    } catch (IOException ex) {
			                    		ex.printStackTrace();
			                    } 
			            }
			        });
        				this.getFileSummaryPaneRef().setCaretPosition(this.getFileSummaryPaneRef().getDocument().getLength());        	            
        				this.getFileSummaryPaneRef().insertComponent(websiteLabel);
        				try {
							doc.insertString(doc.getLength(), "\n", attr );
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				
        			}			
        		}
        
		this.getNetworkSummaryPanelRef().revalidate();
		this.getNetworkSummaryPanelRef().repaint();
	}

	/**
	 * Set network summary panel reference
	 * @param JPanel networkSummaryPanel
	 * @return null
	 */
	private  void setNetworkSummaryPanelRef(JPanel networkSummaryPanel) {
		this.networkSummaryPanelRef = networkSummaryPanel;
	}

	/**
	 * Get network summary panel reference
	 * @param null
	 * @return JPanel networkSummaryPanel
	 */
	private  JPanel getNetworkSummaryPanelRef() {
		return this.networkSummaryPanelRef;
	}

	/**
	 * Get visual style help button
	 * @param null
	 * @return JButton visualStyleHelpButton
	 */
	public  JButton getVisualStyleHelpButton() {
		return this.helpButton;
	}

	/**
	 * Set visual style help button
	 * @param JButton visualStyleHelpButton
	 * @return null
	 */
	public  void setVisualStyleHelpButton(JButton visualStyleHelpButton) {
		this.helpButton = visualStyleHelpButton;
	}

	/**
	 * Get network summary pane reference
	 * @param null
	 * @return JTextPane networkSummaryPaneRef
	 */
	public  JTextPane getNetworkSummaryPaneRef() {
		return this.networkSummaryPaneRef;
	}

	/**
	 * Set network summary pane reference
	 * @param JTextPane networkSummaryPaneRef
	 * @return null
	 */
	public  void setNetworkSummaryPaneRef(JTextPane networkSummaryPaneRef) {
		this.networkSummaryPaneRef = networkSummaryPaneRef;
	}
	/**
	 * Get network summary pane reference
	 * @param null
	 * @return JTextPane networkSummaryPaneRef
	 */
	public  JTextPane getFileSummaryPaneRef() {
		return this.fileSummaryPaneRef;
	}

	/**
	 * Set network summary pane reference
	 * @param JTextPane networkSummaryPaneRef
	 * @return null
	 */
	public  void setFileSummaryPaneRef(JTextPane fileSummaryPaneRef) {
		this.fileSummaryPaneRef = fileSummaryPaneRef;
	}
	public AcademiaPanel getAcademiaPanel() {
		return academiaPanel;
	}

	public void setAcademiaPanel(AcademiaPanel academiaPanel) {
		this.academiaPanel = academiaPanel;
	}
	
	

}
