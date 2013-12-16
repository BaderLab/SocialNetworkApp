package org.baderlab.csapps.socialnetwork.model.academia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.xmlbeans.impl.common.Levenshtein;
import org.baderlab.csapps.socialnetwork.model.AbstractNode;
import org.baderlab.csapps.socialnetwork.model.BasicSocialNetworkVisualstyle;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.IncitesVisualStyle;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParser;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;


/**
 * An author for an article, journal review, or scientific paper
 * @author Victor Kofia
 */
public class Author extends AbstractNode {
	/**
	 * Author's department
	 */
	private String department = "N/A";
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
	 * True iff author's citations for a given publication
	 * have already been counted
	 */
	private boolean alreadyBeenAdded = false;
	/**
	 * List of all publications author has authored / co-authored
	 */
	private List<String> pubList = null;
	
	/*
	 * location Map 
	 */
	private Incites_InstitutionLocationMap locationMap = null;
	
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
				// Initialize attribute map for Scopus author
				this.setNodeAttrMap(Scopus.constructScopusAttrMap(this));
				String[] scopusNames = rawAuthorText.split("\\s|,");
				if (scopusNames.length == 1) {
					this.setLastName(scopusNames[0]);
				} else  {
					String lastName = scopusNames[0];
					int i = 1;
					for (i = 1; i < scopusNames.length - 1; i++) {
						lastName += " " + scopusNames[i];
					}
					this.setLastName(lastName);
					String[] initials = scopusNames[i].split("\\.");
					if (initials.length == 1) {
						this.setFirstName(initials[0]);
						this.setFirstInitial(initials[0]);
					} else if (initials.length > 1) {
						this.setFirstName(initials[0]);
						this.setFirstInitial(initials[0]);
						this.setMiddleInitial(initials[1]);
					}
				}
				this.setLabel(this.getFirstInitial() + " " + this.getLastName());
				break;
			case Category.PUBMED:
				// Initialize attribute map for Pubmed author (~ same as Scopus)
				this.setNodeAttrMap(Scopus.constructScopusAttrMap(this));
				String[] pubmedNames = rawAuthorText.split("\\s");
				if (pubmedNames.length == 1) {
					this.lastName =  pubmedNames[0];
				} else {
					String lastName = pubmedNames[0];
					int i = 1;
					for (i = 1; i < pubmedNames.length - 1; i++) {
						lastName += " " + pubmedNames[i];
					}
					this.setLastName(lastName);
					if (pubmedNames[i].length() >= 2) {
						// Extract both first initial & middle initial
						this.setFirstInitial(pubmedNames[i].substring(0,1));
						this.setMiddleInitial(pubmedNames[i].substring(1));
					} else {
						// If no middle initial is specified, it will be marked
						// as unknown
						this.setFirstInitial(pubmedNames[i]);
					}
					this.setLabel(this.getFirstInitial() + " " + this.getLastName());
				}
				break;			
			case Category.FACULTY:
				String[] authorAttr = rawAuthorText.split(";");
				// Since faculty authors are facsimiles, only a bare
				// minimum of attributes are needed to create them.
				// Last name and first initial are enough to identify
				// any author.
				this.setLastName(authorAttr[0]);
				this.setFirstName(authorAttr[1]);
				this.setFirstInitial(this.getFirstName().substring(0,1));
				// Set attributes to null to prevent Cytoscape from
				// trying to put data in non-existent columns
				this.setNodeAttrMap(null);
				break;
		}
		
		// Format names to ensure consistency
		format();
	}
	/*
	 * Constructor specific to incites as the location map is required to build an incites author object
	 */
	public Author(String rawAuthorText, int origin, Incites_InstitutionLocationMap locationMap) {
		this.setOrigin(origin);
			switch (origin) {
			
			case Category.INCITES:
				this.locationMap = locationMap;
				// Initialize attribute map for Incites author
				this.setNodeAttrMap(constructIncitesAttrMap());
				this.setFirstName(IncitesParser.parseFirstName(rawAuthorText));
				if (! this.getFirstName().equalsIgnoreCase("N/A")) {
					this.setFirstInitial(this.getFirstName().substring(0,1));
				}
				this.setMiddleInitial(IncitesParser.parseMiddleInitial(rawAuthorText));
				this.setLastName(IncitesParser.parseLastName(rawAuthorText));
				this.setLabel(this.getFirstName() + " " + this.getLastName());
				this.setInstitution(IncitesParser.parseInstitution(rawAuthorText));
				this.setLocation(locationMap.getLocationMap().get(this.getInstitution()));
				break;
			
		}
		
		// Format names to ensure consistency
		format();
	}

	/* given - a name
	 * returns true if this author has the same name
	 * If one of the names is only a first initial checks to see if the initials are the same.  if the same returns true
	 */
	private boolean isSameFirstName(String name){
		
		boolean isEqualFirstName = false;
		
		//If the first name has middle initial get rid of it and just compare first name to first name
		String firstname1 = (this.getFirstName().split(" ").length > 1) ? this.getFirstName().split(" ")[0] : this.getFirstName();
		String firstname2 = (name.split(" ").length > 1) ? name.split(" ")[0] : name;
		
		double distance = Levenshtein.distance(firstname1.toLowerCase(), 
				                        firstname2.toLowerCase());
		double similarity = 1 - ((double) distance) / (Math.max(firstname1.length(), 
				                        firstname2.length()));
		if (similarity >= 0.75) {
			isEqualFirstName = true;
			
		// If Levenshtein distance is too small, check to see if 
		// if the one of the first names is actually an initial i.e. 'V'
		} else {
			//check if either first name is a single character
			//or if it has 2 characters and one of the characters is a . as initials are often written V.
			if((firstname1.length() ==1) || (firstname2.length() == 1) ||
			(firstname1.length()==2 && firstname1.contains("."))||
			(firstname2.length()==2 && firstname2.contains("."))){
				//check to see if they have the same initial
				isEqualFirstName = firstname1.substring(0, 1).equalsIgnoreCase(firstname2.substring(0,1));
			}
			
		}
			return isEqualFirstName;
	}
	
	/**
	 * Return true iff author is the same as other
	 * @param Object other
	 * @return boolean
	 */
	public boolean equals(Object other) {
		
		//compare the current author to the given author
		//method used by contains method with hash of a set of Authors
		
		Author otherAuthor = (Author) other;
		boolean isEqual = false, isEqualFirstName = false, isEqualLastName = false;
		int distance = 0;
		double similarity = 0;
		// Incites
		if (this.getOrigin() == Category.INCITES && otherAuthor.getOrigin() == Category.INCITES) {
			boolean isEqualInstitution = false;
			// Determine whether or not last names are equal
			isEqualLastName = this.getLastName().equalsIgnoreCase(otherAuthor.getLastName());
			// Determine whether or not first names are equal
			isEqualFirstName = this.isSameFirstName(otherAuthor.getFirstName());
			
			// Determine whether or not both authors share the same institution
			String myInstitution = this.getInstitution(), otherInstitution = otherAuthor.getInstitution();
			isEqualInstitution = myInstitution.equalsIgnoreCase(otherInstitution);
			if (isEqualLastName && isEqualFirstName && ! isEqualInstitution) {
				prioritizeInstitution(this, otherAuthor);
				isEqualInstitution = true;
			}
			isEqual = isEqualLastName && isEqualFirstName && isEqualInstitution;
		// Incites (~ Faculty)
		} else if ((this.getOrigin() == Category.INCITES && otherAuthor.getOrigin() == Category.FACULTY) || 
				(this.getOrigin() == Category.FACULTY && otherAuthor.getOrigin() == Category.INCITES)){
			isEqualLastName = this.getLastName().equalsIgnoreCase(otherAuthor.getLastName());
			// Determine whether or not first names are equal			
			isEqualFirstName = this.isSameFirstName(otherAuthor.getFirstName());
			
			isEqual = isEqualLastName && isEqualFirstName;
			// Check to see if the other author has been correctly ID'ed. If so, set his identification
			// status to true.
			if (isEqual) {
				otherAuthor.setIdenfitication(true);
			}
		// Pubmed / Scopus
		} else if (this.getOrigin() == Category.PUBMED && otherAuthor.getOrigin() == Category.PUBMED ||
				   this.getOrigin() == Category.SCOPUS && otherAuthor.getOrigin() == Category.SCOPUS) {
			isEqualLastName = this.getLastName().equalsIgnoreCase(otherAuthor.getLastName());
			isEqualFirstName = this.getFirstInitial().equalsIgnoreCase(otherAuthor.getFirstInitial());
			isEqual = isEqualFirstName && isEqualLastName;
		}
		return isEqual;
	}
	
	/**
	 * Modify institution for both author1 and author2 (who are assumed to be the same).
	 * Set institution to the higher priority as defined in the app locationMap
	 * Assumes that author and otherAuthor are the same
	 * individual but otherAuthor is the active reference.
	 * @param Author author
	 * @param Author other
	 * @return null
	 */
	public  void prioritizeInstitution(Author author, Author otherAuthor) {
		String location = author.getLocation();
		String otherLocation = otherAuthor.getLocation();
		Map<String, Integer> rankMap = this.locationMap.getLocationRankingMap();
		// Initialize rank and otherRank to a low rank
		// NOTE: Highest rank is 6 and lowest rank is 1
		int rank = 0, otherRank = 0;
		if (rankMap.containsKey(location)) {
			rank = rankMap.get(location);
		}
		if (rankMap.containsKey(otherLocation)) {
			otherRank = rankMap.get(otherLocation);
		}
		if (rank > otherRank) {
			otherAuthor.setInstitution(author.getInstitution());
			otherAuthor.setLocation(author.getLocation());
		} else if (rank == otherRank) {
			Author[] randomAuthorArray = new Author[] {author, otherAuthor};
			Random rand = new Random();
		    int i = rand.nextInt((1 - 0) + 1) + 0;
		    String randomInstitution = randomAuthorArray[i].getInstitution();
		    String randomLocation = randomAuthorArray[i].getLocation();
	        otherAuthor.setInstitution(randomInstitution);
			otherAuthor.setLocation(randomLocation);
		}
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
		String firstName = this.getFirstName().substring(0,1).toUpperCase() + 
				           this.getFirstName().substring(1).toLowerCase();
		this.setFirstName(firstName);
		String lastName = this.getLastName().substring(0,1).toUpperCase() + 
				          this.getLastName().substring(1).toLowerCase();
		this.setLastName(lastName);
		// Ensure that the first and middle initials are capitalized
		this.setFirstInitial(this.getFirstInitial().toUpperCase());
		this.setMiddleInitial(this.getMiddleInitial().toUpperCase());
	}

	/**
	 * Get author's department
	 * @param null
	 * @return String department
	 */
	public String getDepartment() {
		return this.department;
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
		if (this.nodeAttrMap == null) {
			this.nodeAttrMap = new HashMap<String, Object>();
		}
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
	 * Set author's department
	 * @param String department
	 * @return null
	 */
	public void setFaculty(String department) {
		this.department = department;
		this.getNodeAttrMap().put(IncitesVisualStyle.nodeattr_dept, department);
	}

	/**
	 * Set first initial
	 * @param String firstInitial
	 * @return null
	 */
	public void setFirstInitial(String firstInitial) {
		this.firstInitial = firstInitial;
	}

	/**
	 * Set author's location. If no location is found on the
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
		this.getNodeAttrMap().put("Location", this.location);
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
		this.getNodeAttrMap().put(BasicSocialNetworkVisualstyle.nodeattr_timescited, timesCited);
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

	/**
	 * Get CyNode
	 * @param null
	 * @return CyNode cyNode
	 */
	public CyNode getCyNode() {
		return this.cyNode;
	}

	/**
	 * Set CyNode
	 * @param CyNode cyNode
	 * @return null
	 */
	public void setCyNode(CyNode cyNode) {
		this.cyNode = cyNode;
	}
	
	/**
	 * Add publication
	 * @param Publication publication
	 * @return null
	 */
	public void addPublication(Publication publication) {
		// Only add publication if publication was not authored by
		// a single author. 
		// NOTE: In lone author publications, the lone author is represented
		// twice
		int timesCited = this.getTimesCited();
		ArrayList<String> pubList = (ArrayList<String>) this.getPubList();
		if (publication.isSingleAuthored()) {
			if (! this.hasAlreadyBeenAdded()) {
				this.setTimesCited(timesCited + publication.getTimesCited());
				pubList.add(publication.getTitle());
				this.setPubList(pubList);
				this.setAlreadyBeenAdded(true);
			} else {
				this.setAlreadyBeenAdded(false);
			}
		} else {
			this.setTimesCited(timesCited + publication.getTimesCited());
			pubList.add(publication.getTitle());
			this.setPubList(pubList);
		}
	}
	
	/**
	 * Set institution
	 * @param String institution
	 * @return null
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
		this.getNodeAttrMap().put(IncitesVisualStyle.nodeattr_inst, institution);
	}
	
	/**
	 * Set first name
	 * @param String firstName
	 * @return null
	 */
	private void setFirstName(String firstName) {
		this.firstName = firstName;
		this.getNodeAttrMap().put(BasicSocialNetworkVisualstyle.nodeattr_fname, firstName);
	}
	
	/**
	 * Set last name
	 * @param String lastName
	 * @return null
	 */
	private void setLastName(String lastName) {
		this.lastName = lastName;
		this.getNodeAttrMap().put("Last Name", lastName);
	}
	
	/**
	 * Set middle initial
	 * @param String middleInitial
	 * @return null
	 */
	private void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}
	
	/**
	 * Get middle initial
	 * @param null
	 * @return String middleInitial
	 */
	private String getMiddleInitial() {
		return this.middleInitial;
	}

	/**
	 * Get label
	 * @param null
	 * @return String label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Set label
	 * @param String label
	 * @return null
	 */
	public void setLabel(String label) {
		this.label = label;
		this.getNodeAttrMap().put(BasicSocialNetworkVisualstyle.nodeattr_label, label);

	}

	/**
	 * True iff publication has 
	 * already been added
	 * @param null
	 * @return Boolean alreadyBeenAdded
	 */
	public boolean hasAlreadyBeenAdded() {
		return this.alreadyBeenAdded;
	}

	/**
	 * Set value for alreadyBeenAdded
	 * <br>NOTE: alreadyBeenAdded should be
	 * false unless a lone author publication
	 * is being parsed
	 * @param Boolean alreadyBeenAdded 
	 * @return null
	 */
	public void setAlreadyBeenAdded(boolean alreadyBeenAdded) {
		this.alreadyBeenAdded = alreadyBeenAdded;
	}

	/**
	 * Get author publist
	 * @param null
	 * @return ArrayList pubList
	 */
	public List<String> getPubList() {
		if (this.pubList == null) {
			this.pubList = new ArrayList<String>();
		}
		return this.pubList;
	}

	/**
	 * Set author publist
	 * @param ArrayList pubList
	 * @return null
	 */
	public void setPubList(List<String> pubList) {
		this.pubList = pubList;
		this.getNodeAttrMap().put(BasicSocialNetworkVisualstyle.nodeattr_pub, pubList);
		this.getNodeAttrMap().put(BasicSocialNetworkVisualstyle.nodeattr_numpub, pubList.size());
	}
	
	/**
	 * Construct Incites attribute map
	 * @param null
	 * @return Map nodeAttrMap
	 */
	public static HashMap<String, Object> constructIncitesAttrMap() {
		HashMap<String, Object> nodeAttrMap = new HashMap<String, Object>();
		String[] columns = new String[] {BasicSocialNetworkVisualstyle.nodeattr_label, BasicSocialNetworkVisualstyle.nodeattr_lname, BasicSocialNetworkVisualstyle.nodeattr_fname,
				                         IncitesVisualStyle.nodeattr_inst, IncitesVisualStyle.nodeattr_location, IncitesVisualStyle.nodeattr_dept,
				                         BasicSocialNetworkVisualstyle.nodeattr_timescited, BasicSocialNetworkVisualstyle.nodeattr_numpub,BasicSocialNetworkVisualstyle.nodeattr_pub};
		int i = 0;
		for (i = 0; i < 6; i++) {
			nodeAttrMap.put(columns[i], "");
		}
		// Initialize Times Cited attribute (~ Integer)
		nodeAttrMap.put(columns[i], 0);
		// Initialize num pub attribute (~ Integer)
		nodeAttrMap.put(columns[i+1], 0);
		// Initialize Publications attribute (~ ArrayList)
		nodeAttrMap.put(columns[i + 2], new ArrayList<String>());
		return nodeAttrMap;
	}
	 
}
