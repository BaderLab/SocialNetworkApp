package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.exceptions.UnableToParseAuthorException;
import main.java.org.baderlab.csapps.socialnetwork.panels.CollapsiblePanel;

/**
 * Methods for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites {
	/**
	 * A reference to the faculty text field. Used to verify correct faculty input.
	 */
	public static JTextField facultyTextFieldRef = new JTextField();
	/**
	 * A reference to an Incites data file. Used to verify correct file path.
	 */
	public static File incitesFileRef = null;
	/**
	 * A reference to the load data text field. Used to verify correct file path.
	 */
	public static JTextField pathTextFieldRef = new JTextField();
	/**
	 * List of publications extracted from Incites data file
	 */
	private static List<Publication> pubList = null;
	/**
	 * Author location map
	 */
	private static Map<String, String> locationMap = null;
	
	/**
	 * Set location map
	 * @param Map locationMap
	 * @return null
	 */
	public static void setLocationMap(Map<String, String> locationMap) {
		Incites.locationMap = locationMap;
	}
	
	/**
	 * Get location map
	 * @param null
	 * @return Map locationMap
	 */
	public static Map<String, String> getLocationMap() {
		
		if (Incites.locationMap == null) {
			try {
				InputStream in = Incites.class.getClassLoader().getResourceAsStream("map.sn");
				ObjectInputStream ois = new ObjectInputStream(in);
				Incites.setLocationMap((Map) ois.readObject());
			} catch (FileNotFoundException e) {
				Cytoscape.notifyUser("Failed to load location map. FileNotFoundException.");
			} catch (IOException e) {
				Cytoscape.notifyUser("Failed to load location map. IOException.");
			} catch (ClassNotFoundException e) {
				Cytoscape.notifyUser("Failed to load location map. ClassNotFoundException.");
			}
		}
		
		return Incites.locationMap;
	}

	/**
	 * Return true iff the provided line comes from an Incites data file
	 * @param String line
	 * @return boolean
	 */
	public static boolean checkIfValid(Scanner in) {
		boolean isValid = false;
		for (int i = 0; i < 5; i++) {
			String line = in.nextLine().trim();
			String[] contents = line.split("[\n\t]");
			if (contents.length != 6) {
				return false;
			} else {
				boolean hasTimesCited = false;
				boolean hasExpectedCitations = false;
				boolean hasPublicationYear = false;
				boolean hasSubjectArea = false;
				boolean hasAuthors = false;
				boolean hasTitle = false;

				String year = null;
				String subjectArea = null;
				String authors = null;
				String title = null;
				String timesCited = null;
				String expectedCitations = "0.00";
				List<Author> coauthorList = null;
				Publication pub;

				timesCited = contents[0].trim().isEmpty() ? "0" : contents[0].trim();
				hasTimesCited = timesCited.matches("\\d+?") ? true : false;

				expectedCitations = contents[1].trim().isEmpty() 
						? "0.00" : contents[1].trim();
				hasExpectedCitations = expectedCitations.matches
						("(\\d+?)\\.?(\\d+?)") ? true : false;

				year = contents[2].trim().isEmpty() ? "0" : contents[2].trim();
				hasPublicationYear = year.matches("\\d+?") ? true : false;

				subjectArea = contents[3];
				hasSubjectArea = subjectArea.matches("[A-Z]+?") ? true : false;

				authors = contents[4];

				try {
					coauthorList = Incites.parseAuthors(authors);
					hasAuthors = true;
				} catch (UnableToParseAuthorException e) {
					hasAuthors = false;
				}

				// Difficult to identify an Incites specific title. Thus, true by default.
				title = contents[5];
				hasTitle = true;

				// Consolidate
				isValid = hasTimesCited && hasExpectedCitations 
						&& hasPublicationYear && hasSubjectArea
						&& hasAuthors && hasTitle;

				if (isValid) {
					pub = new Publication(title, year, subjectArea, timesCited, 
							expectedCitations, coauthorList);
					pubList.add(pub);
				} else {
					return ! isValid;
				}

			}
		}
		return ! isValid;
	}

	/**
	 * Create 'create network' button. Create network button 
	 * attempts to create a network out of a file specified
	 * by the user.
	 * @param null
	 * @return JButton createNetworkButton
	 */
	private static JButton createCNB() {
		JButton createNetworkButton = new JButton("Create Network");
		createNetworkButton.setToolTipText("Create network");
		createNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (Incites.getIncitesFileRef() == null || 
					Incites.getFacultyTextFieldRef().getText() == null) {
					Cytoscape.notifyUser("Please select a file and/or specify network name.");
				} else { 
					if (! Incites.getIncitesFileRef().getAbsolutePath().trim()
							.equalsIgnoreCase(Incites.getPathTextFieldRef()
							.getText().trim())) {
						Cytoscape.notifyUser("Please select a file.");
					} else if (Incites.getFacultyTextFieldRef().getText().trim()
							   .isEmpty()) {
						Cytoscape.notifyUser("Please specify network name.");
					} else {
						try {
							// Create network
							Cytoscape.createNetwork(Incites.getIncitesFileRef());						
						} catch (FileNotFoundException e) {
							Cytoscape.notifyUser(Incites.getPathTextFieldRef().getText()
									+ " does not exist");
						}
					}
				}
			}
		});
		return createNetworkButton;
	}

	/**
	 * Create Incites info panel.
	 * Allows user to load Incites derived text files
	 * @param null
	 * @return JPanel incitesInfoPanel
	 */
	public static JPanel createIncitesInfoPanel() {
		
		// Create new Incites info panel.
		JPanel incitesInfoPanel = new JPanel();
		incitesInfoPanel.setBorder(BorderFactory.createTitledBorder("Incites"));
			
		// Set layout
		incitesInfoPanel.setLayout(new BoxLayout(incitesInfoPanel
		, BoxLayout.Y_AXIS));	
		
		// Add load panel
		incitesInfoPanel.add(Incites.createLoadDataPanel()
		, BorderLayout.NORTH);
		
		// Add faculty panel
		incitesInfoPanel.add(Incites.createSpecifyNetworkNamePanel());
		
		// Add 'create network button' to panel
		incitesInfoPanel.add(Incites.createCNB());
		
		return incitesInfoPanel;
	}
	
	/**
	 * Extract filename from path
	 * @param String path
	 * @return String filename
	 */
	public static String parseFileName(String path) {
		Pattern pattern = Pattern.compile("([^\\/]+?)(\\.xlsx|\\.txt)$");
		Matcher matcher = pattern.matcher(path);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "N/A";
	}

	/**
	 *Create load button. Load button loads data file onto Cytoscape for parsing
	 *@param null
	 *@return JButton load
	 */
	private static JButton createLoadButton() {
		
		JButton loadButton = new JButton("...");
		loadButton.setToolTipText("Load Incites data");
		
		// Clicking of button results in the popping up of a dialog box that implores the user
		// to select a new data file. 
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
					Incites.setIncitesFile(textFile);
					Incites.getPathTextFieldRef().setText(textFile.getAbsolutePath());
					Incites.getFacultyTextFieldRef().setText(Incites.parseFileName(textFile.getAbsolutePath()));
				} else {
					Incites.setIncitesFile(null);
					Incites.getPathTextFieldRef().setText(null);
				}
				
			}
			
		});
		
		return loadButton;
	}

	/**
	 * Create new load data panel. Allows user to specify path
	 * of desired data file
	 * @param null
	 * @return JPanel loadDataPanel
	 */
	private static JPanel createLoadDataPanel() {
		JPanel loadDataPanel = new JPanel();
		loadDataPanel.setBorder(BorderFactory.createTitledBorder("Load File"));
		loadDataPanel.setLayout(new BoxLayout(loadDataPanel, BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		Incites.setLoadTextField(new JTextField());
		Incites.getPathTextFieldRef().setEditable(true);
		// Add text field to collapsible panel
		loadDataPanel.add(Incites.getPathTextFieldRef());
		// Add load data button to collapsible panel
		loadDataPanel.add(Incites.createLoadButton());
		return loadDataPanel;
	}

	/**Create specify network name panel. The purpose of 
	 * this panel should be fairly obvious.
	 * @param null
	 * @return JPanel networkNamePanel
	 */
	private static JPanel createSpecifyNetworkNamePanel() {
		JPanel specifyNetworkNamePanel = new JPanel();
		specifyNetworkNamePanel.setBorder(BorderFactory.createTitledBorder("Specify Network Name"));
		specifyNetworkNamePanel.setLayout(new BoxLayout(specifyNetworkNamePanel, BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		Incites.setFacultyTextFieldRef(new JTextField());
		Incites.getFacultyTextFieldRef().setEditable(true);
		// Add text field to collapsible panel
		specifyNetworkNamePanel.add(Incites.getFacultyTextFieldRef());
		return specifyNetworkNamePanel;
	}

	/**
	 * Get faculty text field
	 * @param null
	 * @return JTextField facultyTextField
	 */
	public static JTextField getFacultyTextFieldRef() {
		return facultyTextFieldRef;
	}
	
	/**
	 * Get selected Incites data file
	 * @param null
	 * @return File incitesData
	 */
	public static File getIncitesFileRef() {
		return incitesFileRef;
	}
	
	/**
	 * Get path text field
	 * @param null
	 * @return JTextField pathTextField
	 */
	public static JTextField getPathTextFieldRef() {
		return pathTextFieldRef;
	}
	
	/**
	 * Return all publications (as well as all associated author info) 
	 * contained in network file.
	 * Note that each publication serves as an edge and each author a node. 
	 * Node info is embedded inside each edge.
	 * @param File networkFile
	 * @return List pubList
	 * @throws FileNotFoundException 
	 */
	public static List<Publication> getPublications(File networkFile) 
			throws FileNotFoundException {
			
		Scanner in = new Scanner(networkFile);
		String line;
		String[] contents;
		String year = null;
		String subjectArea = null;
		String authors = null;
		String title = null;
		String timesCited = null;
		String expectedCitations = "0.00";
		Publication pub;
		boolean isValid = false;

		List<Author> coauthorList = null;
		
		Incites.pubList = new ArrayList<Publication>();

		// Verify that file is in fact derived from Incites
		if (! in.hasNext()) {
			isValid = false;
		} else {
			isValid = Incites.checkIfValid(in);
		}
		
		// Read Incites data file
		if (isValid) {
			while (in.hasNext()) {
				line = in.nextLine().trim();
				contents = line.split("[\t\n]");

				if (contents.length == 6) {
					// Get publication info
					timesCited = contents[0].trim().isEmpty() 
							     ? "0" : contents[0].trim();
					expectedCitations = contents[1].trim().isEmpty() 
							     ? "0.00" : contents[1].trim();
					year = contents[2].trim().isEmpty() 
							     ? "0" : contents[2].trim();
					subjectArea = contents[3];
					authors = contents[4];
					title = contents[5];

					// Get author list
					try {
						coauthorList = Incites.parseAuthors(authors);
					} catch (UnableToParseAuthorException e) {
						return null;
					}

					// Set publication info
					pub = new Publication(title, year, subjectArea, 
							              timesCited, expectedCitations, coauthorList);
//					System.out.println("Title: " + title
//							           + "\nYear: " + year
//							           + "\nSubject Area: " + subjectArea
//							           + "\nTimes Cited: " + timesCited
//							           + "\nExpected Citations: " + expectedCitations
//							           + "\nCo-Authors: " + coauthorList.toString() + "\n\n");
					//Add publication to overall list
					Incites.pubList.add(pub);

				} 
				
//				if (contents.length == 7) {
//					System.out.println("Col#1: " + contents[0]
//					           + "\nCol#2: " + contents[1]
//					           + "\nCol#3: " + contents[2]
//					           + "\nCol#4: " + contents[3]
//					           + "\nCol#5: " + contents[4]
//					           + "\nCol#6: " + contents[5]
//					           + "\nCol#7: " + contents[6] + "\n\n");
//				}
			}
			
			
			return Incites.pubList;
		}

		// If file is invalid, null will be returned
		return null;
	}
	
	/**
	 * Parse raw author text and return array list containing all authors
	 * and their associated info
	 * @param String rawAuthorText
	 * @return ArrayList authorList
	 */
	public static ArrayList<Author> parseAuthors(String rawAuthorText) 
			      throws UnableToParseAuthorException {
		String[] authors = rawAuthorText.split(";");
		if (authors.length == 0) {
			throw new UnableToParseAuthorException();
		}
		ArrayList<Author> pubAuthorList = new ArrayList<Author>();
		Author author = null;
		for (String authorText : authors) {
			author = new Author(authorText, Category.INCITES);
			if (! pubAuthorList.contains(author)) {
				pubAuthorList.add(author);
			}
		}
		return pubAuthorList;
	}
	
	/**
	 * Parse author's first name
	 * @param String incitesText
	 * @return String firstName
	 */
	public static String parseFirstName(String incitesText) {
		Pattern firstNamePattern = Pattern.compile(",(.+?)\\s");
		Matcher firstNameMatcher = firstNamePattern.matcher(incitesText);
		if (firstNameMatcher.find()) {
			return firstNameMatcher.group(1).trim();
		}
		return "N/A";
	}

	/**
	 * Parse author's institution
	 * @param String incitesText
	 * @return String institution
	 */
	public static String parseInstitution(String incitesText) {
		Pattern institution = Pattern.compile("\\((.+?)\\)");
		Matcher matchInstitution = institution.matcher(incitesText);
		if (matchInstitution.find()) {
			return matchInstitution.group(1).trim();
		}
		return "N/A";
	}

	/**
	 * Parse author's lastname
	 * @param String incitesText
	 * @return String lastName
	 */
	public static String parseLastName(String incitesText) {
		Pattern lastNamePattern = Pattern.compile("\"?\\s?(.+?),");
		Matcher lastNameMatcher = lastNamePattern.matcher(incitesText);
		if (lastNameMatcher.find()) {
			return lastNameMatcher.group(1).trim();
		}
		return "N/A";
	}

	/**
	 * Parse author's middle initial
	 * @param String incitesText
	 * @return String middleInitial
	 */
	public static String parseMiddleInitial(String incitesText) {
		Pattern middleInitialPattern = Pattern.compile("\\s(\\w)\\.");
		Matcher middleInitialMatcher = middleInitialPattern.matcher(incitesText);
		if (middleInitialMatcher.find()) {
			return middleInitialMatcher.group(1).trim();
		}
		return "N/A";
	}
	
	/**
	 * Set faculty text field
	 * @param JTextField facultyTextField
	 * @return null
	 */
	public static void setFacultyTextFieldRef(JTextField facultyTextField) {
		facultyTextFieldRef = facultyTextField;
	}

	/**
	 * Set selected Incites data file
	 * @param File incitesData
	 * @return null
	 */
	public static void setIncitesFile(File selectedFile) {
		incitesFileRef = selectedFile;
	}
	
	/**
	 * Set path text field
	 * @param JTextField pathTextField
	 * @return null
	 */
	public static void setLoadTextField(JTextField pathTextField) {
		pathTextFieldRef = pathTextField;
	}
	
}