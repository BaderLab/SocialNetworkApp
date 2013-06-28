package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.org.baderlab.csapps.socialnetwork.CollapsiblePanel;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.Network;
import main.java.org.baderlab.csapps.socialnetwork.UserPanel;
import main.java.org.baderlab.csapps.socialnetwork.exceptions.UnableToParseAuthorException;

/**
 * Methods for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites {
	/**
	 * Confirmed. (Used when assessing Faculty input).
	 */
	final private static String CONFIRM = "CONFIRM";
	/**
	 * Not confirmed. (Used when assessing Faculty input).
	 */
	final private static String NOT_CONFIRMED = "NOT CONFIRMED";
	/**
	 * Icon shown when user has not explicitly confirmed faculty input
	 */
	final private static ImageIcon ICON_NOT_CONFIRMED = 
	new ImageIcon(Incites.class.getClassLoader().getResource("new.png"));
	/**
	 * Icon shown when user has explicitly confirmed faculty input
	 */
	final private static ImageIcon ICON_CONFIRMED = 
	new ImageIcon(Incites.class.getClassLoader().getResource("tick.png"));
	/**
	 * List of publications extracted from Incites data file
	 */
	private static List<Publication> pubList = null;
	/**
	 * A reference to the confirm button. Necessary for doing icon swapping. 
	 * Has to be handled directly since it mainly interacts with static entities. 
	 * Use with EXTREME caution. 
	 */
	private static JButton confirmButtonRef = null;
	/**
	 * A reference to the faculty text field. Used to verify correct faculty input.
	 */
	public static JTextField facultyTextFieldRef = new JTextField();
	/**
	 * A reference to the load data text field. Used to verify correct file path.
	 */
	public static JTextField pathTextFieldRef = new JTextField();
	/**
	 * A reference to an Incites data file. Used to verify correct file path.
	 */
	public static File incitesFileRef = null;


	/**
	 * Get confirm button reference. Used to verify
	 * that faculty name has been entered correctly
	 * @param null
	 * @return JButton confirmButtonRef
	 */
	private static JButton getConfirmButtonRef() {
		return Incites.confirmButtonRef;
	}
	
	/**
	 * Set confirm button reference. Used to verify
	 * that faculty name has been entered correctly
	 * @param JButton confirmButtonRef
	 * @return null
	 */
	private static void setConfirmButtonRef(JButton confirmButtonRef) {
		Incites.confirmButtonRef = confirmButtonRef;
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
	 * Set faculty text field
	 * @param JTextField facultyTextField
	 * @return null
	 */
	public static void setFacultyTextFieldRef(JTextField facultyTextField) {
		facultyTextFieldRef = facultyTextField;
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
	 * Set path text field
	 * @param JTextField pathTextField
	 * @return null
	 */
	public static void setLoadTextField(JTextField pathTextField) {
		pathTextFieldRef = pathTextField;
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
	 * Set selected Incites data file
	 * @param File incitesData
	 * @return null
	 */
	public static void setIncitesFile(File selectedFile) {
		incitesFileRef = selectedFile;
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
			author = new Author(authorText, Author.INCITES);
			if (! pubAuthorList.contains(author)) {
				pubAuthorList.add(author);
			}
		}
		return pubAuthorList;
	}
	
	/**
	 * Return true iff the provided line comes from an Incites data file
	 * @param String line
	 * @return boolean
	 */
	public static boolean checkIfValid(String line) {
		String[] contents = line.split("[\n\t]");
		if (contents.length < 6) {
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
			boolean isValid = hasTimesCited && hasExpectedCitations 
					          && hasPublicationYear && hasSubjectArea
					          && hasAuthors && hasTitle;
			
			if (isValid) {
				pub = new Publication(title, year, subjectArea, timesCited, 
						              expectedCitations, coauthorList);
				pubList.add(pub);
				return isValid;
			} else {
				return ! isValid;
			}

		}
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
			line = in.nextLine().trim();
			isValid = Incites.checkIfValid(line);
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

					//Add publication to overall list
					pubList.add(pub);

				} 
			}
			
			return pubList;
		}

		// If file is invalid, null will be returned
		return null;
	}

	/**
	 * Create 'create network' button. Create network button 
	 * attempts to create a network out of file specified
	 * by user.
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
					Cytoscape.notifyUser("Network could not be created. " +
							             "Please load file and/or specify faculty.");
				} else { 
					if (! Incites.getIncitesFileRef().getAbsolutePath().trim()
							.equalsIgnoreCase(Incites.getPathTextFieldRef()
							.getText().trim())) {
						Cytoscape.notifyUser("Network could not be created. " +
								             "Problem with file path. Please load file again.");
					} else if (Incites.getFacultyTextFieldRef().getText().trim()
							   .isEmpty()) {
						Cytoscape.notifyUser("Network could not be created. " +
								             "Please specify faculty.");
					} else {
						if (Incites.getConfirmButtonRef().getName()
								   .equalsIgnoreCase(Incites.NOT_CONFIRMED)) {
							Cytoscape.notifyUser("Network could not be created. " +
									             "Please confirm faculty name " +
									             "by pressing plus sign");	
						} else {
							try {
								
								// Create network
								Cytoscape.createNetwork(Incites.getIncitesFileRef());
														
								// Get a reference to the network
							    try {
							        Thread.sleep(15000);
							    } catch (InterruptedException e) {
							        // We've been interrupted: no more messages.
							        return;
							    }
							    
								Network lastCreatedNetwork = Cytoscape.getNetworkMap().get(Cytoscape.getNetworkName());
										                
								UserPanel.addNetworkToNetworkPanel(lastCreatedNetwork);								
								UserPanel.addNetworkVisualStyles(lastCreatedNetwork);
																
							} catch (FileNotFoundException e) {
								Cytoscape.notifyUser(Incites.getPathTextFieldRef().getText()
										+ " does not exist");
							}
						}
					}
				}
			}
		});
		return createNetworkButton;
	}

	/**
	 * Create confirm button. Used to confirm faculty name entries.
	 * @param null
	 * @return JButton confirmButton
	 */
	private static JButton createConfirmButton() {
		final JButton confirmButton = new JButton(Incites.ICON_NOT_CONFIRMED);
		confirmButton.setBorder(BorderFactory.createEmptyBorder());
		// Default setting is NOT_CONFIRMED
		confirmButton.setName(Incites.NOT_CONFIRMED);
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Set confirmation status to no, iff there's no text in the faculty textfield
				if (Incites.getFacultyTextFieldRef().getText() == null || 
					Incites.getFacultyTextFieldRef().getText().trim().isEmpty()) {
					confirmButton.setIcon(Incites.ICON_NOT_CONFIRMED);
					confirmButton.setName(Incites.NOT_CONFIRMED);
				} else {
					if (confirmButton.getName().equalsIgnoreCase(Incites.NOT_CONFIRMED)) {
						confirmButton.setIcon(Incites.ICON_CONFIRMED);
						confirmButton.setName(Incites.CONFIRM);
					}
				}
			}
		});	
		return confirmButton;
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
					Incites.getConfirmButtonRef().setName(Incites.NOT_CONFIRMED);
					Incites.getConfirmButtonRef().setIcon(Incites.ICON_NOT_CONFIRMED);
				} else {
					Incites.setIncitesFile(null);
					Incites.getPathTextFieldRef().setText(null);
				}
				
			}
			
		});
		
		return loadButton;
	}
	
	/**Create faculty spec panel. Allows user to specify faculty.
	 * @param null
	 * @return JPanel facultyPanel
	 */
	private static JPanel createFacultySpecPanel() {
		CollapsiblePanel facultyPanel = new CollapsiblePanel("Specify Network Name");
		facultyPanel.setCollapsed(true);
		facultyPanel.getContentPane()
		.setLayout(new BoxLayout(facultyPanel.getContentPane(), BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		Incites.setFacultyTextFieldRef(new JTextField());
		Incites.getFacultyTextFieldRef().setEditable(true);
		Incites.getFacultyTextFieldRef().getDocument().addDocumentListener(new DocumentListener() {
			
			// Change confirmation status to NO_CONFIRMATION once any change is registered on the textfield
			public void changedUpdate(DocumentEvent e) {
				Incites.getConfirmButtonRef().setName(Incites.NOT_CONFIRMED);
				Incites.getConfirmButtonRef().setIcon(Incites.ICON_NOT_CONFIRMED);
			}
	
			public void insertUpdate(DocumentEvent e) {
				Incites.getConfirmButtonRef().setName(Incites.NOT_CONFIRMED);
				Incites.getConfirmButtonRef().setIcon(Incites.ICON_NOT_CONFIRMED);
			}
	
			public void removeUpdate(DocumentEvent e) {
				Incites.getConfirmButtonRef().setName(Incites.NOT_CONFIRMED);
				Incites.getConfirmButtonRef().setIcon(Incites.ICON_NOT_CONFIRMED);
			}
		});
		// Add text field to collapsible panel
		facultyPanel.getContentPane().add(Incites.getFacultyTextFieldRef());
		// Add confirmation button to collapsible panel
		Incites.setConfirmButtonRef(Incites.createConfirmButton());
		facultyPanel.getContentPane().add(Incites.getConfirmButtonRef());
		return facultyPanel;
	}

	/**
	 * Create new load data panel. Allows user to specify path
	 * of desired data file
	 * @param null
	 * @return JPanel loadDataPanel
	 */
	private static JPanel createLoadDataPanel() {
		CollapsiblePanel loadDataPanel = new CollapsiblePanel("Load File");
		loadDataPanel.setCollapsed(true);
		loadDataPanel.getContentPane()
		.setLayout(new BoxLayout(loadDataPanel.getContentPane(), BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		Incites.setLoadTextField(new JTextField());
		Incites.getPathTextFieldRef().setEditable(true);
		// Add text field to collapsible panel
		loadDataPanel.getContentPane().add(Incites.getPathTextFieldRef());
		// Add load data button to collapsible panel
		loadDataPanel.getContentPane().add(Incites.createLoadButton());
		return loadDataPanel;
	}
	
	/**
	 * Return new Incites info panel.
	 * Allows user to load Incites derived text files
	 * @param null
	 * @return JPanel incitesInfoPanel
	 */
	public static JPanel createIncitesInfoPanel() {
		
		// Create new Incites info panel.
		CollapsiblePanel incitesInfoPanel = new CollapsiblePanel("Incites");
		incitesInfoPanel.setCollapsed(true);
			
		// Set layout
		incitesInfoPanel
		.getContentPane().setLayout(new BoxLayout(incitesInfoPanel.getContentPane()
		, BoxLayout.Y_AXIS));	
		
		// Add load panel
		incitesInfoPanel.getContentPane().add(Incites.createLoadDataPanel()
		, BorderLayout.NORTH);
		
		// Add faculty panel
		incitesInfoPanel.getContentPane().add(Incites.createFacultySpecPanel());
		
		// Add 'create network button' to panel
		incitesInfoPanel.getContentPane().add(Incites.createCNB());
		
		return incitesInfoPanel;
	}
	
}