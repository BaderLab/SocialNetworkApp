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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.org.baderlab.csapps.socialnetwork.pubmed.Pubmed;

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
	private static int selectedWebsite = Search.DEFAULT;
	/**
	 * Reference to the currently displayed info panel
	 */
	private static JPanel infoPanelRef = null;
	/**
	 * Reference to search panel
	 */
	private static JPanel searchPanelRef = null;
	/**
	 * Reference to control panel
	 */
	private static JPanel controlPanelRef = null;
	/**
	 * Reference to the actual panel object being manipulated
	 */
	private static UserPanel PanelObjectRef = null;
	/**
	 * Reference to the search box. Necessary for extracting queries.
	 */
	private static JTextField searchBoxRef = null;
		
	private static final long serialVersionUID = 8292806967891823933L;
	
	/**
	 * Create a new panel
	 * @param null
	 * @return null
	 */
	public UserPanel() {
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400,200));
		
		// Add search box
		UserPanel.searchPanelRef = UserPanel.createSearchPanel();
		this.add(UserPanel.searchPanelRef, BorderLayout.NORTH);
		
		// Add default info panel
		this.setSelectedInfoPanel(getDefaultInfoPanel());
		this.add(infoPanelRef, BorderLayout.CENTER);
		
		// Add control toolbar
		UserPanel.controlPanelRef = UserPanel.createControlPanel();
		this.add(UserPanel.controlPanelRef, BorderLayout.SOUTH);
		
		// Save a reference to this panel object
		UserPanel.setPanelObjectRef(this);
		
	}

	/**
	 * Get user panel searchboc
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
	 * Set selected website
	 * @param int website
	 * @return null
	 */
	private static void setSelectedWebsite(int website) {
		UserPanel.selectedWebsite = website;
	}
	
	/**
	 * Get selected website
	 * @param null
	 * @return int selectedWebsite
	 */
	private static int getSelectedWebsite() {
		return UserPanel.selectedWebsite;
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
	 * Return new search panel for use in main app panel. Search panel will
	 * allow user to perform searches.
	 * 
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
				} else if (UserPanel.isValidInput(UserPanel.getSearchBox().getText().trim())) {
					Cytoscape.notifyUser("Illegal characterrs present. Please enter a valid search term.");
				} else {
					Cytoscape.createNetwork(UserPanel.getSearchBox().getText(), UserPanel.getSelectedWebsite());
				}
			}
		});
		
		// Add search box to panel
		searchPanel.add(UserPanel.getSearchBox());
		
		// Add search button to panel
		searchPanel.add(UserPanel.createSearchButton());
		
		// Add option selector to panel
		searchPanel.add(UserPanel.createOptionSelector());
		
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
				} else if (! UserPanel.isValidInput(UserPanel.getSearchBox().getText().trim())) {
					Cytoscape.notifyUser("Illegal characters present. Please enter a valid search term.");
				} else {
					Cytoscape.createNetwork(UserPanel.getSearchBox().getText(), UserPanel.getSelectedWebsite());
				}
			}
		});
		return searchButton;
	}	
	
	/**
	 * Load default info panel
	 * @param null
	 * @return null
	 */
	private static JPanel getDefaultInfoPanel() {
		JPanel defaultInfoPanel = new JPanel();
		defaultInfoPanel.setName("--SELECT--");
		return defaultInfoPanel;
	}
	
	/**
	 * Switch info panel to one that's specific
	 * to the currently selected website
	 * @param null
	 * @return null
	 */
	private void performSwitcharoo() {
			this.remove(this.getSelectedInfoPanel());
			switch (UserPanel.getSelectedWebsite()) {
				case Search.DEFAULT:
					setSelectedInfoPanel(UserPanel.getDefaultInfoPanel());
					break;
				case Search.PUBMED: 
					setSelectedInfoPanel(Pubmed.getPubmedInfoPanel());
					break;
			}
			this.add(this.getSelectedInfoPanel(), BorderLayout.CENTER);	
			this.revalidate();
			this.repaint();
	}
	
	/**
	 * Return panel's primary option selector.
	 * @param null 
	 * @return JComboBox optionSelector
	 */
	private static JComboBox createOptionSelector() {
		//Create new JComboBox
		JComboBox optionSelector = new JComboBox(Search.getSiteList());
		optionSelector.setEditable(false);
		optionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		optionSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String website = (String) jcmbType.getSelectedItem();
				// Set selected website before performing switcharoo
				// NOTE: This step is imperative. Not doing this will
				// result in a null pointer exception being thrown.
				UserPanel.setSelectedWebsite(Search.getSiteMap().get(website));
				// Perform switcharoo iff the panel being switched to is 
				// distinct from the current panel
				if (! website.trim().equalsIgnoreCase(UserPanel.getPanelObjectRef().getSelectedInfoPanel().getName())) {
					UserPanel.getPanelObjectRef().performSwitcharoo();
				}
			}
		});
		return optionSelector;
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
