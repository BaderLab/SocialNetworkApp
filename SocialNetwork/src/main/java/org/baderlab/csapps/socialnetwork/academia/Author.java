package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;
import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.Tester;

/**
 * The author of an article, journal review, or scientific paper
 * @author Victor Kofia
 *
 */
public class Author extends AbstractNode {
	/**
	 * Incites (IP = 167.68.24.112)
	 */
	final public static int INCITES = (167 << 24) + (68 << 16) + (24 << 8) + 112;
	/**
	 * PubMed (IP = 130.14.29.110)
	 */
	final public static int PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;
	/**
	 * Author's first initial
	 */
	private String firstInitial = "N/A";
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
			case Author.PUBMED:
				String[] names = rawAuthorText.split("\\s");
				if (names.length == 2) {
					this.lastName = names[0];
					if (names[1].length() == 2) {
						//Extract both first initial & middle initial
						this.firstInitial = names[1].substring(0,1);
						this.middleInitial = names[1].substring(1);
					} else {
						//If no middle initial is specified, it will be marked as unknown
						this.firstInitial = names[1];
					}
				} else if (names.length == 1) {
					this.lastName = names[0];
				}
				break;
			case Author.INCITES:
				this.firstName = Incites.parseFirstName(rawAuthorText);
				this.middleInitial = Incites.parseMiddleInitial(rawAuthorText);
				this.lastName = Incites.parseLastName(rawAuthorText);
				this.institution = Incites.parseInstitution(rawAuthorText);
				Map<String, String> locationMap = Incites.getLocationMap();
				this.setLocation(Incites.getLocationMap().get(institution));
				break;
		}
		
		// Construct author's attribute map
		constructNodeAttrMap();
		
		// Format names to ensure consistency
		format();
	}

	/**
	 * Construct attribute map
	 * @param null
	 * @return null
	 */
	public void constructNodeAttrMap() {
		nodeAttrMap = new HashMap<String, Object>();
		nodeAttrMap.put("Last Name", this.lastName);
		nodeAttrMap.put("First Name", this.firstName);
		nodeAttrMap.put("Times Cited", this.timesCited);
		nodeAttrMap.put("Institution", this.institution);
		nodeAttrMap.put("Location", this.location);
	}

	/**
	 * Return true iff author is the same as other
	 * @param Object other
	 * @return boolean
	 */
	public boolean equals(Object other) {
		return this.lastName.equalsIgnoreCase(((Author)other).lastName) &&
			   this.firstInitial.equalsIgnoreCase(((Author)other).firstInitial);
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

	/**Set author's location
	 * @param String location
	 * @return null
	 */
	public void setLocation(String location) {
		if (location == null) {
			this.location = "N/A";
		} else {
			this.location = location;
		}
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
	 * Set author's total number of citations
	 * @param int timesCited
	 * @return null
	 */
	public void setTimesCited(int timesCited) {
		this.timesCited = timesCited;
		this.getNodeAttrMap().put("Times Cited", timesCited);
	}
	
	/**
	 * Return a string representation of author in the format [Last Name] [First Initial]
	 * @param null
	 * @return String author
	 */
	public String toString() {
		return "Name: " + lastName + "-" + firstInitial
			+  "\nInstitution: " + institution + "\n\n";
	}
 
}
