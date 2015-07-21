package org.baderlab.csapps.socialnetwork.model.visualstyles;

import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.cytoscape.model.CyNetwork;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public abstract class AbstractVisualStyle {
    
    /**
     * Apply an edge style to every edge in network.
     *
     * @param {@link CyNetwork} network
     * @param {@link SocialNetwork} socialNetwork
     */
    protected abstract void applyEdgeStyle(CyNetwork network, SocialNetwork socialNetwork);
    
    /**
     * Apply a node style to every node in network.
     *
     * @param {@link CyNetwork} network
     * @param {@link SocialNetwork} socialNetwork
     */
    protected abstract void applyNodeStyle(CyNetwork network, SocialNetwork socialNetwork);
    
    /**
     * Apply a visual style to network.
     *
     * @param {@link CyNetwork} network
     * @param {@link SocialNetwork} socialNetwork
     */
    public abstract void applyVisualStyle(CyNetwork network, SocialNetwork socialNetwork);

}
