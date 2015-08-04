package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write enum description
public enum NetworkAttribute {
    
    UNIDENTIFIED_FACULTY_LIST("List of unidentified Faculty"),
    TOTAL_FACULTY("Total number of Faculty"),
    TOTAL_PUBLICATIONS("Total Publications"),
    TOTAL_UNIDENTIFIED_FACULTY("Total number of unidentified Faculty");
    
    private String attrName = null;
    
    /**
     * Create a new {@link NetworkAttribute} enum
     * 
     * @param String attrName
     */
    NetworkAttribute(String attrName) {
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
