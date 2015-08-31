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

package org.baderlab.csapps.socialnetwork.model.academia.parsers.incites;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Incites-data parsing feature
 *
 * @author Victor Kofia
 */
public class IncitesParserTest {

    private TaskMonitor taskMonitor = mock(TaskMonitor.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    /**
     * Confirm the existence of defective rows in an invalid spreadsheet
     * NOTE: Defective rows are rows that contain 4 or 5 columns rather
     * than the standard 6. Inconsistency in column # could potentially break
     * the app. It is therefore imperative that defective rows be tracked and
     * dealt with accordingly.
     */
    public void testDefectiveRowsExist() {
        String path = this.getClass().getResource("defective_rows_exist.xlsx").getFile();
        File defectiveRowsExistFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(defectiveRowsExistFile, taskMonitor);
        assertTrue(incitesParser.getDefectiveRows() == 7);
    }

    @Test
    /**
     * Confirm the non-existence of defective rows in a valid spreadsheet
     */
    public void testDefectiveRowsNonExistent() {
        String path = this.getClass().getResource("no_defective_rows.xlsx").getFile();
        File defectiveRowsNotExistFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(defectiveRowsNotExistFile, taskMonitor);
        assertTrue(incitesParser.getDefectiveRows() == 0);
    }

    @Test
    /**
     * Confirm correct identification of existing faculty
     */
    public void testIdentifiedFaculty() {
        String path = this.getClass().getResource("faculty.xlsx").getFile();
        File ignoredRowsNotExistFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(ignoredRowsNotExistFile, taskMonitor);
        ArrayList<Author> identifiedFacultyList = incitesParser.getIdentifiedFacultyList();
        assertTrue(identifiedFacultyList.size() == 14);
    }

    @Test
    /**
     * Confirm the non-existence of ignored rows in a valid spreadsheet
     * NOTE: 'Ignored' rows are rows that contain data
     * but can't be parsed due to inconsistent formatting. The
     * presence of ignored rows means that some data has been lost.
     * Ideally, there should be ZERO ignored rows after parsing a data file.
     */
    public void testIgnoredRowsNonExistent() {
        String path = this.getClass().getResource("no_ignored_rows.xlsx").getFile();
        File ignoredRowsNotExistFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(ignoredRowsNotExistFile, taskMonitor);
        assertTrue(incitesParser.getIgnoredRows() == 0);
    }

    @Test
    /**
     * Confirm correct identification of lone author
     * NOTE: A lone author is one who has published
     * a paper by himself / herself
     */
    public void testLoneAuthor() {
        String path = this.getClass().getResource("lone_author_exists.xlsx").getFile();
        File loneAuthorFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(loneAuthorFile, taskMonitor);
        ArrayList<Author> loneAuthorList = incitesParser.getLoneAuthorList();
        assertTrue(loneAuthorList.size() == 1);
    }

    @Test
    /**
     * Confirm correct identification of missing faculty
     */
    public void testUnidentifiedFaculty() {
        String path = this.getClass().getResource("faculty.xlsx").getFile();
        File ignoredRowsNotExistFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(ignoredRowsNotExistFile, taskMonitor);
        ArrayList<Author> unidentifiedFacultyList = incitesParser.getUnidentifiedFacultyList();
        assertTrue(unidentifiedFacultyList.size() == 13);
    }
    
    @Test
    /**
     * Confirm that the calculated times cited value for the main author is correct
     */
    public void testTimesCited() {
        boolean status = false;
        String path = getClass().getResource("incites_times_cited.xlsx").getFile();
        File xlsxFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(xlsxFile, taskMonitor);
        Interaction interaction = new Interaction(incitesParser.getPubList(), Category.ACADEMIA, 500);
        Set<Collaboration> collaboratorSet = interaction.getAbstractMap().keySet();
        Collaboration[] collabArray = new Collaboration[collaboratorSet.size()];
        collaboratorSet.toArray(collabArray);
        Author author = CytoscapeUtilities.getAuthor("A Person", collabArray); 
        if (author != null) {
            status = author.getTimesCited() == 306;
        }
        assertTrue(status);
    }
    
    @Test
    /**
     * Confirm that institutions are being ranked correctly.
     */
    public void testInstitutionRanking() {
        boolean status = false;
        String path = getClass().getResource("incites_institution_ranking.xlsx").getFile();
        File xlsxFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(xlsxFile, taskMonitor);
        Interaction interaction = new Interaction(incitesParser.getPubList(), Category.ACADEMIA, 500);
        Set<Collaboration> collaboratorSet = interaction.getAbstractMap().keySet();
        Collaboration[] collabArray = new Collaboration[collaboratorSet.size()];
        collaboratorSet.toArray(collabArray);
        Author author = CytoscapeUtilities.getAuthor("Some Person", collabArray); 
        if (author != null) {
            status = author.getMainInstitution().equals("UNIV TORONTO");
            if (status) {
                List<String> x = author.getInstitutions();
                status = x.get(0).equals("UNIV TORONTO") &&
                         x.get(1).equals("FLORIDA STATE UNIV") &&
                         x.get(2).equals("GLYCAEM INDEX TESTING INC");
            }
        }
        assertTrue(status);
    }
    
    @Test
    /**
     * Confirm that the expected citations value of each author is being calculated correctly
     */
    public void testExpectedCitations() {
        boolean status = false;
        String path = getClass().getResource("expected_citations.xlsx").getFile();
        File xlsxFile = new File(path);
        IncitesParser incitesParser = new IncitesParser(xlsxFile, taskMonitor);
        Interaction interaction = new Interaction(incitesParser.getPubList(), Category.ACADEMIA, 500);
        Set<Collaboration> collaboratorSet = interaction.getAbstractMap().keySet();
        Collaboration[] collabArray = new Collaboration[collaboratorSet.size()];
        collaboratorSet.toArray(collabArray);
        // Retrieve authors
        Author bashir = CytoscapeUtilities.getAuthor("Julian Bashir", collabArray); 
        Author nerys = CytoscapeUtilities.getAuthor("Kira Nerys", collabArray); 
        Author sisko = CytoscapeUtilities.getAuthor("Benjamin Sisko", collabArray); 
        Author quark = CytoscapeUtilities.getAuthor("R Quark", collabArray); 
        // Verifying expected citations value
        if (bashir != null && nerys != null && sisko != null && quark != null) {
            status = String.format( "%.2f", bashir.getExpectedCitations()).equals("0.37");
            if (status) {
                status = String.format( "%.2f", nerys.getExpectedCitations()).equals("0.40");
                if (status) {
                    status = String.format( "%.2f", sisko.getExpectedCitations()).equals("0.40");
                    if (status) {
                        status = String.format( "%.2f", quark.getExpectedCitations()).equals("0.34");
                    }
                }
            }
        }
        assertTrue(status);
    }
}