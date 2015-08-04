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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.VisualStyles;
import org.baderlab.csapps.socialnetwork.util.GenerateReports;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.util.swing.FileUtil;

/**
 * Main user panel for Social Network App
 *
 * @author Victor Kofia
 */
@SuppressWarnings("serial")
public class UserPanel extends JPanel implements CytoPanelComponent {

    private static final Logger logger = Logger.getLogger(UserPanel.class.getName());

    /**
     * Apply a threshold (if applicable) and create a network
     *
     * @param {@link SocialNetworkAppManager} appManager
     * @param {@code boolean} isSelected
     * @param String text
     * @param {@code int} categoryType
     */
    public static void createNetwork(SocialNetworkAppManager appManager, boolean isSelected, String thresholdText, String searchTerm, int categoryType) {
        int threshold = -1;
        if (isSelected) {
            if (!thresholdText.isEmpty() && Pattern.matches("[0-9]+", thresholdText)) {
                threshold = Integer.parseInt(thresholdText);
                if (threshold > 500) {
                    CytoscapeUtilities.notifyUser("Warning! The max author threshold has been "
                            + "set to a value greater than 500. This may lead to computer slowdown.");
                }
                appManager.createNetwork(searchTerm, categoryType, threshold);
            } else {
                CytoscapeUtilities.notifyUser("Illegal input for max threshold. Please specify a "
                        + "valid threshold value. Threshold must be a positive integer.");
                return;
            }
        } else {
            appManager.createNetwork(searchTerm, categoryType, threshold);
        }
    }

    /**
     * Returns a valid threshold iff user has set one
     *
     * @return int threshold
     */
    public static int getValidThreshold(boolean isSelected, String text) {
        int threshold = -1;
        if (isSelected) {
            String thresholdText = text;
            if (!thresholdText.isEmpty() && Pattern.matches("[0-9]+", thresholdText)) {
                threshold = Integer.parseInt(text);
                if (threshold > 500) {
                    CytoscapeUtilities.notifyUser("Warning! The max author threshold has been "
                            + "set to a value greater than 500. This may lead to computer slowdown.");
                }
            } else {
                CytoscapeUtilities.notifyUser("Illegal input for max threshold. Please specify a "
                        + "valid threshold value. Threshold must be a positive integer.");
            }
        }
        return threshold;
    }

    /**
     * Reference to control panel
     */
    private JPanel controlPanelRef = null;
    /**
     * Reference to the currently displayed info panel
     */
    private JPanel infoPanelRef = null;
    /**
     * Reference to network panel
     */
    private JPanel networkPanelRef = null;
    /**
     * Reference to network table
     */
    private JTable networkTableRef = null;
    /**
     * Reference to the search box. Necessary for extracting queries.
     */
    private JTextField searchBoxRef = null;
    /**
     * Search filter
     */
    private JComboBox<String> searchFilter = null;
    /**
     * Category that user has selected
     */
    private int selectedCategory = Category.DEFAULT;
    /**
     * Network that user has selected
     */
    private String selectedNetwork = null;
    /**
     * Visual style that user has selected
     */
    private int selectedVisualStyle = Category.DEFAULT;
    /**
     * Reference to top panel
     */
    private JPanel topPanelRef = null;
    /**
     * Reference to help button
     */
    private JButton helpButton = null;
    /**
     * Reference to visual style panel
     */
    private JPanel visualStylePanel = null;
    /**
     * Visual style selector
     */
    private JComboBox<String> visualStyleSelector = null;
    /**
     * The visual style selector type currently being displayed to the user
     */
    private int visualStyleSelectorType = Category.DEFAULT;

    /**
     * Reference to network summary panel
     */
    private JPanel networkSummaryPanelRef = null;

    /**
     * Reference to network summary pane
     */
    private JTextPane networkSummaryPaneRef = null;
    /**
     * Reference to network summary pane
     */
    private JTextPane fileSummaryPaneRef = null;
    /**
     * Reference to the main appManager
     */
    private SocialNetworkAppManager appManager = null;

    private FileUtil fileUtil = null;

    private CySwingApplication cySwingAppRef = null;

    /**
     * instance of academia panel
     */
    private AcademiaPanel academiaPanel = null;

    /**
     * Create a new panel
     */
    public UserPanel(SocialNetworkAppManager appManager, FileUtil fileUtil, CySwingApplication cySwingAppRef) {

        // Save a reference to this panel object
        this.appManager = appManager;
        this.fileUtil = fileUtil;
        this.cySwingAppRef = cySwingAppRef;

        this.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension((int) screenSize.getWidth() / 5, 200));

        // NOTE: An 'Academia' flavored UI has been set as the default
        this.setSelectedCategory(Category.ACADEMIA);

        // Add top panel
        this.topPanelRef = this.createTopPanel();
        this.add(this.topPanelRef, BorderLayout.NORTH);

        // Add the default info panel (Academia)
        this.academiaPanel = new AcademiaPanel(appManager, this.fileUtil, this.cySwingAppRef);
        this.setSelectedInfoPanel(this.academiaPanel.createAcademiaInfoPanel());
        this.add(this.infoPanelRef, BorderLayout.CENTER);

        // Add bottom panel
        this.add(this.createBottomPanel(), BorderLayout.SOUTH);

    }

    /**
     * Add a network to app's network panel
     *
     * @param CyNetwork network
     */
    public void addNetworkToNetworkPanel(SocialNetwork socialNetwork) {

        try {

            CyNetwork network = socialNetwork.getCyNetwork();
            String networkName = socialNetwork.getNetworkName();
            int networkType = socialNetwork.getNetworkType();

            // Create a new table and add network's info if there's none
            if (getNetworkTableRef() == null) {

                DefaultTableModel model = new DefaultTableModel();
                JTable networkTable = new JTable(model);
                networkTable.setEnabled(true);

                // Add columns to table
                for (String columnName : this.appManager.getNetworkTableColumnNames()) {
                    model.addColumn(columnName);
                }

                // Add row to table
                model.addRow(new Object[] { networkName, network.getNodeCount(), network.getEdgeCount(), Category.toString(networkType) });

                // Set table reference
                this.setNetworkTableRef(networkTable);

                // Context menu. Displayed to user on the event of a click (on
                // the
                // network table)
                final JPopupMenu destroyNetworkContextMenu = new JPopupMenu();
                destroyNetworkContextMenu.add(this.createDestroyNetworkMenuItem());

                networkTable.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {

                        // Clicking on a network once causes it to be selected
                        if (e.getClickCount() == 1) {

                            JTable target = (JTable) e.getSource();
                            int row = target.getSelectedRow();

                            // Get selected network name
                            String networkName = (String) getNetworkTableRef().getModel().getValueAt(row, 0);
                            setSelectedNetwork(networkName);
                            
                            // Change network visual style
                            // TODO: Temporarily disabled
                            //changeNetworkVisualStyle(networkName);
                            
                            // Show network view
                            UserPanel.this.appManager.setCurrentNetworkView(networkName);
                        }
                        // Right clicking on a network will bring up
                        // the destroy network context menu
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            // Display context menu to user with all associated
                            // options.
                            destroyNetworkContextMenu.show(getNetworkTableRef(), e.getX(), e.getY());
                        }
                    }
                });

                JScrollPane networkTablePane = new JScrollPane(this.getNetworkTableRef());
                networkTablePane.setPreferredSize(new Dimension(200, 100));
                this.getNetworkPanelRef().add(networkTablePane, BorderLayout.NORTH);

                this.setNetworkSummaryPanelRef(this.createNetworkSummaryPanel(socialNetwork));
                this.getNetworkPanelRef().add(this.getNetworkSummaryPanelRef(), BorderLayout.CENTER);

                // Add network info to table
            } else {
                DefaultTableModel networkTableModel = (DefaultTableModel) getNetworkTableRef().getModel();
                networkTableModel
                        .addRow(new Object[] { networkName, network.getNodeCount(), network.getEdgeCount(), Category.toString(networkType) });

            }

            this.updateNetworkSummaryPanel(socialNetwork);
            this.addNetworkVisualStyle(socialNetwork);

            this.getNetworkPanelRef().revalidate();
            this.getNetworkPanelRef().repaint();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser(String.format("An error occurred while adding %s to the network table", socialNetwork.getNetworkName()));
        }
    }

    /**
     * Add network's visual style to user panel
     *
     * @param Network network
     */
    public void addNetworkVisualStyle(SocialNetwork socialNetwork) {
        // int networkType = (socialNetwork == null) ? Category.DEFAULT :
        // socialNetwork.getNetworkType();
        // int visualStyleType = (socialNetwork == null) ?
        // VisualStyles.DEFAULT_VISUAL_STYLE :
        // socialNetwork.getDefaultVisualStyle();
        // String networkName = (socialNetwork == null) ? "DEFAULT" :
        // socialNetwork.getNetworkName();
        int visualStyleType = VisualStyles.DEFAULT_VISUAL_STYLE;
        String networkName = "DEFAULT";
        if (socialNetwork != null) {
            visualStyleType = socialNetwork.getDefaultVisualStyle();
            networkName = socialNetwork.getNetworkName();
        }
        // TODO: Visual styles panel disabled temporarily
        /*
        if (this.getVisualStylePanel() == null) {
            // Create new visual style panel
            this.setVisualStylePanel(this.createVisualStylePanel(networkName));
            // It is imperative that visual selector type be set before the
            // visual
            // selector. Not doing this will cause random & seemingly
            // untraceable
            // errors to occur.
            this.setVisualStyleSelectorType(visualStyleType);
            this.setVisualStyleSelector(this.createVisualStyleSelector(visualStyleType));
            this.getVisualStylePanel().add(this.getVisualStyleSelector());
            this.setVisualStyleHelpButton(this.createHelpButton());
            this.getVisualStylePanel().add(this.getVisualStyleHelpButton());
            this.getNetworkPanelRef().add(this.getVisualStylePanel(), BorderLayout.SOUTH);
        } else {
            this.changeNetworkVisualStyle(networkName);
        }
        */
    }

    /**
     * Change network visual style to one more suited to the specified network.
     * Assumes network is already loaded into Cytoscape.
     *
     * @param String networkName
     */
    public void changeNetworkVisualStyle(String networkName) {
        int visualStyleType = this.appManager.getSocialNetworkMap().get(networkName).getDefaultVisualStyle();
        TitledBorder visualStylePanelBorder = (TitledBorder) this.getVisualStylePanel().getBorder();
        if (networkName.length() >= 35) {
            networkName = networkName.substring(0, 34) + "...";
        }
        visualStylePanelBorder.setTitle(networkName + " Visual Styles");
        this.swapVisualStyleSelector(visualStyleType);
        this.getVisualStylePanel().revalidate();
        this.getVisualStylePanel().repaint();
    }

    /**
     * Create bottom panel. Bottom panel will contain main panel controls
     * (reset, close). As well as a network panel that will allow the user to
     * change network parameters.
     *
     * @return JPanel bottomPanel
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        this.setNetworkPanelRef(this.createNetworkPanel());
        bottomPanel.add(this.getNetworkPanelRef(), BorderLayout.NORTH);
        this.setControlPanelRef(this.createControlPanel());
        bottomPanel.add(this.getControlPanelRef(), BorderLayout.SOUTH);
        return bottomPanel;
    }

    /**
     * Create category option selector.
     *
     * @return JComboBox optionSelector
     */
    private JComboBox<String> createCategoryOptionSelector() {
        // Create new JComboBox
        JComboBox<String> categoryOptionSelector = new JComboBox<String>(Category.getCategoryList());
        categoryOptionSelector.setEditable(false);
        categoryOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryOptionSelector.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
                String category = (String) jcmbType.getSelectedItem();
                // Set selected category before performing info panel switch
                // NOTE: This step is imperative. Not doing this will
                // result in a null pointer exception being thrown.
                setSelectedCategory(Category.getCategoryID(category));
                // Perform info panel switch iff the panel being switched to is
                // distinct from the current panel
                if (!category.trim().equalsIgnoreCase(UserPanel.this.appManager.getUserPanelRef().getSelectedInfoPanel().getName())) {
                    UserPanel.this.appManager.getUserPanelRef().performInfoPanelSwitch();
                }
                // Create new search filter
                getSearchFilter().setModel(new DefaultComboBoxModel<String>(Category.getSearchFilterList(getSelectedCategory())));

            }

        });
        return categoryOptionSelector;
    }

    /**
     * Create close button. Close button closes current panel
     *
     * @return JButton closeButton
     */
    private JButton createCloseButton() {
        JButton closeButton = new JButton("Close");
        closeButton.setToolTipText("Close Social Network Panel");
        // Clicking of button results in the closing of current panel
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                UserPanel.this.appManager.closeUserPanel();
            }
        });
        return closeButton;
    }

    /**
     * Create new control panel for use in main app panel; will allow users to
     * reset or close the main app panel.
     *
     * @return JPanel control panel
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();

        controlPanel.setLayout(new FlowLayout());

        controlPanel.add(this.createResetButton());
        controlPanel.add(this.createCloseButton());

        return controlPanel;
    }

    /**
     * Create <i>DestroyMenuItem</i>. Function is self-evident.
     *
     * @return JMenuItem destroyNetworkMenuItem
     */
    public JMenuItem createDestroyNetworkMenuItem() {
        // Create new menu item.
        JMenuItem destroyNetworkMenuItem = new JMenuItem("Destroy Network");
        // Clicking of menu item results in the destruction of a network
        destroyNetworkMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                // Destroy currently selected network
                String selectedNetwork = getSelectedNetwork();
                UserPanel.this.appManager.destroyNetwork(UserPanel.this.appManager.getSocialNetworkMap().get(selectedNetwork).getCyNetwork());
            }
        });
        return destroyNetworkMenuItem;
    }

    /**
     * Create help button
     *
     * @return JButton helpButton
     */
    private JButton createHelpButton() {
        // URL iconURL =
        // Thread.currentThread().getContextClassLoader().getResource("help.png");
        URL iconURL = this.getClass().getClassLoader().getResource("help.png");
        ImageIcon iconSearch = new ImageIcon(iconURL);
        JButton helpButton = new JButton(iconSearch);
        helpButton.setBorder(null);
        helpButton.setContentAreaFilled(false);
        helpButton.setToolTipText("Visual Style Help");
        helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                switch (getVisualStyleSelectorType()) {
                    case VisualStyles.DEFAULT_VISUAL_STYLE:
                        help("Default visual style", VisualStyles.getHelpMessage(VisualStyles.DEFAULT_VISUAL_STYLE));
                        break;
                    case VisualStyles.INCITES_VISUAL_STYLE:
                        help("InCites Visual Style", VisualStyles.getHelpMessage(VisualStyles.INCITES_VISUAL_STYLE));
                        break;
                    case VisualStyles.PUBMED_VISUAL_STYLE:
                        help("PubMed Visual Style", VisualStyles.getHelpMessage(VisualStyles.PUBMED_VISUAL_STYLE));
                        break;
                    case VisualStyles.SCOPUS_VISUAL_STYLE:
                        help("Scopus Visual Style", VisualStyles.getHelpMessage(VisualStyles.SCOPUS_VISUAL_STYLE));
                        break;
                }
            }
        });
        return helpButton;
    }

    /**
     * Create new network panel; will allow lay users to easily modify network
     * parameters
     *
     * @return JPanel networkPanel
     */
    private JPanel createNetworkPanel() {
        JPanel networkPanel = new JPanel();
        networkPanel.setBorder(BorderFactory.createTitledBorder("Social Network"));
        networkPanel.setLayout(new BorderLayout());
        return networkPanel;
    }

    /**
     * Create network summary panel
     *
     * @param SocialNetwork socialNetwork
     * @return JPanel networkSummaryPanel
     */
    public JPanel createNetworkSummaryPanel(SocialNetwork socialNetwork) {
        // Create network summary panel
        JPanel networkSummaryPanel = new JPanel();
        networkSummaryPanel.setLayout(new BorderLayout());

        // Create network summary pane
        JTextPane networkSummaryPane = new JTextPane();
        networkSummaryPane.setContentType("text/html");
        networkSummaryPane.setEditable(false);
        networkSummaryPane.setAutoscrolls(true);
        this.setNetworkSummaryPaneRef(networkSummaryPane);
        JTextPane fileSummaryPane = new JTextPane();
        fileSummaryPane.setContentType("text/html");
        fileSummaryPane.setEditable(false);
        fileSummaryPane.setAutoscrolls(true);
        this.setFileSummaryPaneRef(fileSummaryPane);

        // add both textPanes to a Panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BorderLayout());
        summaryPanel.add(networkSummaryPane, BorderLayout.NORTH);
        summaryPanel.add(fileSummaryPane, BorderLayout.SOUTH);

        JScrollPane wrapperPane = new JScrollPane();
        wrapperPane.getViewport().add(summaryPanel);
        wrapperPane.setPreferredSize(new Dimension(50, 100));
        networkSummaryPanel.add(wrapperPane, BorderLayout.NORTH);

        return networkSummaryPanel;
    }

    /**
     * Create reset button. Reset button resets info panel. All form fills will
     * be cleared and info panel will revert to its natural undisturbed state
     *
     * @return JButton resetButton
     */
    private JButton createResetButton() {
        JButton resetButton = new JButton("Reset");
        resetButton.setToolTipText("Reset");
        // 'Reset' is achieved by creating a new panel
        // and replacing the current panel with the new
        // (Memory cost is trivial)
        resetButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                UserPanel.this.appManager.getUserPanelRef().performInfoPanelSwitch();
            }
        });
        return resetButton;
    }

    /**
     * Create new search box. Will allow user to search any social website
     *
     * @return JTextField searchBox
     */
    private JTextField createSearchBox() {
        // Create searchbox. Save a reference to it, and add
        JTextField searchBox = new JTextField();
        searchBox.setEditable(true);
        // Tapping enter results in the automatic generation of a network
        searchBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                if (getSearchBox().getText().trim().isEmpty()) {
                    CytoscapeUtilities.notifyUser("Please enter a search term into the search box");
                } else if (!isValidInput(getSearchBox().getText().trim())) {
                    CytoscapeUtilities.notifyUser("Illegal characters present. Please enter a valid search term.");
                } else {
                    createNetwork(UserPanel.this.appManager,
                    // getAcademiaPanel().thresholdIsSelected(),
                            true, // TODO: Suppose that the threshold radio
                                  // button is always selected
                            getAcademiaPanel().getThresholdTextAreaRef().getText().trim(), getSearchBox().getText().trim(), getSelectedCategory());
                }
            }
        });
        return searchBox;
    }

    /**
     * Create search button. Search button allows user to commit search. NOTE:
     * Button not 100% necessary. Its function can be duplicated by a simple tap
     * on the return key
     *
     * @return JButton search
     */
    private JButton createSearchButton() {
        // URL iconURL =
        // Thread.currentThread().getContextClassLoader().getResource("search.png");
        URL iconURL = this.getClass().getClassLoader().getResource("search.png");
        ImageIcon iconSearch = new ImageIcon(iconURL);
        JButton searchButton = new JButton(iconSearch);
        searchButton.setToolTipText("Search");
        searchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                if (getSearchBox().getText().trim().isEmpty()) {
                    CytoscapeUtilities.notifyUser("Please enter a search term");
                } else if (!isValidInput(getSearchBox().getText().trim())) {
                    CytoscapeUtilities.notifyUser("Illegal characters present. Please enter a valid search term.");
                } else {
                    createNetwork(UserPanel.this.appManager,
                    // getAcademiaPanel().thresholdIsSelected(),
                            true, // TODO: Suppose that the threshold radio
                                  // button is always selected
                            getAcademiaPanel().getThresholdTextAreaRef().getText().trim(), getSearchBox().getText().trim(), getSelectedCategory());
                }
            }
        });
        return searchButton;
    }

    /**
     * Create search option selector.
     *
     * @return JComboBox optionSelector
     */
    private JComboBox createSearchFilter() {
        JComboBox searchOptionSelector = new JComboBox(Category.getSearchFilterList(this.getSelectedCategory()));
        searchOptionSelector.setEditable(false);
        searchOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchOptionSelector.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // SEARCH FILTERS
                // TODO: (Work In Progress)
            }
        });
        return searchOptionSelector;
    }

    /**
     * Create new search panel. Will allow user to search for a particular
     * network. A default search filter will also be made available to the user.
     *
     * @return JPanel searchPanel
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();

        // Organize panel horizontally.
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        searchPanel.setBorder(BorderFactory.createTitledBorder("PubMed Search"));

        // Add search box to panel
        this.setSearchBox(this.createSearchBox());
        searchPanel.add(this.getSearchBox());

        // Add search button to panel
        searchPanel.add(this.createSearchButton());

        // Add search filter to panel
        /**
         * NOTE: DISABLED TEMPORARILY
         */
        // UserPanel.setSearchFilter(UserPanel.createSearchFilter());
        // searchPanel.add(UserPanel.getSearchFilter());

        return searchPanel;
    }

    /**
     * Create new top panel for use in main app panel. Top panel will contain
     * search box and category option selector.
     *
     * @return JPanel topPanel
     */
    private JPanel createTopPanel() {

        JPanel topPanel = new JPanel();

        topPanel.setLayout(new BorderLayout());
        /**
         * NOTE: DISABLED TEMPORARILY
         */
        // TODO: Enable at future date
        // topPanel.add(UserPanel.createCategoryPanel(), BorderLayout.NORTH);
        topPanel.add(this.createSearchPanel(), BorderLayout.SOUTH);

        return topPanel;
    }

    /**
     * Create visual style panel. Will allow the user to switch visual styles
     * for a particular network.
     *
     * @param String networkName
     * @return JPanel visualStylePanel
     */
    public JPanel createVisualStylePanel(String networkName) {
        JPanel visualStylePanel = new JPanel();
        visualStylePanel.setBorder(BorderFactory.createTitledBorder(networkName + " Visual Styles"));
        visualStylePanel.setLayout(new BoxLayout(visualStylePanel, BoxLayout.X_AXIS));
        return visualStylePanel;
    }

    /**
     * Create visual style selector. The type of selector created is dependent
     * on the network's type. Different networks have different custom visual
     * styles.
     *
     * @param int visualStyleSelectorType
     * @return JComboBox visualStyleSelector
     */
    public JComboBox<String> createVisualStyleSelector(int visualStyleSelectorType) {
        JComboBox<String> visualStyleSelector = new JComboBox<String>(new VisualStyles().getVisualStyleList(visualStyleSelectorType));
        visualStyleSelector.setEditable(false);
        visualStyleSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        visualStyleSelector.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
                String visualStyle = (String) jcmbType.getSelectedItem();
                int visualStyleID = new VisualStyles().getVisualStyleID(visualStyle);
                setSelectedVisualStyle(visualStyleID);
                UserPanel.this.appManager.applyVisualStyle(visualStyle);
            }
        });
        return visualStyleSelector;
    }

    /**
     * Get academia panel
     *
     * @return {@link AcademiaPanel} academiaPanel
     */
    public AcademiaPanel getAcademiaPanel() {
        return this.academiaPanel;
    }

    /**
     *
     * @return {@link Component} component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * Get control panel reference
     *
     * @return JPanel controlPanelRef
     */
    private JPanel getControlPanelRef() {
        return this.controlPanelRef;
    }

    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

    /**
     * Get network summary pane reference
     *
     * @return JTextPane fileSummaryPaneRef
     */
    public JTextPane getFileSummaryPaneRef() {
        return this.fileSummaryPaneRef;
    }

    /**
     * Get panel icon
     *
     * @return Icon panelIcon
     */
    public Icon getIcon() {
        URL iconURL = this.getClass().getResource("socialNetwork_logo_small.png");
        ImageIcon SNIcon = null;
        if (iconURL != null) {
            SNIcon = new ImageIcon(iconURL);
        }
        return SNIcon;
    }

    /**
     * Get network panel reference
     *
     * @return JPanel networkPanelRef
     */
    public JPanel getNetworkPanelRef() {
        return this.networkPanelRef;
    }

    /**
     * Get network summary panel reference
     *
     * @return JPanel networkSummaryPanel
     */
    private JPanel getNetworkSummaryPanelRef() {
        return this.networkSummaryPanelRef;
    }

    /**
     * Get network summary pane reference
     *
     * @return JTextPane networkSummaryPaneRef
     */
    public JTextPane getNetworkSummaryPaneRef() {
        return this.networkSummaryPaneRef;
    }

    /**
     * Get network table reference
     *
     * @return JTable networkTableRef
     */
    public JTable getNetworkTableRef() {
        return this.networkTableRef;
    }

    /**
     * Get user panel searchbox
     *
     * @return JTextField searchBox
     */
    public JTextField getSearchBox() {
        return this.searchBoxRef;
    }

    /**
     * Get search filter
     *
     * @return JComboBox searchFilter
     */
    private JComboBox<String> getSearchFilter() {
        return this.searchFilter;
    }

    /**
     * Get selected category
     *
     * @return int selectedCategory
     */
    public int getSelectedCategory() {
        return this.selectedCategory;
    }

    /**
     * Get current info panel
     *
     * @return JPanel currentInfoPanel
     */
    private JPanel getSelectedInfoPanel() {
        return this.infoPanelRef;
    }

    /**
     * Get selected network
     *
     * @return String network
     */
    public String getSelectedNetwork() {
        return this.selectedNetwork;
    }

    /**
     * Get selected visual style
     *
     * @return int visualStyle
     */
    private int getSelectedVisualStyle() {
        return this.selectedVisualStyle;
    }

    /**
     * Get panel title
     *
     * @return String panelTitle
     */
    public String getTitle() {
        return "Social Network";
    }

    /**
     * Get visual style help button
     *
     * @return JButton visualStyleHelpButton
     */
    public JButton getVisualStyleHelpButton() {
        return this.helpButton;
    }

    /**
     * Get visual style panel
     *
     * @return JPanel visualStylePanel
     */
    public JPanel getVisualStylePanel() {
        return this.visualStylePanel;
    }

    /**
     * Get visual style selector
     *
     * @return JComboBox visualStyleSelector
     */
    public JComboBox<String> getVisualStyleSelector() {
        return this.visualStyleSelector;
    }

    /**
     * Get visual style type
     *
     * @return int visualStyleType
     */
    public int getVisualStyleSelectorType() {
        return this.visualStyleSelectorType;
    }

    /**
     * Present help information to user in a pop-up dialog box
     *
     * @param String dialogTitle
     * @param String helpInfo
     */
    private void help(String dialogTitle, String helpInfo) {
        String formatting = "<html><body style='width: 300px'>";
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, formatting + helpInfo, dialogTitle, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Return true iff input does not contain illegal characters i.e. (!@#$%^&*)
     *
     * @param String input
     * @return boolean
     */
    private boolean isValidInput(String input) {
        Pattern pattern = Pattern.compile("[!@#$%^&*~]+?");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return false;
        }
        return true;
    }

    /**
     * Switch info panel to the new one that user's selected
     */
    private void performInfoPanelSwitch() {
        // Remove current info panel
        this.remove(this.getSelectedInfoPanel());
        switch (this.getSelectedCategory()) {
            case Category.DEFAULT:
                this.setSelectedInfoPanel(Category.createDefaultInfoPanel());
                break;
            case Category.ACADEMIA:
                this.setSelectedInfoPanel(this.academiaPanel.createAcademiaInfoPanel());
                break;
            case Category.TWITTER:
                this.setSelectedInfoPanel(Category.createTwitterInfoPanel());
                break;
            case Category.LINKEDIN:
                this.setSelectedInfoPanel(Category.createLinkedInInfoPanel());
                break;
            case Category.YOUTUBE:
                this.setSelectedInfoPanel(Category.createYoutubeInfoPanel());
                break;
        }
        // Add selected panel
        this.add(this.getSelectedInfoPanel(), BorderLayout.CENTER);
        // Refresh user panel to reflect update
        this.revalidate();
        this.repaint();
    }

    /**
     * Set academia panel
     *
     * @param {@link AcademiaPanel} academiaPanel
     */
    public void setAcademiaPanel(AcademiaPanel academiaPanel) {
        this.academiaPanel = academiaPanel;
    }

    /**
     * Set control panel reference
     *
     * @param JPanel controlPanelRef
     */
    private void setControlPanelRef(JPanel controlPanelRef) {
        this.controlPanelRef = controlPanelRef;
    }

    /**
     * Set network summary pane reference
     *
     * @param JTextPane networkSummaryPaneRef
     */
    public void setFileSummaryPaneRef(JTextPane fileSummaryPaneRef) {
        this.fileSummaryPaneRef = fileSummaryPaneRef;
    }

    /**
     * Set network panel reference
     *
     * @param JPanel networkPanelRef
     */
    private void setNetworkPanelRef(JPanel networkPanelRef) {
        this.networkPanelRef = networkPanelRef;
    }

    /**
     * Set network summary panel reference
     *
     * @param JPanel networkSummaryPanel
     */
    private void setNetworkSummaryPanelRef(JPanel networkSummaryPanel) {
        this.networkSummaryPanelRef = networkSummaryPanel;
    }

    /**
     * Set network summary pane reference
     *
     * @param JTextPane networkSummaryPaneRef
     */
    public void setNetworkSummaryPaneRef(JTextPane networkSummaryPaneRef) {
        this.networkSummaryPaneRef = networkSummaryPaneRef;
    }

    /**
     * Set network table reference
     *
     * @param JTable networkTableRef
     */
    public void setNetworkTableRef(JTable networkTableRef) {
        this.networkTableRef = networkTableRef;
    }

    /**
     * Set user panel searchbox
     *
     * @param JTextField searchBox
     */
    public void setSearchBox(JTextField searchBox) {
        this.searchBoxRef = searchBox;
    }

    /**
     * Set search filter
     *
     * @param JComboBox searchFilter
     */
    private void setSearchFilter(JComboBox<String> searchOptionSelector) {
        this.searchFilter = searchOptionSelector;
    }

    /**
     * Set selected category
     *
     * @param int category
     */
    private void setSelectedCategory(int category) {
        this.selectedCategory = category;
    }

    /**
     * Set current info panel
     *
     * @param JPanel infoPanel
     */
    private void setSelectedInfoPanel(JPanel infoPanel) {
        this.infoPanelRef = infoPanel;
    }

    /**
     * Set selected network
     *
     * @param String network
     */
    public void setSelectedNetwork(String network) {
        this.selectedNetwork = network;
    }

    /**
     * Set selected visual style
     *
     * @param int visualStyle
     */
    public void setSelectedVisualStyle(int visualStyle) {
        this.selectedVisualStyle = visualStyle;
    }

    /**
     * Set visual style help button
     *
     * @param JButton visualStyleHelpButton
     */
    public void setVisualStyleHelpButton(JButton visualStyleHelpButton) {
        this.helpButton = visualStyleHelpButton;
    }

    /**
     * Set visual style panel
     *
     * @param JPanel visualStylePanel
     */
    public void setVisualStylePanel(JPanel visualStylePanel) {
        this.visualStylePanel = visualStylePanel;
    }

    /**
     * Set visual style selector
     *
     * @param JComboBox visualStyleSelector
     */
    public void setVisualStyleSelector(JComboBox<String> visualStyleSelector) {
        this.visualStyleSelector = visualStyleSelector;
    }

    /**
     * Set visual style selector type.
     *
     * @param int visualStyleType
     */
    public void setVisualStyleSelectorType(int visualStyleType) {
        this.visualStyleSelectorType = visualStyleType;
    }

    /**
     * Swap visual style selector to given type
     *
     * @param int type
     */
    public void swapVisualStyleSelector(int visualStyleSelectorType) {
        if (this.getVisualStyleSelectorType() != visualStyleSelectorType) {
            this.getVisualStylePanel().remove(this.getVisualStyleSelector());
            this.getVisualStylePanel().remove(this.getVisualStyleHelpButton());
            this.setVisualStyleSelectorType(visualStyleSelectorType);
            this.setVisualStyleSelector(this.createVisualStyleSelector(this.getVisualStyleSelectorType()));
            this.getVisualStylePanel().add(this.getVisualStyleSelector());
            this.getVisualStylePanel().add(this.getVisualStyleHelpButton());
        }
    }

    /**
     * Update network summary panel (for Academia networks only)
     *
     * @param SocialNetwork socialNetwork
     */
    public void updateNetworkSummaryPanel(SocialNetwork socialNetwork) {
        String networkName = "DEFAULT", networkSummary = "N/A";
        if (socialNetwork != null) {
            networkName = socialNetwork.getNetworkName();
            if (networkName.length() >= 10) {
                networkName = networkName.substring(0, 9) + " ...";
            }
            networkSummary = socialNetwork.getNetworkSummary();
        }
        this.getNetworkSummaryPanelRef().setBorder(BorderFactory.createTitledBorder(networkName + " Summary"));
        this.getNetworkSummaryPaneRef().setText(networkSummary);
        this.getFileSummaryPaneRef().setText("");

        GenerateReports gr = new GenerateReports(socialNetwork);

        HashMap<String, String> files = new HashMap<String, String>();

        switch (socialNetwork.getNetworkType()) {
            case Category.INCITES:
                files = gr.createIncitesReports();
                break;
            case Category.PUBMED:
                if (socialNetwork.getExcludedPubs().size() > 0) {
                    files = gr.createPubmedReports();
                }
                break;
            case Category.SCOPUS:
                if (socialNetwork.getExcludedPubs().size() > 0) {
                    files = gr.createScopusReports();
                }
                break;
            default:
                break;
        }

        if (files.size() > 0) {
            Object[] keys = files.keySet().toArray();
            Arrays.sort(keys);

            StyledDocument doc = this.getFileSummaryPaneRef().getStyledDocument();
            SimpleAttributeSet attr = new SimpleAttributeSet();

            // Add a heading label
            JLabel header = new JLabel("List of summary files:");
            this.getFileSummaryPaneRef().insertComponent(header);
            try {
                doc.insertString(doc.getLength(), "\n", attr);
            } catch (BadLocationException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            // for each file add a clickable label to launch file in browser.
            for (Object key : keys) {
                JLabel websiteLabel = new JLabel();
                final File url = new File(files.get(key));
                websiteLabel.setText("<html><a href=\"\">" + key + "</a><br></html>");
                websiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                websiteLabel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop.getDesktop().open(url);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                this.getFileSummaryPaneRef().setCaretPosition(this.getFileSummaryPaneRef().getDocument().getLength());
                this.getFileSummaryPaneRef().insertComponent(websiteLabel);
                try {
                    doc.insertString(doc.getLength(), "\n", attr);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        }

        this.getNetworkSummaryPanelRef().revalidate();
        this.getNetworkSummaryPanelRef().repaint();
    }

}
