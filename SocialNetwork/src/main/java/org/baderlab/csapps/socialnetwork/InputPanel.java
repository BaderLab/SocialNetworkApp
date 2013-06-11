package main.java.org.baderlab.csapps.socialnetwork;
	
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.xml.sax.SAXException;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.ParserConfigurationException;
	
/**
 * Panel for Social Network App
 * @author Victor Kofia
 */
public class InputPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;
	
	/**
	 * Create a new panel
	 * @param null
	 * @return null
	 */
	public InputPanel() {
		
		// Set panel layout
		this.setLayout(new BorderLayout());
		
		// Set preferred size
		this.setPreferredSize(new Dimension(400,200));
		
		// Add search box
		this.add(createSearchBox(), BorderLayout.NORTH);
		
		// Add load button 
		this.add(createLoadButton(), BorderLayout.SOUTH);
		
	}
	
	
	/**
	 * Return new search panel for use in main app panel. Search panel will
	 * allow user to perform searches.
	 * 
	 * @param null
	 * @return JPanel searchPanel
	 */
	private static JPanel createSearchBox() {
		
		// Create new panel.
		JPanel searchControls = new JPanel();
		// Organize panel horizontally.
		searchControls
		.setLayout(new BoxLayout(searchControls, BoxLayout.X_AXIS));
		// Set borders.
		searchControls
		.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 10));
		
		// Create new JLabel.
		JLabel searchLabel = new JLabel("Search: ");
		// Set font.
		searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
		// Add 'Search' label to toolbar.
		searchControls.add(searchLabel);
		
		// Create new text field. It will be used to search through library
		// contents using some user-defined parameter.
		final JTextField searchBox = new JTextField();
		// Allow user to enter a query of their choosing
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
		
		searchBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					// Tapping enter will result in the automatic creation of a network
					Cytoscape.createNetwork(searchBox.getText(), Search.PUBMED);
				} catch (ParserConfigurationException e) {
					Cytoscape.notifyUser("Check createSearchBox() in panel.java. An exception occurred there.");
				} catch (SAXException e) {
					Cytoscape.notifyUser("Check createSearchBox() in panel.java. An exception occurred there.");
				} catch (IOException e) {
					Cytoscape.notifyUser("Check createSearchBox() in panel.java. An exception occurred there.");
				}
			}
		});
		
		// Add JComboBox (search bar) to toolbar.
		searchControls.add(searchBox);
		// Return fully configured search box.
		return searchControls;
		
	}

	
	/**
	 *Create load button. Load button loads data file onto Cytoscape for parsing
	 *@param null
	 *@return JButton load
	 */
	static JButton createLoadButton() {
		// Create new icon object.
		URL iconURL = InputPanel.class.getClassLoader().getResource("new.png");
		ImageIcon iconLoad = new ImageIcon(iconURL);
		// Use icon object to create a new load button. 
		JButton loadButton = new JButton("Load", iconLoad);
//		JButton loadButton = new JButton("Load");
		// Add ToolTipText.
		loadButton.setToolTipText("Load Incites data");
		// Clicking of button results in the popping of a dialog box that implores the user
		// to select a new data file. 
		// New data file is then loaded to Cytoscape.
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Ask user to select the appropriate data file.
				JFileChooser chooser = new JFileChooser();
				// Initialize the chooser dialog box to desktop
				File directory = new File("~/Desktop");
				chooser.setCurrentDirectory(directory);
				chooser.setDialogTitle("Data Selection");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int check = chooser.showDialog(null, "OK");
				// Only attempt to read data file if user clicks "OK"
				if (check == JFileChooser.APPROVE_OPTION) {
					File textFile = chooser.getSelectedFile();
					try {
						Cytoscape.createNetwork(textFile);
					} catch (ParserConfigurationException e) {
						Cytoscape.notifyUser("Check createLoadButton() in panel.java. An exception occurred there.");
					} catch (SAXException e) {
						Cytoscape.notifyUser("Check createLoadButton() in panel.java. An exception occurred there.");
					} catch (IOException e) {
						Cytoscape.notifyUser("Check createLoadButton() in panel.java. An exception occurred there.");
					}
				}
			}
		});
		return loadButton;
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
		return "Co-Pub App";
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
