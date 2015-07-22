package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write enum description
public enum NetworkAttribute {
    
    ListUnidentifiedFaculty("List of unidentified Faculty"),
    TotalFaculty("Total number of Faculty"),
    TotalPublications("Total Publications"),
    TotalUnidentifiedFaculty("Total number of unidentified Faculty");
    
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
