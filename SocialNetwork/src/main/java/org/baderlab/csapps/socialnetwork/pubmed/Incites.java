package main.java.org.baderlab.csapps.socialnetwork.pubmed;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import main.java.org.baderlab.csapps.socialnetwork.CollapsiblePanel;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.Search;
import main.java.org.baderlab.csapps.socialnetwork.exceptions.UnableToParseAuthorException;

/**
 * Methods for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites {
	
	private static List<Publication> pubList = null;
	private static JTextField browseTextField;
	private static File selectedFile;
	private static JTextField facultyTextField;

	
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
	 * Parse middle initial
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
	public static ArrayList<Author> parseAuthors(String rawAuthorText) throws UnableToParseAuthorException {
		String[] authors = rawAuthorText.split(";");
		if (authors.length == 0) {
			throw new UnableToParseAuthorException();
		}
		ArrayList<Author> pubAuthorList = new ArrayList<Author>();
		Author author = null;
		for (String authorText : authors) {
			author = new Author(authorText, Search.INCITES);
			if (! pubAuthorList.contains(author)) {
				pubAuthorList.add(author);
			}
		}
		return pubAuthorList;
	}
	
	/**
	 * Return true iff the provided line comes from an Incites data file
	 * @param String data
	 * @return boolean
	 */
	public static boolean checkIfValid(String data) {
		String[] contents = data.split("[\n\t]");
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
			
			expectedCitations = contents[1].trim().isEmpty() ? "0.00" : contents[1].trim();
			hasExpectedCitations = expectedCitations.matches("(\\d+?)\\.?(\\d+?)") ? true : false;
			
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
			boolean isValid = hasTimesCited && hasExpectedCitations && hasPublicationYear && hasSubjectArea
					&& hasAuthors && hasTitle;
			
			if (isValid) {
				pub = new Publication(title, year, subjectArea, timesCited, expectedCitations, coauthorList);
				pubList.add(pub);
				return isValid;
			} else {
				return ! isValid;
			}

		}
	}

	/**
	 * Return all publications (as well as all associated author info) contained in network file.
	 * Note that each publication serves as an edge and each author a node. 
	 * Node info is embedded inside each edge.
	 * @param File networkFile
	 * @return List pubList
	 * @throws FileNotFoundException 
	 */
	public static List<Publication> getPublications(File networkFile) throws FileNotFoundException {
			
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
					timesCited = contents[0].trim().isEmpty() ? "0" : contents[0].trim();
					expectedCitations = contents[1].trim().isEmpty() ? "0.00" : contents[1].trim();
					year = contents[2].trim().isEmpty() ? "0" : contents[2].trim();
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
					pub = new Publication(title, year, subjectArea, timesCited, expectedCitations, coauthorList);

					//Add publication to overall list
					pubList.add(pub);

				} 
			}
			
			return pubList;
		}

		return null;
	}

	/**
	 *Create load button. Load button loads data file onto Cytoscape for parsing
	 *@param null
	 *@return JButton load
	 */
	private static JButton createLoadButton() {
		JButton loadButton = new JButton("...");
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
					Incites.setSelectedFile(textFile);
					Incites.getBrowseTextField().setText(textFile.getAbsolutePath());
				} else {
					Incites.setSelectedFile(null);
					Incites.getBrowseTextField().setText("");
				}
			}
		});
		
		return loadButton;
	}
	
	/**
	 * Get selected Incites data file
	 * @param null
	 * @return File incitesData
	 */
	private static File getSelectedFile() {
		return Incites.selectedFile;
	}
	
	/**
	 * Set selected Incites data file
	 * @param File incitesData
	 * @return null
	 */
	private static void setSelectedFile(File selectedFile) {
		Incites.selectedFile = selectedFile;
	}
	
	/**
	 * Create create network button. Create network button reads the data file specified
	 * by user and attempts to create a network out of it.
	 * @param null
	 * @return JButton createNetworkButton
	 */
	private static JButton createNetworkButton() {
		JButton createNetworkButton = new JButton("Create Network");
		createNetworkButton.setToolTipText("Create network");
		createNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					if (Incites.getSelectedFile() == null) {
						Cytoscape.notifyUser("Network could not be created. Please specify file path");
					} else if (Incites.getFacultyTextField().getText().trim().isEmpty()) {
						Cytoscape.notifyUser("Network could not be created. Please specify faculty");
					} else {
						Cytoscape.createNetwork(Incites.getSelectedFile());
					}
				} catch (ParserConfigurationException e) {
					Cytoscape.notifyUser("A problem occured in Incites.createNetworkButton()! ParserConfigurationException!!");
				} catch (SAXException e) {
					Cytoscape.notifyUser("A problem occured in Incites.createNetworkButton()! SAXException!!");
				} catch (IOException e) {
					Cytoscape.notifyUser("A problem occured in Incites.createNetworkButton()! IOException!!");
				}
			}
		});
		return createNetworkButton;
	}
	
	/**
	 * Create new browse panel. Will allow user to specify the path
	 * of the data file they wish to load.
	 * @param null
	 * @return JPanel browsePanel
	 */
	private static JPanel createBrowsePanel() {
		CollapsiblePanel browsePanel = new CollapsiblePanel("Data File");
		browsePanel.setCollapsed(true);
		browsePanel.getContentPane().setLayout(new BoxLayout(browsePanel.getContentPane(), BoxLayout.X_AXIS));
		Incites.setBrowseTextField(new JTextField());
		Incites.getBrowseTextField().setEditable(true);
		browsePanel.getContentPane().add(Incites.getBrowseTextField());
		browsePanel.getContentPane().add(Incites.createLoadButton());
		return browsePanel;
	}
	
	/**
	 * Set browse text field
	 * @param JTextField browseTextField
	 * @return null
	 */
	private static void setBrowseTextField(JTextField browseTextField) {
		Incites.browseTextField = browseTextField;
	}
	
	/**
	 * Get browse text field
	 * @param null
	 * @return JTextField browseTextField
	 */
	private static JTextField getBrowseTextField() {
		return Incites.browseTextField;
	}
	
	/**
	 * Set faculty text field
	 * @param JTextField facultyTextField
	 * @return null
	 */
	private static void setFacultyTextField(JTextField facultyTextField) {
		Incites.facultyTextField = facultyTextField;
	}
	
	/**
	 * Get faculty text field
	 * @param null
	 * @return JTextField facultyTextField
	 */
	public static JTextField getFacultyTextField() {
		return Incites.facultyTextField;
	}
	
	/**Create choose faculty panel
	 * @pararm null
	 * @return JPanel facultyPanel
	 */
	private static JPanel createFacultyPanel() {
		CollapsiblePanel facultyPanel = new CollapsiblePanel("Faculty");
		facultyPanel.setCollapsed(true);
		facultyPanel.getContentPane().setLayout(new BoxLayout(facultyPanel.getContentPane(), BoxLayout.X_AXIS));
		Incites.setFacultyTextField(new JTextField());
		Incites.getFacultyTextField().setEditable(true);
		facultyPanel.getContentPane().add(Incites.getFacultyTextField());
		facultyPanel.getContentPane().add(Incites.createConfirmButton());
		return facultyPanel;
	}
	
	/**
	 * Create confirm button. Will be used to confirm
	 * faculty name entries.
	 * @param null
	 * @return JButton confirmButton
	 */
	private static JButton createConfirmButton() {
		final ImageIcon iconNotConfirmed = new ImageIcon(Incites.class.getClassLoader().getResource("new.png"));
		final ImageIcon iconConfirmed = new ImageIcon(Incites.class.getClassLoader().getResource("tick.png"));
		final JButton confirmButton = new JButton(iconNotConfirmed);
		
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (Incites.getFacultyTextField().getText() == null || Incites.getFacultyTextField().getText().trim().isEmpty()) {
					confirmButton.setIcon(iconNotConfirmed);
					confirmButton.setName("not confirmed");
				} else {
					if (confirmButton.getName().equalsIgnoreCase("not confirmed")) {
						confirmButton.setIcon(iconConfirmed);
						confirmButton.setName("confirmed");
					} 
				} 
			}	
		});
		
		return confirmButton;
	}

	/**
	 * Return new incites panel for use in info panel.
	 * Will allow user to load Incites derived text files
	 * @param null
	 * @return JPanel incitesPanel
	 */
	public static JPanel createIncitesPanel() {
		// Create new Incites panel.
		CollapsiblePanel incitesPanel = new CollapsiblePanel("Incites");
		incitesPanel.setCollapsed(true);
			
		incitesPanel
		.getContentPane().setLayout(new BoxLayout(incitesPanel.getContentPane(), BoxLayout.Y_AXIS));	
		
		// Add browse panel to incites panel
		incitesPanel.getContentPane().add(Incites.createBrowsePanel(), BorderLayout.NORTH);
		
		// Add faculty panel
		incitesPanel.getContentPane().add(Incites.createFacultyPanel());
		
		// Add create network button to browse panel
		incitesPanel.getContentPane().add(Incites.createNetworkButton());
		
		return incitesPanel;
	}
	
}