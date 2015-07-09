package org.baderlab.csapps.socialnetwork;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class NotificationThread extends Thread {

    private String message = null;

    /**
     * ??
     * 
     * @param String message
     */
    // TODO: Write constructor description
    public NotificationThread(String message) {
        this.message = message;
    }

    public void run() {
        JOptionPane.showMessageDialog(new JPanel(), "<html><body style='width: 200px'>" + this.message);
    }

}