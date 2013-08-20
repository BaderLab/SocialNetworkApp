package main.java.org.baderlab.csapps.socialnetwork;

import java.util.HashMap;
import java.util.Map;


import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Categories
 * @author Victor Kofia
 */
public class Category {
	/**
	 * Academia (IP = 74.125.226.112)
	 */
	final public static int ACADEMIA = (74 << 24) + (125 << 16) + (226 << 8) + 112;
	/**
	 * Chipped network view
	 */
	final public static int CHIPPED = -100;
	/**
	 * Default category
	 */
	final public static int DEFAULT = -1;
	/**
	 * Faculty category 
	 */
	final public static int FACULTY = -2;
	/**
	 * Incites (IP = 84.18.180.87)
	 */
	final public static int INCITES = (84 << 24) + (18 << 16) + (180 << 8) + 87;
	/**
	 * LinkedIn (IP = 216.52.242.80)
	 */
	final public static int LINKEDIN = (216 << 24) + (52 << 16) + (242 << 8) + 80;
	/**
	 * Pubmed (IP = 130.14.29.110)
	 */
	final public static int PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;
	/**
	 * Scopus (IP = 198.185.19.57)
	 */
	final public static int SCOPUS = (198 << 24) + (185 << 16) + (19 << 8) + 57;
	/**
	 * Twitter (IP = 199.59.150.39)
	 */
	final public static int TWITTER = (199 << 24) + (59 << 16) + (150 << 8) + 39;
	/**
	 * Vanue network view
	 */
	final public static int VANUE = -101;
	
	/**
	 * Youtube (IP = 74.125.226.101)
	 */
	final public static int YOUTUBE = (74 << 24) + (125 << 16) + (226 << 8) + 101;
	/** 
	 * A category map 
	 *<br> key: String representation of category
	 *<br> value: category ID
	 */
	private static Map<String, Integer> categoryMap = null;
	/**
	 * A visual style map
	 * <br> key: String representation of visual style
	 * <br> value: visual style ID
	 */
	private static Map<String, Integer> visualStyleMap = null;

	/**
	 * Create default info panel
	 * @param null
	 * @return JPanel defaultInfoPanel
	 */
	public static JPanel createDefaultInfoPanel() {
		JPanel defaultInfoPanel = new JPanel();
		defaultInfoPanel.setName("--SELECT CATEGORY--");
		return defaultInfoPanel;
	}

	/**
	 * Create LinkedIn info panel
	 * @param null
	 * @return JPanel linkedInInfoPanel
	 */
	public static JPanel createLinkedInInfoPanel() {
		JPanel linkedInInfoPanel = new JPanel();
		linkedInInfoPanel.setName("LinkedIn");
		linkedInInfoPanel.setBorder(BorderFactory.createTitledBorder("LinkedIn"));
		return linkedInInfoPanel;
	}

	/**
	 * Create Twitter info panel
	 * @param null
	 * @return JPanel twitterInfoPanel
	 */
	public static JPanel createTwitterInfoPanel() {
		JPanel twitterInfoPanel = new JPanel();
		twitterInfoPanel.setName("Twitter");
		twitterInfoPanel.setBorder(BorderFactory.createTitledBorder("Twitter"));
		return twitterInfoPanel;
	}

	/**
	 * Create Youtube info panel
	 * @param null
	 * @return JPanel youtubeInfoPanel
	 */
	public static JPanel createYoutubeInfoPanel() {
		JPanel youtubeInfoPanel = new JPanel();
		youtubeInfoPanel.setName("Youtube");
		youtubeInfoPanel.setBorder(BorderFactory.createTitledBorder("Youtube"));
		return youtubeInfoPanel;
	}
	
	/**
	 * Get unique id (numeral) associated with category
	 * @param String category
	 * @return int categoryID
	 */
	public static int getCategoryID(String category) {
		if (Category.categoryMap == null) {
			Category.categoryMap = new HashMap<String, Integer>();
			categoryMap.put("--SELECT CATEGORY--", DEFAULT);
			categoryMap.put("Academia", ACADEMIA);
			categoryMap.put("Twitter", TWITTER);
			categoryMap.put("LinkedIn", LINKEDIN);
			categoryMap.put("Youtube", YOUTUBE);
			categoryMap.put("Incites", INCITES);
			categoryMap.put("Scopus", SCOPUS);
			categoryMap.put("Pubmed", PUBMED);
		}
		return categoryMap.get(category);
	}

	/**
	 * Get list of all categories currently supported by app
	 * @param null
	 * @return List categoryList
	 */
	public static String[] getCategoryList() {
		String[] categoryList = {"Academia"};
		return categoryList;
	}

	/**
	 * Get list of search filters.
	 * Filter type varies with category.
	 * @param int selectedCategory
	 * @return String[] searchFilterList
	 */
	public static String[] getSearchFilterList(int selectedCategory) {
		String[] searchFilterList = null;
		switch (selectedCategory) {
			case Category.DEFAULT:
				searchFilterList = new String[] {"--SELECT FILTER--"};
				break;
			case Category.ACADEMIA:
				searchFilterList = new String[] {"Authors"};
				break;
			case Category.TWITTER:
				searchFilterList = new String[] { "--SELECT FILTER--", "Users", "HashTags"};
				break;
			case Category.LINKEDIN:
				searchFilterList = new String[] { "--SELECT FILTER--", "Users", "Groups", "Companies"};
				break;
			case Category.YOUTUBE:
				searchFilterList = new String[] { "--SELECT FILTER--", "Videos", "Channels"};
				break;
		}
		return searchFilterList;
	}

	/**
	 * Get unique id (numeral) associated with visual style
	 * @param String visualStyle
	 * @return int visualStyleID
	 */
	public static int getVisualStyleID(String visualStyle) {
		if (Category.visualStyleMap == null) {
			Category.visualStyleMap = new HashMap<String, Integer>();
			Category.visualStyleMap.put("--SELECT NETWORK VISUAL STYLE--", Category.DEFAULT);
			Category.visualStyleMap.put("Chipped", Category.CHIPPED);
			Category.visualStyleMap.put("Vanue", Category.VANUE);
		}
		return Category.visualStyleMap.get(visualStyle);
	}

	/**
	 * Get visual style list of a certain type
	 * @param int visualStyleSelectorType
	 * @return String[] visualStyleList
	 */
	public static String[] getVisualStyleList(int visualStyleSelectorType) {
		String[] visualStyleList = null;
		switch(visualStyleSelectorType) {
			case Category.ACADEMIA:
				visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--"};
				break;
			case Category.DEFAULT:
				visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--"};
				break;
			case Category.INCITES:
				visualStyleList = new String[] {"Chipped"};
				break;
			case Category.PUBMED:
				visualStyleList = new String[] {"Vanue"};
				break;
			case Category.SCOPUS:
				visualStyleList = new String[] {"Vanue"};
				break;
			case Category.TWITTER:
				visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--", "TwitterVerse", "IndigoWave" };
				break;
		}
		return visualStyleList;
	}
	
	/**
	 * Return string representation of category
	 * @param int categoryID
	 * @return String category
	 */
	public static String toString(int categoryID) {
		String category = null;
		switch (categoryID) {
			case Category.DEFAULT:
				category = "--SELECT CATEGORY--";
				break;
			case Category.ACADEMIA:
				category = "Academia";
				break;
			case Category.TWITTER:
				category = "Twitter";
				break;
			case Category.LINKEDIN:
				category = "LinkedIn";
				break;
			case Category.YOUTUBE:
				category = "Youtube";
				break;
			case Category.INCITES:
				category = "Incites";
				break;
			case Category.SCOPUS:
				category = "Scopus";
				break;
			case Category.PUBMED:
				category = "Pubmed";
				break;
		}
		return category;
	}

}
