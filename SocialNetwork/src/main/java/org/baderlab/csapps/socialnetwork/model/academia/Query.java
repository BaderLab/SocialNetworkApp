package org.baderlab.csapps.socialnetwork.model.academia;


/**
 * A PubMed Entrez query
 * @author Victor Kofia
 */
public class Query {
  
	/**
	 * Augment query with server history tag
	 * @param String query
	 * @return String query
	 */
	private String augmentHistory(String query) {
		return String.format("%s&usehistory=y", query);
	}
	
	/**
	 * Augment query with journal tag
	 * @param String query
	 * @param String journal
	 * @return String query
	 */
	private String augmentJournal(String query, String journal) {
		if (journal.trim().isEmpty()) {
			return query;
		}
		return String.format("%s[journal]+AND+%s", journal.toLowerCase(), query);
	}
	
	/**
	 * Augment query with limit tag
	 * @param String query
	 * @param String limit
	 * @return String query
	 */
	private String augmentLimit(String query, String limit) {
		if (limit.trim().isEmpty()) {
			return query;
		}
		return String.format("%s&retmax=%s", query, limit);
	}
	
	/**
	 * Augment query with date tag
	 * @param String query
	 * @param String year
	 * @return String query
	 */
	private String augmentYear(String query, String year) {
		if (year.trim().isEmpty()) {
			return query;
		}
		return String.format("%s+AND+%s[pdat]", query, year);
	}
	
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
		rawQuery = rawQuery.replace(",", "");
		rawQuery = rawQuery.replace(" ", "+");
		this.query = augmentHistory(rawQuery);
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
		rawQuery = rawQuery.replace(" ", "+");
		rawQuery = augmentYear(rawQuery, year);
		rawQuery = augmentJournal(rawQuery, journal);
		rawQuery = augmentHistory(rawQuery);
		this.query = augmentLimit(rawQuery, limit);	
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
