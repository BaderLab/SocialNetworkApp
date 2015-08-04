package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

/**
 * 
 * @author Victor Kofia
 */
// TODO: Write enum description
public enum NodeAttribute {
    
    DEPARTMENT("Department"),
    FIRST_NAME("First Name"),
    INSTITUTION("Institutions"),
    IS_SELECTED("Is Selected"),
    LABEL("Label"),
    LAST_NAME("Last Name"),
    LOCATION("Location"),
    MAIN_INSTITUTION("Main Institution"),
    PUBLICATION_COUNT("# of Publications"),
    PUBLICATIONS("Publications"),
    TIMES_CITED("Times Cited"),
    /**
     * List attribute of author's pub count in 
     * the years that author was active
     */
    PUBS_PER_YEAR("Pubs Per Year"),
    /**
     * List attribute of years that author was active
     */
    YEARS("Years Active");
    
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