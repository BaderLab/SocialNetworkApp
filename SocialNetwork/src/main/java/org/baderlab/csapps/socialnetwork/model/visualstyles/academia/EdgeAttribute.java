package org.baderlab.csapps.socialnetwork.model.visualstyles.academia;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write enum description
public enum EdgeAttribute {
    
    Journal("Journal"),
    NumCopublications("# of copubs"),
    PublicationDate("Pub Date"),
    TimesCited("Times Cited"),
    Title("Title");
    
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
