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

package org.baderlab.csapps.socialnetwork.model.visualstyles;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Contains attributes present in every visual style in use by the app. Every
 * social network visual style extends this class.
 *
 * @author risserlin
 */
public class BasicSocialNetworkVisualstyle {

    public static final String nodeattr_label = "Label";
    public static final String nodeattr_fname = "First Name";
    public static final String nodeattr_lname = "Last Name";
    public static final String nodeattr_inst = "Institution";
    public static final String nodeattr_pub = "Publications";
    public static final String nodeattr_numpub = "# of Publications";
    public static final String edgeattr_numcopubs = "# of copubs";
    public static final String nodeattr_timescited = "Times Cited";
    public static final String networkattr_totalPub = "Total Publications";

    private CyNetwork network;
    private SocialNetwork socialNetwork;

    /**
     * Apply an edge style to every edge in network.
     *
     * @param {@link CyNetwork} network
     * @param {@link SocialNetwork} socialNetwork
     */
    protected void applyEdgeStyle(CyNetwork network, SocialNetwork socialNetwork) {
        // Edge table reference
        CyTable edgeTable = network.getDefaultEdgeTable();
        // Edge width variables
        int minEdgeWidth = 0;
        int maxEdgeWidth = 0;
        // Specify EDGE_WIDTH
        edgeTable = network.getDefaultEdgeTable();
        CyColumn copubColumn = edgeTable.getColumn(edgeattr_numcopubs);
        ArrayList<Integer> copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
        maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
        socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, new Object[] { edgeattr_numcopubs, minEdgeWidth + 1, maxEdgeWidth });

        // Specify EDGE_TRANSPARENCY
        socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, new Object[] { edgeattr_numcopubs });
    }

    /**
     * Apply a node style to every node in network.
     *
     * @param {@link CyNetwork} network
     * @param {@link SocialNetwork} socialNetwork
     */
    protected void applyNodeStyle(CyNetwork network, SocialNetwork socialNetwork) {
        // Node table reference
        CyTable nodeTable = null;
        // Node size variables
        int minNodeSize = 0;
        int maxNodeSize = 0;
        socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, new Object[] { nodeattr_label });
        // Specify Node_SIZE
        nodeTable = network.getDefaultNodeTable();
        CyColumn timesCitedColumn = nodeTable.getColumn(nodeattr_timescited);
        ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
        minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
        maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
        socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, new Object[] { nodeattr_timescited, minNodeSize + 1, maxNodeSize });
    }

    /**
     * Apply a visual style to network.
     *
     * @param {@link CyNetwork} network
     * @param {@link SocialNetwork} socialNetwork
     */
    public void applyVisualStyle(CyNetwork network, SocialNetwork socialNetwork) {
        this.network = network;
        this.socialNetwork = socialNetwork;
        applyNodeStyle(this.network, this.socialNetwork);
        applyEdgeStyle(this.network, this.socialNetwork);
    }

    /**
     * Get largest value given cut-off point.
     *
     * @param List list
     * @param Double cutoff
     * @return Integer value
     */
    protected int getLargestInCutoff(ArrayList<Integer> list, Double cutoff) {
        Collections.sort(list);
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int value : list) {
            stats.addValue(value);
        }
        Double percentile = stats.getPercentile(cutoff);
        for (int i = list.size() - 1; i > -1; i--) {
            if (list.get(i) <= percentile) {
                return list.get(i);
            }
        }
        return list.get(list.size() - 1);
    }

    /**
     * Get smallest value given cutoff.
     *
     * @param List list
     * @param Double cutoff
     * @return Integer value
     */
    protected int getSmallestInCutoff(ArrayList<Integer> list, Double cutoff) {
        Collections.sort(list);
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int value : list) {
            stats.addValue(value);
        }
        Double percentile = stats.getPercentile(cutoff);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) >= percentile) {
                return list.get(i);
            }
        }
        return list.get(0);
    }

}
