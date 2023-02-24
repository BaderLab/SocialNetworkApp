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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.util.GenerateReports;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.util.swing.FileUtil;

/**
 * Main user panel for Social Network App
 *
 * @author Victor Kofia
 */
@SuppressWarnings("serial")
public class UserPanel extends JPanel implements CytoPanelComponent {

    private static final Logger logger = Logger.getLogger(UserPanel.class.getName());

    private JPanel controlPanel;
    private JPanel infoPanel;
    private JPanel networkPanel;
    private JTable networkTable;
    private JPanel networkSummaryPanel;
    private JTextPane networkSummaryPane;
    private JTextPane fileSummaryPane;
    private JButton closeButton;
    private JButton resetButton;
    
    private final AcademiaPanel academiaPanel;
    
    /** Category that user has selected */
    private int selectedCategory = Category.ACADEMIA;
    
    /** Network that user has selected */
    private String selectedNetwork;
    
    private final SocialNetworkAppManager appManager;
    private final FileUtil fileUtil;
    private final CySwingApplication cySwingAppRef;

    public UserPanel(SocialNetworkAppManager appManager, FileUtil fileUtil, CySwingApplication cySwingAppRef) {
        this.appManager = appManager;
        this.fileUtil = fileUtil;
        this.cySwingAppRef = cySwingAppRef;

        this.academiaPanel = new AcademiaPanel(appManager, this.fileUtil, this.cySwingAppRef);
        
        init();
    }
    
    @Override
	public String getTitle() {
        return "Social Network Input Panel";
    }
    
    @Override
	public Icon getIcon() {
        var url = getClass().getResource("socialNetwork_logo_small.png");
        ImageIcon icon = null;
        
        if (url != null)
            icon = new ImageIcon(url);
        
        return icon;
    }
    
    @Override
	public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }
    
    @Override
	public Component getComponent() {
        return this;
    }
    
    public AcademiaPanel getAcademiaPanel() {
        return academiaPanel;
    }
    
    public JTable getNetworkTable() {
        return networkTable;
    }

    public void addNetworkToNetworkPanel(SocialNetwork socialNetwork) {
        try {
            var network = socialNetwork.getCyNetwork();
            var networkName = socialNetwork.getNetworkName();
            int networkType = socialNetwork.getNetworkType();

            // Create a new table and add network's info if there's none
            if (getNetworkTable() == null) {
                var model = new DefaultTableModel();
                networkTable = new JTable(model);
                networkTable.setEnabled(true);

                // Add columns to table
                for (var columnName : appManager.getNetworkTableColumnNames()) {
                    model.addColumn(columnName);
                }

                // Add row to table
                model.addRow(new Object[] { networkName, network.getNodeCount(), network.getEdgeCount(), Category.toString(networkType) });

				// Context menu. Displayed to user on the event of a click (on the network table)
                var destroyNetworkContextMenu = new JPopupMenu();
                destroyNetworkContextMenu.add(createDestroyNetworkMenuItem());

                networkTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        // Clicking on a network once causes it to be selected
                        if (e.getClickCount() == 1) {
                            var target = (JTable) e.getSource();
                            int row = target.getSelectedRow();

                            // Get selected network name
                            selectedNetwork = (String) getNetworkTable().getModel().getValueAt(row, 0);
                            
                            // Change network visual style
                            // TODO: Temporarily disabled
                            //changeNetworkVisualStyle(selectedNetwork);
                            
                            // Show network view
                            appManager.setCurrentNetworkView(selectedNetwork);
                        }
                        
                        // Right clicking on a network will bring up the destroy network context menu
                        if (e.getButton() == MouseEvent.BUTTON3) // Display context menu to user with all associated options.
                            destroyNetworkContextMenu.show(getNetworkTable(), e.getX(), e.getY());
                    }
                });

                var networkTablePane = new JScrollPane(this.getNetworkTable());
                networkTablePane.setPreferredSize(new Dimension(200, 100));
                
                getNetworkPanel().add(networkTablePane, BorderLayout.NORTH);
                getNetworkPanel().add(getNetworkSummaryPanel(), BorderLayout.CENTER);
            } else {
                var networkTableModel = (DefaultTableModel) getNetworkTable().getModel();
                networkTableModel
                        .addRow(new Object[] { networkName, network.getNodeCount(), network.getEdgeCount(), Category.toString(networkType) });

            }

            updateNetworkSummaryPanel(socialNetwork);
            addNetworkVisualStyle(socialNetwork);

            getNetworkPanel().revalidate();
            getNetworkPanel().repaint();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred", e);
            CytoscapeUtilities.notifyUser(String.format("An error occurred while adding %s to the network table", socialNetwork.getNetworkName()));
        }
    }
    
	public void updateNetworkSummaryPanel(SocialNetwork socialNetwork) {
        String networkName = "DEFAULT", networkSummary = "N/A";
        
        if (socialNetwork != null) {
            networkName = socialNetwork.getNetworkName();
            
            if (networkName.length() >= 10)
                networkName = networkName.substring(0, 9) + " ...";
            
            networkSummary = socialNetwork.getNetworkSummary();
        }
        
        /*
        getNetworkSummaryPanel().setBorder(BorderFactory.createTitledBorder(networkName + " Summary"));
        */
        getNetworkSummaryPane().setText(networkSummary);
        getNetworkSummaryPane().revalidate();
        getNetworkSummaryPane().repaint();
        getFileSummaryPane().setText("");

        var files = new HashMap<String, String>();
        
        if (socialNetwork != null) {
        	var gr = new GenerateReports(socialNetwork);
	
	        switch (socialNetwork.getNetworkType()) {
	            case Category.INCITES:
	                files = gr.createIncitesReports();
	                break;
	            case Category.PUBMED:
	                if (socialNetwork.getExcludedPubs().size() > 0)
	                    files = gr.createPubmedReports();
	                break;
	            case Category.SCOPUS:
	                if (socialNetwork.getExcludedPubs().size() > 0)
	                    files = gr.createScopusReports();
	                break;
	            default:
	                break;
	        }
        }

        if (files.size() > 0) {
        	var keys = files.keySet().toArray();
            Arrays.sort(keys);

            var doc = getFileSummaryPane().getStyledDocument();
            var attr = new SimpleAttributeSet();

            // Add a heading label
            var header = new JLabel("List of summary files:");
            getFileSummaryPane().insertComponent(header);
            
            try {
                doc.insertString(doc.getLength(), "\n", attr);
            } catch (BadLocationException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            // for each file add a clickable label to launch file in browser.
            for (var key : keys) {
                var websiteLabel = new JLabel();
                var url = new File(files.get(key));
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
                getFileSummaryPane().setCaretPosition(getFileSummaryPane().getDocument().getLength());
                getFileSummaryPane().insertComponent(websiteLabel);
                
                try {
                    doc.insertString(doc.getLength(), "\n", attr);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        }

        getNetworkSummaryPanel().revalidate();
        getNetworkSummaryPanel().repaint();
    }

    /**
     * Add network's visual style to user panel
     */
    public void addNetworkVisualStyle(SocialNetwork socialNetwork) {
        // int networkType = (socialNetwork == null) ? Category.DEFAULT :
        // socialNetwork.getNetworkType();
        // int visualStyleType = (socialNetwork == null) ?
        // VisualStyles.DEFAULT_VISUAL_STYLE :
        // socialNetwork.getDefaultVisualStyle();
        // String networkName = (socialNetwork == null) ? "DEFAULT" :
        // socialNetwork.getNetworkName();
//        int visualStyleType = VisualStyles.DEFAULT_VISUAL_STYLE;
//        String networkName = "DEFAULT";
//        if (socialNetwork != null) {
//            visualStyleType = socialNetwork.getDefaultVisualStyle();
//            networkName = socialNetwork.getNetworkName();
//        }
        // TODO: Visual styles panel disabled temporarily
        /*
        if (getVisualStylePanel() == null) {
            // Create new visual style panel
            setVisualStylePanel(createVisualStylePanel(networkName));
            // It is imperative that visual selector type be set before the
            // visual
            // selector. Not doing this will cause random & seemingly
            // untraceable
            // errors to occur.
            setVisualStyleSelectorType(visualStyleType);
            setVisualStyleSelector(createVisualStyleSelector(visualStyleType));
            getVisualStylePanel().add(getVisualStyleSelector());
            setVisualStyleHelpButton(createHelpButton());
            getVisualStylePanel().add(getVisualStyleHelpButton());
            getNetworkPanelRef().add(getVisualStylePanel(), BorderLayout.SOUTH);
        } else {
            changeNetworkVisualStyle(networkName);
        }
        */
    }

    /**
     * Apply a threshold (if applicable) and create a network.
     */
	public static void createNetwork(
			SocialNetworkAppManager appManager,
			boolean isSelected,
			String thresholdText,
			String searchTerm,
			int categoryType
	) {
		int threshold = -1;

		if (isSelected) {
			if (!thresholdText.isEmpty() && Pattern.matches("[0-9]+", thresholdText)) {
				threshold = Integer.parseInt(thresholdText);

				if (threshold > 500)
					CytoscapeUtilities.notifyUser("Warning! The max author threshold has been "
							+ "set to a value greater than 500. This may lead to computer slowdown.");
				
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
	 * Returns a valid threshold if user has set one.
	 */
	public static int getValidThreshold(boolean isSelected, String text) {
		int threshold = -1;
		
		if (isSelected) {
			var thresholdText = text;
			
			if (!thresholdText.isEmpty() && Pattern.matches("[0-9]+", thresholdText)) {
				threshold = Integer.parseInt(text);
				
				if (threshold > 500)
					CytoscapeUtilities.notifyUser("Warning! The max author threshold has been "
							+ "set to a value greater than 500. This may lead to computer slowdown.");
			} else {
				CytoscapeUtilities.notifyUser("Illegal input for max threshold. Please specify a "
						+ "valid threshold value. Threshold must be a positive integer.");
			}
		}
		
		return threshold;
	}

	private void init() {
		var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) screenSize.getWidth() / 5, 200));

		setLayout(new BorderLayout());

		setSelectedInfoPanel(academiaPanel.createAcademiaInfoPanel());
		add(getSelectedInfoPanel(), BorderLayout.CENTER);

		// Add bottom panel
		var bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(getNetworkPanel(), BorderLayout.NORTH);
		bottomPanel.add(getControlPanel(), BorderLayout.SOUTH);

		add(bottomPanel, BorderLayout.SOUTH);
	}

	private JPanel getSelectedInfoPanel() {
		return infoPanel;
	}

	private void setSelectedInfoPanel(JPanel panel) {
		this.infoPanel = panel;
	}
    
    private JPanel getNetworkPanel() {
        if (networkPanel == null) {
        	networkPanel = new JPanel();
            // networkPanel.setBorder(BorderFactory.createTitledBorder("Social Network"));
        	networkPanel.setLayout(new BorderLayout());
            
            return networkPanel;
        }
        
        return networkPanel;
    }
    
    private JPanel getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new JPanel();
			controlPanel.setLayout(new FlowLayout());

			controlPanel.add(getResetButton());
			controlPanel.add(getCloseButton());
		}
        
        return controlPanel;
    }
    
    private JTextPane getFileSummaryPane() {
    	if (fileSummaryPane == null) {
    		fileSummaryPane = new JTextPane();
    		fileSummaryPane.setContentType("text/html");
    		fileSummaryPane.setEditable(false);
    		fileSummaryPane.setAutoscrolls(true);
    	}
    	
        return fileSummaryPane;
    }

    private JPanel getNetworkSummaryPanel() {
        if (networkSummaryPanel == null) {
        	// Create network summary panel
        	networkSummaryPanel = new JPanel();
        	networkSummaryPanel.setLayout(new BorderLayout());

            // add both textPanes to a Panel
            var summaryPanel = new JPanel();
            summaryPanel.setLayout(new BorderLayout());
            summaryPanel.add(getNetworkSummaryPane(), BorderLayout.NORTH);
            summaryPanel.add(getFileSummaryPane(), BorderLayout.SOUTH);

            var wrapperPane = new JScrollPane(summaryPanel);
            wrapperPane.setPreferredSize(new Dimension(50, 100));
            wrapperPane.getViewport().setBackground(networkSummaryPanel.getBackground());           
            wrapperPane.getViewport().getView().setBackground(networkSummaryPanel.getBackground());           
            
            networkSummaryPanel.add(wrapperPane, BorderLayout.CENTER);
        }
        
        return networkSummaryPanel;
    }

    private JTextPane getNetworkSummaryPane() {
    	if (networkSummaryPane == null) {
    		networkSummaryPane = new JTextPane();
    		networkSummaryPane.setContentType("text/html");
    		networkSummaryPane.setEditable(false);
    		networkSummaryPane.setAutoscrolls(true);
    	}
    	
        return networkSummaryPane;
    }
    
    private JButton getCloseButton() {
    	if (closeButton == null) {
	        closeButton = new JButton("Close");
	        closeButton.setToolTipText("Close Social Network Panel");
	        // Clicking of button results in the closing of current panel
	        closeButton.addActionListener(evt -> appManager.closeUserPanel());
    	}
        
        return closeButton;
    }
    
	/**
	 * Create reset button. Reset button resets info panel. All form fills will be
	 * cleared and info panel will revert to its natural undisturbed state
	 */
    private JButton getResetButton() {
    	if (resetButton == null) {
	        resetButton = new JButton("Reset");
	        resetButton.setToolTipText("Reset");
	        // 'Reset' is achieved by creating a new panel and replacing the current panel with the new one
	        resetButton.addActionListener(evt -> appManager.getUserPanelRef().performInfoPanelSwitch());
    	}
    	
        return resetButton;
    }

	private JMenuItem createDestroyNetworkMenuItem() {
		var destroyNetworkMenuItem = new JMenuItem("Destroy Network");
		destroyNetworkMenuItem.addActionListener(evt -> {
			// Destroy currently selected network
			var map = appManager.getSocialNetworkMap();
			var socialNetwork = map.get(selectedNetwork);
			appManager.destroyNetwork(socialNetwork.getCyNetwork());
		});

		return destroyNetworkMenuItem;
	}

    /**
     * Switch info panel to the new one that user's selected
     */
    private void performInfoPanelSwitch() {
        // Remove current info panel
        remove(getSelectedInfoPanel());
        
        switch (selectedCategory) {
            case Category.DEFAULT:
                setSelectedInfoPanel(Category.createDefaultInfoPanel());
                break;
            case Category.ACADEMIA:
                setSelectedInfoPanel(academiaPanel.createAcademiaInfoPanel());
                break;
            case Category.TWITTER:
                setSelectedInfoPanel(Category.createTwitterInfoPanel());
                break;
            case Category.LINKEDIN:
                setSelectedInfoPanel(Category.createLinkedInInfoPanel());
                break;
            case Category.YOUTUBE:
                setSelectedInfoPanel(Category.createYoutubeInfoPanel());
                break;
        }
        
        // Add selected panel
        add(getSelectedInfoPanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

//  /**
//  * Change network visual style to one more suited to the specified network.
//  * Assumes network is already loaded into Cytoscape.
//  */
// public void changeNetworkVisualStyle(String networkName) {
//     int visualStyleType = appManager.getSocialNetworkMap().get(networkName).getDefaultVisualStyle();
//     var visualStylePanelBorder = (TitledBorder) getVisualStylePanel().getBorder();
//     
//     if (networkName.length() >= 35)
//         networkName = networkName.substring(0, 34) + "...";
//     
//     visualStylePanelBorder.setTitle(networkName + " Visual Styles");
//     swapVisualStyleSelector(visualStyleType);
//     getVisualStylePanel().revalidate();
//     getVisualStylePanel().repaint();
// }
    
//    public void swapVisualStyleSelector(int newType) {
//        if (visualStyleSelectorType != newType) {
//            getVisualStylePanel().remove(getVisualStyleSelector());
//            getVisualStylePanel().remove(getVisualStyleHelpButton());
//            visualStyleSelectorType = newType;
//            visualStyleSelector = createVisualStyleSelector(visualStyleSelectorType);
//            getVisualStylePanel().add(getVisualStyleSelector());
//            getVisualStylePanel().add(getVisualStyleHelpButton());
//        }
//    }
  
//    private JButton helpButton;
//    private JPanel visualStylePanel;
//    private JComboBox<String> visualStyleSelector;
    
//    private JButton getVisualStyleHelpButton() {
//        return helpButton;
//    }
//
//    private JPanel getVisualStylePanel() {
//        return visualStylePanel;
//    }
//
//    private JComboBox<String> getVisualStyleSelector() {
//        return visualStyleSelector;
//    }

// /**
//  * Create category option selector.
//  *
//  * @return JComboBox optionSelector
//  */
// private JComboBox<String> createCategoryOptionSelector() {
//     // Create new JComboBox
//     JComboBox<String> categoryOptionSelector = new JComboBox<String>(Category.getCategoryList());
//     categoryOptionSelector.setEditable(false);
//     categoryOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
//     categoryOptionSelector.addActionListener(new ActionListener() {
//
//         @Override
//			public void actionPerformed(ActionEvent e) {
//             @SuppressWarnings("unchecked")
//             JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
//             String category = (String) jcmbType.getSelectedItem();
//             // Set selected category before performing info panel switch
//             // NOTE: This step is imperative. Not doing this will
//             // result in a null pointer exception being thrown.
//             setSelectedCategory(Category.getCategoryID(category));
//             // Perform info panel switch iff the panel being switched to is
//             // distinct from the current panel
//             if (!category.trim().equalsIgnoreCase(appManager.getUserPanelRef().getSelectedInfoPanel().getName())) {
//                 appManager.getUserPanelRef().performInfoPanelSwitch();
//             }
//             // Create new search filter
//             getSearchFilter().setModel(new DefaultComboBoxModel<String>(Category.getSearchFilterList(getSelectedCategory())));
//
//         }
//
//     });
//     return categoryOptionSelector;
// }

//  /** The visual style selector type currently being displayed to the user */
//  private int visualStyleSelectorType = Category.DEFAULT;
//  
//  /**
//   * Create visual style selector. The type of selector created is dependent
//   * on the network's type. Different networks have different custom visual styles.
//   */
//  @SuppressWarnings("unchecked")
//  private JComboBox<String> createVisualStyleSelector(int visualStyleSelectorType) {
//      var visualStyleSelector = new JComboBox<String>(new VisualStyles().getVisualStyleList(visualStyleSelectorType));
//      visualStyleSelector.setEditable(false);
//      visualStyleSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
//      visualStyleSelector.addActionListener(e -> {
//          var jcmbType = (JComboBox<String>) e.getSource();
//          var visualStyle = (String) jcmbType.getSelectedItem();
//          appManager.applyVisualStyle(visualStyle);
//      });
//      
//      return visualStyleSelector;
//  }
    
//	private void setSelectedCategory(int category) {
//		this.selectedCategory = category;
//	}
    
//    private JComboBox<String> searchFilter;
    
//    private JComboBox<String> getSearchFilter() {
//        return searchFilter;
//    }

//    private JButton createHelpButton() {
//        // URL iconURL =
//        // Thread.currentThread().getContextClassLoader().getResource("help.png");
//        URL iconURL = getClass().getClassLoader().getResource("help.png");
//        ImageIcon iconSearch = new ImageIcon(iconURL);
//        JButton helpButton = new JButton(iconSearch);
//        helpButton.setBorder(null);
//        helpButton.setContentAreaFilled(false);
//        helpButton.setToolTipText("Visual Style Help");
//        helpButton.addActionListener(new ActionListener() {
//
//            @Override
//			public void actionPerformed(ActionEvent event) {
//                switch (visualStyleSelectorType) {
//                    case VisualStyles.DEFAULT_VISUAL_STYLE:
//                        help("Default visual style", VisualStyles.getHelpMessage(VisualStyles.DEFAULT_VISUAL_STYLE));
//                        break;
//                    case VisualStyles.INCITES_VISUAL_STYLE:
//                        help("InCites Visual Style", VisualStyles.getHelpMessage(VisualStyles.INCITES_VISUAL_STYLE));
//                        break;
//                    case VisualStyles.PUBMED_VISUAL_STYLE:
//                        help("PubMed Visual Style", VisualStyles.getHelpMessage(VisualStyles.PUBMED_VISUAL_STYLE));
//                        break;
//                    case VisualStyles.SCOPUS_VISUAL_STYLE:
//                        help("Scopus Visual Style", VisualStyles.getHelpMessage(VisualStyles.SCOPUS_VISUAL_STYLE));
//                        break;
//                }
//            }
//        });
//        return helpButton;
//    }

//    private JComboBox<String> createSearchFilter() {
//        var searchOptionSelector = new JComboBox<String>(Category.getSearchFilterList(getSelectedCategory()));
//        searchOptionSelector.setEditable(false);
//        searchOptionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
//        searchOptionSelector.addActionListener(evt -> { /* TODO: (Work In Progress) */ });
//        
//        return searchOptionSelector;
//    }

    /*
     * Create new top panel for use in main app panel. Top panel will contain
     * search box and category option selector.
     */
//    private JPanel createTopPanel() {
//        JPanel topPanel = new JPanel();
//
//        topPanel.setLayout(new BorderLayout());
//        /**
//         * NOTE: DISABLED TEMPORARILY
//         *
//        // TODO: Enable at future date
//        // topPanel.add(UserPanel.createCategoryPanel(), BorderLayout.NORTH);
//        topPanel.add(createSearchPanel(), BorderLayout.SOUTH);
//
//        return topPanel;
//    }

//    /**
//     * Create visual style panel. Will allow the user to switch visual styles
//     * for a particular network.
//     */
//    private JPanel createVisualStylePanel(String networkName) {
//        var visualStylePanel = new JPanel();
//        visualStylePanel.setBorder(BorderFactory.createTitledBorder(networkName + " Visual Styles"));
//        visualStylePanel.setLayout(new BoxLayout(visualStylePanel, BoxLayout.X_AXIS));
//        return visualStylePanel;
//    }
    
//    /**
//     * Present help information to user in a pop-up dialog box
//     */
//    private void help(String dialogTitle, String helpInfo) {
//        var formatting = "<html><body style='width: 300px'>";
//        var frame = new JFrame();
//        JOptionPane.showMessageDialog(frame, formatting + helpInfo, dialogTitle, JOptionPane.QUESTION_MESSAGE);
//    }
}
