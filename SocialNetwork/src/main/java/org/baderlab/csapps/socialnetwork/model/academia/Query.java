/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates,
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.model.academia;

/**
 * A PubMed Entrez query
 *
 * @author Victor Kofia
 */
public class Query {

    /**
     * Query is available globally to enable incremental building
     */
    private String query;

    /**
     * Create new valid query from rawQuery
     *
     * @param String rawQuery
     */
    public Query(String rawQuery) {
        rawQuery = rawQuery.replace(",", "");
        rawQuery = rawQuery.replace(" ", "+");
        this.query = augmentHistory(rawQuery);
    }

    /**
     * Create new valid query from rawQuery. New query should incorporate the
     * specified journal, year and limit
     *
     * @param String rawQuery
     * @param String journal
     * @param String year
     * @param String limit
     */
    public Query(String rawQuery, String journal, String year, String limit) {
        rawQuery = rawQuery.replace(" ", "+");
        rawQuery = augmentYear(rawQuery, year);
        rawQuery = augmentJournal(rawQuery, journal);
        rawQuery = augmentHistory(rawQuery);
        this.query = augmentLimit(rawQuery, limit);
    }

    /**
     * Augment query with server history tag
     *
     * @param String query
     * @return String query
     */
    private String augmentHistory(String query) {
        return String.format("%s&usehistory=y", query);
    }

    /**
     * Augment query with journal tag
     *
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
     *
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
     *
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
     * Return string representation of query
     *
     * @return String query
     */
    @Override
    public String toString() {
        return this.query;
    }

}
