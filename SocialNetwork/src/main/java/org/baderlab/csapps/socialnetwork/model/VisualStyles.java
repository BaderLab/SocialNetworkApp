package main.java.org.baderlab.csapps.socialnetwork.model;

import java.util.HashMap;
import java.util.Map;

/**
 * All the visual styles currently supported
 * by the Social Network App.
 * @author Victor Kofia
 */
public class VisualStyles {
	
	/**
	 * Default visual style
	 */
	final public static int DEFAULT_VISUAL_STYLE = -99;
	
	/**
	 * Default visual style help message
	 */
	final public static String DEFAULT_VISUAL_STYLE_HELP =  "The default visual style is the bas" +
															"e visual style. It makes use of no " +
															"network attributes.";

	/**
	 * Pubmed 'Lite' visual style
	 */
	final public static int PUBMED_LITE_VISUAL_STYLE = -100;
	
	/**
	 * Pubmed 'Lite' visual style help message
	 */
	final public static String PUBMED_LITE_VISUAL_STYLE_HELP =  "The Pubmed 'Lite' visual style is d" +
																"esigned for Pubmed networks. Pubmed" +
																" networks typically lack institutio" +
																"n information. Thus, no color codin" +
																"g is present. However they do conta" +
																"in all the standard list of attribu" +
																"tes found in Academia data-sets. In" +
																"formation on the total citation cou" +
																"nt and total co-publication count o" +
																"f each individual author is present" +
																" and can be visualized via node-siz" +
																"e and edge width respectively.";
	
	/**
	 * Scopus 'Lite' visual style
	 */
	final public static int SCOPUS_LITE_VISUAL_STYLE = -101;
	
	/**
	 * Scopus 'Lite' visual style help message
	 */
	final public static String SCOPUS_LITE_VISUAL_STYLE_HELP =  "The Scopus 'Lite' visual style is d" +
																"esigned for Scopus networks. Scopus" +
																" networks typically lack institutio" +
																"n information. Thus, no color codin" +
																"g is present. However they do conta" +
																"in all the standard list of attribu" +
																"tes found in Academia data-sets. In" +
																"formation on the total citation cou" +
																"nt and total co-publication count o" +
																"f each individual author is present" +
																" and can be visualized via node-siz" +
																"e and edge width respectively.";
	
	/**
	 * Incites 'Lite' visual style
	 */
	final public static int INCITES_LITE_VISUAL_STYLE = -103;
	
	/**
	 * Incites 'Lite' visual style help message
	 */
	final public static String INCITES_LITE_VISUAL_STYLE_HELP = "The Incites 'Lite' visual style is " +
																"designed for Incites networks. Inci" +
																"tes networks are fully annotated. T" +
																"his means that in addition to the s" +
																"tandard list of attributes typicall" +
																"y found in Academia data-sets they " +
																"also contain institution informatio" +
																"n. The Incites 'Lite' visual style " +
																"works by matching institutions to t" +
																"heir respective locations and color" +
																"-coding them. Institutions for whic" +
																"h no location has been found are cl" +
																"assified as 'other' (purple). Locat" +
																"ion matching is determined by a loc" +
																"al map and as such may or may not b" +
																"e adequate. To add an institution a" +
																"nd it's associated location go to:<" +
																"br> <b>Tools</b> - <b>Incites</b> -" +
																" <b>Add institution</b>.";

	/**
	 * A visual style map
	 * <br> Key: string representation of visual style
	 * <br> Value: visual style ID
	 */
	private static Map<String, Integer> visualStyleMap = null;

	/**
	 * Get unique id (numeral) associated with visual style
	 * @param String visualStyle
	 * @return int visualStyleID
	 */
	public static int getVisualStyleID(String visualStyle) {
		if (VisualStyles.visualStyleMap == null) {
			VisualStyles.visualStyleMap = new HashMap<String, Integer>();
			VisualStyles.visualStyleMap.put("--SELECT NETWORK VISUAL STYLE--", Category.DEFAULT);
			VisualStyles.visualStyleMap.put("Incites 'Lite'", INCITES_LITE_VISUAL_STYLE);
			VisualStyles.visualStyleMap.put("Pubmed 'Lite'", PUBMED_LITE_VISUAL_STYLE);
			VisualStyles.visualStyleMap.put("Scopus 'Lite'", PUBMED_LITE_VISUAL_STYLE);
		}
		return VisualStyles.visualStyleMap.get(visualStyle);
	}

	/**
	 * Get visual style list of a certain type
	 * @param int visualStyleSelectorType
	 * @return String[] visualStyleList
	 */
	public static String[] getVisualStyleList(int visualStyleSelectorType) {
		String[] visualStyleList = null;
		switch(visualStyleSelectorType) {
			case VisualStyles.DEFAULT_VISUAL_STYLE:
				visualStyleList = new String[] { "--SELECT NETWORK VISUAL STYLE--"};
				break;
			case VisualStyles.INCITES_LITE_VISUAL_STYLE:
				visualStyleList = new String[] {"Incites 'Lite'"};
				break;
			case VisualStyles.PUBMED_LITE_VISUAL_STYLE:
				visualStyleList = new String[] {"Pubmed 'Lite'"};
				break;
			case VisualStyles.SCOPUS_LITE_VISUAL_STYLE:
				visualStyleList = new String[] {"Scopus 'Lite'"};
				break;
		}
		return visualStyleList;
	}

}