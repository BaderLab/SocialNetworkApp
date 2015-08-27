package org.baderlab.csapps.socialnetwork.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.tasks.CreateChartTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.HideAuthorsTaskFactory;
import org.baderlab.csapps.socialnetwork.tasks.ShowAllNodesTaskFactory;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskManager;


public class InfoPanel extends JPanel implements CytoPanelComponent, ChangeListener, PropertyChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private CreateChartTaskFactory createChartTaskFactory = null;
    private CyServiceRegistrar cyServiceRegistrarRef = null;
    private HideAuthorsTaskFactory hideAuthorsTaskFactory = null;
    private ShowAllNodesTaskFactory showAllNodesTaskFactory = null;
    private JSlider sliderButton = null;
    private SocialNetwork socialNetwork = null;
    private int startYear = -1, endYear = -1;
    private TaskManager<?, ?> taskManager = null;
    private JTextField textField = null;
    
    public InfoPanel(TaskManager<?, ?> taskManager, HideAuthorsTaskFactory hideAuthorsTaskFactory, SocialNetwork socialNetwork,
            CyServiceRegistrar cyServiceRegistrarRef, ShowAllNodesTaskFactory showAllNodesTaskFactory,
            CreateChartTaskFactory createChartTaskFactory) {
               
        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createTitledBorder("Select Database"));
        
        this.taskManager = taskManager;
        this.hideAuthorsTaskFactory = hideAuthorsTaskFactory;
        this.socialNetwork = socialNetwork;
        this.cyServiceRegistrarRef = cyServiceRegistrarRef;
        this.showAllNodesTaskFactory = showAllNodesTaskFactory;
        this.createChartTaskFactory = createChartTaskFactory;
        this.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension((int) screenSize.getWidth() / 5, 200));

        this.startYear = socialNetwork.getStartYear(); 
        this.endYear = socialNetwork.getEndYear();            
        
        //Create the label.
        JLabel sliderLabel = new JLabel("Selected Year: ", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.textField = new JTextField();
        this.textField.setEditable(false);
        //this.textField.setText(String.valueOf(startYear));
        this.textField.setText("ALL");
        this.textField.setColumns(5);
        //this.textField.addPropertyChangeListener(this);
        
        JPanel labelAndTextField = new JPanel();
        labelAndTextField.add(sliderLabel);
        labelAndTextField.add(textField);
        
        // Create the slider.
        this.sliderButton = new JSlider(JSlider.HORIZONTAL, startYear, endYear, startYear);
        this.sliderButton.setValue(startYear);
        this.sliderButton.addChangeListener(this);
        this.sliderButton.setMinorTickSpacing(1);
        this.sliderButton.setPaintTicks(true);
        this.sliderButton.setPaintLabels(true);
        this.sliderButton.setSnapToTicks(true);
        
        // Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer(this.startYear), new JLabel(String.valueOf(this.startYear)) );
        labelTable.put( new Integer(this.endYear), new JLabel(String.valueOf(this.endYear)) );
        this.sliderButton.setLabelTable(labelTable);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(labelAndTextField);
        sliderPanel.add(this.sliderButton);
        JPanel sliderAndChartControlPanel = new JPanel(new FlowLayout());
        sliderAndChartControlPanel.add(this.createGenerateChartButton());
        sliderAndChartControlPanel.add(this.createShowAllButton());
        sliderPanel.add(sliderAndChartControlPanel);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(this.createCloseButton());
        
        this.add(sliderPanel, BorderLayout.NORTH);
        this.add(controlPanel, BorderLayout.SOUTH);
        
        // Set initial values
        SocialNetworkAppManager.setSelectedYear(startYear);
        SocialNetworkAppManager.setSelectedSocialNetwork(this.socialNetwork);
        
    }
    
    /**
     * Close the info panel
     */
    public void closePanel() {
        SocialNetworkAppManager.setInfoPanel(null);
        InfoPanel.this.cyServiceRegistrarRef.unregisterService(InfoPanel.this, CytoPanelComponent.class);
    }
    
    /**
     * Create close button. Close button closes current panel
     *
     * @return JButton closeButton
     */
    private JButton createCloseButton() {
        JButton closeButton = new JButton("Close");
        closeButton.setToolTipText("Close Social Network Display Options Panel");
        // Clicking of button results in the closing of current panel
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SocialNetworkAppManager.setInfoPanel(null);
                InfoPanel.this.cyServiceRegistrarRef.unregisterService(InfoPanel.this, CytoPanelComponent.class);
            }
        });
        return closeButton;
    }
    
    /**
     * 
     * @return JButton generateChartButton
     */
    private JButton createGenerateChartButton() {
        JButton generateChartButton = new JButton("Chart");
        generateChartButton.setToolTipText("Create charts for each node in the existing network");
        // Clicking of button results in the closing of current panel
        generateChartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                InfoPanel.this.taskManager.execute(InfoPanel.this.createChartTaskFactory.createTaskIterator());
            }
        });
        return generateChartButton;
    }
    
    private JButton createShowAllButton() {
        JButton showAllButton = new JButton("Show All Nodes and Edges");
        showAllButton.setToolTipText("Show every node and edge in the existing network.");
        // Clicking of button results in the closing of current panel
        showAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                InfoPanel.this.taskManager.execute(InfoPanel.this.showAllNodesTaskFactory.createTaskIterator());
            }
        });
        return showAllButton;
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
    
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
    
    public void setSliderButton(JSlider sliderButton) {
        this.sliderButton = sliderButton;
    }
    
    public void setSocialNetwork(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }
    
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
    
    public void setTextField(JTextField textField) {
        this.textField = textField;
    }
    
    public void stateChanged(ChangeEvent evt) {
        JSlider source = (JSlider) evt.getSource();
        int year = (int) source.getValue();
        if (!source.getValueIsAdjusting()) { // Done adjusting
            this.textField.setText(String.valueOf(year));
            SocialNetworkAppManager.setSelectedYear(year);
            SocialNetworkAppManager.setSelectedSocialNetwork(this.socialNetwork);
            this.taskManager.execute(this.hideAuthorsTaskFactory.createTaskIterator());                
        } else {
            this.textField.setText(String.valueOf(year));
        }
    }
    
    public void update(SocialNetwork socialNetwork) {
        this.setSocialNetwork(socialNetwork);
        
        int startYear = socialNetwork.getStartYear();
        int endYear = socialNetwork.getEndYear();
        this.setStartYear(startYear);
        this.setEndYear(endYear);
        
        /* Text field */
        this.textField.setText("ALL");
        this.textField.repaint();
        
        /* Slider button */
        this.sliderButton.setMinimum(startYear);
        this.sliderButton.setMaximum(endYear);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer(this.startYear), new JLabel(String.valueOf(this.startYear)) );
        labelTable.put( new Integer(this.endYear), new JLabel(String.valueOf(this.endYear)) );
        this.sliderButton.setLabelTable(labelTable);
        //this.sliderButton.setValue(startYear);
        this.sliderButton.repaint();
        
        if (this.socialNetwork.getNetworkView() != null) {
            // All nodes and edges have to be made visible 
            for (final CyNode node : socialNetwork.getCyNetwork().getNodeList()) {
                this.socialNetwork.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_VISIBLE, true);                 
            }
            
            for (final CyEdge edge : socialNetwork.getCyNetwork().getEdgeList()) {
                this.socialNetwork.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);                    
            }
            
            this.socialNetwork.getNetworkView().updateView();            
        }

        
        this.updateUI();
    }

}