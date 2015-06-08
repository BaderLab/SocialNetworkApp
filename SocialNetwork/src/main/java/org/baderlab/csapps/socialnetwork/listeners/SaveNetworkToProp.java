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

package org.baderlab.csapps.socialnetwork.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.session.events.SessionAboutToBeSavedEvent;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;

/**
 * Scans the session about to be saved for any social networks and
 * saves any important information to a property file that can be
 * accessed later on.
 *
 * @author Victor Kofia
 */
public class SaveNetworkToProp implements SessionAboutToBeSavedListener {

    private SocialNetworkAppManager appManager = null;

    /**
     * Constructor for {@link SaveNetworkToProp}
     *
     * @param {@link SocialNetworkAppManager} appManager
     */
    public SaveNetworkToProp(SocialNetworkAppManager appManager) {
        super();
        this.appManager = appManager;
    }

    /**
     * Invoked when a session is about to be saved
     *
     * @param {@link SessionAboutToBeSavedEvent} e
     */
    public void handleEvent(SessionAboutToBeSavedEvent e){

        String tmpDir = System.getProperty("java.io.tmpdir");
        File propFile = new File(tmpDir, "socialnetwork.props");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(propFile));
            // Save social networks in current session to file
            for (String networkName : this.appManager.getSocialNetworkMap().keySet()) {
                writer.write(networkName);
                writer.write("?"); // TODO: question mark being used as separator. Wise decision?
            }
            writer.newLine();
            for (SocialNetwork socialNetwork : this.appManager.getSocialNetworkMap().values()) {
                writer.write(Category.toString(socialNetwork.getNetworkType()));
                writer.write("?");
            }
            writer.newLine();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ArrayList<File> files = new ArrayList<File>();
        files.add(propFile);
        try {
            e.addAppFiles("socialnetwork", files);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
