package main.java.org.baderlab.csapps.socialnetwork.model.academia;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JRadioButton;

import main.java.org.baderlab.csapps.socialnetwork.model.Cytoscape;

/**
 * Tools for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites {
	
	/**
	 * Reference to Incites radio button
	 */
	public static JRadioButton incitesRadioButton = null;

	/**
	 * Author location map
	 */
	private static Map<String, String> locationMap = null;

	/**
	 * Location ranking map.
	 * <br> key: <i>location</i>
	 * <br> value: <i>rank</i>
	 */
	private static Map<String, Integer> locationRankingMap = null;
	
	/**
	 * Get Incites radio button
	 * @param null
	 * @return JRadioButton incitesRadioButton
	 */
	public static JRadioButton getIncitesRadioButton() {
		return Incites.incitesRadioButton;
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
				Incites.setLocationMap((Map<String, String>) ois.readObject());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Cytoscape.notifyUser("Failed to load location map. FileNotFoundException.");
			} catch (IOException e) {
				e.printStackTrace();
				Cytoscape.notifyUser("Failed to load location map. IOException.");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				Cytoscape.notifyUser("Failed to load location map. ClassNotFoundException.");
			}
		}
		return Incites.locationMap;
	}
	
	/**
	 * Set Incites radio button
	 * @param JRadioButton incitesRadioButton
	 * @return null
	 */
	public static void setIncitesRadioButton(JRadioButton incitesRadioButton) {
		Incites.incitesRadioButton = incitesRadioButton;
	}

	/**
	 * Set location map
	 * @param Map locationMap
	 * @return null
	 */
	public static void setLocationMap(Map<String, String> locationMap) {
	Incites.locationMap = locationMap;
	}
	
	/**
	 * Set location ranking map
	 * @param Map locationRankingMap
	 * @return null
	 */
	private static void setLocationRankingMap(Map<String, Integer> map) {
		Incites.locationRankingMap = map;
	}
	
	/**
	 * Get location ranking map
	 * @param null
	 * @return Map locationRankingMap
	 */
	public static Map<String, Integer> getLocationRankingMap() {
		if (Incites.locationRankingMap == null) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			String[] locations = new String[] {"UNIV TORONTO", "Ontario", "Canada", 
                                                 "United States", "International", "Other"};
			for (int i = 0; i < 6; i++) {
				map.put(locations[i], 6 - i);
			}
			Incites.setLocationRankingMap(map);
		}
		return Incites.locationRankingMap;
	}
	
	/**
	 * Construct Incites attribute map
	 * @param null
	 * @return Map nodeAttrMap
	 */
	public static HashMap<String, Object> constructIncitesAttrMap(Author author) {
		HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
		String[] columns = new String[] {"Label", "Last Name", "First Name",
				                         "Institution", "Location", "Faculty",
				                         "Times Cited"};
		int i = 0;
		for (i = 0; i < 6; i++) {
			nodeAttrMap.put(columns[i], "");
		}
		nodeAttrMap.put(columns[i], 0);
		return nodeAttrMap;
	}
	
	/**
	 * Validate institution for both author1 and author2.
	 * Assumes that author and otherAuthor are the same
	 * individual but otherAuthor is the active reference.
	 * @param Author author
	 * @param Author other
	 * @return null
	 */
	public static void validateInstitution(Author author, Author otherAuthor) {
		String location = author.getLocation();
		String otherLocation = otherAuthor.getLocation();
		Map<String, Integer> rankMap = Incites.getLocationRankingMap();
		// Initialize rank and otherRank to a low rank
		// NOTE: Highest rank is 6 and lowest rank is 1
		int rank = 0, otherRank = 0;
		if (rankMap.containsKey(location)) {
			rank = rankMap.get(location);
		}
		if (rankMap.containsKey(otherLocation)) {
			otherRank = rankMap.get(otherLocation);
		}
		if (rank > otherRank) {
			otherAuthor.setInstitution(author.getInstitution());
			otherAuthor.setLocation(author.getLocation());
		} else if (rank == otherRank) {
			Author[] randomAuthorArray = new Author[] {author, otherAuthor};
			Random rand = new Random();
		    int i = rand.nextInt((1 - 0) + 1) + 0;
		    String randomInstitution = randomAuthorArray[i].getInstitution();
		    String randomLocation = randomAuthorArray[i].getLocation();
	        otherAuthor.setInstitution(randomInstitution);
			otherAuthor.setLocation(randomLocation);
		}
	}

}