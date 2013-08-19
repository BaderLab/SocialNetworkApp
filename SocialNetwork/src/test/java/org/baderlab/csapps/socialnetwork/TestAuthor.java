package test.java.org.baderlab.csapps.socialnetwork;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.academia.Author;
import main.java.org.baderlab.csapps.socialnetwork.Category;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Author identity tests
 * @author Victor Kofia
 */
public class TestAuthor {
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		
	}

	@Test
	/**
	 * Test if two author objects are equal to each other
	 */
	public void testEquality() {
		Author author1 = new Author("somebody h", Category.PUBMED);
		Author author2 = new Author("somebody hm", Category.PUBMED);
		assertTrue(author1.equals(author2));
	}
	
	@Test
	/**
	 * Test whether or not a defective first-name can be fixed
	 */
	public void testFixDefectiveFirstName() {
		Author author1 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia, Vica (UNIV TORONTO)", Category.INCITES);
		Author check = null;
		HashMap<Author, Author> authorMap = new HashMap<Author, Author>();
		authorMap.put(author2, author2);
		boolean fixed = false;
		check = authorMap.get(author1);
		if (check != null) {
			fixed = author2.getFirstName().equalsIgnoreCase(author1.getFirstName());
		}
		assertTrue(fixed);
	}
	
	@Test
	/**
	 * Test whether or not an author's first name will be updated if it is found 
	 * to be lacking (i.e. only consists of a single character)
	 */
	public void testFixFirstName() {
		Author author1 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia, V (UNIV TORONTO)", Category.INCITES);
		Object check = null;
		HashMap<Author, Author> authorMap = new HashMap<Author, Author>();
		authorMap.put(author2, author2);
		boolean switched = false;
		check = authorMap.get(author1);
		if (check != null) {
			switched = author2.getFirstName().equalsIgnoreCase(author1.getFirstName());
		}
		assertTrue(switched);
	}
	
	@Test
	/**
	 * Test if two author objects have the exact same hashcode
	 */
	public void testHashCode() {
		Author author1 = new Author("somebody h", Category.PUBMED);
		Author author2 = new Author("somebody hm", Category.PUBMED);
		assertTrue(author1.hashCode() == author2.hashCode());
	}
	
	@Test
	/**
	 * Test whether or not an author with a defective first name can be identified 
	 */
	public void testIdentifyAuthorWithDefectiveFirstName() {
		Author author1 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia, Vica (UNIV TORONTO)", Category.INCITES);
		Author author3 = null;
		HashMap<Author, Author> authorMap = new HashMap<Author, Author>();
		authorMap.put(author2, author2);
		boolean authorIdentified = false;
		author3 = authorMap.get(author1);
		if (author3 != null) {
			authorIdentified = author3.equals(author1) && author3.equals(author2);
		}
		assertTrue(authorIdentified);
	}
	
	
	@Test
	/**
	 * Test whether or not an author can be identified if his/her first name
	 * only consists of a single character
	 */
	public void testIdentifyAuthorWithOnlyFirstInitialA() {
		Author author1 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia, V (UNIV TORONTO)", Category.INCITES);
		Author author3 = null;
		HashMap<Author, Author> authorMap = new HashMap<Author, Author>();
		authorMap.put(author2, author2);
		boolean authorIdentified = false;
		author3 = authorMap.get(author1);
		if (author3 != null) {
			authorIdentified = author3.equals(author1) && author3.equals(author2);
		}
		assertTrue(authorIdentified);
	}
	
	@Test
	/**
	 * Test whether or not an author can be identified if his/her first name
	 * only consists of a single character
	 */
	public void testIdentifyAuthorWithOnlyFirstInitialB() {
		Author author1 = new Author("Kofia, V (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author3 = null;
		HashMap<Author, Author> authorMap = new HashMap<Author, Author>();
		authorMap.put(author2, author2);
		boolean authorIdentified = false;
		author3 = authorMap.get(author1);
		if (author3 != null) {
			authorIdentified = author3.equals(author1) && author3.equals(author2);
		}
		assertTrue(authorIdentified);
	}
	
	@Test
	/**
	 * Test if an array list can recognize two author objects with similar
	 * names
	 */
	public void testListRecognition() {
		Author author1 = new Author("somebody h", Category.PUBMED);
		Author author2 = new Author("somebody hm", Category.PUBMED);
		ArrayList<Author> authorList = new ArrayList<Author>();
		authorList.add(author1);
		assertTrue(authorList.contains(author2));
	}
	
	@Test
	/**
	 * Test if a hash map can recognize two author objects with similar
	 * names
	 */
	public void testMapRecognition() {
		Author author1 = new Author("somebody h", Category.PUBMED);
		Author author2 = new Author("somebody hm", Category.PUBMED);
		Map<Author, String> authorMap = new HashMap<Author, String>();
		authorMap.put(author2, "intro");
		assertTrue(authorMap.containsKey(author1));
	}

	@Test
	/**
	 * Test if two authors are equal when the only difference
	 * between the two is casing
	 */
	public void testUpperCaseEquality() {
		Author author = new Author("somebody h", Category.PUBMED);
		Author authorUpperCase = new Author("sOMeBODy H", Category.PUBMED);
		assertTrue(authorUpperCase.equals(author));
	}
	
	@Test
	/**
	 * Test if two author objects have the same hashcode when the only difference 
	 * between the two is casing
	 */
	public void testUpperCaseHashCode() {
		Author author = new Author("somebody hm", Category.PUBMED);
		Author authorUpperCase = new Author("sOMeBODy H", Category.PUBMED);
		assertTrue(authorUpperCase.hashCode() == author.hashCode());
	}
	
	@Test
	/**
	 * Verify that nothing will happen to an author's first name if it is valid
	 */
	public void testVerifyFirstNameA() {
		Author author1 = new Author("Kofia, Vica (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author3 = null;
		HashMap<Author, Author> authorMap = new HashMap<Author, Author>();
		authorMap.put(author2, author2);
		boolean switched = true;
		author3 = authorMap.get(author1);
		if (author3 != null) {
			switched = ! author3.getFirstName().equalsIgnoreCase(author2.getFirstName());
		}
		assertTrue(! switched);
	}


	@Test
	/**
	 * Verify that an author's first name will NOT be changed if it's found to be valid
	 * (i.e. consists of more than a single character)
	 */
	public void testVerifyFirstNameB() {
		Author author1 = new Author("Kofia, V (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author3 = null;
		HashMap<Author, Author> authorMap = new HashMap<Author, Author>();
		authorMap.put(author2, author2);
		boolean switched = true;
		author3 = authorMap.get(author1);
		if (author3 != null) {
			switched = ! author3.getFirstName().equalsIgnoreCase(author2.getFirstName());
		}
		assertTrue(! switched);
	}
	
	@Test
	/**
	 * Test whether or not an author can be identified correctly as faculty
	 */
	public void testFacultyIdentificationInSet() {
		Author author1 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia;Victor", Category.FACULTY);
		HashSet<Author> authorSet = new HashSet<Author>();
		authorSet.add(author2);
		assertTrue(authorSet.contains(author1));
	}
	
	@Test
	/**
	 * Test whether or not an author with a slightly defective first name
	 * can be identified correctly as faculty
	 */
	public void testFacultyIdentificationInSetWhenIdentifierIsDefective() {
		Author author1 = new Author("Kofia, Vic (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia;Victor", Category.FACULTY);
		HashSet<Author> authorSet = new HashSet<Author>();
		authorSet.add(author2);
		assertTrue(authorSet.contains(author1));
	}
	
	@Test
	/**
	 * Test whether or not an author can be correctly identified even though his/her
	 * faculty designation is slightly defective
	 */
	public void testFacultyIdentificationInSetWhenIdentifiedIsDefective() {
		Author author1 = new Author("Kofia, Victor (UNIV TORONTO)", Category.INCITES);
		Author author2 = new Author("Kofia;Vic", Category.FACULTY);
		HashSet<Author> authorSet = new HashSet<Author>();
		authorSet.add(author2);
		assertTrue(authorSet.contains(author1));
	}

}
