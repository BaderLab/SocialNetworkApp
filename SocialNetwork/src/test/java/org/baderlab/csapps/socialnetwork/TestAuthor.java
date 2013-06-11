package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.Author;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAuthor {
	private Author author1 = null;
	private Author author2 = null;
	ArrayList<Author> authorList = null;
	Map<Author, String> authorMap = null;
	

	@Before
	public void setUp() {
		this.author1 = new Author("somebody h", Author.PUBMED);
		this.author2 = new Author("somebody hm", Author.PUBMED);
	}

	@After
	public void tearDown() {
		
	}

	/**
	 * Test if two analogous author objects are equal to each other
	 */
	@Test
	public void testEquality() {
		assertTrue(author1.equals(author2));
	}
	
	/**
	 * Test if two analogous author objects have the exact same hashcode
	 */
	@Test
	public void testHashCode() {
		assertTrue(author1.hashCode() == author2.hashCode());
	}
	
	@Test
	/**
	 * Test if an array list can fail to distinguish between two 
	 * author objects with the same name
	 */
	public void testListRecognition() {
		ArrayList<Author> authorList = new ArrayList<Author>();
		authorList.add(author1);
		assertTrue(authorList.contains(author2));
	}
	
	/**
	 * Test if a hash map can recognize two author objects with similar
	 * names as being one and the same
	 */
	@Test
	public void testMapRecognition() {
		Map<Author, String> authorMap = new HashMap<Author, String>();
		authorMap.put(author2, "intro");
		assertTrue(authorMap.containsKey(author1));
	}
	
	@Test
	/**
	 * Test if two authors are equal when the only difference
	 * between the two is casing
	 */
	public void testAuthorUpperCaseEquality() {
		Author authorUpperCase = new Author("sOMeBODy H", Author.PUBMED);
		assertTrue(authorUpperCase.equals(this.author2));
	}
	
	@Test
	/**
	 * Test if two author objects have the same hashcode when the only difference 
	 * between the two is casing
	 */
	public void testAuthorUpperCaseHashCode() {
		Author authorUpperCase = new Author("sOMeBODy H", Author.PUBMED);
		System.out.println(authorUpperCase.hashCode());
		System.out.println(author1.hashCode());
		assertTrue(authorUpperCase.hashCode() == this.author1.hashCode());
	}

}
