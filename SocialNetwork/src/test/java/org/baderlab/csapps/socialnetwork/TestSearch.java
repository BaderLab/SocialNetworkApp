package org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;


import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Search;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.panels.UserPanel;
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
		SocialNetworkAppManager appManager = new SocialNetworkAppManager ();
		appManager.setUserPanelRef(new UserPanel(appManager));
		Search search = new Search("emili a", Category.ACADEMIA,appManager);
		int hits = search.getTotalHits();
		int results = search.getResults().size();
		assertTrue(hits == results);
	}

}
