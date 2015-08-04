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
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.baderlab.csapps.socialnetwork.tasks.UpdateVisualStyleTaskFactory;
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
    private int startYear = -1, endYear = -1;
    private VisualMappingManager visualMappingManager = null;
    private TaskManager<?, ?> taskManager = null;
    private SocialNetwork socialNetwork = null;
    private VisualStyleFactory visualStyleFactoryServiceRef;
    private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef = null;
    private VisualMappingFunctionFactory continuousMappingFactoryServiceRef = null;
    private VisualMappingFunctionFactory discreteMappingFactoryServiceRef = null;
    private UpdateVisualStyleTaskFactory updateVisualStyleTaskFactory = null;
    
    public InfoPanel(SocialNetwork socialNetwork, TaskManager<?, ?> taskManager, VisualMappingManager visualMappingManager,
            VisualStyleFactory vsFactory, VisualMappingFunctionFactory passthrough, VisualMappingFunctionFactory continuous,
            VisualMappingFunctionFactory discrete, UpdateVisualStyleTaskFactory updateVisualStyleTaskFactory) {
        this.socialNetwork = socialNetwork;
        this.taskManager = taskManager;
        this.visualMappingManager = visualMappingManager;
        this.visualStyleFactoryServiceRef = vsFactory;
        this.passthroughMappingFactoryServiceRef = passthrough;
        this.continuousMappingFactoryServiceRef = continuous;
        this.discreteMappingFactoryServiceRef = discrete;
        this.updateVisualStyleTaskFactory = updateVisualStyleTaskFactory;
        this.cyNetwork = socialNetwork.getCyNetwork();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(400, 200));
        
        String startYearTxt = SocialNetworkAppManager.getStartDateTextFieldRef().getText().trim();
        String endYearTxt = SocialNetworkAppManager.getEndDateTextFieldRef().getText().trim();
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
        this.sliderButton.setValue(startYear);
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
            SocialNetworkAppManager.setSelectedYear(year);
            // Update the network
            // TODO: put this in a task
            // ------------------------------------------------------------------------------------
            
            // TODO:
            this.taskManager.execute(this.updateVisualStyleTaskFactory.createTaskIterator());
            
                
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