package org.baderlab.csapps.socialnetwork.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.session.events.SessionAboutToBeSavedEvent;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;

/**
 *
 * @author
 */
public class SaveStateFile implements SessionAboutToBeSavedListener {

    private SocialNetworkAppManager appManager = null;

    public SaveStateFile(SocialNetworkAppManager appManager) {
        super();
        this.appManager = appManager;
    }

    // Save app state in a file
    public void handleEvent(SessionAboutToBeSavedEvent e){

        String tmpDir = System.getProperty("java.io.tmpdir");
        File propFile = new File(tmpDir, "socialnetwork.props");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(propFile));
            for (String networkName : this.appManager.getSocialNetworkMap().keySet()) {
                writer.write(networkName);
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
