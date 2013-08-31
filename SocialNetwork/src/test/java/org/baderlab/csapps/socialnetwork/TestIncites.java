package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import main.java.org.baderlab.csapps.socialnetwork.model.academia.Author;
import main.java.org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Incites-data parsing feature
 * @author Victor Kofia
 */
public class TestIncites {

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
		File defectiveRowsExistFile = new File("src/test/resources/incites/data/defective_rows_exist.xlsx");
		IncitesParser incitesParser = new IncitesParser(defectiveRowsExistFile);
		assertTrue(incitesParser.getDefectiveRows() == 7);
	}
	
	@Test
	/**
	 * Confirm the non-existence of defective rows in a valid spreadsheet
	 */
	public void testDefectiveRowsNonExistent() {
		File defectiveRowsNotExistFile = new File("src/test/resources/incites/data/no_defective_rows.xlsx");
		IncitesParser incitesParser = new IncitesParser(defectiveRowsNotExistFile);
		assertTrue(incitesParser.getDefectiveRows() == 0);
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
		File ignoredRowsNotExistFile = new File("src/test/resources/incites/data/no_ignored_rows.xlsx");
		IncitesParser incitesParser = new IncitesParser(ignoredRowsNotExistFile);
		assertTrue(incitesParser.getIgnoredRows() == 0);
	}
	
	@Test
	/**
	 * Confirm correct identification of missing faculty
	 */
	public void testUnidentifiedFaculty() {
		File ignoredRowsNotExistFile = new File("src/test/resources/incites/data/faculty.xlsx");
		IncitesParser incitesParser = new IncitesParser(ignoredRowsNotExistFile);
		ArrayList<Author> unidentifiedFacultyList = incitesParser.getUnidentifiedFacultyList();
		assertTrue(unidentifiedFacultyList.size() == 13);
	}
	
	@Test
	/**
	 * Confirm correct identification of existing faculty
	 */
	public void testIdentifiedFaculty() {
		File ignoredRowsNotExistFile = new File("src/test/resources/incites/data/faculty.xlsx");
		IncitesParser incitesParser = new IncitesParser(ignoredRowsNotExistFile);
		ArrayList<Author> identifiedFacultyList = incitesParser.getIdentifiedFacultyList();
		assertTrue(identifiedFacultyList.size() == 14);
	}
	
	@Test
	/**
	 * Confirm correct identification of lone author
	 * NOTE: A lone author is one who has published 
	 * a paper by himself / herself
	 */
	public void testLoneAuthor() {
		File loneAuthorFile = new File("src/test/resources/incites/data/lone_author_exists.xlsx");
		IncitesParser incitesParser = new IncitesParser(loneAuthorFile);
		ArrayList<Author> loneAuthorList = incitesParser.getLoneAuthorList();
		assertTrue(loneAuthorList.size() == 1);
	}
}