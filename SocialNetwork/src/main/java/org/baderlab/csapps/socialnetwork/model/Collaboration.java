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

/**
 * Two nodes that share an interaction (or interactions)
 *
 * @author Victor Kofia
 */
public class Collaboration {

    /**
     * A node (~ node1)
     */
    private AbstractNode node1 = null;
    /**
     * A node (~ node2)
     */
    private AbstractNode node2 = null;

    /**
     * Create a new Collaboration object composed of node1 and node2
     *
     * @param AbstractNode node1
     * @param AbstractNode node2
     */
    public Collaboration(AbstractNode node1, AbstractNode node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    /**
     * Return true iff collaboration is equal to other.
     *
     * @param Object Collaboration
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Collaboration other = (Collaboration) obj;
        if (this.node1 == null) {
            if (other.node1 != null)
            {
                return false;
                // Collaborations are considered equal regardless of order (i.e.
                // node1 and node2
                // are interchangeable).
            }
        } else if (!(this.node1.equals(other.node1) | this.node1.equals(other.node2))) {
            return false;
        }
        if (this.node2 == null) {
            if (other.node2 != null) {
                return false;
            }
        } else if (!(this.node2.equals(other.node1) | this.node2.equals(other.node2))) {
            return false;
        }
        return true;
    }

    /**
     * Get node1. Returns null if no node is present.
     *
     * @return {@link AbstractNode} author1
     */
    public AbstractNode getNode1() {
        return this.node1;
    }

    /**
     * Get node2. Returns null if no node is present.
     *
     * @return AbstractNode author2
     */
    public AbstractNode getNode2() {
        return this.node2;
    }

    /**
     * Return Collaboration hash code. Used to determine a collaboration's
     * identity. Current system considers collaborations holding the exact same
     * nodes but in a different ordering (i.e. Victor stored in node2 instead of
     * node1) to be one and the same.
     *
     * @return int hashCode
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.node2 == null) ? 0 : this.node1.hashCode() + this.node2.hashCode());
        return result;
    }

    /**
     * Return string representation of collaboration in the format: <br>
     * Node#1: <i>node</i> <br>
     * Node#2: <i>node</i>
     *
     * @return String collaboration
     */
    @Override
    public String toString() {
        return "Node#1: " + this.node1.toString() + "\nNode#2: " + this.node2.toString() + "\n\n";
    }
}
