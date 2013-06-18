package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import main.java.org.baderlab.csapps.socialnetwork.pubmed.Incites;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * Test Incites data parsing feature
 * @author Victor Kofia
 */
public class TestIncitesParser {

	@Before
	/**
	 * N/A
	 * @throws Exception
	 */
	public void setUp() throws Exception {
	}

	@After
	/**
	 * N/A
	 * @throws Exception
	 */
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Verify correct flagging of empty Incites data file
	 */
	public void testEmptyFile() {
		File emptyFile = new File("src/test/resources/empty_incites_data_file.txt");
		try {
			assertTrue(Incites.getPublications(emptyFile) == null);
		} catch (FileNotFoundException e) {
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Verify correct flagging of invalid Incites data file
	 */
	public void testInvalidFile() {
		File invalidFile = new File("src/test/resources/invalid_incites_data_file.txt");
		try {
			assertTrue(Incites.getPublications(invalidFile) == null);
		} catch (FileNotFoundException e) {
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Verify correct identification of valid Incites data file
	 */
	public void testValidFile() {
		File validFile = new File("src/test/resources/valid_incites_data_file.txt");
		try {
			assertTrue(Incites.getPublications(validFile) != null);
		} catch (FileNotFoundException e) {
			assertTrue(false);
		}
	}

}