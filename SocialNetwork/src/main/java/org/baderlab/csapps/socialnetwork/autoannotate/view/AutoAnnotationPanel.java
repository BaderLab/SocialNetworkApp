package org.baderlab.csapps.socialnetwork.autoannotate.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationParameters;
import org.baderlab.csapps.socialnetwork.autoannotate.action.AnnotateButtonActionListener;
import org.baderlab.csapps.socialnetwork.autoannotate.action.ClusterTableSelctionAction;
import org.baderlab.csapps.socialnetwork.autoannotate.model.AnnotationSet;
import org.baderlab.csapps.socialnetwork.autoannotate.model.Cluster;
import org.baderlab.csapps.socialnetwork.autoannotate.model.ClusterTableModel;
import org.baderlab.csapps.socialnetwork.autoannotate.task.RemoveAnnotationTask;
import org.baderlab.csapps.socialnetwork.autoannotate.task.UpdateAnnotationTask;
import org.baderlab.csapps.socialnetwork.autoannotate.task.VisualizeClusterAnnotationTaskFactory;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.DeleteClusterTask;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.ExtractClusterTask;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.MergeClustersTask;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.UpdateClusterLabelTask;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.swing.BasicCollapsiblePanel;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

/**
 * @author arkadyark
 * <p>
 * Date   June 16, 2014<br>
 * Time   11:26:32 AM<br>
 */

public class AutoAnnotationPanel extends JPanel implements CytoPanelComponent {

    private static final long serialVersionUID = 7901088595186775935L;

    private static final String defaultButtonString = "Use clusterMaker defaults";
    private static final String specifyColumnButtonString = "Select cluster column";

    // Dropdown menus
    private JComboBox<String> nameColumnDropdown;
    private JComboBox<String> clusterColumnDropdown;
    private JComboBox<String> clusterAlgorithmDropdown;

    // Radio buttons to choose between clusterMaker defaults and a cluster column
    private ButtonGroup clusterButtonGroup;
    private JRadioButton defaultButton;
    private JRadioButton specifyColumnButton;

    // Label that shows the name of the selected network (updates)
    private JLabel networkLabel;
    // Used to update panel on network selection
    private HashMap<CyNetworkView, JComboBox<AnnotationSet>> networkViewToClusterSetDropdown;
    // Used to update table on annotation set selection
    //private HashMap<AnnotationSet, JTable> clustersToTables;
    // Used to store the output table
    private JPanel outputPanel;
    // Reference needed to hide when not needed
    private JPanel bottomButtonPanel;

    private CyNetworkView selectedView;
    private CyNetwork selectedNetwork;

    private AutoAnnotationManager autoAnnotationManager;
    private AutoAnnotationParameters params;

    private JCheckBox layoutCheckBox;
    private JCheckBox groupsCheckBox;

    private DisplayOptionsPanel displayOptionsPanel;

    // Keeps track of when selection of a cluster is happening, to ignore events this fires
    private boolean selecting = false;
    // Keeps track of when annotation is happening, to ignore events this fires
    private boolean annotating = false;

    public AutoAnnotationPanel(CySwingApplication application, DisplayOptionsPanel displayOptionsPanel){

        //Hashmap tracking the views to the group of annotation sets
        this.networkViewToClusterSetDropdown = new HashMap<CyNetworkView, JComboBox<AnnotationSet>>();

        this.displayOptionsPanel = displayOptionsPanel;

        this.autoAnnotationManager = AutoAnnotationManager.getInstance();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 500));

        JPanel inputPanel = createInputPanel();

        this.bottomButtonPanel = createBottomButtonPanel();
        this.bottomButtonPanel.setAlignmentX(LEFT_ALIGNMENT);

        this.outputPanel = createOutputPanel();
        JScrollPane clusterTableScrollPane = new JScrollPane(this.outputPanel);

        add(inputPanel, BorderLayout.NORTH);
        add(clusterTableScrollPane, BorderLayout.CENTER);
        add(this.bottomButtonPanel, BorderLayout.SOUTH);
    }

    public void addClusters(AnnotationSet annotationSet, AutoAnnotationParameters params) {
        params.addAnnotationSet(annotationSet);
        // If this is the panel's first AnnotationSet for this view
        CyNetworkView clusterView = params.getNetworkView();
        if (!this.networkViewToClusterSetDropdown.containsKey(clusterView)) {
            addNetworkView(clusterView);
        }

        // Create scrollable clusterTable
        JTable clusterTable = createClusterTable(annotationSet);
        JScrollPane clusterTableScroll = new JScrollPane(clusterTable);
        clusterTableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Add the table to the panel
        this.outputPanel.add(clusterTableScroll, BorderLayout.CENTER);
        annotationSet.setClusterTable(clusterTable);
        // Add the annotation set to the dropdown
        JComboBox<AnnotationSet> clusterSetDropdown = this.networkViewToClusterSetDropdown.get(clusterView);
        clusterSetDropdown.addItem(annotationSet);
        // Select the most recently added annotation set
        clusterSetDropdown.setSelectedIndex(clusterSetDropdown.getItemCount()-1);
    }

    private void addNetworkView(CyNetworkView view) {
        // Create dropdown with cluster sets of this networkView
        JComboBox<AnnotationSet> annotationSetDropdown = new JComboBox<AnnotationSet>();
        annotationSetDropdown.addItemListener(new ItemListener(){
            // Switch the selected annotation set - this method is also called when you reload a session
            public void itemStateChanged(ItemEvent itemEvent) {
                CyGroupManager groupManager = AutoAnnotationPanel.this.autoAnnotationManager.getGroupManager();
                CyGroupFactory groupFactory = AutoAnnotationPanel.this.autoAnnotationManager.getGroupFactory();
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    AnnotationSet annotationSet = (AnnotationSet) itemEvent.getItem();
                    AutoAnnotationPanel.this.displayOptionsPanel.setSelectedAnnotationSet(annotationSet);
                    AutoAnnotationPanel.this.params.setSelectedAnnotationSet(annotationSet);
                    // Update the selected annotation set
                    annotationSet.updateCoordinates();
                    String annotationSetName = annotationSet.getName();
                    // Get the table where WordCloud results are stored
                    Long clusterTableSUID = AutoAnnotationPanel.this.selectedNetwork.getDefaultNetworkTable().getRow(
                            AutoAnnotationPanel.this.selectedNetwork.getSUID()).get(annotationSetName, Long.class);
                    CyTable clusterSetTable = AutoAnnotationPanel.this.autoAnnotationManager.getTableManager().getTable(clusterTableSUID);
                    TaskIterator currentTasks = new TaskIterator();
                    for (Cluster cluster : annotationSet.getClusterMap().values()) {
                        if (cluster.getEllipse() == null && cluster.getTextAnnotation() == null) {
                            // Update the text label of the selected cluster
                            currentTasks.append(new UpdateClusterLabelTask(cluster, clusterSetTable));
                        }
                        // Redraw selected clusters
                        VisualizeClusterAnnotationTaskFactory visualizeCluster = new VisualizeClusterAnnotationTaskFactory(cluster);
                        currentTasks.append(visualizeCluster.createTaskIterator());

                    }
                    //execute all the cluster and label drawing tasks
                    AutoAnnotationManager.getInstance().getDialogTaskManager().execute(currentTasks);

                    setOutputVisibility(true);
                    annotationSet.getClusterTable().getParent().getParent().setVisible(true); // Show selected table
                    updateUI();
                } else if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                    AnnotationSet clusters = (AnnotationSet) itemEvent.getItem();
                    for (Cluster cluster : clusters.getClusterMap().values()) {
                        // Hide the annotations
                        cluster.erase();
                        // Remove the groups if being used
                        CyGroup group = cluster.getGroup();
                        if (group != null) {
                            if (cluster.isCollapsed()) {
                                cluster.getGroup().expand(AutoAnnotationPanel.this.selectedNetwork);
                            }
                            group.removeGroupFromNetwork(AutoAnnotationPanel.this.selectedNetwork);
                            groupManager.destroyGroup(group);
                            cluster.removeGroup();
                        }
                        // Hide the table for deselected AnnotationSet clusters
                        clusters.getClusterTable().getParent().getParent().setVisible(false);
                    }



                    setOutputVisibility(false);
                    updateUI();
                }
            }
        });
        this.outputPanel.add(annotationSetDropdown, BorderLayout.NORTH);
        this.networkViewToClusterSetDropdown.put(view, annotationSetDropdown);

        this.selectedView = view;
        this.selectedNetwork = this.autoAnnotationManager.getApplicationManager().getCurrentNetwork();
        this.params = this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters().get(view);

        this.networkLabel.setText(this.selectedNetwork.toString());
        updateUI();
    }

    public void columnCreated(CyTable source, String columnName) {
        CyTable nodeTable = this.selectedNetwork.getDefaultNodeTable();
        if (source == nodeTable) {
            CyColumn column = nodeTable.getColumn(columnName);
            if (column.getType() == String.class) {
                if (((DefaultComboBoxModel<String>) this.nameColumnDropdown.getModel()).getIndexOf(column) == -1) { // doesn't already contain column
                    this.nameColumnDropdown.addItem(column.getName());
                }
            } else if (column.getType() == Integer.class || (column.getType() == List.class && column.getListElementType() == Integer.class)) {
                if (((DefaultComboBoxModel<String>) this.clusterColumnDropdown.getModel()).getIndexOf(column) == -1) { // doesn't already contain column
                    this.clusterColumnDropdown.addItem(column.getName());
                }
            }
        }
    }

    public void columnDeleted(CyTable source, String columnName) {
        if (source == this.selectedNetwork.getDefaultNodeTable()) {
            for (int i = 0; i < this.nameColumnDropdown.getItemCount(); i++) {
                if (this.nameColumnDropdown.getModel().getElementAt(i) == columnName) {
                    this.nameColumnDropdown.removeItem(columnName);
                }
            }
            for (int i = 0; i < this.clusterColumnDropdown.getItemCount(); i++) {
                if (this.clusterColumnDropdown.getModel().getElementAt(i) == columnName) {
                    this.clusterColumnDropdown.removeItem(columnName);
                }
            }
        }
    }

    private BasicCollapsiblePanel createAdvancedOptionsPanel() {
        BasicCollapsiblePanel optionsPanel = new BasicCollapsiblePanel("Advanced Options");

        JPanel innerPanel = new JPanel(new BorderLayout());

        // Buttons to choose whether to use ClusterMaker or specify a column
        this.defaultButton = new JRadioButton(defaultButtonString);
        this.specifyColumnButton = new JRadioButton(specifyColumnButtonString);
        this.defaultButton.addItemListener(new ItemListener() {
            // Ensure only one dropdown is shown at any time
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    AutoAnnotationPanel.this.clusterAlgorithmDropdown.setVisible(true);
                    AutoAnnotationPanel.this.clusterColumnDropdown.setVisible(false);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    AutoAnnotationPanel.this.clusterAlgorithmDropdown.setVisible(false);
                    AutoAnnotationPanel.this.clusterColumnDropdown.setVisible(true);
                }
            }
        });

        // Group buttons together to make them mutually exclusive
        this.clusterButtonGroup = new ButtonGroup();
        this.clusterButtonGroup.add(this.defaultButton);
        this.clusterButtonGroup.add(this.specifyColumnButton);

        JPanel clusterButtonPanel = new JPanel();
        clusterButtonPanel.setLayout(new BoxLayout(clusterButtonPanel, BoxLayout.PAGE_AXIS));
        clusterButtonPanel.add(this.defaultButton);
        clusterButtonPanel.add(this.specifyColumnButton);

        // Dropdown with all the available algorithms
        DefaultComboBoxModel<String> clusterDropdownModel = new DefaultComboBoxModel<String>();
        for (String algorithm : this.autoAnnotationManager.getAlgorithmToColumnName().keySet()) {
            clusterDropdownModel.addElement(algorithm);
        }

        // To choose a clusterMaker algorithm
        this.clusterAlgorithmDropdown = new JComboBox<String>(clusterDropdownModel);
        this.clusterAlgorithmDropdown.setPreferredSize(new Dimension(135, 30));
        // Alternatively, user can choose a cluster column themselves (if they've run clusterMaker themselves)
        this.clusterColumnDropdown = new JComboBox<String>();
        this.clusterColumnDropdown.setPreferredSize(new Dimension(135, 30));

        // Only one dropdown visible at a time
        this.clusterAlgorithmDropdown.setVisible(true);
        this.clusterColumnDropdown.setVisible(false);

        this.clusterAlgorithmDropdown.setSelectedItem("MCL Cluster");

        JPanel dropdownPanel = new JPanel();
        dropdownPanel.add(this.clusterAlgorithmDropdown);
        dropdownPanel.add(this.clusterColumnDropdown);

        // By default use clusterMaker defaults
        this.defaultButton.setSelected(true);

        // By default layout nodes by cluster
        this.layoutCheckBox = new JCheckBox("Layout nodes by cluster");
        this.layoutCheckBox.setSelected(false);

        // By default layout nodes by cluster
        this.groupsCheckBox = new JCheckBox("Create Groups for clusters");
        this.groupsCheckBox.setSelected(false);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.add(this.layoutCheckBox);
        checkBoxPanel.add(this.groupsCheckBox);

        JPanel nonClusterOptionPanel = new JPanel();
        nonClusterOptionPanel.add(checkBoxPanel);

        JPanel clusterOptionPanel = new JPanel(new BorderLayout());
        clusterOptionPanel.setBorder(BorderFactory.createTitledBorder("ClusterMaker Options"));
        clusterOptionPanel.add(clusterButtonPanel, BorderLayout.WEST);
        clusterOptionPanel.add(dropdownPanel, BorderLayout.EAST);

        innerPanel.add(clusterOptionPanel, BorderLayout.NORTH);
        innerPanel.add(nonClusterOptionPanel, BorderLayout.SOUTH);

        optionsPanel.add(innerPanel);

        return optionsPanel;
    }



    private JPanel createBottomButtonPanel() {
        JPanel bottomButtonPanel = new JPanel();

        // Button to get remove an annotation set
        JButton removeButton = new JButton("Remove");
        ActionListener clearActionListener = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this annotation set? This cannot be undone.",
                        "Remove confirmation", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {

                    JComboBox<AnnotationSet> clusterSetDropdown = AutoAnnotationPanel.this.networkViewToClusterSetDropdown.get(AutoAnnotationPanel.this.selectedView);
                    AnnotationSet annotationSet = (AnnotationSet) clusterSetDropdown.getSelectedItem();
                    // Get rid of the table associated with this cluster set
                    remove(annotationSet.getClusterTable().getParent());
                    RemoveAnnotationTask remove = new RemoveAnnotationTask(AutoAnnotationPanel.this.params);
                    AutoAnnotationPanel.this.autoAnnotationManager.getDialogTaskManager().execute(new TaskIterator(remove));
                    // Remove cluster set from dropdown
                    clusterSetDropdown.removeItem(annotationSet);
                }
            }
        };
        removeButton.addActionListener(clearActionListener);
        removeButton.setToolTipText("Remove Annotation Set");

        // Button to update the current cluster set
        JButton updateButton = new JButton("Update");
        ActionListener updateActionListener = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                AnnotationSet annotationSet = (AnnotationSet) AutoAnnotationPanel.this.networkViewToClusterSetDropdown.
                        get(AutoAnnotationPanel.this.selectedView).getSelectedItem();
                UpdateAnnotationTask update = new UpdateAnnotationTask(annotationSet);
                AutoAnnotationPanel.this.autoAnnotationManager.getDialogTaskManager().execute(new TaskIterator(update));

            }
        };
        updateButton.addActionListener(updateActionListener);
        updateButton.setToolTipText("Update Annotation Set");

        bottomButtonPanel = new JPanel();
        bottomButtonPanel.add(new JLabel("Annotation Sets:"));
        bottomButtonPanel.add(removeButton);
        bottomButtonPanel.add(updateButton);

        return bottomButtonPanel;
    }

    private JTable createClusterTable(final AnnotationSet annotationSet) {

        // Populate the table model
        Object[][] data = new Object[annotationSet.getClusterMap().size()][2];
        int i=0;
        for (Cluster cluster : annotationSet.getClusterMap().values()) {
            // Each row contains the cluster (printed as a string, its label), and how many nodes it contains
            data[i][0] = cluster;
            data[i][1] = cluster.getSize();
            i++;
        }
        String[] columnNames = {"Cluster","Number of nodes"};

        //Create a new Cluster Table Model
        ClusterTableModel model = new ClusterTableModel(columnNames, data,annotationSet.getClusterMap());

        JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(320, 250));
        table.getColumnModel().getColumn(0).setPreferredWidth(210);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);


        annotationSet.setClusterTable(table);

        table.getSelectionModel().addListSelectionListener(new ClusterTableSelctionAction(annotationSet));
        table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Make it so that the cluster table can be sorted
        table.setAutoCreateRowSorter(true);

        return table;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Label showing the current network
        this.networkLabel = new JLabel("No network selected");
        Font font = this.networkLabel.getFont();
        this.networkLabel.setFont(new Font(font.getFamily(), font.getStyle(), 18));

        JLabel nameColumnDropdownLabel = new JLabel("   Select the column with the appropriate descriptions:");
        // Gives the user a choice of column with gene names
        this.nameColumnDropdown = new JComboBox<String>();
        // Collapsible panel with advanced cluster options
        final BasicCollapsiblePanel advancedOptionsPanel = createAdvancedOptionsPanel();

        // Button to run the annotation
        JButton annotateButton = new JButton("Annotate!");
        AnnotateButtonActionListener annotateAction = new AnnotateButtonActionListener( this);
        annotateButton.addActionListener(annotateAction);

        annotateButton.setToolTipText("Create a new annotation set");

        inputPanel.add(this.networkLabel);
        inputPanel.add(nameColumnDropdownLabel);
        inputPanel.add(this.nameColumnDropdown);
        inputPanel.add(advancedOptionsPanel);
        inputPanel.add(annotateButton);

        this.networkLabel.setAlignmentX(CENTER_ALIGNMENT);
        nameColumnDropdownLabel.setAlignmentX(CENTER_ALIGNMENT);
        this.nameColumnDropdown.setAlignmentX(CENTER_ALIGNMENT);
        advancedOptionsPanel.setAlignmentX(CENTER_ALIGNMENT);
        annotateButton.setAlignmentX(CENTER_ALIGNMENT);

        return inputPanel;
    }

    private JPanel createOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout());

        JButton extractButton = new JButton("Extract");
        ActionListener extractActionListener = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                AnnotationSet annotationSet = (AnnotationSet) AutoAnnotationPanel.this.networkViewToClusterSetDropdown.
                        get(AutoAnnotationPanel.this.selectedView).getSelectedItem();
                ExtractClusterTask extract = new ExtractClusterTask(annotationSet);
                AutoAnnotationPanel.this.autoAnnotationManager.getDialogTaskManager().execute(new TaskIterator(extract));
            }
        };
        extractButton.addActionListener(extractActionListener);
        extractButton.setToolTipText("Create a new cluster");

        // Button to merge two clusters
        JButton mergeButton = new JButton("Merge");
        ActionListener mergeActionListener = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                AnnotationSet annotationSet = (AnnotationSet) AutoAnnotationPanel.this.networkViewToClusterSetDropdown.
                        get(AutoAnnotationPanel.this.selectedView).getSelectedItem();
                MergeClustersTask merge = new MergeClustersTask(annotationSet);
                AutoAnnotationPanel.this.autoAnnotationManager.getDialogTaskManager().execute(new TaskIterator(merge));
            }
        };
        mergeButton.addActionListener(mergeActionListener);
        mergeButton.setToolTipText("Merge clusters into one");
        ActionListener deletActionListener = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                AutoAnnotationManager.getInstance().getDialogTaskManager().execute(new TaskIterator(new DeleteClusterTask(AutoAnnotationPanel.this.params)));
            }
        };
        // Button to delete a cluster from an annotation set
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(deletActionListener);
        deleteButton.setToolTipText("Delete selected cluster(s)");

        // Buttons to edit clusters
        JPanel outputButtonPanel = new JPanel();
        outputButtonPanel.add(new JLabel("Clusters:"));
        outputButtonPanel.add(extractButton);
        outputButtonPanel.add(mergeButton);
        outputButtonPanel.add(deleteButton);

        JPanel outputBottomPanel = new JPanel();
        outputBottomPanel.setLayout(new BoxLayout(outputBottomPanel, BoxLayout.PAGE_AXIS));

        outputBottomPanel.add(outputButtonPanel);

        outputPanel.add(outputBottomPanel, BorderLayout.SOUTH);

        return outputPanel;
    }

    public String getAlgorithm(){
        return (String) this.clusterAlgorithmDropdown.getSelectedItem();
    }

    public String getAnnotationColumnName(){
        return (String) this.nameColumnDropdown.getSelectedItem();
    }

    public String getClusterColumnName(AutoAnnotationParameters currentParams){
        if(isClusterMaker()){
            String clusteringColumnName1 = AutoAnnotationManager.algorithmToColumnName.get(getAlgorithm());
            CyTable defaultNodetable = this.autoAnnotationManager.getApplicationManager().getCurrentNetwork().getDefaultNodeTable();
            return currentParams.nextClusterColumnName(clusteringColumnName1, defaultNodetable);
        } else {
            return (String) this.clusterColumnDropdown.getSelectedItem();
        }
    }

    public JTable getClusterTable(AnnotationSet annotationSet) {
        return annotationSet.getClusterTable();
    }

    public Component getComponent() {
        return this;
    }

    private JTable getCurrentSelectedTable(CyNetworkView view){
        if(view != null) {
            if(this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters() != null) {
                if(this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters().containsKey(view)) {
                    if(this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters().get(view).getSelectedAnnotationSet() != null) {
                        return this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters().get(view).getSelectedAnnotationSet().getClusterTable();
                    }
                }
            }
        }

        return null;

    }

    //Getter and Setters for parameters needed by the Actionlisteners.
    public CyNetworkView getCurrentView(){
        return this.selectedView;
    }

    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

    public Icon getIcon() {
        //create an icon for the enrichment map panels
        URL EMIconURL = this.getClass().getResource("enrichmentmap_logo_notext_small.png");
        ImageIcon EMIcon = null;
        if (EMIconURL != null) {
            EMIcon = new ImageIcon(EMIconURL);
        }
        return EMIcon;
    }

    public AnnotationSet getSelectedAnnotationSet() {
        return this.params.getSelectedAnnotationSet();
    }

    public String getTitle() {
        return "Annotation Panel";
    }

    public boolean isClusterMaker(){
        return this.defaultButton.isSelected();
    }

    public boolean isGroupsSelected(){
        return this.groupsCheckBox.isSelected();
    }

    public boolean isLayoutNodeSelected(){
        return this.layoutCheckBox.isSelected();
    }

    public void removeNetworkView(CyNetworkView view) {
        if (this.networkViewToClusterSetDropdown.size() == 1 && this.networkViewToClusterSetDropdown.containsKey(view)) {

            this.networkLabel.setText("No network selected");
            this.nameColumnDropdown.removeAllItems();
            this.clusterColumnDropdown.removeAllItems();
            updateUI();
            if (this.networkViewToClusterSetDropdown.containsKey(view)) {
                JComboBox<?> clusterSetDropdown = this.networkViewToClusterSetDropdown.get(view);
                Container clusterTable = getCurrentSelectedTable(view).getParent().getParent();
                clusterSetDropdown.getParent().remove(clusterSetDropdown);
                clusterTable.getParent().remove(clusterTable);
                this.networkViewToClusterSetDropdown.remove(view);
            }

            this.selectedView = null;
            this.selectedNetwork = null;
            this.params = null;
        }
    }

    public void setAnnotating(boolean b) {
        this.annotating = b;
    }

    public void setDisplayOptionsPanel(DisplayOptionsPanel displayOptionsPanel) {
        this.displayOptionsPanel = displayOptionsPanel;
    }

    public void setOutputVisibility(boolean b) {
        // Sets the visibility of the output related components
        this.outputPanel.getParent().getParent().setVisible(b);
        this.bottomButtonPanel.setVisible(b);
        if (b) {
            this.autoAnnotationManager.getDisplayOptionsPanelAction().actionPerformed(new ActionEvent("",0,""));
        } else {
            this.displayOptionsPanel.setVisible(b);
        }
    }

    public void updateColumnName(CyTable source, String oldColumnName,
            String newColumnName) {
        // Column name has been changed, check if it is
        // in the dropdowns and update if needed
        if (source == this.selectedNetwork.getDefaultNodeTable()) {
            for (int i = 0; i < this.nameColumnDropdown.getItemCount(); i++) {
                if (this.nameColumnDropdown.getModel().getElementAt(i) == oldColumnName) {
                    this.nameColumnDropdown.removeItem(oldColumnName);
                    this.nameColumnDropdown.insertItemAt(newColumnName, i);
                }
            }
            for (int i = 0; i < this.clusterColumnDropdown.getItemCount(); i++) {
                if (this.clusterColumnDropdown.getModel().getElementAt(i) == oldColumnName) {
                    this.clusterColumnDropdown.removeItem(oldColumnName);
                    this.clusterColumnDropdown.insertItemAt(newColumnName, i);
                }
            }
        }
    }

    public void updateParameters(){

        // Update current params to newly created
        this.params = this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters().get(this.selectedView);

    }

    public void updateSelectedView(CyNetworkView view) {
        this.selectedView = view;
        this.selectedNetwork = this.autoAnnotationManager.getApplicationManager().getCurrentNetwork();

        if (this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters().containsKey(this.selectedView)) {
            // There exists a cluster set for this view
            this.params = this.autoAnnotationManager.getNetworkViewToAutoAnnotationParameters().get(this.selectedView);
            if (!this.networkViewToClusterSetDropdown.containsKey(this.selectedView)) {
                // Params has just been loaded, add its annotationSets to the dropdown
                // Also adds its network view
                for (AnnotationSet annotationSet : this.params.getAnnotationSets().values()) {
                    addClusters(annotationSet,this.params);
                }
                // Restore selected annotation set
                if (this.params.getSelectedAnnotationSet() != null) {
                    this.networkViewToClusterSetDropdown.get(this.selectedView).setSelectedItem(this.params.getSelectedAnnotationSet());
                }
            }
            // Show the table and buttons
            setOutputVisibility(true);
        } else {
            // Hide/keep hidden the table and buttons
            setOutputVisibility(false);
        }

        // Repopulate the dropdown menus
        this.nameColumnDropdown.removeAllItems();
        this.clusterColumnDropdown.removeAllItems();
        for (CyColumn column : view.getModel().getDefaultNodeTable().getColumns()) {
            // Add string columns to nameColumnDropdown
            if (column.getType() == String.class) {
                this.nameColumnDropdown.addItem(column.getName());
            } else if (column.getType() == List.class) {
                this.nameColumnDropdown.addItem(column.getName());
                // Add integer/integer list columns to clusterColumnDropdown
            } else if (column.getType() == Integer.class || (column.getType() == List.class && column.getListElementType() == Integer.class)) {
                this.clusterColumnDropdown.addItem(column.getName());
            }
        }

        // Try to guess the appropriate columns
        for (int i = 0; i < this.nameColumnDropdown.getItemCount(); i++) {
            if (this.nameColumnDropdown.getItemAt(i).getClass() == String.class) {
                if (this.nameColumnDropdown.getItemAt(i).contains("Publications")) {
                    this.nameColumnDropdown.setSelectedIndex(i);
                }
            }
        }

        // Update the label with the network name
        this.networkLabel.setText("  " + this.selectedNetwork.toString());
        updateUI();
        // Hide previous dropdown and table
        if (this.networkViewToClusterSetDropdown.containsKey(this.selectedView)) {
            JComboBox<?> currentDropdown = this.networkViewToClusterSetDropdown.get(this.selectedView);
            currentDropdown.setVisible(false);
            // Hide the scrollpane containing the clusterTable
            getCurrentSelectedTable(this.selectedView).getParent().getParent().setVisible(false);
        }

        // Show new dropdown and table
        if (this.networkViewToClusterSetDropdown.containsKey(this.selectedView)) {
            this.networkViewToClusterSetDropdown.get(this.selectedView).setVisible(true);
            getCurrentSelectedTable(this.selectedView).getParent().getParent().setVisible(true);
        }
    }

}