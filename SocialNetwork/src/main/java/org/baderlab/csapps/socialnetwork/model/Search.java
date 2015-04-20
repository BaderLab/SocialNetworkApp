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

package org.baderlab.csapps.socialnetwork.model;

import java.awt.Cursor;
import java.util.List;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.academia.Pubmed;

/**
 * A search session
 *
 * @author Victor Kofia
 */
public class Search {

    /**
     * Search results
     */
    public List<? extends AbstractEdge> results = null;
    /**
     * Total hits
     */
    public int totalHits = 0;

    /**
     * Create a new search session
     *
     * @param String searchTerm
     * @param int website
     * @return null
     */
    public Search(String searchTerm, int website, SocialNetworkAppManager appManager) {
        appManager.getUserPanelRef().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        switch (website) {
            case Category.DEFAULT:
                CytoscapeUtilities.notifyUser("Click --SELECT CATEGORY-- to select a category");
                break;
            case Category.ACADEMIA:
                Pubmed pubmed = new Pubmed(searchTerm);
                this.results = pubmed.getListOfPublications();
                this.totalHits = pubmed.getListOfPublications().size();
                break;
        }
    }

    /**
     * Get results
     *
     * @param null
     * @return List results
     */
    public List<? extends AbstractEdge> getResults() {
        return this.results;
    }

    /**
     * Get total number of hits
     *
     * @param null
     * @return in totalHits
     */
    public int getTotalHits() {
        return this.totalHits;
    }

}
