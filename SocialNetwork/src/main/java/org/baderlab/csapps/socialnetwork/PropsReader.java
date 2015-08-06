package org.baderlab.csapps.socialnetwork;

import org.cytoscape.property.AbstractConfigDirPropsReader;
import org.cytoscape.property.CyProperty;

public class PropsReader extends AbstractConfigDirPropsReader {
    public PropsReader(String name, String fileName) {
        super(name, fileName, CyProperty.SavePolicy.CONFIG_DIR);
    }
}
