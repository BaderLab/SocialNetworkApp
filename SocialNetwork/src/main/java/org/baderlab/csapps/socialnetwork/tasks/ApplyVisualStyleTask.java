/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates,
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.tasks;

import java.util.Iterator;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.VisualStyles;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * For applying visual styles
 *
 * @author Victor Kofia
 */
public class ApplyVisualStyleTask extends AbstractTask {

    private VisualMappingManager vmmServiceRef;
    private TaskMonitor taskMonitor;

    /**
     * Main app manager
     */
    SocialNetworkAppManager appManager = null;

    /**
     * Create new apply visual style task
     *
     * @param vmmServiceRef
     * @param SocialNetworkAppManager appManager
     */
    public ApplyVisualStyleTask(VisualMappingManager vmmServiceRef,SocialNetworkAppManager appManager) {
        this.vmmServiceRef = vmmServiceRef;
        this.appManager = appManager;
    }

    /**
     * Get task monitor
     *
     * @return TaskMonitor taskMonitor
     */
    private TaskMonitor getTaskMonitor() {
        return this.taskMonitor;
    }

    /**
     * Return the visual style with the specified name in the set of all visual
     * styles. null is returned if no visual style is found.
     * 
     * @param String name
     * 
     * @return VisualStyle visualStyle
     */
    private VisualStyle getVisualStyle(String name) {
        Iterator<VisualStyle> it = this.vmmServiceRef.getAllVisualStyles().iterator();
        VisualStyle visualStyle = null;
        while (it.hasNext()) {
            visualStyle = it.next();
            if (visualStyle.getTitle().equalsIgnoreCase(name)) {
                break;
            }
            visualStyle = null;
        }
        return visualStyle;
    }

    /**
     * Apply selected network view
     *
     * @param TaskMonitor taskMonitor
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        this.setTaskMonitor(taskMonitor);
        VisualStyle visualStyle = null;
        String visualStyleName = null;
        SocialNetwork socialNetwork = this.appManager.getCurrentlySelectedSocialNetwork();
        switch (socialNetwork.getVisualStyleId()) {
            case Category.DEFAULT:
                visualStyleName = "Default";
                break;
            case VisualStyles.INCITES_VISUAL_STYLE:
                visualStyleName = String.format("%s_InCites", socialNetwork.getNetworkName());
                break;
            case VisualStyles.PUBMED_VISUAL_STYLE:
                visualStyleName = String.format("%s_PubMed", socialNetwork.getNetworkName());
                break;
            case VisualStyles.SCOPUS_VISUAL_STYLE:
                visualStyleName = String.format("%s_Scopus", socialNetwork.getNetworkName());
                break;
        }
        visualStyle = getVisualStyle(visualStyleName);
        if (visualStyle != null) {
            this.getTaskMonitor().setTitle(String.format("Loading %s Visual Style ... ", visualStyleName));
            this.getTaskMonitor().setProgress(0.0);
            this.getTaskMonitor().setStatusMessage("");
        }
        this.vmmServiceRef.setCurrentVisualStyle(visualStyle);
        return;
    }

    /**
     * Set task monitor
     *
     * @param TaskMonitor taskMonitor
     */
    private void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }

}
