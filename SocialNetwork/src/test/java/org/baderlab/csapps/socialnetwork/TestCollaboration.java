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

package org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Collaboration identity tests
 *
 * @author Victor Kofia
 */
public class TestCollaboration {

    Author author1 = null;
    Author author2 = null;
    Collaboration cons1 = null;
    Collaboration cons2 = null;

    @Before
    public void setUp() throws Exception {
        this.author1 = new Author("ntu P", Category.PUBMED);
        this.author2 = new Author("homme G", Category.PUBMED);
        this.cons1 = new Collaboration(this.author1, this.author2);
        this.cons2 = new Collaboration(this.author2, this.author1);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    /**
     * Test whether or not two consortiums are 'equal' to each other.
     * NOTE: These consortiums differ only in node order
     * (i.e. node1 is node2 and node2 is node1)
     */
    public void testEquality() {
        assertTrue(this.cons1.equals(this.cons2));
    }

    @Test
    /**
     * Test whether or not two consortiums have the same hash-code
     * NOTE: These consortiums differ only in node order
     * (i.e. node1 is node2 and node2 is node1)
     */
    public void testHashCode() {
        assertTrue(this.cons1.hashCode() == this.cons2.hashCode());
    }

    @Test
    /**
     * Test whether or not one consortium (cons1) can be used to identify
     * another (cons2) in a list
     * NOTE: cons1 and cons2 differ only in node order
     */
    public void testListRecognition() {
        ArrayList<Collaboration> consortiumList = new ArrayList<Collaboration>();
        consortiumList.add(this.cons1);
        assertTrue(consortiumList.contains(this.cons2));
    }

    @Test
    /**
     * Test whether or not one consortium (cons1) can be used to identify
     * another (cons2) in a map
     * NOTE: cons1 and cons2 differ only in node order
     */
    public void testMapRecognition() {
        Map<Collaboration, String> consortiumMap = new HashMap<Collaboration, String>();
        consortiumMap.put(this.cons2, "intro");
        assertTrue(consortiumMap.containsKey(this.cons1));
    }

}
