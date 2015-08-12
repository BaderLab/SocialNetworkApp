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
import org.baderlab.csapps.socialnetwork.tasks.HideAuthorsTaskFactory;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskManager;


public class InfoPanel extends JPanel implements CytoPanelComponent, ChangeListener, PropertyChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JSlider sliderButton = null;
    private JTextField textField = null;
    private int startYear = -1, endYear = -1;
    private TaskManager<?, ?> taskManager = null;
    private HideAuthorsTaskFactory updateVisualStyleTaskFactory = null;
    private SocialNetwork socialNetwork = null;
    private int lastYear = -1;
    private CyServiceRegistrar cyServiceRegistrarRef = null;
    
    public InfoPanel(TaskManager<?, ?> taskManager, HideAuthorsTaskFactory updateVisualStyleTaskFactory, SocialNetwork socialNetwork,
            CyServiceRegistrar cyServiceRegistrarRef) {
        this.taskManager = taskManager;
        this.updateVisualStyleTaskFactory = updateVisualStyleTaskFactory;
        this.socialNetwork = socialNetwork;
        this.cyServiceRegistrarRef = cyServiceRegistrarRef;
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
        //this.sliderButton.setMajorTickSpacing(10);
        this.sliderButton.setMinorTickSpacing(1);
        this.sliderButton.setPaintTicks(true);
        this.sliderButton.setPaintLabels(true);
        this.sliderButton.setPaintLabels(false);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(labelAndTextField);
        sliderPanel.add(this.sliderButton);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(this.createCloseButton());
        
        this.add(sliderPanel, BorderLayout.NORTH);
        this.add(controlPanel, BorderLayout.SOUTH);
        
        // Set initial values
        this.textField.setText(String.valueOf(startYear));
        SocialNetworkAppManager.setSelectedYear(startYear);
        SocialNetworkAppManager.setSelectedSocialNetwork(this.socialNetwork);
        
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
            SocialNetworkAppManager.setSelectedYear(year);
            SocialNetworkAppManager.setSelectedSocialNetwork(this.socialNetwork);
            this.taskManager.execute(this.updateVisualStyleTaskFactory.createTaskIterator());   
        } else {
            this.textField.setText(String.valueOf(year));
        }
        this.lastYear = year;
    }
    
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
    
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
    
    public void setSocialNetwork(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }
    
    public void update(SocialNetwork socialNetwork) {
        this.setSocialNetwork(socialNetwork);
        
        int startYear = socialNetwork.getStartYear();
        int endYear = socialNetwork.getEndYear();
        this.setStartYear(startYear);
        this.setEndYear(endYear);
        
        /* Text field */
        this.getTextField().setText(String.valueOf(startYear));
        
        /* Slider button */
        this.getSliderButton().setMinimum(startYear);
        this.getSliderButton().setMaximum(endYear);
        this.getSliderButton().setValue(startYear);
        this.getSliderButton().repaint();
        
        this.updateUI();
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

}