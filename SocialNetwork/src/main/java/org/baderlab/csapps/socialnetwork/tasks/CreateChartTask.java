package org.baderlab.csapps.socialnetwork.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.listeners.SocialNetworkChartListener;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics2;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics2Factory;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifier;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifierFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListSingleSelection;

public class CreateChartTask extends AbstractTask implements TunableValidator {
    
    private CyApplicationManager applicationManager;
    private SocialNetworkChartListener customChartListener;
    private VisualMappingManager visualMappingManager;
    private CyColumnIdentifierFactory columnIdFactory;

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
            VisualMappingManager visualMappingManager, CyColumnIdentifierFactory columnIdFactory) {
        this.applicationManager = applicationManager;
        this.customChartListener = customChartManager;
        this.visualMappingManager = visualMappingManager;
        this.columnIdFactory = columnIdFactory;
        this.domainColumnNames = computeNumericNodeColumns();
        this.rangeColumnNames = computeNumericNodeColumns();
        this.visualProperties = computeVisualProperties();
    }
    
    
    /**
     * Select the columns from the node table that are numeric.
     */
    private ListSingleSelection<Callable<CyColumn>> computeNumericNodeColumns() {
        CyNetwork network = applicationManager.getCurrentNetwork();
        CyTable nodeTable = network.getDefaultNodeTable();
        Collection<CyColumn> nodeColumns = nodeTable.getColumns();
        
        List<Callable<CyColumn>> numericColumnNames = new ArrayList<Callable<CyColumn>>();
        
        for(final CyColumn column : nodeColumns) {
            if (column.getType() == List.class) {
                if((Number.class.isAssignableFrom(column.getListElementType())) && !column.isPrimaryKey()) {
                    numericColumnNames.add(new Callable<CyColumn>() {
                        public CyColumn call() { 
                            return column; 
                        }
                        public String toString() { 
                            return column.getName(); 
                        }
                    });
                }
            } else {
                if((Number.class.isAssignableFrom(column.getType())) && !column.isPrimaryKey()) {
                    numericColumnNames.add(new Callable<CyColumn>() {
                        public CyColumn call() { 
                            return column; 
                        }
                        public String toString() { 
                            return column.getName(); 
                        }
                    });
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
        monitor.setTitle("Generating Charts");
        
        // Get the values the user selected
        final CyColumn domainColumn = domainColumnNames.getSelectedValue().call();
        final CyColumn rangeColumn = rangeColumnNames.getSelectedValue().call();
        final VisualProperty<CyCustomGraphics2<?>> visualProperty = visualProperties.getSelectedValue().call();
        
        monitor.setStatusMessage("Using Visual Property '" + visualProperty.getDisplayName() + "'");
        monitor.setStatusMessage("Creating charts");
        
        // Get the graphics factory from the listener.
        CyCustomGraphics2Factory<?> customGraphicsFactory = customChartListener.getFactory();
        
        // Get the current network view
        VisualStyle chartVisualStyle = CytoscapeUtilities.getVisualStyle("Social Network Chart", visualMappingManager);
        visualMappingManager.setCurrentVisualStyle(chartVisualStyle);
        CyNetworkView networkView = applicationManager.getCurrentNetworkView();
        if(networkView == null) {
            monitor.showMessage(TaskMonitor.Level.ERROR, "No network view");
            return;
        }
        
        // Set the chart properties, tell the chart to use the new column we created as the data source.
        CyColumnIdentifier domainColumnId = columnIdFactory.createColumnIdentifier(domainColumn.getName());
        CyColumnIdentifier rangeColumnId = columnIdFactory.createColumnIdentifier(rangeColumn.getName());
        Map<String,Object> chartProps = new HashMap<String, Object>();
        chartProps.put("cy_dataColumns", Arrays.asList(rangeColumnId));
        chartProps.put("cy_domainLabelsColumn", Arrays.asList(domainColumnId));
        chartProps.put("cy_autoRange", "true");
        //chartProps.put("cy_globalRange", "true"); // TODO: Specify range?
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

