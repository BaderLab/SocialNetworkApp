package org.baderlab.csapps.socialnetwork.actions;

import java.awt.event.ActionEvent;
import java.util.Map;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.tasks.CreateChartTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;

public class CreateChartAction extends AbstractCyAction {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private CreateChartTaskFactory createChartTaskFactory = null;
    private TaskManager<?, ?> taskManagerServiceRef = null;
    private SocialNetworkAppManager appManager = null;

    public CreateChartAction(Map<String, String> configProps, CyApplicationManager applicationManager, CyNetworkViewManager networkViewManager,
            TaskFactory factory, TaskManager<?, ?> taskManagerServiceRef, CreateChartTaskFactory createChartTaskFactory,
            SocialNetworkAppManager appManager) {
        super(configProps, applicationManager, networkViewManager, factory);
        this.createChartTaskFactory = createChartTaskFactory;
        this.taskManagerServiceRef = taskManagerServiceRef;
        this.appManager = appManager;
        putValue(NAME, "Generate Charts");
    }

    public void actionPerformed(ActionEvent e) {
        if (this.appManager.getCurrentlySelectedSocialNetwork() != null) {
            this.taskManagerServiceRef.execute(this.createChartTaskFactory.createTaskIterator());            
        }
    }

}
