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
import java.awt.Color;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
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
     * Reference to academia info panel. Shows information specific to academic
     * copublication networks.
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
     * A reference to the max author threshold text field. Used to set the max #
     * of authors in a publication that the app will build a network out of.
     */
    private JTextArea thresholdTextAreaRef = null;

    // TODO: Write description for these instance variables
    private JRadioButton incitesRadioButtonRef = null;
    private JRadioButton pubmedRadioButtonRef = null;
    private JRadioButton scopusRadioButtonRef = null;
    private JRadioButton thresholdRadioButtonRef = null;
    private JTextField startDateTextFieldRef = null;
    private JTextField endDateTextFieldRef = null;

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
     * Constructor for {@link AcademiaPanel}
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
     * Create academia info panel. In addition to PubMed specific features, this
     * panel will also enable users to load InCites data.
     *
     * @return {@link JPanel} academiaInfoPanel
     */
    public JPanel createAcademiaInfoPanel() {
        JPanel academiaInfoPanel = new JPanel();

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BorderLayout());
        wrapperPanel.add(this.createDatabaseInfoPanel(), BorderLayout.NORTH);
        //wrapperPanel.add(this.createAdvancedOptionsPanel(), BorderLayout.SOUTH);

        academiaInfoPanel.setLayout(new BorderLayout());
        academiaInfoPanel.setName("Academia");
        academiaInfoPanel.setBorder(BorderFactory.createTitledBorder("Academia"));
        academiaInfoPanel.add(wrapperPanel, BorderLayout.NORTH);
        // Set a reference to this panel for later access
        this.setAcademiaInfoPanelRef(academiaInfoPanel);

        return academiaInfoPanel;
    }

    /**
     * Create an advanced options panel that will enable users to have
     * additional control on how networks are generated. Hidden by default.
     *
     * @return {@link BasicCollapsiblePanel} advancedOptionsPanel
     */
    private BasicCollapsiblePanel createAdvancedOptionsPanel() {
        BasicCollapsiblePanel advancedOptionsPanel = new BasicCollapsiblePanel("Advanced Options");
        advancedOptionsPanel.setCollapsed(true);
        advancedOptionsPanel.add(this.createSpecifyMaxAuthorThresholdPanel(), BorderLayout.NORTH);
        advancedOptionsPanel.add(this.createSpecifyTimeIntervalPanel(), BorderLayout.SOUTH);
        return advancedOptionsPanel;
    }

    /**
     * Create Database info panel. Allows user to load InCites or Scopus derived
     * data files
     *
     * @return {@link JPanel} databaseInfoPanel
     */
    private JPanel createDatabaseInfoPanel() {

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

        databaseInfoPanel.add(createAdvancedOptionsPanel());
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
    private JPanel createDatabasePanel() {
        JPanel databasePanel = new JPanel();

        // Set bordered title
        databasePanel.setBorder(BorderFactory.createTitledBorder("Select Database"));

        // Organize panel horizontally.
        databasePanel.setLayout(new BoxLayout(databasePanel, BoxLayout.X_AXIS));

        // Create InCites radio button
        this.incitesRadioButtonRef = new JRadioButton("InCites", true);
        this.incitesRadioButtonRef.setFocusable(true);

        // Create PubMed radio button
        this.pubmedRadioButtonRef = new JRadioButton("PubMed", false);
        this.pubmedRadioButtonRef.setFocusable(true);

        // Create Scopus radio button
        this.scopusRadioButtonRef = new JRadioButton("Scopus", false);
        this.scopusRadioButtonRef.setFocusable(false);

        // Ensures that only one button is selected at a time
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.incitesRadioButtonRef);
        buttonGroup.add(this.pubmedRadioButtonRef);
        buttonGroup.add(this.scopusRadioButtonRef);

        databasePanel.add(this.incitesRadioButtonRef);
        databasePanel.add(this.pubmedRadioButtonRef);
        databasePanel.add(this.scopusRadioButtonRef);

        return databasePanel;

    }

    /**
     * Create load button. Load button loads data file onto Cytoscape for
     * parsing
     *
     * @return {@link JButton} load
     */
    private JButton createLoadButton() {

        JButton loadButton = new JButton("...");
        loadButton.setToolTipText("Load InCites / Scopus data");

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
                FileChooserFilter filter5 = new FileChooserFilter("pubmed report(xml)", "xml");
                HashSet<FileChooserFilter> filters = new HashSet<FileChooserFilter>();
                filters.add(filter1);
                filters.add(filter2);
                filters.add(filter3);
                filters.add(filter4);
                filters.add(filter5);
                File textFile = AcademiaPanel.this.fileUtil.getFile(AcademiaPanel.this.cySwingAppRef.getJFrame(), "Data File Selection",
                        FileUtil.LOAD, filters);

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
    private JPanel createLoadDataPanel() {
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
     * Create <i>CreateNetwork</i> button. When pressed, the
     * <i>CreateNetwork</i> button attempts to create a network out of a file
     * specified by the user.
     *
     * @return {@link JButton} createNetworkButton
     */
    private JButton createNetworkButton() {
        JButton createNetworkButton = new JButton("Create Network");
        createNetworkButton.setToolTipText("Create network");
        createNetworkButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                // check to see which analysis type is selected
                if (AcademiaPanel.this.incitesRadioButtonRef.isSelected()) {
                    AcademiaPanel.this.appManager.setAnalysis_type(SocialNetworkAppManager.ANALYSISTYPE_INCITES);
                }
                if (AcademiaPanel.this.pubmedRadioButtonRef.isSelected()) {
                    AcademiaPanel.this.appManager.setAnalysis_type(SocialNetworkAppManager.ANALYSISTYPE_PUBMED);
                }
                if (AcademiaPanel.this.scopusRadioButtonRef.isSelected()) {
                    AcademiaPanel.this.appManager.setAnalysis_type(SocialNetworkAppManager.ANALYSISTYPE_SCOPUS);
                }
                if (getSelectedFileRef() == null || getFacultyTextFieldRef().getText() == null) {
                    CytoscapeUtilities.notifyUser("Please select a file and/or specify network name.");
                } else {
                    if (!getSelectedFileRef().getAbsolutePath().trim().equalsIgnoreCase(getPathTextFieldRef().getText().trim())) {
                        CytoscapeUtilities.notifyUser("Please select a file.");
                    } else if (getFacultyTextFieldRef().getText().trim().isEmpty()) {
                        CytoscapeUtilities.notifyUser("Please specify network name.");
                    } else {
                        try {
                            int maxAuthorThreshold = UserPanel.getValidThreshold(true, getThresholdTextAreaRef().getText());
                            AcademiaPanel.this.appManager.createNetwork(getSelectedFileRef(), maxAuthorThreshold);
                        } catch (FileNotFoundException e) {
                            CytoscapeUtilities.notifyUser(getPathTextFieldRef().getText() + " does not exist");
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
    private JPanel createSpecifyNetworkNamePanel() {
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
     * Create and return the threshold panel. The threshold panel is located
     * under the Advanced Options collapsible panel. It allows users to specify
     * a threshold with which they can limit the number of authors in a
     * publication.
     * 
     * @return JPanel specifyMaxThresholdPanel
     */
    private JPanel createSpecifyMaxAuthorThresholdPanel() {
        JPanel thresholdPanel = new JPanel();
        thresholdPanel.setBorder(BorderFactory.createTitledBorder("Specify Max Author Per Pub"));
        thresholdPanel.setLayout(new BoxLayout(thresholdPanel, BoxLayout.X_AXIS));
        this.thresholdRadioButtonRef = new JRadioButton("Set max authors per pub");
        this.thresholdRadioButtonRef.setEnabled(true); // Set the ?? as true
        this.thresholdRadioButtonRef.setToolTipText("Set the maximum # of authors to be considered per publication. "
                + "Publications that exceed the threshold will be excluded.");
        this.thresholdRadioButtonRef.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // TODO:
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    // TODO:
                }
            }
        });
        // TODO: Hide the radio button to prevent users from disabling it
        // innerPanel.add(this.thresholdRadioButton);
        thresholdPanel.add(Box.createHorizontalStrut(5));
        thresholdPanel.add(getThresholdTextAreaRef());
        return thresholdPanel;
    }
    
    /**
     * Create and return the specify time interval panel. This panel is located
     * under the Advanced Options collapsible panel. It allows users to specify 
     * the time intervals (years) that will be considered when creating charts.
     * 
     * @return
     */
    private JPanel createSpecifyTimeIntervalPanel() {
        BasicCollapsiblePanel specifyTimeIntervalPanel = new BasicCollapsiblePanel("Time Interval");
        specifyTimeIntervalPanel.add(getStartDatePanel(), BorderLayout.NORTH);
        specifyTimeIntervalPanel.add(getEndDatePanel(), BorderLayout.SOUTH);
        return specifyTimeIntervalPanel;
    }
    
    private JPanel getStartDatePanel() {
        JPanel startDatePanel = new JPanel();
        startDatePanel.setLayout(new BoxLayout(startDatePanel, BoxLayout.X_AXIS));
        startDatePanel.add(Box.createHorizontalStrut(5));
        startDatePanel.add(new JLabel("Start Date"));
        startDatePanel.add(Box.createHorizontalStrut(5));
        startDatePanel.add(SocialNetworkAppManager.getStartDateTextFieldRef());
        return startDatePanel;
    }
    
    private JPanel getEndDatePanel() {
        JPanel endDatePanel = new JPanel();
        endDatePanel.setLayout(new BoxLayout(endDatePanel, BoxLayout.X_AXIS));
        endDatePanel.add(Box.createHorizontalStrut(5));
        endDatePanel.add(new JLabel("End Date"));
        endDatePanel.add(Box.createHorizontalStrut(5));
        endDatePanel.add(SocialNetworkAppManager.getEndDateTextFieldRef());
        return endDatePanel;
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
    private JTextField getPathTextFieldRef() {
        return this.pathTextFieldRef;
    }

    /**
     * Get selected data file
     *
     * @return {@link File} selectedFile
     */
    private File getSelectedFileRef() {
        return this.selectedFileRef;
    }

    /**
     * Get a reference to the threshold JTextArea
     *
     * @return {@link JTextArea} thresholdTextAreaRef
     */
    public JTextArea getThresholdTextAreaRef() {
        if (this.thresholdTextAreaRef == null) {
            JTextArea textArea = new JTextArea("500");
            Border border = BorderFactory.createLineBorder(Color.GRAY);
            textArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 5, 0, 5)));
            setThresholdTextAreaRef(textArea);
        }
        return this.thresholdTextAreaRef;
    }

    /**
     * Extract filename from path
     *
     * @param String path
     * @return String filename
     */
    private String parseFileName(String path) {
        Pattern pattern = Pattern.compile("([^\\\\/]+?)(\\.xlsx|\\.txt|\\.csv||\\.xml)$");
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
    private void setDataFile(File selectedFile) {
        this.selectedFileRef = selectedFile;
    }

    /**
     * Set faculty text field
     *
     * @param {@link JTextField} facultyTextField
     */
    private void setFacultyTextFieldRef(JTextField facultyTextField) {
        this.facultyTextFieldRef = facultyTextField;
    }

    /**
     * Set path text field
     *
     * @param {@link JTextField} pathTextField
     */
    private void setLoadTextField(JTextField pathTextField) {
        this.pathTextFieldRef = pathTextField;
    }

    /**
     *
     * @param {@link JTextArea} thresholdTextFieldRef
     */
    private void setThresholdTextAreaRef(JTextArea thresholdTextFieldRef) {
        this.thresholdTextAreaRef = thresholdTextFieldRef;
    }

    /**
     * Return {@code true} iff user wants a threshold to be applied
     *
     * @return boolean
     */
    public boolean thresholdIsSelected() {
        if (this.thresholdRadioButtonRef == null) {
            return false;
        } else {
            return this.thresholdRadioButtonRef.isSelected();
        }
    }

}
