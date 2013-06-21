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
	 * Get list of all categories currently supported by app
	 * @param null
	 * @return List categoryList
	 */
	public static String[] getCategoryList() {
		String[] categoryList = { "--SELECT CATEGORY--", "Academia"};
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
		return categoryMap;
	}

	/**
	 * Get academia info panel. In addition to Pubmed specific features, 
	 * this panel will also enable the user to load Incites data.
	 * @param null
	 * @return 
	 * @return JPanel pubmedInfoPanel
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

}
