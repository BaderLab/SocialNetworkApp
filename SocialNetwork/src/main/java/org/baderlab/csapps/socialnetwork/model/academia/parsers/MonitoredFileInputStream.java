package org.baderlab.csapps.socialnetwork.model.academia.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import org.cytoscape.work.TaskMonitor;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class MonitoredFileInputStream extends FileInputStream {

    /**
     * Progress bar variables
     */
    private int currentSteps = 0;
    private int totalSteps = 0;
    private double progress = 0.0;
    private TaskMonitor taskMonitor = null;

    /**
     * Create a new MonitoredFileInputStream
     * 
     * @param File file
     * @param TaskMonitor taskMonitor
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MonitoredFileInputStream(File file, TaskMonitor taskMonitor, String message) throws FileNotFoundException, IOException {
        super(file);
        this.taskMonitor = taskMonitor;
        setProgressMonitor(message, available());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileInputStream#read()
     */
    @Override
    public int read() throws IOException {
        return super.read();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileInputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileInputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    /**
     * Set progress monitor
     *
     * @param TaskMonitor taskMonitor
     * @param String taskName
     * @param int totalSteps
     */
    private void setProgressMonitor(String taskName, int totalSteps) {
        this.taskMonitor.setTitle(taskName);
        this.taskMonitor.setProgress(0.0);
        this.currentSteps = 0;
        this.totalSteps = totalSteps;
    }

    /**
     * Return progress as a percentage
     *
     * @param Double progress
     * @return String percentage
     */
    private String toPercent(double progress) {
        progress = progress * 100;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(progress) + "%";
    }

    /**
     * Update progress monitor
     *
     * @param int currentSteps
     */
    private void updateProgress() {
        this.currentSteps += 1;
        this.progress = (double) this.currentSteps / this.totalSteps;
        this.taskMonitor.setStatusMessage("Complete: " + toPercent(this.progress));
        this.taskMonitor.setProgress(this.progress);
    }

}
