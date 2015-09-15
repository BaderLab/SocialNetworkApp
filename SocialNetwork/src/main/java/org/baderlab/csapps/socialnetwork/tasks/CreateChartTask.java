package org.baderlab.csapps.socialnetwork.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.swing.JTextField;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkChartListener;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.ChartVisualStyle;
import org.baderlab.csapps.socialnetwork.model.academia.visualstyles.NodeAttribute;
import org.baderlab.csapps.socialnetwork.panels.InfoPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics2;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics2Factory;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifier;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifierFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListSingleSelection;

public class CreateChartTask extends AbstractTask implements TunableValidator {
    
    private CyApplicationManager cyAppManager;
    private SocialNetworkChartListener customChartListener;
    private VisualMappingManager visualMappingManager;
    private CyColumnIdentifierFactory columnIdFactory;
    private Callable<CyColumn> defaultDomain;
    private Callable<CyColumn> defaultRange;
    protected VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    protected VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    protected VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
    private SocialNetwork socialNetwork = null;
    private VisualStyleFactory visualStyleFactoryServiceRef = null;


    // Tunable fields are set by the user
    // Note: Callables are used for the selection lists in order to provide a custom toString() method
    // and still be able to get back the selected object.
    
    @Tunable(description="Set Domain:")
    public ListSingleSelection<Callable<CyColumn>> domainColumnNames;
    
    @Tunable(description="Set Range:")
    public ListSingleSelection<Callable<CyColumn>> rangeColumnNames;
    
    @Tunable(description="Visual Property:")
    public ListSingleSelection<Callable<VisualProperty<CyCustomGraphics2<?>>>> visualProperties;
    
    public CreateChartTask(CyApplicationManager applicationManager, SocialNetworkChartListener customChartManager, 
            VisualMappingManager visualMappingManager, CyColumnIdentifierFactory columnIdFactory, SocialNetworkAppManager socialNetworkAppManager,
            VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef, VisualStyleFactory visualStyleFactoryServiceRef) {
        this.cyAppManager = applicationManager;
        this.customChartListener = customChartManager;
        this.visualMappingManager = visualMappingManager;
        this.columnIdFactory = columnIdFactory;
        this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
        this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
        this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
        this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
        this.socialNetwork = socialNetworkAppManager.getCurrentlySelectedSocialNetwork();
        this.domainColumnNames = computeNumericNodeColumns();
        this.domainColumnNames.setSelectedValue(this.defaultDomain);
        this.rangeColumnNames = computeNumericNodeColumns();
        this.rangeColumnNames.setSelectedValue(this.defaultRange);
        this.visualProperties = computeVisualProperties();
    }
    
    
    /**
     * Select the columns from the node table that are numeric.
     */
    private ListSingleSelection<Callable<CyColumn>> computeNumericNodeColumns() {
        CyNetwork network = cyAppManager.getCurrentNetwork();
        CyTable nodeTable = network.getDefaultNodeTable();
        Collection<CyColumn> nodeColumns = nodeTable.getColumns();
        
        List<Callable<CyColumn>> numericColumnNames = new ArrayList<Callable<CyColumn>>();
        
        Callable<CyColumn> callableColumn = null;
        
        for(final CyColumn column : nodeColumns) {
            if (column.getType() == List.class) {
                if((Number.class.isAssignableFrom(column.getListElementType())) && !column.isPrimaryKey()) {
                    callableColumn = new Callable<CyColumn>() {
                        public CyColumn call() { 
                            return column; 
                        }
                        public String toString() { 
                            return column.getName(); 
                        }
                    };
                    if (column.getName().equals(NodeAttribute.YEARS_ACTIVE.toString())) {
                        this.defaultDomain = callableColumn;
                    }
                    if (column.getName().equals(NodeAttribute.PUBS_PER_YEAR.toString())) {
                        this.defaultRange = callableColumn;
                    }
                    numericColumnNames.add(callableColumn);
                }
            }
        }
        
        return new ListSingleSelection<Callable<CyColumn>>(numericColumnNames);
    }
    
    
    /**
     * Initialize the list of available custom graphics visual properties.
     */
    @SuppressWarnings("unchecked")
    private ListSingleSelection<Callable<VisualProperty<CyCustomGraphics2<?>>>> computeVisualProperties() {
        Set<VisualLexicon> lexicons = visualMappingManager.getAllVisualLexicon();
        
        List<Callable<VisualProperty<CyCustomGraphics2<?>>>> visualProperties = new ArrayList<Callable<VisualProperty<CyCustomGraphics2<?>>>>();
        for(VisualLexicon lexicon : lexicons) {
            for(int i = 1; i <= 9; i++) {
                
                final VisualProperty<CyCustomGraphics2<?>> visualProperty = 
                        (VisualProperty<CyCustomGraphics2<?>>) lexicon.lookup(CyNode.class, "nodeCustomGraphics" + i);
                
                if(visualProperty != null) {
                    visualProperties.add(new Callable<VisualProperty<CyCustomGraphics2<?>>>() {
                        public VisualProperty<CyCustomGraphics2<?>> call() { 
                            return visualProperty; 
                        }
                        public String toString() { 
                            return visualProperty.getDisplayName(); 
                        }
                    });
                }
            }
        }
        
        return new ListSingleSelection<Callable<VisualProperty<CyCustomGraphics2<?>>>>(visualProperties);
    }
    
    
    public ValidationState getValidationState(Appendable message) {
        return ValidationState.OK;
    }
    
    @Override
    public void run(TaskMonitor monitor) throws Exception {
        monitor.setTitle("Generating Charts ...");
        
        // Get the values the user selected
        final CyColumn domainColumn = domainColumnNames.getSelectedValue().call();
        final CyColumn rangeColumn = rangeColumnNames.getSelectedValue().call();
        final VisualProperty<CyCustomGraphics2<?>> visualProperty = visualProperties.getSelectedValue().call();
        
        monitor.setStatusMessage("Using Visual Property '" + visualProperty.getDisplayName() + "'");
        monitor.setStatusMessage("Creating charts");
        
        // Get the graphics factory from the listener.
        CyCustomGraphics2Factory<?> customGraphicsFactory = customChartListener.getFactory();
        
        // Get the current network view
        
        CyNetwork network = this.socialNetwork.getCyNetwork();
        
        ChartVisualStyle chartVisualStyle = new ChartVisualStyle(cyAppManager, network, this.socialNetwork, 
                this.visualStyleFactoryServiceRef, this.passthroughMappingFactoryServiceRef, this.continuousMappingFactoryServiceRef,
                this.discreteMappingFactoryServiceRef, true);
        visualMappingManager.addVisualStyle(chartVisualStyle.getVisualStyle());                                                        
        visualMappingManager.setCurrentVisualStyle(chartVisualStyle.getVisualStyle());
        
        CyNetworkView networkView = this.socialNetwork.getNetworkView();
        if(networkView == null) {
            monitor.showMessage(TaskMonitor.Level.ERROR, "No network view");
            return;
        }
                
        // All nodes and edges have to be made visible before applying charts
        for (final CyNode node : network.getNodeList()) {
            networkView.getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_VISIBLE, true);                 
        }

        for (final CyEdge edge : network.getEdgeList()) {
            networkView.getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);                    
        }
        
        InfoPanel infoPanel = SocialNetworkAppManager.getInfoPanel();
        if (infoPanel != null) {
            JTextField infoPanelTextField = infoPanel.getTextField();
            infoPanelTextField.setText("ALL");
            infoPanelTextField.repaint();
        }
        
        // Set the chart properties, tell the chart to use the new column we created as the data source.
        CyColumnIdentifier domainColumnId = columnIdFactory.createColumnIdentifier(domainColumn.getName());
        CyColumnIdentifier rangeColumnId = columnIdFactory.createColumnIdentifier(rangeColumn.getName());
        Map<String,Object> chartProps = new HashMap<String, Object>();
        chartProps.put("cy_dataColumns", Arrays.asList(rangeColumnId));
        chartProps.put("cy_domainLabelsColumn", domainColumnId);
        chartProps.put("cy_globalRange", "true");
        chartProps.put("cy_showDomainAxis", "true");
        chartProps.put("cy_showRangeAxis", "true");
        chartProps.put("cy_orientation", "VERTICAL");
        chartProps.put("cy_domainLabelPosition", "UP_45");
        chartProps.put("cy_separation", 0.2);
        chartProps.put("cy_type", "GROUPED");
        chartProps.put("cy_colorScheme", "MODULATED");
        
        // create the chart instance
        CyCustomGraphics2<?> customGraphics = customGraphicsFactory.getInstance(chartProps);
        
        //String scheme = customGraphics.getProperties().get("cy_colorScheme").toString();

        // Set the custom graphics on the visual style
        VisualStyle visualStyle = visualMappingManager.getCurrentVisualStyle();
        visualStyle.setDefaultValue(visualProperty, customGraphics);
        
        // must do this or charts won't show up instantly
        networkView.updateView();            
    }
    
}

