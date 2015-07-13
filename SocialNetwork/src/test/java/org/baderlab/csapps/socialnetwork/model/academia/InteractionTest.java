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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InteractionTest {

    private TaskMonitor taskMonitor = mock(TaskMonitor.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    /**
     * Verify the correct calculation of times cited in two publications
     * NOTE: The publications being tested only have three distinct authors
     */
    public void testTimesCitedMultiplePub() {
        String path = this.getClass().getResource("times_cited_multiple_pub.xlsx").getFile();
        File timesCitedFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(timesCitedFile, taskMonitor);
        Interaction interaction = new Interaction(incitesParser.getPubList(), Category.ACADEMIA, -1);
        Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
        boolean status = true;
        Iterator<Map.Entry<Collaboration, ArrayList<AbstractEdge>>> it = map.entrySet().iterator();
        Map.Entry<Collaboration, ArrayList<AbstractEdge>> pair = null;
        Collaboration cons = null;
        Author authorA = null, authorB = null;
        while (it.hasNext() && status == true) {
            pair = (Map.Entry<Collaboration, ArrayList<AbstractEdge>>) it.next();
            cons = pair.getKey();
            authorA = (Author) cons.getNode1();
            authorB = (Author) cons.getNode2();
            status = status && verifyAuthor(authorA);
            status = status && verifyAuthor(authorB);
        }
        assertTrue(status);
    }

    @Test
    /**
     * Verify the correct calculation of times cited in a single publication
     * NOTE: The publication being tested only has two authors and each author
     * has been duplicated once (i.e. Jack, Giant (UNIV); Jack, Giant (UNIV)
     */
    public void testTimesCitedSinglePub() {
        String path = this.getClass().getResource("times_cited_single_pub.xlsx").getFile();
        File timesCitedFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(timesCitedFile, taskMonitor);
        Interaction interaction = new Interaction(incitesParser.getPubList(), Category.ACADEMIA, -1);
        Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
        Collaboration cons = (Collaboration) map.keySet().toArray()[0];
        Author authorA = (Author) cons.getNode1();
        Author authorB = (Author) cons.getNode2();
        assertTrue(authorA.getTimesCited() == 2 && authorB.getTimesCited() == 2);
    }

    /**
     * Returns true iff author's identity is legitimate.
     * 
     * @param Author author
     * @return boolean
     */
    private boolean verifyAuthor(Author author) {
        boolean status = true;
        switch (author.getTimesCited()) {
            case 2:
                status = status && author.getLabel().equals("Julian Bashir");
                break;
            case 7:
                status = status && author.getLabel().equals("Kira Nerys");
                break;
            case 9:
                status = status && author.getLabel().equals("Benjamin Sisko");
                break;
        }
        return status;
    }

}
