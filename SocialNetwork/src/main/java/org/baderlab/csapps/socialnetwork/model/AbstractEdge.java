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

import java.util.List;
import java.util.Map;
import org.cytoscape.model.CyEdge;

/**
 * A Cytoscape edge
 *
 * @author Victor Kofia
 */
public abstract class AbstractEdge {

    /**
     * CyEdge reference
     */
    protected CyEdge cyEdge;

    /**
     * A map containing all of edge's attributes
     */
    protected Map<String, Object> edgeAttrMap;

    /**
     * Construct an attribute map for edge
     *
     * @param null
     * @return null
     */
    public abstract void constructEdgeAttrMap();

    /**
     * Get CyEdge
     *
     * @param null
     * @return {@link CyEdge} cyEdge
     */
    public abstract CyEdge getCyEdge();

    /**
     * Return map containing all of edge's attributes
     *
     * @param null
     * @return Map attrMap
     */
    public abstract Map<String, Object> getEdgeAttrMap();

    /**
     * Return all nodes attached to edge
     *
     * @param null
     * @return List nodes
     */
    public abstract List<? extends AbstractNode> getNodes();

    /**
     * Set CyEdge
     *
     * @param {@link CyEdge} cyEdge
     * @return null
     */
    public abstract void setCyEdge(CyEdge cyEdge);

}
