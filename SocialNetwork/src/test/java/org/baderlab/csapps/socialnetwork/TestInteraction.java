package org.baderlab.csapps.socialnetwork;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestInteraction {

	@Before
	public void setUp() throws Exception {
	}
	

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	/**
	 * Verify the correct calculation of times cited in a single publication
	 * NOTE: The publication being tested only has two authors and each author
	 * has been duplicated once (i.e. Jack, Giant (UNIV); Jack, Giant (UNIV)
	 */
	public void testTimesCitedSinglePub() {
		File timesCitedFile = new File("src/test/resources/incites/data/times_cited_single_pub.xlsx");
		IncitesParser incitesParser = new IncitesParser(timesCitedFile);
		Interaction interaction = new Interaction(incitesParser.getPubList(), Category.ACADEMIA);
		Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
		Collaboration cons = (Collaboration) map.keySet().toArray()[0];
		Author authorA = (Author) cons.getNode1();
		Author authorB = (Author) cons.getNode2();
		assertTrue(authorA.getTimesCited() == 2 && authorB.getTimesCited() == 2);
	}

	@Test
	/**
	 * Verify the correct calculation of times cited in two publications
	 * NOTE: The publications being tested only have three distinct authors 
	 */
	public void testTimesCitedMultiplePub() {
		File timesCitedFile = new File("src/test/resources/incites/data/times_cited_multiple_pub.xlsx");
		IncitesParser incitesParser = new IncitesParser(timesCitedFile);
		Interaction interaction = new Interaction(incitesParser.getPubList(), Category.ACADEMIA);
		Map<Collaboration, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
		Collaboration cons = (Collaboration) map.keySet().toArray()[0];
		Author authorA = (Author) cons.getNode1();
		Author authorB = (Author) cons.getNode2();
		assertTrue(authorA.getTimesCited() == 9 && authorB.getTimesCited() == 7);
	}


}
