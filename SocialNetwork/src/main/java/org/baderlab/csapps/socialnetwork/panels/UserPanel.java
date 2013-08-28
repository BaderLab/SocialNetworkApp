package main.java.org.baderlab.csapps.socialnetwork.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;


import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.SocialNetwork;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;

import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
	
/**
 * Main panel for Social Network App
 * @author Victor Kofia
 */
public class UserPanel extends JPanel implements CytoPanelComponent {
	/**
	 * Reference to control panel
	 */
	private static JPanel controlPanelRef = null;
	/**
	 * Reference to the currently displayed info panel
	 */
	private static JPanel infoPanelRef = null;
	/**
	 * Reference to network panel
	 */
	private static JPanel networkPanelRef = null;
	/**
	 * Reference to network table
	 */
	private static JTable networkTableRef = null;
	/**
	 * Reference to the search box. Necessary for extracting queries.
	 */
	private static JTextField searchBoxRef = null;
	/**
	 * Search filter
	 */
	private static JComboBox searchFilter = null;
	/**
	 * Category that user has selected
	 */
	private static int selectedCategory = Category.DEFAULT;
	/**
	 * Network that user has selected
	 */
	private static String selectedNetwork = null;
	/**
	 * Visual style that user has selected
	 */
	private static int selectedVisualStyle = Category.DEFAULT;
	private static final long serialVersionUID = 8292806967891823933L;
	/**
	 * Reference to top panel
	 */
	private static JPanel topPanelRef = null;
	/**
	 * Reference to help button
	 */
	private static JButton helpButton = null;
	/**
	 * Reference to visual style panel
	 */
	private static JPanel visualStylePanel = null;
	/**
	 * Visual style selector
	 */
	private static JComboBox visualStyleSelector = null;
	/**
	 * The visual style selector type currently being displayed to the user
	 */
	private static int visualStyleSelectorType = Category.DEFAULT;
	/**
	 * Reference to network stats panel
	 */
	private static JPanel networkStatsPanelRef = null;
	/**
	 * Reference to network stats pane
	 */
	private static JTextPane networkStatsPaneRef = null;
	
	/**
	 * Create a new panel
	 * @param null
	 * @return null
	 */
	public UserPanel() {
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400,200));
		
		// NOTE: An 'Academia' flavored UI has been set as the default
		UserPanel.setSelectedCategory(Category.ACADEMIA);
			
		// Add top panel
		UserPanel.topPanelRef = UserPanel.createTopPanel();
		this.add(UserPanel.topPanelRef, BorderLayout.NORTH);
		
		// Add the default info panel (Academia)
		this.setSelectedInfoPanel(AcademiaPanel.createAcademiaInfoPanel());
		this.add(UserPanel.infoPanelRef, BorderLayout.CENTER);
		
		// Add bottom panel
		this.add(UserPanel.createBottomPanel(), BorderLayout.SOUTH);
		
		// Save a reference to this panel object
		Cytoscape.setUserPanelRef(this);
		
	}

	/**
	 * Add network's visual style to user panel
	 * @param Network network
	 * @return null
	 */
	public static void addNetworkVisualStyle(SocialNetwork socialNetwork) {
		int networkType = (socialNetwork == null) ? Category.DEFAULT : socialNetwork.getNetworkType();
		String networkName = (socialNetwork == null) ? "DEFAULT" : socialNetwork.getNetworkName();
		if (UserPanel.getVisualStylePanel()  == null) {
			// Create new visual style panel
			UserPanel.setVisualStylePanel(UserPanel.createVisualStylePanel(networkName));
			// It is imperative that visual selector type be set before the visual
			// selector. Not doing this will cause random & seemingly untraceable
			// errors to occur.
			UserPanel.setVisualStyleSelectorType(networkType);
			Cytoscape.setVisualStyleID(networkType);
			UserPanel.setVisualStyleSelector(UserPanel.createVisualStyleSelector
					                        (networkType));
			UserPanel.getVisualStylePanel().add(UserPanel.getVisualStyleSelector());
			UserPanel.setVisualStyleHelpButton(UserPanel.createHelpButton());
			UserPanel.getVisualStylePanel().add(UserPanel.getVisualStyleHelpButton());
			UserPanel.getNetworkPanelRef().add(UserPanel.getVisualStylePanel(), 
			BorderLayout.SOUTH);
		} else {
			UserPanel.changeNetworkVisualStyle(networkName);
		}	
	}
	
	/**
	 * Present help information to user in a pop-up dialog
	 * box
	 * @param String dialogTitle
	 * @param String helpInfo
	 * @return null
	 */
	private static void help(String dialogTitle, String helpInfo) {
		String formatting = "<html><body style='width: 300px'>";
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame, formatting + helpInfo, dialogTitle, JOptionPane.QUESTION_MESSAGE);
	}
	
	/**
	 * Create help button
	 * @param null
	 * @return JButton helpButton
	 */
	private static JButton createHelpButton() {
		URL iconURL = UserPanel.class.getClassLoader().getResource("help.png");
		ImageIcon iconSearch = new ImageIcon(iconURL);
		JButton helpButton = new JButton(iconSearch);
	    helpButton.setBorder(null);
	    helpButton.setContentAreaFilled(false);
		helpButton.setToolTipText("Visual Style Help");
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				switch (Cytoscape.getVisualStyleID()) {
					case Category.DEFAULT:
						UserPanel.help("Default visual style", 
								"The default visual style is the bas" +
								"e visual style. It makes use of no n" +
								"etwork attributes and is for all in" +
								"tents and purposes non-essential.");
						break;
					case Category.CHIPPED:
						UserPanel.help("Chipped Visual Style", 
								"The Chipped visual style is designe" +
								"d for fully annotated networks. Thes" +
								"e are networks that are assumed to " +
								"contain reasonably accurate institu" +
								"tion information. Institutions are m" +
								"atched to their respective location" +
								"s, which are color matched. Institut" +
								"ions for which no location has been" +
								" found are classified as 'other' (p" +
								"urple). Color matching is determined" +
								" by a local map and as such may or " +
								"may not be adequate. Manually adding" +
								" an institution and its correspondi" +
								"ng location can be done by navigati" +
								"ng to: <b>Apps</b> - <b>SocialNetwo" +
								"rkApp</b> - <b>Incites</b> - <b>Add" +
								" institution</b>");
						break;
					case Category.VANUE:
						UserPanel.help("Vanue Visual Style",
								"The Vanue visual style is designed " +
								"for academia networks that lack ins" +
								"titution information.. Node size is d" +
								"etermined by times cited value and " +
								"edge width varies depending on how " +
								"many publications two individual no" +
								"des (authors) have co-authored."
);
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
	public static void addNetworkToNetworkPanel(SocialNetwork socialNetwork) {
		
		CyNetwork network = socialNetwork.getCyNetwork();
		String networkName = socialNetwork.getNetworkName();
		int networkType = socialNetwork.getNetworkType();
	
		// Create a new table and add network's info if there's none
		if (getNetworkTableRef() ==  null) {		
			
			DefaultTableModel model = new DefaultTableModel(); 
			JTable networkTable = new JTable(model);
			networkTable.setEnabled(true);
			
			// Add columns to table
			for (String columnName : Cytoscape.getNetworkTableColumnNames()) {
				model.addColumn((String) columnName);
			}
			
			// Add row to table
			model.addRow(new Object[] {(String) networkName, (Integer) network.getNodeCount(), 
					         (Integer) network.getEdgeCount(), Category.toString(networkType)});
					
			// Set table reference
			UserPanel.setNetworkTableRef(networkTable);
			
			// Context menu. Displayed to user on the event of a click (on the network table)
			final JPopupMenu destroyNetworkContextMenu = new JPopupMenu();
			destroyNetworkContextMenu.add(UserPanel.createDestroyNetworkMenuItem());
		    
			networkTable.addMouseListener(new MouseAdapter() {
		        public void mousePressed(MouseEvent e) {
		        
		            // Clicking on a network once causes it to be selected
		            if (e.getClickCount() == 1) {
		            	
		                JTable target = (JTable) e.getSource();		                
		                int row = target.getSelectedRow();
		                
		                // Get selected network name
		                String networkName = (String) UserPanel.getNetworkTableRef().getModel()
		                		                        .getValueAt
		                		                        (row, 0);
		                UserPanel.setSelectedNetwork(networkName);
		    			// Change network visual style		                
			    		UserPanel.changeNetworkVisualStyle(networkName);
			    		// Show network view
			    		Cytoscape.setCurrentNetworkView(networkName);
		            }
					// Right clicking on a network will bring up
		        	// the destroy network context menu
					if (e.getButton() == MouseEvent.BUTTON3) {
						// Display context menu to user with all associated options.
						destroyNetworkContextMenu.show(UserPanel.getNetworkTableRef(), e.getX(), e.getY());
					} 
		        }
		    });
	
			JScrollPane networkTablePane = new JScrollPane(UserPanel.getNetworkTableRef());
			networkTablePane.setPreferredSize(new Dimension(200, 100));
			UserPanel.getNetworkPanelRef().add(networkTablePane, BorderLayout.NORTH);
			
			UserPanel.setNetworkStatsPanelRef(UserPanel.createNetworkStatsPanel(socialNetwork));
			UserPanel.getNetworkPanelRef().add(UserPanel.getNetworkStatsPanelRef(), BorderLayout.CENTER);
	
		// Add network info to table
		} else {
			DefaultTableModel networkTableModel = (DefaultTableModel) getNetworkTableRef()
					                                                           .getModel();
			networkTableModel.addRow(new Object[] {networkName, (Integer) network.getNodeCount(), 
					         (Integer) network.getEdgeCount(), Category.toString(networkType)});
			
		}
		
		
		UserPanel.updateNetworkStatsPanel(socialNetwork);
		
		// Add network visual styles
		UserPanel.addNetworkVisualStyle(socialNetwork);
	
		getNetworkPanelRef().revalidate();
		getNetworkPanelRef().repaint();
	
	}
	
	/**
	 * Change network visual style to one more suited to
	 * the specified network. Assumes network is already
	 * loaded into Cytoscape.
	 * @param String networkName
	 * @return null
	 */
	public static void changeNetworkVisualStyle(String networkName) {
		int networkType = (Integer) Cytoscape.getSocialNetworkMap().get(networkName)
				                             .getNetworkType();
		TitledBorder visualStylePanelBorder = (TitledBorder) UserPanel.getVisualStylePanel()
				                                                      .getBorder();
		if (networkName.length() >= 35) {
			networkName = networkName.substring(0, 34) + "...";
		}
		visualStylePanelBorder.setTitle(networkName + " Visual Styles");
		UserPanel.swapVisualStyleSelector(networkType);
		UserPanel.getVisualStylePanel().revalidate();
		UserPanel.getVisualStylePanel().repaint();
	}

	/**
	 * Create bottom panel. Bottom panel will contain main
	 * panel controls (reset, close). As well as a network
	 * panel that will allow the user to change network
	 * parameters at their convenience.
	 * @param null
	 * @return JPanel bottomPanel
	 */
	private static JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		UserPanel.setNetworkPanelRef(UserPanel.createNetworkPanel());
		bottomPanel.add(UserPanel.getNetworkPanelRef(), BorderLayout.NORTH);
		UserPanel.setControlPanelRef(UserPanel.createControlPanel());
		bottomPanel.add(UserPanel.getControlPanelRef(), BorderLayout.SOUTH);
		return bottomPanel;
	}

	/**
	 * Create category option selector.
	 * @param null 
	 * @return JComboBox optionSelector
	 */
	private static JComboBox createCategoryOptionSelector() {
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
				UserPanel.setSelectedCategory(Category.getCategoryID(category));
				// Perform switcharoo iff the panel being switched to is 
				// distinct from the current panel
				if (! category.trim().equalsIgnoreCase(Cytoscape
					.getUserPanelRef().getSelectedInfoPanel().getName())) {
					Cytoscape.getUserPanelRef().performSwitcharoo();
				}
				//Create new search filter
				UserPanel.getSearchFilter()
				.setModel(new DefaultComboBoxModel(Category.getSearchFilterList(UserPanel.getSelectedCategory())));
				
			}
	
		});
		return categoryOptionSelector;
	}

	/**
	 * Create new category panel; will allow user to select a particular search category
	 * @param null
	 * @return JPanel categoryPanel
	 */
	private static JPanel createCategoryPanel() {
		JPanel categoryPanel = new JPanel();
		categoryPanel.setBorder(BorderFactory.createTitledBorder("Category"));
		categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));
		categoryPanel.add(UserPanel.createCategoryOptionSelector());
		return categoryPanel;
	}

	/**
	 *Create close button. Close button closes current panel
	 *@param null
	 *@return JButton closeButton
	 */
	private static JButton createCloseButton() {
		JButton closeButton = new JButton("Close");
		closeButton.setToolTipText("Close Social Network Panel");
		// Clicking of button results in the closing of current panel
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Cytoscape.closeUserPanel();
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
	private static JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();
			
		controlPanel
		.setLayout(new FlowLayout());
	
		controlPanel.add(UserPanel.createResetButton());
		controlPanel.add(UserPanel.createCloseButton());
		
		return controlPanel;
	}

	/**
	 * Create 'destroy menu item'. Function is self-evident.
	 * @param null
	 * @return JMenuItem destroyNetworkMenuItem
	 */
	public static JMenuItem createDestroyNetworkMenuItem() {
		// Create new menu item.
		JMenuItem destroyNetworkMenuItem = new JMenuItem("Destroy Network");
		// Clicking of menu item results in the destruction of a network
		destroyNetworkMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Destroy currently selected network
				String selectedNetwork = UserPanel.getSelectedNetwork();
				Cytoscape.destroyNetwork(Cytoscape.getSocialNetworkMap().get(selectedNetwork)
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
	private static JPanel createNetworkPanel() {
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
	private static JButton createResetButton() {
		JButton resetButton = new JButton("Reset");
		resetButton.setToolTipText("Reset");
		// 'Reset' is achieved by creating a new panel
		// and replacing the current panel with the new
		// (Memory cost is trivial)
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Cytoscape.getUserPanelRef().performSwitcharoo();
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
	private static JTextField createSearchBox() {
		// Create searchbox. Save a reference to it, and add
		JTextField searchBox = new JTextField();
		searchBox.setEditable(true);
		// Tapping enter results in the automatic generation of a network
		searchBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (UserPanel.getSearchBox().getText().trim().isEmpty()) {
					Cytoscape.notifyUser("Please enter a search term into the search box");
				} else if (! UserPanel.isValidInput(UserPanel.getSearchBox().getText().trim())) {
					Cytoscape.notifyUser("Illegal characters present. Please enter a valid search term.");
				} else {
					Cytoscape.createNetwork(UserPanel.getSearchBox().getText(), UserPanel.getSelectedCategory());
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
	private static JButton createSearchButton() {
		URL iconURL = UserPanel.class.getClassLoader().getResource("search.png");
		ImageIcon iconSearch = new ImageIcon(iconURL);
		JButton searchButton = new JButton(iconSearch);
		searchButton.setToolTipText("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (UserPanel.getSearchBox().getText().trim().isEmpty()) {
					Cytoscape.notifyUser("Please enter a search term");
				} else if (! UserPanel.isValidInput(UserPanel.getSearchBox()
						   .getText().trim())) {
					Cytoscape.notifyUser("Illegal characters present. " +
							             "Please enter a valid search term.");
				} else {
					// Create a network 
					Cytoscape.createNetwork(UserPanel.getSearchBox().getText()
					, UserPanel.getSelectedCategory());
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
	private static JComboBox createSearchFilter() {
		JComboBox searchOptionSelector = new JComboBox(Category.getSearchFilterList 
				                                      (UserPanel.getSelectedCategory()));
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
	private static JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel();
		
		// Organize panel horizontally.
		searchPanel
		.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		
		// Add search box to panel
		UserPanel.setSearchBox(UserPanel.createSearchBox());
		searchPanel.add(UserPanel.getSearchBox());
		
		// Add search button to panel
		searchPanel.add(UserPanel.createSearchButton());
		
		// Add search filter to panel
		UserPanel.setSearchFilter(UserPanel.createSearchFilter());
		searchPanel.add(UserPanel.getSearchFilter());
		
		return searchPanel;
	}

	/**
	 * Create new top panel for use in main app panel. Top panel
	 * will contain search box and category option seelctor.
	 * 
	 * @param null
	 * @return JPanel topPanel
	 */
	private static JPanel createTopPanel() {
		
		JPanel topPanel = new JPanel();
		
		topPanel.setLayout(new BorderLayout());
		topPanel.add(UserPanel.createCategoryPanel(), BorderLayout.NORTH);
		topPanel.add(UserPanel.createSearchPanel(), BorderLayout.SOUTH);
		
		return topPanel;
	}

	/**
	 * Create visual style panel. Will allow the user to switch visual styles for 
	 * a particular network.
	 * @param String networkName
	 * @return JPanel visualStylePanel
	 */
	public static JPanel createVisualStylePanel(String networkName) {
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
	public static JComboBox createVisualStyleSelector(int visualStyleSelectorType) {
		JComboBox visualStyleSelector = new JComboBox(Category.getVisualStyleList(visualStyleSelectorType));
		visualStyleSelector.setEditable(false);
		visualStyleSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		visualStyleSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String visualStyle = (String) jcmbType.getSelectedItem();
				int visualStyleID = Category.getVisualStyleID(visualStyle);
				UserPanel.setSelectedVisualStyle(visualStyleID);
				Cytoscape.applyVisualStyle(visualStyle);
			}
		});
		return visualStyleSelector;
	}

	/**
	 * Get control panel reference
	 * @param null
	 * @return JPanel controlPanelRef
	 */
	private static JPanel getControlPanelRef() {
		return UserPanel.controlPanelRef;
	}

	/**
	 * Get network panel reference
	 * @param null
	 * @return JPanel networkPanelRef
	 */
	public static JPanel getNetworkPanelRef() {
		return UserPanel.networkPanelRef;
	}

	/**
	 * Get network table reference
	 * @param null
	 * @return JTable networkTableRef
	 */
	public static JTable getNetworkTableRef() {
		return UserPanel.networkTableRef;
	}

	/**
	 * Get user panel searchbox
	 * @param null
	 * @return JTextField searchBox
	 */
	public static JTextField getSearchBox() {
		return UserPanel.searchBoxRef;
	}

	/**
	 * Get search filter
	 * @param null
	 * @return JComboBox searchFilter
	 */
	private static JComboBox getSearchFilter() {
		return UserPanel.searchFilter;
	}

	/**
	 * Get selected category
	 * @param null
	 * @return int selectedCategory
	 */
	public static int getSelectedCategory() {
		return UserPanel.selectedCategory;
	}

	/**
	 * Get selected network
	 * @param null
	 * @return String network
	 */
	public static String getSelectedNetwork() {
		return UserPanel.selectedNetwork;
	}

	/**
	 * Get selected visual style
	 * @param null
	 * @return int visualStyle
	 */
	private static int getSelectedVisualStyle() {
		return UserPanel.selectedVisualStyle;
	}

	/**
	 * Get visual style panel
	 * @param null
	 * @return JPanel visualStylePanel
	 */
	public static JPanel getVisualStylePanel() {
		return UserPanel.visualStylePanel;
	}

	/**
	 * Get visual style selector
	 * @param null
	 * @return JComboBox visualStyleSelector
	 */
	public static JComboBox getVisualStyleSelector() {
		return UserPanel.visualStyleSelector;
	}

	/**
	 * Get visual style type
	 * @param null
	 * @return int visualStyleType
	 */
	public static int getVisualStyleSelectorType() {
		return UserPanel.visualStyleSelectorType;
	}

	/**
	 * Return true iff input does not contain illegal characters i.e. (!@#$%^&*)
	 * @param String input
	 * @return boolean
	 */
	private static boolean isValidInput(String input) {
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
	private static void setControlPanelRef(JPanel controlPanelRef) {
		UserPanel.controlPanelRef = controlPanelRef;
	}	
	
	/**
	 * Set network panel reference
	 * @param JPanel networkPanelRef
	 * @return null
	 */
	private static void setNetworkPanelRef(JPanel networkPanelRef) {
		UserPanel.networkPanelRef = networkPanelRef;
	}

	/**
	 * Set network table reference
	 * @param JTable networkTableRef
	 * @return null
	 */
	public static void setNetworkTableRef(JTable networkTableRef) {
		UserPanel.networkTableRef = networkTableRef;
	}

	/**
	 * Set user panel searchbox
	 * @param JTextField searchBox
	 * @return null
	 */
	public static void setSearchBox(JTextField searchBox) {
		UserPanel.searchBoxRef = searchBox;
	}

	/**
	 * Set search filter
	 * @param JComboBox searchFilter
	 * @return null
	 */
	private static void setSearchFilter(JComboBox searchOptionSelector) {
		UserPanel.searchFilter = searchOptionSelector;
	}

	/**
	 * Set selected category
	 * @param int category
	 * @return null
	 */
	private static void setSelectedCategory(int category) {
		UserPanel.selectedCategory = category;
	}

	/**
	 * Set selected network
	 * @param String network
	 * @return null
	 */
	public static void setSelectedNetwork(String network) {
		UserPanel.selectedNetwork = network;
	}

	/**
	 * Set selected visual style 
	 * @param int visualStyle
	 * @return null
	 */
	public static void setSelectedVisualStyle(int visualStyle) {
		UserPanel.selectedVisualStyle = visualStyle;
	}

	/**
	 * Set visual style panel
	 * @param JPanel visualStylePanel
	 * @return null
	 */
	public static void setVisualStylePanel(JPanel visualStylePanel) {
		UserPanel.visualStylePanel = visualStylePanel;
	}

	/**
	 * Set visual style selector
	 * @param JComboBox visualStyleSelector
	 * @return null
	 */
	public static void setVisualStyleSelector(JComboBox visualStyleSelector) {
		UserPanel.visualStyleSelector = visualStyleSelector;
	}

	/**
	 * Set visual style selector type. 
	 * @param int visualStyleType
	 * @return null
	 */
	public static void setVisualStyleSelectorType(int visualStyleType) {
		UserPanel.visualStyleSelectorType = visualStyleType;
	}

	/**
	 * Swap visual style selector to given type
	 * @param int type
	 * @return null
	 */
	public static void swapVisualStyleSelector(int visualStyleSelectorType) {
		if (UserPanel.getVisualStyleSelectorType() != visualStyleSelectorType) {
			UserPanel.getVisualStylePanel().remove(UserPanel.getVisualStyleSelector());
			UserPanel.getVisualStylePanel().remove(UserPanel.getVisualStyleHelpButton());
			UserPanel.setVisualStyleSelectorType(visualStyleSelectorType);
			Cytoscape.setVisualStyleID(visualStyleSelectorType);
			UserPanel.setVisualStyleSelector(UserPanel.createVisualStyleSelector(UserPanel
					                                  .getVisualStyleSelectorType()));
			UserPanel.getVisualStylePanel().add(UserPanel.getVisualStyleSelector());
			UserPanel.getVisualStylePanel().add(UserPanel.getVisualStyleHelpButton());
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
		return UserPanel.infoPanelRef;
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
			switch (UserPanel.getSelectedCategory()) {
				case Category.DEFAULT:
					this.setSelectedInfoPanel(Category.createDefaultInfoPanel());
					break;
				case Category.ACADEMIA: 
					this.setSelectedInfoPanel(AcademiaPanel.createAcademiaInfoPanel());
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
		UserPanel.infoPanelRef = infoPanel;
	}

	/**
	 * Create stats panel for network
	 * @param SocialNetwork socialNetwork
	 * @return JPanel networkStatsPanel
	 */
	public static JPanel createNetworkStatsPanel(SocialNetwork socialNetwork) {
		// Create network stats 'panel'
		JPanel networkStatsPanel = new JPanel();
	    networkStatsPanel.setLayout(new BorderLayout());
	    
	    //Create network stats 'pane'
	    JTextPane networkStatsPane = new JTextPane();
		networkStatsPane.setContentType("text/html");
		networkStatsPane.setEditable(false);
		networkStatsPane.setAutoscrolls(true);
	    UserPanel.setNetworkStatsPaneRef(networkStatsPane);
	    JScrollPane wrapperPane = new JScrollPane();
		wrapperPane.getViewport().add(networkStatsPane);
		wrapperPane.setPreferredSize(new Dimension(50,50));
	    networkStatsPanel.add(wrapperPane, BorderLayout.NORTH);
	    
		return networkStatsPanel;
	}

	/**
	 * Update network stats panel (for Academia networks only)
	 * @param SocialNetwork socialNetwork
	 * @return null
	 */
	public static void updateNetworkStatsPanel(SocialNetwork socialNetwork) {
		String networkName = "DEFAULT", networkStats = "N/A";
		if (socialNetwork != null) {
			networkName = socialNetwork.getNetworkName();
			if (networkName.length() >= 10) {
				networkName = networkName.substring(0, 9) + " ...";
			}
			networkStats = socialNetwork.getStats();
		}
		UserPanel.getNetworkStatsPanelRef().setBorder(BorderFactory.createTitledBorder(networkName + " Summary"));
		UserPanel.getNetworkStatsPaneRef().setText(networkStats);
		UserPanel.getNetworkStatsPanelRef().revalidate();
		UserPanel.getNetworkStatsPanelRef().repaint();
	}

	/**
	 * Set network stats panel reference
	 * @param JPanel networkStatsPanel
	 * @return null
	 */
	private static void setNetworkStatsPanelRef(JPanel networkStatsPanel) {
		UserPanel.networkStatsPanelRef = networkStatsPanel;
	}

	/**
	 * Get network stats panel reference
	 * @param null
	 * @return JPanel networkStatsPanel
	 */
	private static JPanel getNetworkStatsPanelRef() {
		return UserPanel.networkStatsPanelRef;
	}

	/**
	 * Get visual style help button
	 * @param null
	 * @return JButton visualStyleHelpButton
	 */
	public static JButton getVisualStyleHelpButton() {
		return UserPanel.helpButton;
	}

	/**
	 * Set visual style help button
	 * @param JButton visualStyleHelpButton
	 * @return null
	 */
	public static void setVisualStyleHelpButton(JButton visualStyleHelpButton) {
		UserPanel.helpButton = visualStyleHelpButton;
	}

	/**
	 * Get network stats pane reference
	 * @param null
	 * @return JTextPane networkStatsPaneRef
	 */
	public static JTextPane getNetworkStatsPaneRef() {
		return UserPanel.networkStatsPaneRef;
	}

	/**
	 * Set network stats pane reference
	 * @param JTextPane networkStatsPaneRef
	 * @return null
	 */
	public static void setNetworkStatsPaneRef(JTextPane networkStatsPaneRef) {
		UserPanel.networkStatsPaneRef = networkStatsPaneRef;
	}

}