package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.Author;
import main.java.org.baderlab.csapps.socialnetwork.Search;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAuthor {
	ArrayList<Author> authorList = null;
	Map<Author, String> authorMap = null;
	

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		
	}

	@Test
	/**
	 * Test if two analogous author objects are equal to each other
	 */
	public void testEquality() {
		Author author1 = new Author("somebody h", Search.PUBMED);
		Author author2 = new Author("somebody hm", Search.PUBMED);
		assertTrue(author1.equals(author2));
	}
	
	@Test
	/**
	 * Test if two analogous author objects have the exact same hashcode
	 */
	public void testHashCode() {
		Author author1 = new Author("somebody h", Search.PUBMED);
		Author author2 = new Author("somebody hm", Search.PUBMED);
		assertTrue(author1.hashCode() == author2.hashCode());
	}
	
	@Test
	/**
	 * Test if an array list can fail to distinguish between two 
	 * author objects with the same name
	 */
	public void testListRecognition() {
		Author author1 = new Author("somebody h", Search.PUBMED);
		Author author2 = new Author("somebody hm", Search.PUBMED);
		ArrayList<Author> authorList = new ArrayList<Author>();
		authorList.add(author1);
		assertTrue(authorList.contains(author2));
	}
	
	@Test
	/**
	 * Test if a hash map can recognize two author objects with similar
	 * names as being one and the same
	 */
	public void testMapRecognition() {
		Author author1 = new Author("somebody h", Search.PUBMED);
		Author author2 = new Author("somebody hm", Search.PUBMED);
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
		Author author = new Author("somebody h", Search.PUBMED);
		Author authorUpperCase = new Author("sOMeBODy H", Search.PUBMED);
		assertTrue(authorUpperCase.equals(author));
	}
	
	@Test
	/**
	 * Test if two author objects have the same hashcode when the only difference 
	 * between the two is casing
	 */
	public void testAuthorUpperCaseHashCode() {
		Author author = new Author("somebody hm", Search.PUBMED);
		Author authorUpperCase = new Author("sOMeBODy H", Search.PUBMED);
		assertTrue(authorUpperCase.hashCode() == author.hashCode());
	}

}
