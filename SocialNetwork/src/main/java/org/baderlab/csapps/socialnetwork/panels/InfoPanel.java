package org.baderlab.csapps.socialnetwork.panels;

import java.awt.Component;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;


public class InfoPanel extends JPanel implements CytoPanelComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
    *
    * @return {@link Component} component
    */
   public Component getComponent() {
       return this;
   }

    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.EAST;
    }

    public Icon getIcon() {
        URL iconURL = this.getClass().getResource("socialNetwork_logo_small.png");
        ImageIcon SNIcon = null;
        if (iconURL != null) {
            SNIcon = new ImageIcon(iconURL);
        }
        return SNIcon;
    }

    public String getTitle() {
        return "Social Network Display Options Panel";
    }

}