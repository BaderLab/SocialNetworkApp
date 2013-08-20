package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.util.Map;

import org.apache.xmlbeans.impl.common.Levenshtein;

import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;
import main.java.org.baderlab.csapps.socialnetwork.Category;

/**
 * An author for an article, journal review, or scientific paper
 * @author Victor Kofia
 */
public class Author extends AbstractNode {
	/**
	 * Author's faculty
	 */
	private String faculty = "N/A";
	/**
	 * Author's first initial
	 */
	private String firstInitial = "?";
	/**
	 * Author's first name
	 */
	private String firstName = "N/A";
	/**
	 * Author's primary institution
	 */
	private String institution = "N/A";
	/**
	 * Author's last name
	 */
	private String lastName = "N/A";
	/**
	 * Author's primary location
	 */
	private String location = "N/A";
	/**
	 * Author's middle initial
	 */
	private String middleInitial = "N/A";
	/**
	 * Author's total number of citations
	 */
	private int timesCited = 0;
	/**
	 * Author's origin
	 */
	private int origin = Category.DEFAULT;
	/**
	 * True iff author has been identified as faculty
	 */
	private boolean identified = false;
	
	/**
	 * Set identification
	 * @param boolean bool
	 * @return null
	 */
	private void setIdenfitication(boolean bool) {
		this.identified = bool;
	}
	
	/**
	 * Returns true iff author is a faculty member and has been
	 * successfully identified
	 * @param null
	 * @return boolean identified
	 */
	public boolean isIdentified() {
		return this.identified;
	}
		
	/**
	 * Create a new author with the first name, last name and middle initial specified 
	 * in rawAuthorText.
	 * Source file origin is specified by a special int value
	 * @param String rawAuthorText
	 * @param int origin
	 * @return null
	 */
	public Author(String rawAuthorText, int origin) {
		this.setOrigin(origin);
		switch (origin) {
			case Category.SCOPUS:
				String[] scopusNames = rawAuthorText.split("\\s|\\.");
				if (scopusNames.length == 1) {
					this.lastName = scopusNames[0];
				} else if (scopusNames.length == 2) {
					this.lastName = scopusNames[0];
					this.firstInitial = scopusNames[1];
				} else if (scopusNames.length >= 3) {
					this.lastName = scopusNames[0];
					this.firstInitial = scopusNames[1];
					this.middleInitial = scopusNames[2];
				}
				// Set Scopus attributes
				this.setNodeAttrMap(Scopus.constructScopusAttrMap(this));
				break;
			case Category.PUBMED:
				String[] pubmedNames = rawAuthorText.split("\\s");
				if (pubmedNames.length == 1) {
					this.lastName =  pubmedNames[0];
				} else {
					this.lastName = pubmedNames[0];
					int i = 1;
					for (i = 1; i < pubmedNames.length - 1; i++) {
						this.lastName += " " + pubmedNames[i];
					}
					if (pubmedNames[i].length() >= 2) {
						// Extract both first initial & middle initial
						this.firstInitial = pubmedNames[i].substring(0,1);
						this.middleInitial = pubmedNames[i].substring(1);
					} else {
						// If no middle initial is specified, it will be marked
						// as unknown
						this.firstInitial = pubmedNames[i];
					}
				}
				// Use Scopus attribute map to set map for Pubmed map 
				// (both are similar)
				this.setNodeAttrMap(Scopus.constructScopusAttrMap(this));
				break;
			case Category.INCITES:
				this.firstName = Incites.parseFirstName(rawAuthorText);
				if (! this.firstName.equalsIgnoreCase("N/A")) {
					this.firstInitial = this.firstName.substring(0,1);
				}
				this.middleInitial = Incites.parseMiddleInitial(rawAuthorText);
				this.lastName = Incites.parseLastName(rawAuthorText);
				this.institution = Incites.parseInstitution(rawAuthorText);
				this.setLocation(Incites.getLocationMap().get(institution));
				this.setNodeAttrMap(Incites.constructIncitesAttrMap(this));
				break;
			case Category.FACULTY:
				String[] authorAttr = rawAuthorText.split(";");
				// Since faculty authors are facsimiles, only a bare
				// minimum of attributes are needed to create them.
				// Last name and first initial are enough to identify
				// any author.
				this.lastName = authorAttr[0];
				this.firstName = authorAttr[1];
				this.firstInitial = this.firstName.substring(0,1);
				// No attribute map needs to be created ... 
				break;
		}
		
		// Format names to ensure consistency
		format();
	}

	/**
	 * Return true iff author is the same as other
	 * @param Object other
	 * @return boolean
	 */
	public boolean equals(Object other) {
		Author otherAuthor = (Author) other;
		boolean isEqual = false, isEqualFirstName = false, isEqualLastName = false;
		int distance = 0;
		double similarity = 0;
		// Incites
		if (this.getOrigin() == Category.INCITES && otherAuthor.getOrigin() == Category.INCITES) {
			boolean isEqualInstitution = false;
			isEqualLastName = this.lastName.equalsIgnoreCase(otherAuthor.lastName);
			isEqualInstitution = this.institution.equalsIgnoreCase(otherAuthor.institution);
			// Determine whether or not first names are equal
			distance = Levenshtein.distance(this.firstName.toLowerCase(), otherAuthor.firstName.toLowerCase());
			similarity = 1 - ((double) distance) / (Math.max(this.firstName.length(), otherAuthor.firstName.length()));
			if (similarity >= 0.5) {
				isEqualFirstName = true;
				if (this.firstName.length() > otherAuthor.firstName.length()) {
					otherAuthor.firstName = this.firstName;
				}
			} else {
				if (isEqualLastName) {
					boolean isSingleCharacter = false;
					boolean isSameAsInitial = false;
					if (this.firstName.length() < otherAuthor.firstName.length()) {
						isSingleCharacter = this.firstName.length() == 1;
						isSameAsInitial = this.firstName.equalsIgnoreCase(otherAuthor.firstInitial);
					} else {
						isSingleCharacter = otherAuthor.firstName.length() == 1;
						isSameAsInitial = otherAuthor.firstName.equalsIgnoreCase(this.firstInitial);
						if (isSingleCharacter && isSameAsInitial) {
							otherAuthor.firstName = this.firstName;
						}
					}
					// First name is only equal if it's a single character and is
					// similar to the other author's first initial
					isEqualFirstName = isSingleCharacter
							&& isSameAsInitial;
				}
			}
			isEqual = isEqualLastName && isEqualFirstName && isEqualInstitution;
		// Incites (~ Faculty)
		} else if (this.getOrigin() == Category.INCITES && otherAuthor.getOrigin() == Category.FACULTY) {
			isEqualLastName = this.lastName.equalsIgnoreCase(otherAuthor.lastName);
			// Determine whether or not first names are equal
			distance = Levenshtein.distance(this.firstName.toLowerCase(), otherAuthor.firstName.toLowerCase());
			similarity = 1 - ((double) distance) / (Math.max(this.firstName.length(), otherAuthor.firstName.length()));
			if (similarity >= 0.5) {
				isEqualFirstName = true;
				if (this.firstName.length() > otherAuthor.firstName.length()) {
					otherAuthor.firstName = this.firstName;
				}
			// If Levenshtein distance is too small, check to see if 
			// if the first name is actually an initial i.e. 'V'
			} else {
				boolean isSingleCharacter = false;
				boolean isSameAsInitial = false;
				if (this.firstName.length() < otherAuthor.firstName.length()) {
					isSingleCharacter = this.firstName.length() == 1;
					isSameAsInitial = this.firstName.equalsIgnoreCase(otherAuthor.firstInitial);
				} else {
					isSingleCharacter = otherAuthor.firstName.length() == 1;
					isSameAsInitial = otherAuthor.firstName.equalsIgnoreCase(this.firstInitial);
					if (isSingleCharacter && isSameAsInitial) {
						otherAuthor.firstName = this.firstName;
					}
				}
				// First name is only equal if it's a single character and is
				// similar to the other author's first initial
				isEqualFirstName = isSingleCharacter
						&& isSameAsInitial;
			}
			isEqual = isEqualLastName && isEqualFirstName;
			// Check to see if the other author has been correctly ID'ed. If so, set his identification
			// status to true.
			if (isEqual) {
				otherAuthor.setIdenfitication(true);
			}
		// Pubmed / Scopus
		} else if (this.getOrigin() == Category.PUBMED && otherAuthor.getOrigin() == Category.PUBMED ||
				   this.getOrigin() == Category.SCOPUS && otherAuthor.getOrigin() == Category.SCOPUS) {
			isEqualLastName = this.lastName.equalsIgnoreCase(otherAuthor.lastName);
			isEqualFirstName = this.getFirstInitial().equalsIgnoreCase(otherAuthor.getFirstInitial());
			isEqual = isEqualFirstName && isEqualLastName;
		}
		return isEqual;
	}

	/**
	 * Capitalize the first letter of string and return. If string is 
	 * a single character letter, it will be capitalized. Empty strings 
	 * will yield empty strings. 
	 * @param null
	 * @return null
	 */
	public void format() {
		// Verify that the first letters in both first and last names are uppercase, and all following letters
		// are in lowercase
		this.firstName = this.firstName.substring(0,1).toUpperCase() + this.firstName.substring(1).toLowerCase();
		this.lastName = this.lastName.substring(0,1).toUpperCase() + this.lastName.substring(1).toLowerCase();
		// Ensure that the first and middle initials are capitalized
		this.firstInitial = this.firstInitial.toUpperCase();
		this.middleInitial = this.middleInitial.toUpperCase();
	}

	/**
	 * Get author's faculty
	 * @param null
	 * @return String faculty
	 */
	public String getFaculty() {
		return faculty;
	}

	/**
	 * Get first initial
	 * @param null
	 * @return String firstInitial
	 */
	public String getFirstInitial() {
		return this.firstInitial;
	}

	/**
	 * Get author's first name
	 * @param null
	 * @return String firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Get author's institution
	 * @param null
	 * @return String institution
	 */
	public String getInstitution() {
		return this.institution;
	}

	
	/**
	 * Get author's last name
	 * @param null
	 * @return String lastName
	 */
	public String getLastName() {
		return this.lastName;
	}
	
	/**
	 * Get author's location
	 * @param null
	 * @return String location
	 */
	public String getLocation() {
		return location;
	} 

	/**
	 * Get attribute map
	 * @param null
	 * @return Map attrMap
	 */
	public Map<String, Object> getNodeAttrMap() {
		return this.nodeAttrMap;
	}
	
	/**
	 * Get author's total number of citations
	 * @param null
	 * @return int timesCited
	 */
	public int getTimesCited() {
		return this.timesCited;
	}
	
	/**
	 * Return author hash code. Hash code uniquely identifies each specific
	 * last name - first initial combo. Individuals with matching last names
	 * and first initials are considered to be the same.
	 * @param null
	 * @return int hashCode
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result += prime * result
				+ ((firstInitial == null) ? 0 : firstInitial.hashCode());
//		result = prime * result
//				+ ((institution == null) ? 0 : institution.hashCode());
		result += prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
//		result = prime * result
//				+ ((location == null) ? 0 : location.hashCode());
//		result = prime * result
//				+ ((middleInitial == null) ? 0 : middleInitial.hashCode());
//		result = prime * result + totalPubs;
		return result;
	}

	/**
	 * Set author's faculty
	 * @param String faculty
	 * @return null
	 */
	public void setFaculty(String faculty) {
		this.faculty = faculty;
		this.getNodeAttrMap().put("Faculty", this.faculty);
	}

	/**
	 * Set first initial
	 * @param String firstInitial
	 * @return null
	 */
	public void setFirstInitial(String firstInitial) {
		this.firstInitial = firstInitial;
	}

	/**Set author's location. If no location is found on the
	 * map then it is assumed that author's not in academia
	 * @param String location
	 * @return null
	 */
	public void setLocation(String location) {
		if (location == null) {
			if (this.getInstitution().equalsIgnoreCase("N/A")) {
				this.location = "N/A";
			} else {
				this.location = "Other";
			}
		} else {
			this.location = location;
		}
	}

	/**
	 * Set attribute map
	 * @param Map attrMap
	 * @return null
	 */
	public void setNodeAttrMap(Map<String, Object> attrMap) {
		this.nodeAttrMap = attrMap;
	}
	
	/**
	 * Set author's total number of citations
	 * @param int timesCited
	 * @return null
	 */
	public void setTimesCited(int timesCited) {
		this.timesCited = timesCited;
		this.getNodeAttrMap().put("Times Cited", timesCited);
	}
	
	/**
	 * Return a string representation of author in the format:
	 * <br><b><i>Last Name, First Name</i></b>
	 * @param null
	 * @return String author
	 */
	public String toString() {
		return lastName + ", " + firstName;
	}

	/**
	 * Get author's origin
	 * @param null
	 * @return int origin
	 */
	public int getOrigin() {
		return origin;
	}

	/**
	 * Set author's origin
	 * @param int origin
	 * @return null
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}
 
}
