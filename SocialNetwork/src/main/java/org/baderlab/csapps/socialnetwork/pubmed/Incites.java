package main.java.org.baderlab.csapps.socialnetwork.pubmed;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import main.java.org.baderlab.csapps.socialnetwork.Author;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.Publication;
import main.java.org.baderlab.csapps.socialnetwork.Search;
import main.java.org.baderlab.csapps.socialnetwork.UserPanel;
import main.java.org.baderlab.csapps.socialnetwork.exceptions.UnableToParseAuthorException;

/**
 * Methods for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites {
	
	private static List<Publication> pubList = null;

	
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
	public static JButton createLoadButton() {
		// Create new icon object.
		URL iconURL = UserPanel.class.getClassLoader().getResource("new.png");
		ImageIcon iconLoad = new ImageIcon(iconURL);
		// Use icon object to create a new load button. 
		JButton loadButton = new JButton("Load", iconLoad);
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
						Cytoscape.notifyUser("Check createLoadButton() in UserPanel.java. An exception occurred there.");
					} catch (SAXException e) {
						Cytoscape.notifyUser("Check createLoadButton() in UserPanel.java. An exception occurred there.");
					} catch (IOException e) {
						Cytoscape.notifyUser("Check createLoadButton() in UserPanel.java. An exception occurred there.");
					}
				}
			}
		});
		
		return loadButton;
	}

	/**
	 * Return new incites panel for use in info panel.
	 * Will allow user to load Incites derived text files
	 * @param null
	 * @return JPanel incitesPanel
	 */
	public static JPanel createIncitesPanel() {
		// Create new Incites panel.
		JPanel incitesPanel = new JPanel();
		// Set preferred size
		incitesPanel.setPreferredSize(new Dimension(200,200));
		// Set border
        incitesPanel.setBorder(BorderFactory.createTitledBorder("Incites"));
		// Organize panel horizontally.
		incitesPanel
		.setLayout(new FlowLayout());	
		// Add button to Incites panel
		incitesPanel.add(Incites.createLoadButton());
		return incitesPanel;
	}
	
}