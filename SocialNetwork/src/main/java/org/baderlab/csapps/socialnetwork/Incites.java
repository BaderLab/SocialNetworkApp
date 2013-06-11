package main.java.org.baderlab.csapps.socialnetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A repository of all important methods needed to manipulate Incites data
 * @author Victor Kofia
 */
public class Incites {
	
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
	public static ArrayList<Author> parseAuthors(String rawAuthorText) {
		String[] authors = rawAuthorText.split(";");
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
	 * Return all publications (as well as all associated author info) contained in network file.
	 * Note that each publication serves as an edge and each author a node. 
	 * Node info is embedded inside each edge.
	 * @param File networkFile
	 * @return List pubList
	 * @throws FileNotFoundException 
	 */
	public static List<Publication> getPublications(File networkFile) throws FileNotFoundException {
		
		List<Publication> pubList = new ArrayList<Publication>();
		List<Author> coauthorList = new ArrayList<Author>();
		
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
		Boolean qualityCheck = true;
		in.nextLine();
		while (in.hasNext()) {
			line = in.nextLine();
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
				coauthorList = parseAuthors(authors);
				
				// Set publication info
				pub = new Publication(title, year, subjectArea, coauthorList);
				pub.setTimesCited(timesCited);
				pub.setExpectedCitations(expectedCitations);
				
				//Add publication to overall list
				pubList.add(pub);
				
			} else {		
				qualityCheck = false;
			}
		}
		if (qualityCheck != true) {
			Cytoscape.notifyUser("Failed to load certain publication data due to inconsistent formatting.");
		}
		return pubList;
	}
	
}
