package org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Consortium identity tests
 * @author Victor Kofia
 */
public class TestConsortium {
	Author author1 = null;
	Author author2 = null;
	Collaboration cons1 = null;
	Collaboration cons2 = null;

	@Before
	public void setUp() throws Exception {
		this.author1 = new Author("ntu P", Category.PUBMED);
		this.author2 = new Author("homme G", Category.PUBMED);
		this.cons1 = new Collaboration(this.author1, this.author2);
		this.cons2 = new Collaboration(this.author2, this.author1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Test whether or not two consortiums are 'equal' to each other.
	 * NOTE: These consortiums differ only in node order 
	 * (i.e. node1 is node2 and node2 is node1)
	 */
	public void testEquality() {
		assertTrue(this.cons1.equals(this.cons2));
	}
	
	@Test
	/**
	 * Test whether or not two consortiums have the same hash-code
	 * NOTE: These consortiums differ only in node order 
	 * (i.e. node1 is node2 and node2 is node1)
	 */
	public void testHashCode() {
		assertTrue(this.cons1.hashCode() == this.cons2.hashCode());
	}
	
	@Test
	/**
	 * Test whether or not one consortium (cons1) can be used to identify 
	 * another (cons2) in a list
	 * NOTE: cons1 and cons2 differ only in node order 
	 */
	public void testListRecognition() {
		ArrayList<Collaboration> consortiumList = new ArrayList<Collaboration>();
		consortiumList.add(cons1);
		assertTrue(consortiumList.contains(cons2));
	}
	
	@Test
	/**
	 * Test whether or not one consortium (cons1) can be used to identify
	 * another (cons2) in a map
	 * NOTE: cons1 and cons2 differ only in node order
	 */
	public void testMapRecognition() {
		Map<Collaboration, String> consortiumMap = new HashMap<Collaboration, String>();
		consortiumMap.put(cons2, "intro");
		assertTrue(consortiumMap.containsKey(cons1));
	}

}
