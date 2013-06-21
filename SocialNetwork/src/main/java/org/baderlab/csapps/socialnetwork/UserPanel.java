package main.java.org.baderlab.csapps.socialnetwork;
	
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
	
/**
 * Main panel for Social Network App
 * @author Victor Kofia
 */
public class UserPanel extends JPanel implements CytoPanelComponent {
	/**
	 * Website that user has selected
	 */
	private static int selectedCategory = Category.DEFAULT;
	/**
	 * Visual style that user has selected
	 */
	private static int selectedVisualStyle = Cytoscape.DEFAULT;
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
	 * Reference to network panel label
	 */
	private static JLabel networkPanelLabelRef = null;
	/**
	 * Reference to the actual panel object being manipulated
	 */
	private static UserPanel PanelObjectRef = null;
	/**
	 * Reference to the search box. Necessary for extracting queries.
	 */
	private static JTextField searchBoxRef = null;
	/**
	 * Search option selector
	 */
	private static JComboBox searchOptionSelector = null;
	/**
	 * Visual style selector
	 */
	private static JComboBox visualStyleSelector = null;
	/**
	 * Reference to visual style panel
	 */
	private static JPanel visualStylePanel = null;
	/**
	 * What type of visual panel is currently being displayed to the user
	 */
	private static int visualStyleSelectorType = Category.DEFAULT;

	
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
	 * Set visual style type
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
	 * Set network panel label ref
	 * @param JLabel networkPanelLabel
	 * @return null
	 */
	public static void setNetworkPanelLabelRef(JLabel networkPanelLabel) {
		UserPanel.networkPanelLabelRef = networkPanelLabel;
	}
	
	/**
	 * Get network panel label ref
	 * @param null
	 * @return JLabel networkPanelLabelRef
	 */
	public static JLabel getNetworkPanelLabelRef() {
		return UserPanel.networkPanelLabelRef;
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
	 * Set search option selector
	 * @param JComboBox searchOptionSelector
	 * @return null
	 */
	private static void setSearchOptionSelector(JComboBox searchOptionSelector) {
		UserPanel.searchOptionSelector = searchOptionSelector;
	}
	
	/**
	 * Get search option selector
	 * @param null
	 * @return JComboBox searchOptionSelector
	 */
	private static JComboBox getSearchOptionSelector() {
		return UserPanel.searchOptionSelector;
	}
		
	private static final long serialVersionUID = 8292806967891823933L;
	
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
		this.setSelectedInfoPanel(Category.getDefaultInfoPanel());
		this.add(infoPanelRef, BorderLayout.CENTER);
		
		// Add bottom panel
		this.add(UserPanel.createBottomPanel(), BorderLayout.SOUTH);
		
		// Save a reference to this panel object
		UserPanel.setPanelObjectRef(this);
		
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
	 * panel that will allow the user to change the network
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
	 * Return new control panel for use in main app panel.
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
	 * Return new category panel. Will allow user to select a particular search category
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
	 * Return new network panel. Will allow the lay user to modify network
	 * parameters easily and conveniently.
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
	 * @param null
	 * @return JPanel visualStylePanel
	 */
	public static JPanel createVisualStylePanel() {
		JPanel visualStylePanel = new JPanel();
		visualStylePanel.setBorder(BorderFactory.createTitledBorder("Visual Style"));
		visualStylePanel.setLayout(new BoxLayout(visualStylePanel, BoxLayout.X_AXIS));
		return visualStylePanel;
	}
	
	/**
	 * Return new top panel for use in main app panel. Top panel
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
	 * Create new search panel. Will allow user to search for a particular
	 * network. Pre-defined search option will be made available to the user
	 * via the search option selector.
	 * @param null
	 * @return JPanel searchPanel
	 */
	private static JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel();
		
		// Organize panel horizontally.
		searchPanel
		.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		
		// Create searchbox. Save a reference to it, and add
		UserPanel.setSearchBox(new JTextField());
		UserPanel.getSearchBox().setEditable(true);
		UserPanel.getSearchBox().getDocument().addDocumentListener(new DocumentListener() {
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
		UserPanel.getSearchBox().addActionListener(new ActionListener() {
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
		
		// Add search box to panel
		searchPanel.add(UserPanel.getSearchBox());
		
		// Add search button to panel
		searchPanel.add(UserPanel.createSearchButton());
		
		// Add option selector to panel
		UserPanel.setSearchOptionSelector(UserPanel.createSearchOptionSelector());
		searchPanel.add(UserPanel.getSearchOptionSelector());
		
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
	 *NOTE: button not 100% necessary. a simple tap on the return key achieves the same ends
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
					Cytoscape.createNetwork(UserPanel.getSearchBox().getText()
					, UserPanel.getSelectedCategory());
				}
			}
		});
		return searchButton;
	}	
	
	/**
	 * Switch info panel to one that's specific
	 * to the currently selected website
	 * @param null
	 * @return null
	 */
	private void performSwitcharoo() {
			this.remove(this.getSelectedInfoPanel());
			switch (UserPanel.getSelectedCategory()) {
				case Category.DEFAULT:
					setSelectedInfoPanel(Category.getDefaultInfoPanel());
					break;
				case Category.ACADEMIA: 
					setSelectedInfoPanel(Category.getAcademiaInfoPanel());
					break;
			}
			this.add(this.getSelectedInfoPanel(), BorderLayout.CENTER);	
			this.revalidate();
			this.repaint();
	}
	
	/**
	 * Return category option selector.
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
				switch(UserPanel.getSelectedCategory()) {
					case Category.DEFAULT: 
						UserPanel.getSearchOptionSelector()
						.setModel(new DefaultComboBoxModel(Search.getDefaultOptionList()));
						break;
					case Category.ACADEMIA: 
						UserPanel.getSearchOptionSelector()
						.setModel(new DefaultComboBoxModel(Search.getAcademiaOptionList()));
						break;
				}
			}
		});
		return categoryOptionSelector;
	}
	
	
	/**
	 * Return search option selector.
	 * @param null 
	 * @return JComboBox optionSelector
	 */
	private static JComboBox createSearchOptionSelector() {
		JComboBox searchOptionSelector = new JComboBox(Search.getDefaultOptionList());
		searchOptionSelector.setEditable(false);
		searchOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		searchOptionSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		return searchOptionSelector;
	}
	
	/**
	 * Create visual style selector. The type of selector created is dependent on
	 * 
	 * @param null
	 * @return JComboBox visualStyleSelector
	 */
	public static JComboBox createVisualStyleSelector() {
		JComboBox visualStyleSelector = new JComboBox(Category.getVisualStyleList());
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
	 * Return panel title
	 * @param null
	 * @return String panelTitle
	 */
	public String getTitle() {
		return "Social Network";
	}
	
	
	/**
	 * Return panel icon
	 * @param null
	 * @return Icon panelIcon
	 */
	public Icon getIcon() {
		return null;
	}
	
	
}
