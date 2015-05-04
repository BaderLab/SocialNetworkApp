/**
 * Created by
 * User: arkadyark
 * Date: Jul 24, 2014
 * Time: 10:52:48 AM
 */
package org.baderlab.csapps.socialnetwork.autoannotate.task;

import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;

/**
 * @author arkadyarkhangorodsky
 *
 */
public class Observer implements TaskObserver {
    // Class to see when a task has finished
    private boolean allTasksFinished = false;

    public void allFinished(FinishStatus arg0) {
        this.allTasksFinished = true;
    }


    public boolean isFinished() {
        return this.allTasksFinished;
    }

    public void taskFinished(ObservableTask arg0) {
        //allTasksFinished = true;
    }

}
