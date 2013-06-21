package main.java.org.baderlab.csapps.socialnetwork;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import main.java.org.baderlab.csapps.socialnetwork.academia.Incites;

/**
 * Categories
 * @author Victor Kofia
 */
public class Category {

	/**
	 * Default website 
	 */
	final public static int DEFAULT = 0;
	/**
	 * Academia
	 */
	final public static int ACADEMIA = 1;
	/**
	 * LinkedIn (IP = 216.52.242.80
	 */
	final public static int LINKEDIN = (216 << 24) + (52 << 16) + (242 << 8) + 80;
	/**
	 * Youtube (IP = 74.125.226.101)
	 */
	final public static int YOUTUBE = (74 << 24) + (125 << 16) + (226 << 8) + 101;
	/**
	 * Twitter (IP = 199.59.150.39)
	 */
	final public static int TWITTER = (199 << 24) + (59 << 16) + (150 << 8) + 39;
	
	/**
	 * Get list of all categories currently supported by app
	 * @param null
	 * @return List categoryList
	 */
	public static String[] getCategoryList() {
		String[] categoryList = { "--SELECT CATEGORY--", "Academia", "Twitter", "LinkedIn", "Youtube"};
		return categoryList;
	}
	
	/**
	 * Construct category map. Keys are string representations of each category
	 * and values are the actual categories themselves
	 * @param null
	 * @return Map<String, int> academiaMap
	 */
	public static Map<String, Integer> getCategoryMap() {
		Map<String, Integer> categoryMap = new HashMap<String, Integer>();
		categoryMap.put("--SELECT CATEGORY--", DEFAULT);
		categoryMap.put("Academia", ACADEMIA);
		categoryMap.put("Twitter", TWITTER);
		categoryMap.put("LinkedIn", LINKEDIN);
		categoryMap.put("Youtube", YOUTUBE);
		return categoryMap;
	}

	/**
	 * Get academia info panel. In addition to Pubmed specific features, 
	 * this panel will also enable the user to load Incites data.
	 * @param null
	 * @return 
	 * @return JPanel academiaInfoPanel
	 */
	public static JPanel getAcademiaInfoPanel() {
		JPanel pubmedInfoPanel = new JPanel();
		pubmedInfoPanel.setName("Academia");
		
		pubmedInfoPanel
		.setLayout(new BorderLayout());
		
	    pubmedInfoPanel.setBorder(BorderFactory.createTitledBorder("Academia"));
	    
		pubmedInfoPanel.add(Incites.createIncitesInfoPanel(), BorderLayout.NORTH);
		
		return pubmedInfoPanel;
	}

	/**
	 * Load default info panel
	 * @param null
	 * @return null
	 */
	public static JPanel getDefaultInfoPanel() {
		JPanel defaultInfoPanel = new JPanel();
		defaultInfoPanel.setName("--SELECT--");
		return defaultInfoPanel;
	}
	
	/**
	 * Get visual style list
	 * @param null
	 * @return String[] visualStyleList
	 */
	public static String[] getVisualStyleList() {
		// Which style list gets selected is dependent on what type of network the 
		String[] visualStyleList = null;
		switch(UserPanel.getVisualStyleSelectorType()) {
			case Category.DEFAULT:
				visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--", "FairyDust", "Milky Way" };
				break;
			case Category.ACADEMIA:
				visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--", "Chipped", "Honor" };
				break;
			case Category.TWITTER:
				visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--", "TwitterVerse", "IndigoWave" };
				break;
		}
		return visualStyleList;
	}
	

	/**
	 * Get view map
	 * @param null
	 * @return Map viewMap
	 */
	public static Map<String, Integer> getVisualStyleMap() {
		Map<String, Integer> visualStyleMap = new HashMap<String, Integer>();
		visualStyleMap.put("--SELECT NETWORK VISUAL STYLE--", Cytoscape.DEFAULT);
		visualStyleMap.put("Chipped", Cytoscape.CHIPPED);
		return visualStyleMap;
	}

}
