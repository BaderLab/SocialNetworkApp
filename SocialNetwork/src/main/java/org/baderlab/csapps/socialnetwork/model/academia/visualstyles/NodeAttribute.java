package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

/**
 * 
 * @author Victor Kofia
 */
// TODO: Write enum description
public enum NodeAttribute {
    
    Department("Department"),
    FirstName("First Name"),
    Institution("Institution"),
    Label("Label"),
    LastName("Last Name"),
    Location("Location"),
    MainInstitution("Main Institution"),
    NumPublications("# of Publications"),
    Publications("Publications"),
    TimesCited("Times Cited"),
    YearlyPublications("Yearly Publications"),
    Years("Years");
    
    private String attrName = null;
    
    /**
     * Create a new {@link NodeAttribute} enum
     * 
     * @param String attrName
     */
    NodeAttribute(String attrName) {
        this.attrName = attrName;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.attrName;
    }
    
}