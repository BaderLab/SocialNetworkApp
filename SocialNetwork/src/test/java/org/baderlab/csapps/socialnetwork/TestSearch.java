package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Search;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSearch {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Verify that the number of hits declared by PubMed's xml file is equal 
	 * to the total number of results returned by search
	 */
	public void testPubmedSearch() {
		Search search = new Search("isserlin r", Category.ACADEMIA);
		int hits = search.getTotalHits();
		int results = search.getResults().size();
		assertTrue(hits == results);
	}

}
