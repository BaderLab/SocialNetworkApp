package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.Search;
import main.java.org.baderlab.csapps.socialnetwork.panels.UserPanel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test search feature
 * @author Victor Kofia
 */
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
		Cytoscape.setUserPanelRef(new UserPanel());
		Search search = new Search("emili a", Category.ACADEMIA);
		int hits = search.getTotalHits();
		int results = search.getResults().size();
		assertTrue(hits == results);
	}

}
