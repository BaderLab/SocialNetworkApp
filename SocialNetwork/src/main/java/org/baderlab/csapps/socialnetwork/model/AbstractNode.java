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

import java.util.Map;

import org.cytoscape.model.CyNode;

/**
 * Wrapper class for a Cytoscape node {@link CyNode}
 *
 * @author Victor Kofia
 */
public abstract class AbstractNode {

    /**
     * CyNode reference
     */
    protected CyNode cyNode;

    /**
     * CyNode label
     */
    protected String label;

    /**
     * A map containing all of node's attributes
     */
    protected Map<String, Object> nodeAttrMap;

    /**
     * Get CyNode
     *
     * @return CyNode cyNode
     */
    public abstract CyNode getCyNode();

    /**
     * Get label
     *
     * @return String label
     */
    public abstract String getLabel();

    /**
     * Return map containing all of node's attributes
     *
     * @return Map attrMap
     */
    public abstract Map<String, Object> getNodeAttrMap();

    /**
     * Set CyNode
     *
     * @param CyNode cyNode
     */
    public abstract void setCyNode(CyNode cyNode);

    /**
     * Set label
     *
     * @param String label
     */
    public abstract void setLabel(String label);

    /**
     * Set map containing all of node's attributes
     *
     * @param Map attrMap
     */
    public abstract void setNodeAttrMap(Map<String, Object> attrMap);

}