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

public class AboutPanel extends JDialog {
	
	 private CySwingApplication application;
	 private OpenBrowser browser;
	
	public AboutPanel(CySwingApplication application, OpenBrowser browser) {
		super(application.getJFrame(), "About Social Network App", false);
        this.application=application;
        this.browser = browser;
        setResizable(false);
        
        	//initialize instance of CytoscapeUtilities
        CytoscapeUtilities util = new CytoscapeUtilities();        
        
        String pluginUrl = CytoscapeUtilities.pluginUrl;
        String pluginVersion = CytoscapeUtilities.pluginVersion;
        String pluginReleaseSuffix = CytoscapeUtilities.pluginReleaseSuffix;

        //main panel for dialog box
        JEditorPane editorPane = new JEditorPane();
        editorPane.setMargin(new Insets(10,10,10,10));
        editorPane.setEditable(false);
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.addHyperlinkListener(new HyperlinkAction(editorPane));
        //URL logoURL = Enrichment_Map_Plugin.class.getResource("enrichmentmap_logo.png");
        URL logoURL = Thread.currentThread().getContextClassLoader().getResource("socialNetwork_logo.png");
        //URL logoURL = Enrichment_Map_Plugin.class.getResource("enrichmentmap_logo.png");
        if ( pluginReleaseSuffix != null && ! pluginReleaseSuffix.contentEquals(""))
            pluginReleaseSuffix = " (" + pluginReleaseSuffix + ")";
        editorPane.setText(
                "<html><body>"+
//                "<div style=\"float:right;\"><img height=\"77\" width=\"125\" src=\""+ logoURL.toString() +"\" ></div>" +
                "<table border='0'><tr>" +
                "<td width='125'></td>"+
                "<td width='200'>"+
                "<p align=center><b>Social Network App v" + pluginVersion + pluginReleaseSuffix + "</b><BR>" + 
                "A Cytoscape App<BR>" +
                "<BR></p>" +
                "</td>"+
//                "<td width='125'><div align='right'><img height='77' width='125' src=\""+ logoURL.toString() +"\" ></div></td>"+
                "</tr></table>" +
                "<p align=center>Social Network app is a method to visualize<BR>"+
                "and interpret social networks from publication records.<BR>" +
                "<BR>" +
                "by Victor Kofia, Ruth Isserlin and Gary Bader<BR>" +
                "(<a href='http://www.baderlab.org/'>Bader Lab</a>, University of Toronto)<BR>" +
                "<BR>" +
                "Plugin Homepage:<BR>" +
                "<a href='" + pluginUrl + "'>" + pluginUrl + "</a><BR>" +               
                "<BR>" +
                "<font size='-1'>" + CytoscapeUtilities.buildId + "</font>" +
                "</p></body></html>"
            );
        setContentPane(editorPane);
    }
	
	private class HyperlinkAction implements HyperlinkListener {
        @SuppressWarnings("unused")
        JEditorPane pane;

        public HyperlinkAction(JEditorPane pane) {
            this.pane = pane;
        }

        public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                
            		browser.openURL(event.getURL().toString());
            }
        }

	}
	
}
