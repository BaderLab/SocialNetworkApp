package org.baderlab.csapps.socialnetwork.model.academia.visualstyles;

public enum EdgeAttribute {
    
    JOURNAL("Journal"),
    COPUBLICATION_COUNT("# of copubs"),
    PUBLICATION_DATE("Pub Date"),
    PUBLICATIONS("Publications"),
    TIMES_CITED("Times Cited"),
    PUBS_PER_YEAR("Pubs Per Year"),
    IS_SELECTED("Is Selected"),
    TITLE("Title");
    
    private String attrName;
    
    EdgeAttribute(String attrName) {
        this.attrName = attrName;
    }

    @Override
    public String toString() {
        return this.attrName;
    }
}
