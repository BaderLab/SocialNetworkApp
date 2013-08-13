package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;

import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
  
/**
 * Methods and fields for manipulating Scopus data
 * @author Victor Kofia
 */
public class Scopus {
	
	/**
	 * Reference to Scopus checkbox
	 */
	public static JCheckBox scopusCheckBox = null;

	/**
	 * Get Scopus checkbox
	 * @param null
	 * @return JCheckBox scopusCheckBox
	 */
	public static JCheckBox getScopusCheckBox() {
		return scopusCheckBox;
	}
	
	/**
	 * Match year. Used for verifying validity of 
	 * Scopus data file.
	 * @param String rawText
	 * @return boolean
	 */
	private static boolean matchYear(String rawText) {
		Pattern pattern = Pattern.compile("^\\d{4}$");
		Matcher matcher = pattern.matcher(rawText.trim());
		return matcher.find();
	}
	
	/**
	 * This exception is thrown when a year can't be parsed (i.e. 
	 * incorrect formatting)
	 * @author Victor Kofia
	 */
	public static class UnableToParseYearException extends Exception {
		
		private static final long serialVersionUID = 1L;
		
	}
	
	/**
	 * Get Scopus publication list
	 * @param File scopusDataFile
	 * @return ArrayList pubList
	 */
	public static ArrayList<Publication> getScopusPubList(File scopusDataFile) {
		ArrayList<Publication> pubList = null;
		try {
			pubList = new ArrayList<Publication>();
			Scanner in = new Scanner(scopusDataFile);
			// Skip column headers
			in.nextLine();
			String line = null, authors = null, year = null;
			String[] columns = null;
			Publication pub = null;
			ArrayList<Author> coauthorList = new ArrayList<Author>();
			String title = null, subjectArea = null, timesCited = null;
			String numericalData = null;
			int lastIndex = 0;
			// Parse for publications
			while (in.hasNext()) {
				line = in.nextLine();
				columns = line.split("\",|,\"");
				authors = columns[0].substring(1);
				coauthorList = Scopus.parseAuthors(authors);
				title = columns[1].substring(1);
				year = columns[2];
				if (! Scopus.matchYear(year)) {
					throw new UnableToParseYearException();
				}
				subjectArea = columns[3];
				numericalData = columns[4];
				lastIndex = numericalData.lastIndexOf(",");
//				System.out.println("Year: " + year
//		         + "\nSubject Area: " + columns[3]
//		         + "\nAuthors: " + authors
//		         + "\nTitle: " + title + "\n\n");
				if (lastIndex == numericalData.length() - 1) {
					timesCited = "0";
				} else {
					timesCited = numericalData.substring(lastIndex + 1);
				}
				pub = new Publication(title, year, subjectArea, 
						timesCited, null, coauthorList);
				pubList.add(pub);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Unable to locate Scopus data file.\nPlease re-load" +
					             " file and try again.");
		} catch (UnableToParseYearException e) {
			pubList = null;
			e.printStackTrace();
			Cytoscape.notifyUser("Scopus data file is corrupt.\nPlease load" +
		             " a valid file and try again.");
		}
		return pubList;
	}

	/**
	 * Return authors
	 * @param String authors
	 * @return ArrayList authorList
	 */
	private static ArrayList<Author> parseAuthors(String authors) {
		ArrayList<Author> authorList = new ArrayList<Author>();
		String[] contents = authors.split(",");
		for (String authorInfo : contents) {
			authorList.add(new Author(authorInfo.trim(), Category.SCOPUS));
		}
		return authorList;
	}

	/**
	 * Set Scopus checkbox
	 * @param JCheckBox scopusCheckBox
	 * @return null
	 */
	public static void setScopusCheckBox(JCheckBox scopusCheckBox) {
		Scopus.scopusCheckBox = scopusCheckBox;
	}
 
}
