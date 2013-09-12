package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Incites;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 * Add a new institution to the location map
 * @author Victor Kofia
 */
public class AddInstitutionAction extends AbstractCyAction {
	
	/**
	 * Set of all accepted locations
	 */
	private HashSet<String> locationSet = null;
	
	/**
	 * Set location set
	 * @param HashSet locationSet
	 * @return null
	 */
	private void setLocationSet(HashSet<String> locationSet) {
		this.locationSet = locationSet;
	}
	
	/**
	 * Get location set
	 * @param null
	 * @return HashSet locationSet
	 */
	private HashSet<String> getLocationSet() {
		return this.locationSet;
	}

	public AddInstitutionAction(Map<String, String> configProps,
			CyApplicationManager applicationManager,
			CyNetworkViewManager networkViewManager) {
		super(configProps, applicationManager, networkViewManager);
		putValue(Action.NAME, "Add Institution");
		HashSet<String> set = new HashSet<String>();
		String[] locations = new String[] {"univ toronto", "ontario", "canada", 
				                           "united states", "international", "other"};
		for (String location : locations) {
			set.add(location);
		}
		this.setLocationSet(set);
	}
	
	/**
	 * Save map to file
	 * @param File file
	 * @return null
	 */
	private void saveMap(File file, Map<String, String> map) {
		// Save map (to Cytoscape directory)
		try {
			FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(map);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		JTextField institutionTextField = new JTextField(5);
		JTextField locationTextField = new JTextField(5);

		JPanel myPanel = new JPanel();
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(new JLabel("Institution"));
		myPanel.add(institutionTextField);
		myPanel.add(new JLabel("Location"));
		myPanel.add(locationTextField);

		int outcome = JOptionPane.OK_OPTION;
		String institution = "N/A", location = "N/A";
		while (outcome == JOptionPane.OK_OPTION) {
			// Display dialog box and get user's outcome.
			outcome = JOptionPane.showConfirmDialog(null, myPanel, "Login",
					JOptionPane.OK_CANCEL_OPTION);
			if (outcome == JOptionPane.OK_OPTION) {
				institution = institutionTextField.getText().trim();
				location = locationTextField.getText().trim();
				if (institution.trim().isEmpty() && location.trim().isEmpty()) {
					CytoscapeUtilities.notifyUser("Please specify both an institution and a location");
				} else {
					if (institution.trim().isEmpty()) {
						CytoscapeUtilities.notifyUser("Please specify an institution");
					} else if (location.trim().isEmpty()) {
						CytoscapeUtilities.notifyUser("Please specify a location");
					} else {
						if (! this.getLocationSet().contains(location.toLowerCase())) {
							CytoscapeUtilities.notifyUser("Location does not exist. Please enter a valid location.");
						} else {
							institution = institution.toUpperCase();
							// Format location (in case casing was done improperly)
							// i.e. united states becomes 'United States'
							String[] words = location.split("\\s");
							location = "";
							for (String word : words) {
								location += word.replaceAll("^\\w", word.substring(0,1).toUpperCase()) + " ";
							}
							location = location.trim();
							outcome = JOptionPane.CANCEL_OPTION;
						}
					}
					
				}
			}			
		}
		
		if (! institution.trim().isEmpty() && ! location.trim().isEmpty()) {
			try {
				// Get map file in jar
				InputStream in = Incites.class.getClassLoader().getResourceAsStream("map.sn");
				ObjectInputStream ois = new ObjectInputStream(in);
				Map<String, String> map = (HashMap<String, String>) ois.readObject();
				// Add insitution / location info to map
				map.put(institution, location);
				// Update map being used by Incites
				Incites.setLocationMap(map);
			} catch (IOException e) {
				e.printStackTrace();
				CytoscapeUtilities.notifyUser("Location map could not be accessed.");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				CytoscapeUtilities.notifyUser("Location map could not be accessed.");
			}

		}

	}


}
