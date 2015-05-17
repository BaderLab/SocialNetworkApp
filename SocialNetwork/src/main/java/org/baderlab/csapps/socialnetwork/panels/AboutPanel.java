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

import java.awt.Insets;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.OpenBrowser;

/**
 * The Social Network app About panel
 *
 * @author Victor Kofia
 */
public class AboutPanel extends JDialog {

    private class HyperlinkAction implements HyperlinkListener {

        @SuppressWarnings("unused")
        JEditorPane pane;

        /**
         * Constructor for {@link HyperlinkAction}
         *
         * @param {@link JEditorPane} pane
         */
        public HyperlinkAction(JEditorPane pane) {
            this.pane = pane;
        }

        /**
         * Launch in browser if link is activated
         *
         * @param {@link HyperlinkEvent} event
         */
        public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                AboutPanel.this.browser.openURL(event.getURL().toString());
            }
        }

    }

    /**
     *
     */
    private static final long serialVersionUID = -4271722804183280880L;
    private CySwingApplication application;
    private OpenBrowser browser;

    /**
     * Constructor for {@link AboutPanel}
     *
     * @param {@link CySwingApplication} application
     * @param {@link OpenBrowser} browser
     */
    public AboutPanel(CySwingApplication application, OpenBrowser browser) {
        super(application.getJFrame(), "About Social Network App", false);
        this.application = application;
        this.browser = browser;
        setResizable(false);
        // Initialize instance of CytoscapeUtilities
        CytoscapeUtilities util = new CytoscapeUtilities();
        String pluginUrl = CytoscapeUtilities.pluginUrl;
        String pluginVersion = CytoscapeUtilities.pluginVersion;
        String pluginReleaseSuffix = CytoscapeUtilities.pluginReleaseSuffix;
        // Main panel for dialog box
        JEditorPane editorPane = new JEditorPane();
        editorPane.setMargin(new Insets(10, 10, 10, 10));
        editorPane.setEditable(false);
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.addHyperlinkListener(new HyperlinkAction(editorPane));
        URL logoURL = this.getClass().getResource("socialNetwork_logo.png");
        if (pluginReleaseSuffix != null && !pluginReleaseSuffix.contentEquals("")) {
            pluginReleaseSuffix = " (" + pluginReleaseSuffix + ")";
        }
        editorPane.setText("<html><body>" +
                // "<div style=\"float:right;\"><img height=\"77\" width=\"125\" src=\""+
                // logoURL.toString() +"\" ></div>" +
                "<table border='0'><tr>" + "<td width='125'></td>" + "<td width='200'>" + "<p align=center><b>Social Network App v" + pluginVersion
                + pluginReleaseSuffix + "</b><BR>"
                + "A Cytoscape App<BR>"
                + "<BR></p>"
                + "</td>"
                + "<td width='177'><div align='right'><img height='97' width='127' src=\""+
                  logoURL.toString() +"\" ></div></td>"+
                "</tr></table>" + "<p align=center>Social Network app is a method to visualize<BR>"
                + "and interpret social networks from publication records.<BR>" + "<BR>" + "by Victor Kofia, Ruth Isserlin and Gary Bader<BR>"
                + "(<a href='http://www.baderlab.org/'>Bader Lab</a>, University of Toronto)<BR>" + "<BR>" + "Plugin Homepage:<BR>" + "<a href='"
                + pluginUrl + "'>" + pluginUrl + "</a><BR>" + "<BR>" + "<font size='-1'>" + CytoscapeUtilities.buildId + "</font>"
                + "</p></body></html>");
        setContentPane(editorPane);
    }

}
