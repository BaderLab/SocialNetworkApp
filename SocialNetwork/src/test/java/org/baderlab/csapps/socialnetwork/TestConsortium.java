package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.Consortium;
import main.java.org.baderlab.csapps.socialnetwork.academia.Author;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConsortium {
	Author author1 = null;
	Author author2 = null;
	Consortium cons1 = null;
	Consortium cons2 = null;

	@Before
	public void setUp() throws Exception {
		this.author1 = new Author("ntu P", Author.PUBMED);
		this.author2 = new Author("homme G", Author.PUBMED);
		this.cons1 = new Consortium(this.author1, this.author2);
		this.cons2 = new Consortium(this.author2, this.author1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEquality() {
		assertTrue(this.cons1.equals(this.cons2));
	}
	
	@Test
	public void testHashCode() {
		assertTrue(this.cons1.hashCode() == this.cons2.hashCode());
	}
	
	@Test
	public void testListRecognition() {
		ArrayList<Consortium> consortiumList = new ArrayList<Consortium>();
		consortiumList.add(cons1);
		assertTrue(consortiumList.contains(cons2));
	}
	
	@Test
	public void testMapRecognition() {
		Map<Consortium, String> consortiumMap = new HashMap<Consortium, String>();
		consortiumMap.put(cons2, "intro");
		assertTrue(consortiumMap.containsKey(cons1));
	}

}
