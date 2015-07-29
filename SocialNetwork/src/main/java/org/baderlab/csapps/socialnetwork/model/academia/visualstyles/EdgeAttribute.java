package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write enum description
public enum EdgeAttribute {
    
    JOURNAL("Journal"),
    COPUBLICATION_COUNT("# of copubs"),
    PUBLICATION_DATE("Pub Date"),
    PUBLICATIONS("Publications"),
    TIMES_CITED("Times Cited"),
    PUBS_PER_YEAR("Pubs Per Year"),
    TITLE("Title");
    
    private String attrName = null;
    
    /**
     * Create a new {@link EdgeAttribute} enum
     * 
     * @param String attrName
     */
    EdgeAttribute(String attrName) {
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
