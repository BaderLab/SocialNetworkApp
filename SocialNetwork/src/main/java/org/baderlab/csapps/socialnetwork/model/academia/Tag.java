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
		return String.format("%s&query_key=%s", query, queryKey);
	}
	
	/**
	 * Augment query with WebEnv
	 * @param String query
	 * @param String webEnv
	 * @return String query
	 */
	public static String augmentWebEnv(String query, String webEnv) {
		return String.format("%s&WebEnv=%s", query, webEnv);
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
