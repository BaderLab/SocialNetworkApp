package org.baderlab.csapps.socialnetwork.model.academia;

/**
 * A PubMed Entrez tag 
 * @author Victor Kofia
 */
public class Tag {
	/**
	 * Augment query with query key
	 * @param String query
	 * @param String queryKey
	 * @return String query
	 */
	public static String augmentQueryKey(String query, String queryKey) {
		return query += "&query_key=" + queryKey;
	}
	
	/**
	 * Augment query with WebEnv
	 * @param String query
	 * @param String webEnv
	 * @return String query
	 */
	public static String augmentWebEnv(String query, String webEnv) {
		return query += "&WebEnv=" + webEnv;
	}
	
	/**
	 * Tag consists of queryKey, webEnv, retStart and retMax values.
	 * Necessary to recover document summaries from PubMed
	 */
	private String tag = null;
	
	/**
	 * Create new tag with the specified query key, webEnv, retStart and retMax
	 * @param String queryKey
	 * @param String webEnv
	 * @param String retStart 
	 * @param String retMax
	 * @return null
	 */
	public Tag(String queryKey, String webEnv, String retStart, String retMax) {
		this.tag = "";
		this.tag = augmentQueryKey(this.tag, queryKey);
		this.tag = augmentWebEnv(this.tag, webEnv);
	}
	
	/**
	 * Return string representation of tag
	 * @param null
	 * @return String tag
	 */
	public String toString() {
		return this.tag;
	}

}
