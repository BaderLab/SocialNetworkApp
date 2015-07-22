package org.baderlab.csapps.socialnetwork.model;


/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public abstract class AbstractVisualStyle {
    
    /**
     * Apply an edge style to every edge in network.
     */
    protected abstract void applyEdgeStyle();
    
    /**
     * Apply a node style to every node in network.
     */
    protected abstract void applyNodeStyle();
    
    /**
     * Apply a visual style to network.
     */
    public abstract void applyVisualStyle();

}
