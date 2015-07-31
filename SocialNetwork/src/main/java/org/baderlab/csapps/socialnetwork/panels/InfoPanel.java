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
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.VisualStyles;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.EdgeAttribute;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.work.TaskManager;


public class InfoPanel extends JPanel implements CytoPanelComponent, ChangeListener, PropertyChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JSlider sliderButton = null;
    private JTextField textField = null;
    private CyNetwork cyNetwork = null;
    private int startYear, endYear;
    private VisualMappingManager visualMappingManager = null;
    private TaskManager<?, ?> taskManager = null;
    private SocialNetwork socialNetwork = null;
    private VisualStyleFactory visualStyleFactoryServiceRef;
    private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
    private VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    private VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    
    public InfoPanel(SocialNetwork socialNetwork, TaskManager<?, ?> taskManager, VisualMappingManager visualMappingManager,
            VisualStyleFactory vsFactory, VisualMappingFunctionFactory passthrough, VisualMappingFunctionFactory continuous,
            VisualMappingFunctionFactory discrete) {
        this.socialNetwork = socialNetwork;
        this.taskManager = taskManager;
        this.visualMappingManager = visualMappingManager;
        this.visualStyleFactoryServiceRef = vsFactory;
        this.passthroughMappingFactoryServiceRef = passthrough;
        this.continuousMappingFactoryServiceRef = continuous;
        this.discreteMappingFactoryServiceRef = discrete;
        this.cyNetwork = socialNetwork.getCyNetwork();
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
            // Update the network
            // TODO: put this in a task
            // ------------------------------------------------------------------------------------
            
            
            // TODO:
            // Set opacity of deselected nodes and deselected edges to 0
            VisualStyle vs = CytoscapeUtilities.getVisualStyle
                    (VisualStyles.toString(socialNetwork.getVisualStyleId()), this.visualMappingManager);
            
            // Modify visual style
            DiscreteMapping opacityMapping = (DiscreteMapping<String, ?>) this.discreteMappingFactoryServiceRef
                    .createVisualMappingFunction(EdgeAttribute.PUBS_PER_YEAR.toString(), String.class, 
                            BasicVisualLexicon.NODE_TRANSPARENCY);
            
            Iterator<CyEdge> edgeIt = this.cyNetwork.getEdgeList().iterator();
            //HashSet<CyEdge> selectedEdges = new HashSet<CyEdge>();
            //HashSet<CyEdge> deselectedEdges = new HashSet<CyEdge>();
            HashSet<CyNode> selectedNodes = new HashSet<CyNode>();
            CyEdge edge = null;
            CyTable defaultEdgeTable = this.cyNetwork.getDefaultEdgeTable();
            while (edgeIt.hasNext()) {
                edge = edgeIt.next();
                List<Integer> pubsPerYear = (List<Integer>) CytoscapeUtilities.getCyTableAttribute(defaultEdgeTable, 
                        edge.getSUID(), EdgeAttribute.PUBS_PER_YEAR.toString());
                if (pubsPerYear.get(year - startYear) == 1) {
                    //selectedEdges.add(edge);
                    selectedNodes.add(edge.getSource());
                    selectedNodes.add(edge.getTarget());
                    opacityMapping.putMapValue(pubsPerYear, 1.0);
                } else {
                    //deselectedEdges.add(edge);
                    // TODO: Change opacity to 0
                    opacityMapping.putMapValue(pubsPerYear, 0.0);
                }
            }
            Iterator<CyNode> nodeIt = this.cyNetwork.getNodeList().iterator();
            CyNode node = null;
            //HashSet<CyNode> deselectedNodes = new HashSet<CyNode>();
            while (nodeIt.hasNext()) {
                node = nodeIt.next();
                if (!selectedNodes.contains(node)) {
                    //deselectedNodes.add(node);
                    // TODO: change opacity to 0
                    opacityMapping.putMapValue(CytoscapeUtilities.getCyTableAttribute(defaultEdgeTable, edge.getSUID(), EdgeAttribute.PUBS_PER_YEAR.toString()), 0.0);
                } else {
                    opacityMapping.putMapValue(CytoscapeUtilities.getCyTableAttribute(defaultEdgeTable, edge.getSUID(), EdgeAttribute.PUBS_PER_YEAR.toString()), 1.0);
                }
            }

            vs.addVisualMappingFunction(opacityMapping);

            // Set current visual style to new modified visual style
            this.visualMappingManager.setCurrentVisualStyle(vs);
            
            // -------------------------------------------------------------------------------------
        } else { //value is adjusting; just set the text
            this.textField.setText(String.valueOf(year));
        }
    }

    public CyNetwork getCyNetwork() {
        return cyNetwork;
    }

    public void setSocialNetwork(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        this.cyNetwork = socialNetwork.getCyNetwork();
    }
    
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
    
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

}