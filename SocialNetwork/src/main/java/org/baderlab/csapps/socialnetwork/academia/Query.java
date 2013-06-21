package main.java.org.baderlab.csapps.socialnetwork.academia;

/**
 * A PubMed Entrez query
 * @author Victor Kofia
 */
public class Query {
	/**
	 * Query is available globally to enable incremental building
	 */
	private String query;
	
	/**
	 * Create new valid query from rawQuery
	 * @param String rawQuery
	 * @return null
	 */
	public Query(String rawQuery) {
		rawQuery = Query.replaceSpaces(rawQuery);
		this.query = Query.augmentHistory(rawQuery);
	}
	
	/**
	 * Create new valid query from rawQuery. New query should incorporate
	 * the specified journal, year and limit
	 * @param String rawQuery
	 * @param String journal
	 * @param String year
	 * @param String limit
	 * @return null
	 */
	public Query(String rawQuery, String journal, String year, String limit) {
		rawQuery = Query.replaceSpaces(rawQuery);
		rawQuery = Query.augmentYear(rawQuery, year);
		rawQuery = Query.augmentJournal(rawQuery, journal);
		rawQuery = Query.augmentHistory(rawQuery);
		this.query = Query.augmentLimit(rawQuery, limit);	
	}
	
	/**
	 * Replace any spaces in query with a plus operator
	 * @param String query
	 * @return String query
	 */
	public static String replaceSpaces(String query) {
		query = query.toLowerCase();
		String[] splitSearch = query.split("\\s");
		// If the query contains only a single character, 
		// then return it as is
		if (splitSearch.length == 1) {
			return query;
		}
		String newSearchTerm = "", word;
		// Build new word with spaces replaced with plus signs
		for (int i = 0; i < splitSearch.length; i++) {
			word = splitSearch[i];
			newSearchTerm += word + "+";
		}
		// Remove final operator
		return newSearchTerm.substring(0,newSearchTerm.length() - 1);
	}
	
	/**
	 * Augment query with date tag
	 * @param String query
	 * @param String year
	 * @return String query
	 */
	public static String augmentYear(String query, String year) {
		if (year.trim().isEmpty()) {
			return query;
		}
		return query + "+AND+" + year + "[pdat]";
	}
	
	/**
	 * Augment query with journal tag
	 * @param String query
	 * @param String journal
	 * @return String query
	 */
	public static String augmentJournal(String query, String journal) {
		if (journal.trim().isEmpty()) {
			return query;
		}
		return journal.toLowerCase() + "[journal]+AND+" + query;
	}
	
	/**
	 * Augment query with limit tag
	 * @param String query
	 * @param String limit
	 * @return String query
	 */
	public static String augmentLimit(String query, String limit) {
		if (limit.trim().isEmpty()) {
			return query;
		}
		return query + "&retmax=" + limit;
	}
	
	/**
	 * Augment query with server history tag
	 * @param String query
	 * @return String query
	 */
	public static String augmentHistory(String query) {
		return query + "&usehistory=y";
	}
	
	/**
	 * Return string representation of query
	 * @param null
	 * @return String query
	 */
	public String toString() {
		return this.query;
	}
	
}
