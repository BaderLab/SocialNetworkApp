package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;
import main.java.org.baderlab.csapps.socialnetwork.Category;

/**
 * The author of an article, journal review, or scientific paper
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
	 * Create a new author with the first name, last name and middle initial specified 
	 * in rawAuthorText.
	 * Source file origin is specified by a special int value
	 * @param String rawAuthorText
	 * @param int origin
	 * @return null
	 */
	public Author(String rawAuthorText, int origin) {
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
				this.setNodeAttrMap(Category.constructScopusAttrMap(this));
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
						//Extract both first initial & middle initial
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
				this.setNodeAttrMap(Category.constructScopusAttrMap(this));
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
				this.setNodeAttrMap(Category.constructIncitesAttrMap(this));
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
		return this.lastName.equalsIgnoreCase(((Author)other).lastName)
			&& this.firstName.equalsIgnoreCase(((Author)other).firstName);
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
	 * NOTE: As author identification becomes more refined it may be wise 
	 * to make a few more adjustments to hashCode(). 
	 */
	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((firstInitial == null) ? 0 : firstInitial.hashCode());
//		result = prime * result
//				+ ((firstName == null) ? 0 : firstName.hashCode());
//		result = prime * result
//				+ ((institution == null) ? 0 : institution.hashCode());
//		result = prime * result
//				+ ((lastName == null) ? 0 : lastName.hashCode());
//		result = prime * result
//				+ ((location == null) ? 0 : location.hashCode());
//		result = prime * result
//				+ ((middleInitial == null) ? 0 : middleInitial.hashCode());
//		result = prime * result + totalPubs;
//		return result;
//	}

	/**
	 * NOTE: As author identification becomes more refined it may be wise 
	 * to make a few more adjustments to equals(). 
	 */
	
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Author other = (Author) obj;
//		if (firstInitial == null) {
//			if (other.firstInitial != null)
//				return false;
//		} else if (!firstInitial.equals(other.firstInitial))
//			return false;
//		if (firstName == null) {
//			if (other.firstName != null)
//				return false;
//		} else if (!firstName.equals(other.firstName))
//			return false;
//		if (institution == null) {
//			if (other.institution != null)
//				return false;
//		} else if (!institution.equals(other.institution))
//			return false;
//		if (lastName == null) {
//			if (other.lastName != null)
//				return false;
//		} else if (!lastName.equals(other.lastName))
//			return false;
//		if (location == null) {
//			if (other.location != null)
//				return false;
//		} else if (!location.equals(other.location))
//			return false;
//		if (middleInitial == null) {
//			if (other.middleInitial != null)
//				return false;
//		} else if (!middleInitial.equals(other.middleInitial))
//			return false;
//		if (totalPubs != other.totalPubs)
//			return false;
//		return true;
//	}
	
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
				this.location = "Community";
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
	 * <br><b>Name:</b> <i>name</i>
	 * <br><b>First Initial:</b> <i>firstInitial</i>
	 * @param null
	 * @return String author
	 */
	public String toString() {
		return "Name: " + lastName + "-" + firstName
			+  "\nInstitution: " + institution + "\n\n";
	}
 
}
