package org.baderlab.csapps.socialnetwork.autoannotate.action;

import java.util.HashSet;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.baderlab.csapps.socialnetwork.autoannotate.AutoAnnotationManager;
import org.baderlab.csapps.socialnetwork.autoannotate.model.AnnotationSet;
import org.baderlab.csapps.socialnetwork.autoannotate.model.Cluster;
import org.baderlab.csapps.socialnetwork.autoannotate.task.Observer;
import org.baderlab.csapps.socialnetwork.autoannotate.task.cluster.SelectClusterTask;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;

//Action listener when the user selects an item in the cluster table.
public class ClusterTableSelctionAction implements ListSelectionListener{

    private AnnotationSet annotationSet;
    private JTable table;
    private HashSet<Integer> currentSelection;

    public ClusterTableSelctionAction(AnnotationSet annotationSet) {
        super();
        this.annotationSet = annotationSet;
        this.table = annotationSet.getClusterTable();
        this.currentSelection = new HashSet<Integer>();
    }

    public void valueChanged(ListSelectionEvent e) {

        AutoAnnotationManager autoAnnotationManager = AutoAnnotationManager.getInstance();

        // Down-click and up-click are separate events,
        //this makes only one of them fire
        if (! e.getValueIsAdjusting()) {

            //fired selection event is currently processing
            autoAnnotationManager.setClusterTableUpdating(true);

            // Get the selected clusters from the selected rows
            //this will return the indices in the table of the selected clusters
            int[] selectedRows = this.table.getSelectedRows();

            HashSet<Integer> newSelection = new HashSet<Integer>();

            //iterator to store the tasks for this selection
            TaskIterator currentTasks = new TaskIterator();

            //for each of the selected rows, if it is already in selection do nothing
            //if it is new then select that cluster
            //Any clusters not in the selection deselect it
            for(int selectedRow : selectedRows){
                //is it in the current selection
                if(this.currentSelection.contains(selectedRow)) {
                    //add it to the new selection but do nothing
                    newSelection.add(selectedRow);
                }
                //new cluster selection
                if(!this.currentSelection.contains(selectedRow)){
                    //add it to the new selection
                    newSelection.add(selectedRow);
                    //select it
                    Cluster cluster = (Cluster) this.table.getModel().getValueAt(
                            this.table.convertRowIndexToModel(selectedRow), 0);
                    if(this.annotationSet.isManualSelection()){
                        //if there are multiple clouds selected at once there is no need to select the clouds as well because you can
                        //only view one cloud at a time.
                        if(selectedRows.length > 1) {
                            currentTasks.append(new SelectClusterTask(cluster,SelectClusterTask.SELECTCLUSTER_NONODES_NOCLOUD));
                        } else {
                            currentTasks.append(new SelectClusterTask(cluster,SelectClusterTask.SELECTCLUSTER_NONODES));
                        }
                    } else {
                        currentTasks.append(new SelectClusterTask(cluster,SelectClusterTask.SELECTCLUSTER_WITHNODES));
                    }
                }

            }

            //deselect all clusters in the previous selection and not in the new selection
            HashSet<Integer> difference = new HashSet<Integer>();
            difference.addAll(this.currentSelection);
            difference.removeAll(newSelection);
            for(Integer todeselect:difference){
                //it is possible that the item that needs to be deselected has been deleted from the table
                Cluster cluster;
                try{
                    cluster = (Cluster) this.table.getModel().getValueAt(this.table.convertRowIndexToModel(todeselect), 0);
                } catch(java.lang.IndexOutOfBoundsException e1){
                    continue;
                }

                if(cluster != null){
                    if(this.annotationSet.isManualSelection()) {
                        currentTasks.append(new SelectClusterTask(cluster,SelectClusterTask.DESELECTCLUSTER_NONODES));
                    } else {
                        currentTasks.append(new SelectClusterTask(cluster,SelectClusterTask.DESELECTCLUSTER_WITHNODES));
                    }
                }
            }

            //make the new selection the current selection
            this.currentSelection.clear();
            this.currentSelection.addAll(newSelection);

            CyNetwork selectedNetwork = autoAnnotationManager.getApplicationManager().getCurrentNetwork();

            //disable Heatmap revalidation while we are updating the annotation selections.
            /*	try {
				//EnrichmentMapManager.getInstance().getMap(selectedNetwork.getSUID()).getParams().setDisableHeatmapAutofocus(true);
				EnrichmentMapUtils.setOverrideHeatmapRevalidation(true);
			} catch (NullPointerException excep) {
				return;
			}
             */
            //If it was not a manual selection you need to wait till the task is done so it can finish selecting the nodes before it tries to
            //update the heatmap.
            if(!this.annotationSet.isManualSelection()){
                if(currentTasks.hasNext()){
                    Observer observer = new Observer();
                    autoAnnotationManager.getDialogTaskManager().execute(currentTasks,observer);

                    //wait until the selection is finished to avoid conflict with the rowsetlistener
                    while (!observer.isFinished()) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                //autoAnnotationManager.flushPayloadEvents();
            }
            else{
                if(currentTasks.hasNext()) {
                    autoAnnotationManager.getDialogTaskManager().execute(currentTasks);
                }
                this.annotationSet.setManualSelection(false);
            }
            //fired selection event is finished processing
            autoAnnotationManager.setClusterTableUpdating(false);
            //EnrichmentMapUtils.setOverrideHeatmapRevalidation(false);


        }

    }
}
