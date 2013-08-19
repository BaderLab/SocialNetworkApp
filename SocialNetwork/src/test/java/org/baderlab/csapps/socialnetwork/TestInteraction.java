package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.AbstractEdge;
import main.java.org.baderlab.csapps.socialnetwork.Category;
import main.java.org.baderlab.csapps.socialnetwork.Consortium;
import main.java.org.baderlab.csapps.socialnetwork.Interaction;
import main.java.org.baderlab.csapps.socialnetwork.academia.Author;
import main.java.org.baderlab.csapps.socialnetwork.academia.Incites;

import org.junit.After;
import org.junit.Before;

/**
 * Test map creation
 * @author Victor Kofia
 */
public class TestInteraction {

	@Before
	public void setUp() throws Exception {
	}
	

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Verify the correct calculation of times cited in a single publication
	 * NOTE: The publication being tested only has two authors and each author
	 * has been duplicated once (i.e. Jack, Giant (UNIV); Jack, Giant (UNIV)
	 */
	public void testTimesCitedSinglePub() {
		File timesCitedFile = new File("src/test/resources/incites/data/times_cited_single_pub.xlsx");
		Incites incites = new Incites(timesCitedFile);
		Interaction interaction = new Interaction(incites.getPubList(), Category.ACADEMIA);
		Map<Consortium, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
		Consortium cons = (Consortium) map.keySet().toArray()[0];
		Author authorA = (Author) cons.getNode1();
		Author authorB = (Author) cons.getNode2();
		assertTrue(authorA.getTimesCited() == 2 && authorB.getTimesCited() == 2);
	}

	/**
	 * Verify the correct calculation of times cited in two publications
	 * NOTE: The publications being tested only have three distinct authors 
	 */
	public void testTimesCitedMultiplePub() {
		File timesCitedFile = new File("src/test/resources/incites/data/times_cited_multiple_pub.xlsx");
		Incites incites = new Incites(timesCitedFile);
		Interaction interaction = new Interaction(incites.getPubList(), Category.ACADEMIA);
		Map<Consortium, ArrayList<AbstractEdge>> map = interaction.getAbstractMap();
		Consortium cons = (Consortium) map.keySet().toArray()[0];
		Author authorA = (Author) cons.getNode1();
		Author authorB = (Author) cons.getNode2();
		assertTrue(authorA.getTimesCited() == 9 && authorB.getTimesCited() == 7);
	}

	
}
