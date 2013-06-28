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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.Network;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
	
/**
 * Main panel for Social Network App
 * @author Victor Kofia
 */
public class UserPanel extends JPanel implements CytoPanelComponent {
	/**
	 * Category that user has selected
	 */
	private static int selectedCategory = Category.DEFAULT;
	/**
	 * Visual style that user has selected
	 */
	private static int selectedVisualStyle = Cytoscape.DEFAULT;
	/**
	 * Network that user has selected
	 */
	private static Network selectedNetwork = null;
	/**
	 * Reference to the currently displayed info panel
	 */
	private static JPanel infoPanelRef = null;
	/**
	 * Reference to top panel
	 */
	private static JPanel topPanelRef = null;
	/**
	 * Reference to control panel
	 */
	private static JPanel controlPanelRef = null;
	/**
	 * Reference to network panel
	 */
	private static JPanel networkPanelRef = null;
	/**
	 * Reference to the actual panel object being manipulated
	 */
	private static UserPanel PanelObjectRef = null;
	/**
	 * Reference to the search box. Necessary for extracting queries.
	 */
	private static JTextField searchBoxRef = null;
	/**
	 * Search filter
	 */
	private static JComboBox searchFilter = null;
	/**
	 * Visual style selector
	 */
	private static JComboBox visualStyleSelector = null;
	/**
	 * Reference to visual style panel
	 */
	private static JPanel visualStylePanel = null;
	/**
	 * The type of the visual style selector currently being displayed to the user
	 */
	private static int visualStyleSelectorType = Category.DEFAULT;
	/**
	 * Reference to network table
	 */
	private static JTable networkTableRef = null;
	/**
	 * Row in network table that user has selected
	 */
	private static int selectedRowInNetworkTable = -1;
	
	
	/**
	 * Set selected row in network table
	 * @param int selectedRow
	 * @return null
	 */
	public static void setSelectedRowInNetworkTable(int selectedRow) {
		UserPanel.selectedRowInNetworkTable = selectedRow;
	}
	
	
	/**
	 * Get selected row in network table
	 * @param null
	 * @return int selectedRow
	 */
	public static int getSelectedRowInNetworkTable() {
		return UserPanel.selectedRowInNetworkTable;
	}
	
	
	
	/**
	 * Set selected network
	 * @param Network network
	 * @return null
	 */
	public static void setSelectedNetwork(Network network) {
		UserPanel.selectedNetwork = network;
	}
	
	/**
	 * Get selected network
	 * @param null
	 * @return Network network
	 */
	public static Network getSelectedNetwork() {
		return UserPanel.selectedNetwork;
	}
	
	/**
	 * Create menu item for destroying networks
	 * 
	 * @param null
	 * @return JMenuItem destroyNetworkMenuItem
	 */
	public static JMenuItem createDestroyNetworkMenuItem() {
		// Create new menu item.
		JMenuItem destroyNetworkMenuItem = new JMenuItem("Destroy Network");
		// Clicking of menu item results in the destruction of a network
		destroyNetworkMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Cytoscape.destroyNetwork(UserPanel.getSelectedNetwork());
				DefaultTableModel model = (DefaultTableModel) UserPanel.getNetworkTableRef().getModel();
				model.removeRow(UserPanel.getSelectedRowInNetworkTable());
				UserPanel.setSelectedNetwork(null);
				UserPanel.setSelectedRowInNetworkTable(-1);
				UserPanel.getNetworkTableRef().validate();
				UserPanel.getNetworkTableRef().repaint();

				TitledBorder visualStylePanelBorder = (TitledBorder) UserPanel.getVisualStylePanel().getBorder();
				visualStylePanelBorder.setTitle("Visual Styles");
				UserPanel.getVisualStylePanel().revalidate();
				UserPanel.getVisualStylePanel().repaint();

				UserPanel.swapVisualStyleSelector(UserPanel.getSelectedCategory());
			}
		});
		return destroyNetworkMenuItem;
	}
	
	/**
	 * Add network to user panel
	 * @param Network network
	 * @return null
	 */
	public static void addNetworkToNetworkPanel(Network network) {
		
		// Create a new table and add network's info if there's none
		if (UserPanel.getNetworkTableRef() ==  null) {		
			
			DefaultTableModel model = new DefaultTableModel(); 
			JTable networkTable = new JTable(model);
			networkTable.setEnabled(true);
			for (Object columnName : network.getColumnNames()) {
				model.addColumn((String) columnName);
			}
			model.addRow(network.getNetworkAttrArray());
			UserPanel.setNetworkTableRef(networkTable);
			
			final JPopupMenu destroyNetworkContextMenu = new JPopupMenu();
			destroyNetworkContextMenu.add(UserPanel.createDestroyNetworkMenuItem());
		    
			networkTable.addMouseListener(new MouseAdapter() {
		        public void mousePressed(MouseEvent e) {
		        
		            // Clicking on a network once causes it to be selected
		            if (e.getClickCount() == 1) {
		                JTable target = (JTable) e.getSource();
		                		                			                
		                UserPanel.setSelectedRowInNetworkTable(target.getSelectedRow());
		                
		                // Get selected network
		                String name = (String) UserPanel.getNetworkTableRef().getModel()
		                		                                  .getValueAt(UserPanel.getSelectedRowInNetworkTable(), 0);
		                UserPanel.setSelectedNetwork(Cytoscape.getNetworkMap().get(name));
		    			
		                // Changing the title of the visual style panel
		    			TitledBorder visualStylePanelBorder = (TitledBorder) UserPanel.getVisualStylePanel().getBorder();
		    			visualStylePanelBorder.setTitle(UserPanel.getSelectedNetwork().getName() + " Visual Styles");
		    			UserPanel.getVisualStylePanel().revalidate();
		    			UserPanel.getVisualStylePanel().repaint();

		    			// Changing the visual style selector type		                		
			    		UserPanel.swapVisualStyleSelector(UserPanel.getSelectedNetwork().getRawVisualStyleSelectorType());

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

		// Add network info to table
		} else {
			DefaultTableModel networkTableModel = (DefaultTableModel) UserPanel.getNetworkTableRef().getModel();
			networkTableModel.addRow(network.getNetworkAttrArray());
		}

		UserPanel.getNetworkPanelRef().revalidate();
		UserPanel.getNetworkPanelRef().repaint();

	}
	
	/**
	 * Swap visual style selector to given type
	 * @param int type
	 * @return null
	 */
	public static void swapVisualStyleSelector(int visualStyleSelectorType) {
		if (UserPanel.getVisualStyleSelectorType() != visualStyleSelectorType) {
			// Remove the current visual style selector
			UserPanel.getVisualStylePanel().remove(UserPanel.getVisualStyleSelector());
			
			// %VST
			UserPanel.setVisualStyleSelectorType(visualStyleSelectorType);
			UserPanel.setVisualStyleSelector(UserPanel.createVisualStyleSelector(UserPanel.getVisualStyleSelectorType()));
			
			UserPanel.getVisualStylePanel().add(UserPanel.getVisualStyleSelector());
			UserPanel.getNetworkPanelRef().revalidate();
			UserPanel.getNetworkPanelRef().repaint();
		}
	}
	
	/**
	 * Add network's visual styles to user panel
	 * @param Network network
	 * @return null
	 */
	public static void addNetworkVisualStyles(Network network) {
		if (UserPanel.getVisualStylePanel()  == null) {
			
			// Create new visual style panel
			UserPanel.setVisualStylePanel(UserPanel.createVisualStylePanel(network.getName()));
			
			// It is imperative that visual selector type be set before the visual
			// selector. Not doing this will cause random & seemingly untraceable
			// errors to occur. (%VST)
			UserPanel.setVisualStyleSelectorType(UserPanel.getSelectedCategory());
			UserPanel.setVisualStyleSelector(UserPanel.createVisualStyleSelector(UserPanel.getVisualStyleSelectorType()));
			
			UserPanel.getVisualStylePanel().add(UserPanel.getVisualStyleSelector());
			UserPanel.getNetworkPanelRef().add(UserPanel.getVisualStylePanel(), 
			BorderLayout.CENTER);
		} else {
			
			TitledBorder visualStylePanelBorder = (TitledBorder) UserPanel.getVisualStylePanel().getBorder();
			
			visualStylePanelBorder.setTitle(network.getName() + " Visual Styles");
			
			UserPanel.swapVisualStyleSelector(network.getRawVisualStyleSelectorType());
		}
		
	}
	
	/**
	 * Create a new panel
	 * @param null
	 * @return null
	 */
	public UserPanel() {
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400,200));
			
		// Add top panel
		UserPanel.topPanelRef = UserPanel.createTopPanel();
		this.add(UserPanel.topPanelRef, BorderLayout.NORTH);
		
		// Add default info panel
		this.setSelectedInfoPanel(Category.createDefaultInfoPanel());
		this.add(infoPanelRef, BorderLayout.CENTER);
		
		// Add bottom panel
		this.add(UserPanel.createBottomPanel(), BorderLayout.SOUTH);
		
		// Save a reference to this panel object
		UserPanel.setPanelObjectRef(this);
		
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
	 * Set network table reference
	 * @param JTable networkTableRef
	 * @return null
	 */
	public static void setNetworkTableRef(JTable networkTableRef) {
		UserPanel.networkTableRef = networkTableRef;
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
	 * Get visual style selector
	 * @param null
	 * @return JComboBox visualStyleSelector
	 */
	public static JComboBox getVisualStyleSelector() {
		return UserPanel.visualStyleSelector;
	}

	
	/**
	 * Set visual style selector type. \
	 * @param int visualStyleType
	 * @return null
	 */
	public static void setVisualStyleSelectorType(int visualStyleType) {
		UserPanel.visualStyleSelectorType = visualStyleType;
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
	 * Set visual style panel
	 * @param JPanel visualStylePanel
	 * @return null
	 */
	public static void setVisualStylePanel(JPanel visualStylePanel) {
		UserPanel.visualStylePanel = visualStylePanel;
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
	 * Set network panel reference
	 * @param JPanel networkPanelRef
	 * @return null
	 */
	private static void setNetworkPanelRef(JPanel networkPanelRef) {
		UserPanel.networkPanelRef = networkPanelRef;
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
	 * Set selected visual style 
	 * @param int visualStyle
	 * @return null
	 */
	private static void setSelectedVisualStyle(int visualStyle) {
		UserPanel.selectedVisualStyle = visualStyle;
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
	 * Set search filter
	 * @param JComboBox searchFilter
	 * @return null
	 */
	private static void setSearchFilter(JComboBox searchOptionSelector) {
		UserPanel.searchFilter = searchOptionSelector;
	}
	
	/**
	 * Get search filter
	 * @param null
	 * @return JComboBox searchFilter
	 */
	private static JComboBox getSearchFilter() {
		return UserPanel.searchFilter;
	}
		
	private static final long serialVersionUID = 8292806967891823933L;
	
	/**
	 * Get user panel searchbox
	 * @param null
	 * @return JTextField searchBox
	 */
	public static JTextField getSearchBox() {
		return UserPanel.searchBoxRef;
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
	 * Get current info panel
	 * @param null
	 * @return JPanel currentInfoPanel
	 */
	private JPanel getSelectedInfoPanel() {
		return UserPanel.infoPanelRef;
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
	 * Get panel object reference
	 * @param null
	 * @return UserPanel panelObject
	 */
	private static UserPanel getPanelObjectRef() {
		return UserPanel.PanelObjectRef;
	}
	
	/**
	 * Set panel object reference
	 * @param UserPanel panelObjectRef
	 * @return null
	 */
	private static void setPanelObjectRef(UserPanel currentObjectRef) {
		UserPanel.PanelObjectRef = currentObjectRef;
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
	 * Get control panel reference
	 * @param null
	 * @return JPanel controlPanelRef
	 */
	private static JPanel getControlPanelRef() {
		return UserPanel.controlPanelRef;
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
	 * Get selected category
	 * @param null
	 * @return int selectedCategory
	 */
	public static int getSelectedCategory() {
		return UserPanel.selectedCategory;
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
	 * Create new control panel for use in main app panel.
	 * Will allow users to reset or close the main app panel at their convenience.
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
	 * Create new category panel. Will allow user to select a particular search category
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
	 * Create new network panel. Will allow lay users to easily modify network
	 * parameters
	 * @param null
	 * @return JPanel networkPanel
	 */
	private static JPanel createNetworkPanel() {
		JPanel networkPanel = new JPanel();
		networkPanel.setBorder(BorderFactory.createTitledBorder("Network"));
		networkPanel.setLayout(new BorderLayout());
		return networkPanel;
	}
	
	/**
	 * Create visual style panel. Will allow the user to switch visual styles for 
	 * a particular network.
	 * @param String visualStylePanelType
	 * @return JPanel visualStylePanel
	 */
	public static JPanel createVisualStylePanel(String visualStylePanelType) {
		JPanel visualStylePanel = new JPanel();
		visualStylePanel.setBorder(BorderFactory.createTitledBorder(visualStylePanelType + " Visual Styles"));
		visualStylePanel.setLayout(new BoxLayout(visualStylePanel, BoxLayout.X_AXIS));
		return visualStylePanel;
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
	 * Create new search box. Will allow user to search 
	 * any social website
	 * @param null
	 * @return JTextField searchBox
	 */
	private static JTextField createSearchBox() {
		// Create searchbox. Save a reference to it, and add
		JTextField searchBox = new JTextField();
		searchBox.setEditable(true);
		searchBox.getDocument().addDocumentListener(new DocumentListener() {
			// Update auto generated list as user changes attributes.
			public void changedUpdate(DocumentEvent e) {
			}
			// Update auto generated list as user inputs data.
			public void insertUpdate(DocumentEvent e) {
			}
			// Update auto generated list once user erases previously written data.
			public void removeUpdate(DocumentEvent e) {
			}
		});
		
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
	 *Create search button. Search button allows user to commit search.
	 *NOTE: button not 100% necessary. a simple tap on the return key is sufficient
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
					setSelectedInfoPanel(Category.createDefaultInfoPanel());
					break;
				case Category.ACADEMIA: 
					setSelectedInfoPanel(Category.createAcademiaInfoPanel());
					break;
				case Category.TWITTER:
					setSelectedInfoPanel(Category.createTwitterInfoPanel());
					break;
				case Category.LINKEDIN:
					setSelectedInfoPanel(Category.createLinkedInInfoPanel());
					break;
				case Category.YOUTUBE:
					setSelectedInfoPanel(Category.createYoutubeInfoPanel());
					break;
			}
			// Add selected panel
			this.add(this.getSelectedInfoPanel(), BorderLayout.CENTER);	
			// Refresh user panel to reflect update
			this.revalidate();
			this.repaint();
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
				UserPanel.setSelectedCategory(Category.getCategoryMap().get(category));
				// Perform switcharoo iff the panel being switched to is 
				// distinct from the current panel
				if (! category.trim().equalsIgnoreCase(UserPanel
					.getPanelObjectRef().getSelectedInfoPanel().getName())) {
					UserPanel.getPanelObjectRef().performSwitcharoo();
				}
				//Create new JComboBox
				UserPanel.getSearchFilter()
				.setModel(new DefaultComboBoxModel(Category.getSearchFilterList(UserPanel.getSelectedCategory())));
				
			}

		});
		return categoryOptionSelector;
	}
	
	
	/**
	 * Create search option selector.
	 * @param null 
	 * @return JComboBox optionSelector
	 */
	private static JComboBox createSearchFilter() {
		JComboBox searchOptionSelector = new JComboBox(Category.getSearchFilterList(UserPanel.getSelectedCategory()));
		searchOptionSelector.setEditable(false);
		searchOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		searchOptionSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// SEARCH FILTERS
				// ??
			}
		});
		return searchOptionSelector;
	}
	
	
	/**
	 * Create visual style selector. The type of selector created is dependent on
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
				int visualStyleID = Category.getVisualStyleMap().get(visualStyle);
				// Only apply visual style if selected visual style is distinct from
				// current
				if (! (UserPanel.getSelectedVisualStyle() == visualStyleID)) {
					UserPanel.setSelectedVisualStyle(visualStyleID);
					Cytoscape.applyVisualStyle(visualStyle);
				}
			}
		});
		return visualStyleSelector;
	}


	/**
	 *Create close button. Close button closes current panel
	 *@param null
	 *@return JButton close
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
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				UserPanel.getPanelObjectRef().performSwitcharoo();
			}
		});
		return resetButton;
	}
	
	
	public Component getComponent() {
		return this;
	}

	
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
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
	 * Get panel icon
	 * @param null
	 * @return Icon panelIcon
	 */
	public Icon getIcon() {
		return null;
	}
	
	
}
