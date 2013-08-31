package main.java.org.baderlab.csapps.socialnetwork.model.academia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JRadioButton;

import main.java.org.baderlab.csapps.socialnetwork.model.Category;
import main.java.org.baderlab.csapps.socialnetwork.model.Cytoscape;
  
/**
 * Tools for manipulating Scopus data
 * @author Victor Kofia
 */
public class Scopus {
	
	/**
	 * Reference to Scopus radio button
	 */
	private static JRadioButton scopusRadioButton = null;
	
	/**
	 * Reference to Scopus publication list
	 */
	private ArrayList<Publication> pubList = null;
	
	/**
	 * Create new Scopus object
	 * @param File csv
	 * @return null
	 */
	public Scopus(File csv) {
		this.parseScopusPubList(csv);
	}

	/**
	 * Get Scopus radio button
	 * @param null
	 * @return JRadioButton radioButton
	 */
	public static JRadioButton getScopusRadioButton() {
		return scopusRadioButton;
	}
	
	/**
	 * Match year. Used for verifying validity of 
	 * Scopus data file.
	 * @param String rawText
	 * @return boolean bool
	 */
	private boolean matchYear(String rawText) {
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
	 * @param null
	 */
	private void parseScopusPubList(File csv) {
		try {
			Scanner in = new Scanner(csv);
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
				coauthorList = this.parseAuthors(authors);
				title = columns[1].substring(1);
				year = columns[2];
				if (! this.matchYear(year)) {
					throw new UnableToParseYearException();
				}
				subjectArea = columns[3];
				numericalData = columns[4];
				lastIndex = numericalData.lastIndexOf(",");
				if (lastIndex == numericalData.length() - 1) {
					timesCited = "0";
				} else {
					timesCited = numericalData.substring(lastIndex + 1);
				}
				pub = new Publication(title, year, subjectArea, 
						timesCited, null, coauthorList);
				this.getPubList().add(pub);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Unable to locate Scopus data file.\nPlease re-load" +
					             " file and try again.");
		} catch (UnableToParseYearException e) {
			this.setPubList(null);
			e.printStackTrace();
			Cytoscape.notifyUser("Scopus data file is corrupt.\nPlease load" +
		             " a valid file and try again.");
		}
	}

	/**
	 * Return authors
	 * @param String authors
	 * @return ArrayList authorList
	 */
	private ArrayList<Author> parseAuthors(String authors) {
		ArrayList<Author> authorList = new ArrayList<Author>();
		String[] contents = authors.split(",");
		for (String authorInfo : contents) {
			authorList.add(new Author(authorInfo.trim(), Category.SCOPUS));
		}
		return authorList;
	}

	/**
	 * Set Scopus radio button
	 * @param JCheckBox scopusRadioButton
	 * @return null
	 */
	public static void setScopusRadioButton(JRadioButton scopusRadioButton) {
		Scopus.scopusRadioButton = scopusRadioButton;
	}
	
	/**
	 * Construct Scopus attribute map
	 * @param null
	 * @return Map nodeAttrMap
	 */
	public static HashMap<String, Object> constructScopusAttrMap(Author author) {
		HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
		String[] columns = new String[] {"Label", "Last Name", "First Name",
				                         "Times Cited", "Publications"};
		int i = 0;
		for (i = 0; i < 4; i++) {
			nodeAttrMap.put(columns[i], "");
		}
		// Initialize Publications attribute (~ ArrayList)
		nodeAttrMap.put(columns[i], new ArrayList<String>());
		return nodeAttrMap;
	}

	/**
	 * Get publication list
	 * @param null
	 * @return ArrayList pubList
	 */
	public ArrayList<Publication> getPubList() {
		if (this.pubList == null) {
			this.pubList = new ArrayList<Publication>();
		}
		return this.pubList;
	}

	/**
	 * Set publication list
	 * @param Publication pubList
	 * @return null
	 */
	private void setPubList(ArrayList<Publication> pubList) {
		this.pubList = pubList;
	}
 
}