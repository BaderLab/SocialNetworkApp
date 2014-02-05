package org.baderlab.csapps.socialnetwork.model.academia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JRadioButton;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;

  
/**
 * Tools for manipulating Scopus data
 * @author Victor Kofia
 */
public class Scopus {
	

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
				//only split by commas not contained in quotes.
				//columns = line.split("(,)(?=(?:[^\"]|\"[^\"]*\")*$)");
				columns = splitQuoted("\"",",",line);
				//columns = line.split("\",|,\"");
				authors = columns[0].replace("\"", "");
				coauthorList = this.parseAuthors(authors);
				title = columns[1].replace("\"", "");
				year = columns[2].replace("\"", "");
				if (! this.matchYear(year)) {
					//the year doesn't match assume something is wonky with this line
					//skip this record, print out the line and continue
					System.out.println("unable to parse scopus line: " + line.toString());
					continue;
					//throw new UnableToParseYearException();
				}
				subjectArea = columns[3].replace("\"", "");
				numericalData = (columns[10] != null) ? columns[10].replace("\"", ""):columns[10];
				if (numericalData == null || numericalData.equalsIgnoreCase("")) {
					timesCited = "0";
				} else {
					timesCited = numericalData;
				}
				pub = new Publication(title, year, subjectArea, 
						timesCited, null, coauthorList);
				this.getPubList().add(pub);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			CytoscapeUtilities.notifyUser("Unable to locate Scopus data file.\nPlease re-load" +
					             " file and try again.");
		} /*catch (UnableToParseYearException e) {
			this.setPubList(null);
			e.printStackTrace();
			CytoscapeUtilities.notifyUser("Scopus data file is corrupt.\nPlease load" +
		             " a valid file and try again.");
		}*/
	}

	private String[] splitQuoted(String quote, String separator, String s){
		
		//scopus file has 14 fields.
		String[] columns = new String[14];
		int current = 0;
		int index = 0;
		int startindex = 0;
		int firstquote = s.indexOf(quote);
		int nextquote = s.indexOf(quote, current +1);
		int nextcomma = s.indexOf(separator);
		while(current < s.length() && index < 14){
		
			if(nextcomma > current && (nextcomma < firstquote || nextcomma > nextquote)){
				columns[index] = (s.substring(startindex,nextcomma));
				startindex = nextcomma+1;
				index++;
			}
			
			current = nextcomma;
			nextcomma = s.indexOf(separator, current+1);
			//if next comma is -1 then we have gotten to end of the string. 
			nextcomma = (nextcomma == -1)? s.length():nextcomma;
			
			if(current > nextquote){
				firstquote = s.indexOf(quote, nextquote+1);
				nextquote = s.indexOf(quote,firstquote+1);
			}
			
		}
		return columns;
			
			
	}
	
	/**
	 * Return authors
	 * @param String authors
	 * @return ArrayList authorList
	 */
	private ArrayList<Author> parseAuthors(String authors) {
		ArrayList<Author> authorList = new ArrayList<Author>();
		String[] contents = authors.split("\\.,");
		for (String authorInfo : contents) {
			authorList.add(new Author(authorInfo.trim(), Category.SCOPUS));
		}
		return authorList;
	}

	
	/**
	 * Construct Scopus attribute map
	 * @param null
	 * @return Map nodeAttrMap
	 */
	public static HashMap<String, Object> constructScopusAttrMap(Author author) {
		HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
		String[] columns = new String[] {BasicSocialNetworkVisualstyle.nodeattr_label, BasicSocialNetworkVisualstyle.nodeattr_lname, BasicSocialNetworkVisualstyle.nodeattr_fname,
				BasicSocialNetworkVisualstyle.nodeattr_timescited,BasicSocialNetworkVisualstyle.nodeattr_numpub, BasicSocialNetworkVisualstyle.nodeattr_pub};
		int i = 0;
		for (i = 0; i < 4; i++) {
			nodeAttrMap.put(columns[i], "");
		}
		//initialize the num publication attribute (~Integer)
		nodeAttrMap.put(columns[i], 0);
		// Initialize Publications attribute (~ ArrayList)
		nodeAttrMap.put(columns[i+1], new ArrayList<String>());
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
