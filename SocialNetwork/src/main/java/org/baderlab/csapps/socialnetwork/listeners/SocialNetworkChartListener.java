package org.baderlab.csapps.socialnetwork.listeners;

import java.util.Map;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics2Factory;

/**
 * OSGi service listener that acquires a reference to a custom graphics service.
 */
public class SocialNetworkChartListener {
    
    private static final String FACTORY_ID = "org.cytoscape.BarChart";
    
    private CyCustomGraphics2Factory<?> factory;

    
    public void addCustomGraphicsFactory(CyCustomGraphics2Factory<?> factory, Map<Object,Object> serviceProps) {
        if(FACTORY_ID.equals(factory.getId())) {
            this.factory = factory;
        }
    }
    
    public void removeCustomGraphicsFactory(CyCustomGraphics2Factory<?> factory, Map<Object,Object> serviceProps) {
        this.factory = null;
    }
    
    public CyCustomGraphics2Factory<?> getFactory() {
        return factory;
    }

}
