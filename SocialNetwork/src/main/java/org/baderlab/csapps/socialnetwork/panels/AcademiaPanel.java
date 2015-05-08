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

package org.baderlab.csapps.socialnetwork.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.BasicCollapsiblePanel;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;

/**
 * Tools for building / working with the Academia Info-Panel
 *
 * @author Victor Kofia
 */
public class AcademiaPanel {
    /**
     * Reference to academia info panel
     */
    private JPanel academiaInfoPanelRef = null;
    /**
     * A reference to the faculty text field. Used to verify correct faculty
     * input.
     */
    private JTextField facultyTextFieldRef = new JTextField();
    /**
     * A reference to the load data text field. Used to verify correct file
     * path.
     */
    private JTextField pathTextFieldRef = new JTextField();
    /**
     * A reference to the max author threshold text field. Used to set the max
     * # of authors in a publication that the app will build a network out of.
     */
    private JTextField thresholdTextFieldRef = new JTextField();
    private JRadioButton incitesRadioButton = null;
    private JRadioButton scopusRadioButton = null;
    private JRadioButton thresholdRadioButton = null;

    /**
     * A reference to a data file. Used to verify correct file path.
     */
    private File selectedFileRef = null;

    /**
     * A reference to the app Manager
     */
    private SocialNetworkAppManager appManager = null;
    private FileUtil fileUtil = null;
    protected CySwingApplication cySwingAppRef = null;

    /**
     *
     * @param {@link SocialNetworkAppManager} appManager
     * @param {@link FileUtil} fileUtil
     * @param {@link CySwingApplication} cySwingAppRef
     */
    public AcademiaPanel(SocialNetworkAppManager appManager, FileUtil fileUtil, CySwingApplication cySwingAppRef) {
        super();
        this.appManager = appManager;
        this.fileUtil = fileUtil;
        this.cySwingAppRef = cySwingAppRef;
    }

    /**
     * Create academia info panel. In addition to Pubmed specific features, this
     * panel will also enable users to load Incites data.
     *
     * @return {@link JPanel} academiaInfoPanel
     */
    public JPanel createAcademiaInfoPanel() {
        JPanel academiaInfoPanel = new JPanel();

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BorderLayout());
        wrapperPanel.add(this.createDatabaseInfoPanel(), BorderLayout.NORTH);
        wrapperPanel.add(this.createAdvancedOptionsPanel(), BorderLayout.SOUTH);

        academiaInfoPanel.setLayout(new BorderLayout());
        academiaInfoPanel.setName("Academia");
        academiaInfoPanel.setBorder(BorderFactory.createTitledBorder("Academia"));
        academiaInfoPanel.add(wrapperPanel, BorderLayout.NORTH);
        // Set a reference to this panel for later access
        this.setAcademiaInfoPanelRef(academiaInfoPanel);

        return academiaInfoPanel;
    }

    /**
     * Create an advanced options panel that will enable users to have additional
     * control on how networks are generated. Hidden by default.
     *
     * @return {@link BasicCollapsiblePanel} advancedOptionsPanel
     */
    public BasicCollapsiblePanel createAdvancedOptionsPanel() {
        BasicCollapsiblePanel advancedOptionsPanel = new BasicCollapsiblePanel("Advanced Options");
        advancedOptionsPanel.setCollapsed(true);;
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        this.thresholdRadioButton = new JRadioButton("Set max authors per pub");
        this.thresholdRadioButton.setToolTipText("Set the maximum # of authors to be considered per publication. "
                + "Publications that exceed the threshold will be excluded.");
        this.thresholdRadioButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // TODO:
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    // TODO:
                }
            }
        });
        innerPanel.add(this.thresholdRadioButton);
        innerPanel.add(getThresholdTextFieldRef());
        advancedOptionsPanel.add(innerPanel);
        return advancedOptionsPanel;
    }

    /**
     * Create Database info panel. Allows user to load Incites or Scopus derived
     * data files
     *
     * @return {@link JPanel} databaseInfoPanel
     */
    public JPanel createDatabaseInfoPanel() {

        // Create new Database info panel.
        JPanel databaseInfoPanel = new JPanel();
        databaseInfoPanel.setBorder(BorderFactory.createTitledBorder("Database"));

        // Set layout
        databaseInfoPanel.setLayout(new BoxLayout(databaseInfoPanel, BoxLayout.Y_AXIS));

        // Add database panel
        databaseInfoPanel.add(createDatabasePanel());

        // Add load panel
        databaseInfoPanel.add(createLoadDataPanel());

        // Add faculty panel
        databaseInfoPanel.add(createSpecifyNetworkNamePanel());

        // Add 'create network button' to panel
        // Button wrapper added for cosmetic reasons
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.add(this.createNetworkButton(), BorderLayout.CENTER);
        databaseInfoPanel.add(buttonWrapper);

        return databaseInfoPanel;
    }

    /**
     * Create database panel
     *
     * @return {@link JPanel} databasePanel
     */
    public JPanel createDatabasePanel() {
        JPanel databasePanel = new JPanel();

        // Set bordered title
        databasePanel.setBorder(BorderFactory.createTitledBorder("Select Database"));

        // Organize panel horizontally.
        databasePanel.setLayout(new BoxLayout(databasePanel, BoxLayout.X_AXIS));

        // Create Incites radio button
        this.incitesRadioButton = new JRadioButton("Incites", true);
        this.incitesRadioButton.setFocusable(true);

        // Create Scopus radio button
        this.scopusRadioButton = new JRadioButton("Scopus", false);
        this.scopusRadioButton.setFocusable(false);

        // Ensures that only one button is selected at a time
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.incitesRadioButton);
        buttonGroup.add(this.scopusRadioButton);

        databasePanel.add(this.incitesRadioButton);
        databasePanel.add(this.scopusRadioButton);

        return databasePanel;

    }

    /**
     * Create load button. Load button loads data file onto Cytoscape for
     * parsing
     *
     * @return {@link JButton} load
     */
    public JButton createLoadButton() {

        JButton loadButton = new JButton("...");
        loadButton.setToolTipText("Load Incites / Scopus data");

        // Clicking of button results in the popping up of a dialog box that
        // implores the user
        // to select a new data file.
        loadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                // Use Cytoscape File util package for the file chooser instead
                // of Java's so
                // we navigate to last cytoscape known directory.
                // Filter - only enable txt, and excel files.
                FileChooserFilter filter1 = new FileChooserFilter("text file", "txt");
                FileChooserFilter filter2 = new FileChooserFilter("excel spreadsheet(xls)", "xls");
                FileChooserFilter filter3 = new FileChooserFilter("excel spreadsheet(xlsx)", "xlsx");
                FileChooserFilter filter4 = new FileChooserFilter("excel spreadsheet(csv)", "csv");
                HashSet<FileChooserFilter> filters = new HashSet<FileChooserFilter>();
                filters.add(filter1);
                filters.add(filter2);
                filters.add(filter3);
                filters.add(filter4);
                File textFile = AcademiaPanel.this.fileUtil.getFile(AcademiaPanel.this.cySwingAppRef.getJFrame(), "Data File Selection", FileUtil.LOAD, filters);

                setDataFile(textFile);
                getPathTextFieldRef().setText(textFile.getAbsolutePath());
                getFacultyTextFieldRef().setText(parseFileName(textFile.getAbsolutePath()));
            }
        });
        return loadButton;
    }

    /**
     * Create new load data panel. Allows user to specify path of desired data
     * file
     *
     * @return {@link JPanel} loadDataPanel
     */
    public JPanel createLoadDataPanel() {
        JPanel loadDataPanel = new JPanel();
        loadDataPanel.setBorder(BorderFactory.createTitledBorder("Load File"));
        loadDataPanel.setLayout(new BoxLayout(loadDataPanel, BoxLayout.X_AXIS));
        // Create new text field and set reference. Reference will be used later
        // on to verify
        // correct file path
        this.setLoadTextField(new JTextField());
        this.getPathTextFieldRef().setEditable(true);
        // Add text field
        loadDataPanel.add(this.getPathTextFieldRef());
        // Add load data button
        loadDataPanel.add(this.createLoadButton());
        return loadDataPanel;
    }

    /**
     * Create <i>CreateNetwork</i> button. When pressed, the <i>CreateNetwork</i> button
     * attempts to create a network out of a file specified by the user.
     *
     * @return {@link JButton} createNetworkButton
     */
    public JButton createNetworkButton() {
        JButton createNetworkButton = new JButton("Create Network");
        createNetworkButton.setToolTipText("Create network");
        createNetworkButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                // check to see which analysis type is selected
                if (AcademiaPanel.this.incitesRadioButton.isSelected()) {
                    AcademiaPanel.this.appManager.setAnalysis_type(SocialNetworkAppManager.ANALYSISTYPE_INCITES);
                }
                if (AcademiaPanel.this.scopusRadioButton.isSelected()) {
                    AcademiaPanel.this.appManager.setAnalysis_type(SocialNetworkAppManager.ANALYSISTYPE_SCOPUS);
                }

                if (!AcademiaPanel.this.incitesRadioButton.isSelected() && !AcademiaPanel.this.scopusRadioButton.isSelected()) {
                    CytoscapeUtilities.notifyUser("Please select a database");
                } else {
                    if (getSelectedFileRef() == null || getFacultyTextFieldRef().getText() == null) {
                        CytoscapeUtilities.notifyUser("Please select a file and/or specify network name.");
                    } else {
                        if (!getSelectedFileRef().getAbsolutePath().trim().equalsIgnoreCase(getPathTextFieldRef().getText().trim())) {
                            CytoscapeUtilities.notifyUser("Please select a file.");
                        } else if (getFacultyTextFieldRef().getText().trim().isEmpty()) {
                            CytoscapeUtilities.notifyUser("Please specify network name.");
                        } else {
                            try {
                                int maxAuthorThreshold = UserPanel.getValidThreshold(-1, thresholdIsSelected(),
                                        getThresholdTextFieldRef().getText());
                                AcademiaPanel.this.appManager.createNetwork(getSelectedFileRef(), maxAuthorThreshold);
                            } catch (FileNotFoundException e) {
                                CytoscapeUtilities.notifyUser(getPathTextFieldRef().getText() + " does not exist");
                            }
                        }
                    }
                }
            }
        });
        return createNetworkButton;
    }

    /**
     * Create specify network name panel.
     *
     * @return {@link JPanel} networkNamePanel
     */
    public JPanel createSpecifyNetworkNamePanel() {
        JPanel specifyNetworkNamePanel = new JPanel();
        specifyNetworkNamePanel.setBorder(BorderFactory.createTitledBorder("Specify Network Name"));
        specifyNetworkNamePanel.setLayout(new BoxLayout(specifyNetworkNamePanel, BoxLayout.X_AXIS));
        // Create new text field and set reference. Reference will be used later
        // on to verify
        // correct file path
        this.setFacultyTextFieldRef(new JTextField());
        getFacultyTextFieldRef().setEditable(true);
        // Add text field
        specifyNetworkNamePanel.add(getFacultyTextFieldRef());
        return specifyNetworkNamePanel;
    }

    /**
     * Get academia info panel reference
     *
     * @return {@link JPanel} academiaInfoPanelRef
     */
    public JPanel getAcademiaInfoPanelRef() {
        return this.academiaInfoPanelRef;
    }

    /**
     * Get faculty text field
     *
     * @return {@link JTextField} facultyTextField
     */
    public JTextField getFacultyTextFieldRef() {
        return this.facultyTextFieldRef;
    }

    /**
     * Get path text field
     *
     * @return {@link JTextField} pathTextField
     */
    public JTextField getPathTextFieldRef() {
        return this.pathTextFieldRef;
    }

    /**
     * Get selected data file
     *
     * @return {@link File} selectedFile
     */
    public File getSelectedFileRef() {
        return this.selectedFileRef;
    }

    /**
     *
     * @return {@link JTextField} thresholdTextFieldRef
     */
    public JTextField getThresholdTextFieldRef() {
        if (this.thresholdTextFieldRef == null) {
            setThresholdTextFieldRef(new JTextField("100"));
        }
        return this.thresholdTextFieldRef;
    }

    /**
     * Extract filename from path
     *
     * @param String path
     * @return String filename
     */
    public String parseFileName(String path) {
        Pattern pattern = Pattern.compile("([^\\\\/]+?)(\\.xlsx|\\.txt|\\.csv)$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "N/A";
    }

    /**
     * Set academia info panel reference
     *
     * @param {@link JPanel} academiaInfoPanelRef
     */
    public void setAcademiaInfoPanelRef(JPanel academiaInfoPanelRef) {
        this.academiaInfoPanelRef = academiaInfoPanelRef;
    }

    /**
     * Set selected data file
     *
     * @param {@link File} data
     */
    public void setDataFile(File selectedFile) {
        this.selectedFileRef = selectedFile;
    }

    /**
     * Set faculty text field
     *
     * @param {@link JTextField} facultyTextField
     */
    public void setFacultyTextFieldRef(JTextField facultyTextField) {
        this.facultyTextFieldRef = facultyTextField;
    }

    /**
     * Set path text field
     *
     * @param {@link JTextField} pathTextField
     */
    public void setLoadTextField(JTextField pathTextField) {
        this.pathTextFieldRef = pathTextField;
    }

    /**
     *
     * @param {@link JTextField} thresholdTextFieldRef
     */
    public void setThresholdTextFieldRef(JTextField thresholdTextFieldRef) {
        this.thresholdTextFieldRef = thresholdTextFieldRef;
    }

    /**
     * Return {@code true} iff user wants a threshold to be applied
     *
     * @return boolean
     */
    public boolean thresholdIsSelected() {
        if (this.thresholdRadioButton == null) {
            return false;
        } else {
            return this.thresholdRadioButton.isSelected();
        }
    }

}
