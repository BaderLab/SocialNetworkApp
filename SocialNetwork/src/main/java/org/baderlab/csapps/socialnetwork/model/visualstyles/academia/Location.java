package org.baderlab.csapps.socialnetwork.model.visualstyles.academia;


public enum Location {
    
    UofT("UNIV TORONTO"),
    Canada("Canada"),
    UnitedStates("United States"),
    Ontario("Ontario"),
    International("International"),
    Other("Other"),
    NA("N/A");
    
    private String locationName = null;
    
    Location(String name) {
        this.locationName = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.locationName;
    }
    
}
