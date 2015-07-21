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

package org.baderlab.csapps.socialnetwork.model.visualstyles.academia;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.visualstyles.AbstractVisualStyle;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Contains attributes present in every academia visual style in use by 
 * the app. Every academia visual style extends this class.
 *
 * @author risserlin
 */
public class BaseAcademiaVisualStyle extends AbstractVisualStyle {

    protected CyNetwork network = null;
    protected SocialNetwork socialNetwork = null;
    
    /**
     * ??
     * 
     * @param CyNetwork network
     * @param SocialNetwork socialNetwork
     */
    public BaseAcademiaVisualStyle(CyNetwork network, SocialNetwork socialNetwork) {
        this.network = network;
        this.socialNetwork = socialNetwork;
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.visualstyles.AbstractVisualStyle#applyEdgeStyle()
     */
    @Override
    protected void applyEdgeStyle() {
        
        // Edge table reference
        CyTable edgeTable = this.network.getDefaultEdgeTable();
        
        // Edge width variables
        int minEdgeWidth = 0;
        int maxEdgeWidth = 0;
        
        // Specify EDGE_WIDTH
        edgeTable = this.network.getDefaultEdgeTable();
        CyColumn copubColumn = edgeTable.getColumn(EdgeAttribute.NumCopublications.toString());
        ArrayList<Integer> copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        copubList = (ArrayList<Integer>) copubColumn.getValues(Integer.class);
        minEdgeWidth = getSmallestInCutoff(copubList, 5.0);
        maxEdgeWidth = getLargestInCutoff(copubList, 100.0);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_WIDTH, new Object[] { EdgeAttribute.NumCopublications.toString(), minEdgeWidth + 1, maxEdgeWidth });

        // Specify EDGE_TRANSPARENCY
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.EDGE_TRANSPARENCY, new Object[] { EdgeAttribute.NumCopublications.toString() });
    }

    /* (non-Javadoc)
     * @see org.baderlab.csapps.socialnetwork.model.visualstyles.AbstractVisualStyle#applyNodeStyle()
     */
    @Override
    protected void applyNodeStyle() {
        // Node table reference
        CyTable nodeTable = null;
        // Node size variables
        int minNodeSize = 0;
        int maxNodeSize = 0;
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_LABEL, new Object[] { NodeAttribute.Label.toString() });
        // Specify Node_SIZE
        nodeTable = this.network.getDefaultNodeTable();
        CyColumn timesCitedColumn = nodeTable.getColumn(NodeAttribute.TimesCited.toString());
        ArrayList<Integer> timesCitedList = (ArrayList<Integer>) timesCitedColumn.getValues(Integer.class);
        minNodeSize = getSmallestInCutoff(timesCitedList, 10.0);
        maxNodeSize = getLargestInCutoff(timesCitedList, 95.0);
        this.socialNetwork.getVisualStyleMap().put(BasicVisualLexicon.NODE_SIZE, new Object[] { NodeAttribute.TimesCited.toString(), minNodeSize + 1, maxNodeSize });
    }

    /**
     * Apply a visual style to network.

     */
    public void applyVisualStyle() {
        applyNodeStyle();
        applyEdgeStyle();
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
