package org.baderlab.csapps.socialnetwork.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.EdgeAttribute;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;


public class InfoPanel extends JPanel implements CytoPanelComponent, ChangeListener, PropertyChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JSlider sliderButton = null;
    private JTextField textField = null;
    private CyNetwork cyNetwork = null;
    private CyTable defaultEdgeTable = null;
    private int startYear, endYear;
    
    public InfoPanel(CyNetwork network) {
        this.cyNetwork = network;
        this.defaultEdgeTable = network.getDefaultEdgeTable();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(400, 200));
        
        String startYearTxt = SocialNetworkAppManager.getStartDateTextFieldRef().getText().trim();
        String endYearTxt = SocialNetworkAppManager.getEndDateTextFieldRef().getText().trim();
        this.startYear = -1;
        this.endYear = -1;
        if (Pattern.matches("[0-9]+", startYearTxt) && Pattern.matches("[0-9]+", endYearTxt)) {
            this.startYear = Integer.parseInt(startYearTxt); 
            this.endYear = Integer.parseInt(endYearTxt);            
        }
        
        //Create the label.
        JLabel sliderLabel = new JLabel("Selected Year: ", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.textField = new JTextField();
        this.textField.setEditable(false);
        this.textField.setText(String.valueOf(startYear));
        this.textField.setColumns(5);
        this.textField.addPropertyChangeListener(this);
        
        JPanel labelAndTextField = new JPanel();
        labelAndTextField.add(sliderLabel);
        labelAndTextField.add(textField);
        
        //Create the slider.
        this.sliderButton = new JSlider(JSlider.HORIZONTAL, startYear, endYear, startYear);
        this.sliderButton.addChangeListener(this);
        this.sliderButton.setPaintLabels(false);

        JPanel labelTextSlider = new JPanel();
        labelTextSlider.add(labelAndTextField, BorderLayout.NORTH);
        labelTextSlider.add(this.sliderButton, BorderLayout.SOUTH);
        this.add(labelTextSlider);
    }
    
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
    
    public JSlider getSliderButton() {
        return this.sliderButton;
    }

    public JTextField getTextField() {
        return this.textField;
    }

    public String getTitle() {
        return "Social Network Display Options Panel";
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("value".equals(evt.getPropertyName())) {
            Number value = (Number) evt.getNewValue();
            if (this.sliderButton != null && value != null) {
                this.sliderButton.setValue(value.intValue());
            }
        }
    }

    public void setSliderButton(JSlider sliderButton) {
        this.sliderButton = sliderButton;
    }

    public void setTextField(JTextField textField) {
        this.textField = textField;
    }

    public void stateChanged(ChangeEvent evt) {
        JSlider source = (JSlider) evt.getSource();
        int year = (int) source.getValue();
        if (!source.getValueIsAdjusting()) { //done adjusting
            this.textField.setText(String.valueOf(year));
            // update the network
            // TODO: put this in a task
            // ------------------------------------------------------------------------------------
            Iterator<CyEdge> edgeIt = this.cyNetwork.getEdgeList().iterator();
            HashSet<CyEdge> selectedEdges = new HashSet<CyEdge>();
            HashSet<CyNode> selectedNodes = new HashSet<CyNode>();
            CyEdge edge = null;
            while (edgeIt.hasNext()) {
                edge = edgeIt.next();
                List<Integer> pubsPerYear = (List<Integer>) CytoscapeUtilities.getCyTableAttribute(defaultEdgeTable, 
                        edge.getSUID(), EdgeAttribute.PUBS_PER_YEAR.toString());
                if (pubsPerYear.get(year - startYear) == 1) {
                    selectedEdges.add(edge);
                    selectedNodes.add(edge.getSource());
                    selectedNodes.add(edge.getTarget());
                }
            }
            Iterator<CyNode> nodeIt = this.cyNetwork.getNodeList().iterator();
            CyNode node = null;
            HashSet<CyNode> deselectedNodes = new HashSet<CyNode>();
            while (nodeIt.hasNext()) {
                node = nodeIt.next();
                if (!selectedNodes.contains(node)) {
                    // Either delete the node or set opacity to 0
                    deselectedNodes.add(node);
                }
            }
            
            // TODO:
            // cyNetwork.removeNodes(deselectedNodes);
            
            // -------------------------------------------------------------------------------------
        } else { //value is adjusting; just set the text
            this.textField.setText(String.valueOf(year));
        }
    }

    public CyNetwork getCyNetwork() {
        return cyNetwork;
    }

    public void setCyNetwork(CyNetwork cyNetwork) {
        this.cyNetwork = cyNetwork;
    }

}