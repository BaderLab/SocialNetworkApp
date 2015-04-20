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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Copublications;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;

/**
 * This class is used to create maps that will later on function as building
 * blocks for networks.
 *
 * @author Victor Kofia
 */
public class Interaction {

    /**
     * Abstract map <br>
     * Key: <i>Consortium</i> <br>
     * Value: <i>Interaction</i>
     */
    private Map<Collaboration, ArrayList<AbstractEdge>> map = null;

    /**
     * Create a new {@link Interaction}
     *
     * @param List<AbstractEdge> edgeList
     * @param Integer type
     */
    public Interaction(List<? extends AbstractEdge> edgeList, int type) {
        switch (type) {
            case Category.PUBMED:
                this.setAbstractMap(this.loadAcademiaMap(edgeList));
                break;
            case Category.ACADEMIA:
                this.setAbstractMap(this.loadAcademiaMap(edgeList));
                break;
        }
    }

    /**
     * Get abstract map. Keys are all distinct collaborations found in map.
     * Values are the various interactions that each individual collaboration
     * shares. <br>
     * Key: <i>Collaboration</i> <br>
     * Value: <i>Interaction</i>
     *
     * @return Map abstractMap
     */
    public Map<Collaboration, ArrayList<AbstractEdge>> getAbstractMap() {
        return this.map;
    }

    /**
     * Load new abstract, collaboration & edgeList hash-map
     *
     * @param ArrayList abstractEdgeList
     * @return Map abstractMap
     */
    private Map<Collaboration, ArrayList<AbstractEdge>> loadAbstractMap(List<? extends AbstractEdge> abstractEdgeList) {
        // Create new map
        Map<Collaboration, ArrayList<AbstractEdge>> abstractMap = new HashMap<Collaboration, ArrayList<AbstractEdge>>();
        // Iterate through each edge
        for (AbstractEdge edge : abstractEdgeList) {
            int i = 0, j = 0;
            Collaboration collaboration = null;
            ArrayList<AbstractEdge> edgeList = null;
            AbstractNode node1 = null;
            AbstractNode node2 = null;
            // Link each node to a consortium consisting of all the other nodes
            // it is attached to via this edge
            while (i < edge.getNodes().size()) {
                node1 = edge.getNodes().get(i);
                j = i + 1;
                while (j < edge.getNodes().size()) {
                    node2 = edge.getNodes().get(j);
                    collaboration = new Collaboration(node1, node2);
                    // Check for collaboration's existence before
                    // it's entered into map
                    if (!abstractMap.containsKey(collaboration)) {
                        edgeList = new ArrayList<AbstractEdge>();
                        edgeList.add(edge);
                        abstractMap.put(collaboration, edgeList);
                    } else {
                        abstractMap.get(collaboration).add(edge);
                    }
                    j += 1;
                }
                i += 1;
            }
        }
        return abstractMap;
    }

    /**
     * Create new Academia hash-map
     *
     * @param facultyName
     * @param ArrayList abstractEdgeList
     * @return Map academiaMap
     */
    private Map<Collaboration, ArrayList<AbstractEdge>> loadAcademiaMap(List<? extends AbstractEdge> results) {
        // Create new academia map
        Map<Collaboration, ArrayList<AbstractEdge>> academiaMap = new HashMap<Collaboration, ArrayList<AbstractEdge>>();
        // Create new author map
        // Key: author's facsimile
        // Value: actual author
        Map<Author, Author> authorMap = new HashMap<Author, Author>();
        int h = 0, i = 0, j = 0;
        Collaboration collaboration = null;
        Author author1 = null, author2 = null;
        Copublications copublications = null;
        Publication publication = null;
        // Iterate through each publication
        while (h <= results.size() - 1) {
            i = 0;
            j = 0;
            collaboration = null;
            author1 = null;
            author2 = null;
            copublications = null;
            publication = (Publication) results.get(h);
            while (i < publication.getNodes().size()) {
                // Add author#1 to map if he / she is not present
                author1 = (Author) publication.getNodes().get(i);
                if (authorMap.get(author1) == null) {
                    authorMap.put(author1, author1);
                }
                // Add current publication to author's total list
                // of publications
                // NOTE: Author's time cited value will be updated
                // automatically
                authorMap.get(author1).addPublication(publication);
                j = i + 1;
                while (j < publication.getNodes().size()) {
                    // Add author#2 to map if he / she is not present
                    author2 = (Author) publication.getNodes().get(j);
                    if (authorMap.get(author2) == null) {
                        authorMap.put(author2, author2);
                    }
                    // Create collaboration out of both authors
                    collaboration = new Collaboration(authorMap.get(author1), authorMap.get(author2));
                    // Check for collaboration's existence before it's entered
                    // into map
                    if (!academiaMap.containsKey(collaboration)) {
                        copublications = new Copublications(collaboration, publication);
                        ArrayList<AbstractEdge> edgeList = new ArrayList<AbstractEdge>();
                        edgeList.add(copublications);
                        academiaMap.put(collaboration, edgeList);
                    } else {
                        ArrayList<AbstractEdge> array = academiaMap.get(collaboration);
                        copublications = (Copublications) array.get(0);
                        copublications.addPublication(publication);
                    }
                    j++;
                }
                i++;
            }
            h++;
        }

        return academiaMap;
    }

    /**
     * Set abstract map. Keys are all distinct collaborations found in map.
     * Values are the various interactions that each individual collaboration
     * shares. <br>
     * Key: <i>Collaboration</i> <br>
     * Value: <i>Interaction</i>
     *
     * @param null
     * @return Map abstractMap
     */
    private void setAbstractMap(Map<Collaboration, ArrayList<AbstractEdge>> abstractMap) {
        this.map = abstractMap;
    }

}