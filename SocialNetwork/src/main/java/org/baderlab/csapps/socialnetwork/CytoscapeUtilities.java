package org.baderlab.csapps.socialnetwork;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JPanel;


/*
 * public utilities class with static methods used repeatedly by the code
 */
public class CytoscapeUtilities {

	 public static String buildId = "";
	 public static String pluginUrl = "";
	 public static String userManualUrl= "";
	 public static String pluginVersion = "";
	 public static String pluginReleaseSuffix ="";
	 public Properties build_props = new Properties();
	 public Properties plugin_props = new Properties();
	 public String pluginName = "";
	 
	 
	 public CytoscapeUtilities(){
		 
	    	try{
	    		this.plugin_props = getPropertiesFromClasspath("plugin.props", false);
	    	}
	    	catch(IOException ei){
	    		System.out.println("Neither of the configuration files could be found");
	    	}
	    		

			pluginUrl = this.plugin_props.getProperty("pluginURL", "http://baderlab.org/UserguideSocialNetworkApp");
			userManualUrl = pluginUrl + "/UserManual";
			pluginVersion = this.plugin_props.getProperty("pluginVersion","0.1");
			pluginReleaseSuffix = this.plugin_props.getProperty("pluginReleaseSuffix","");
			pluginName = this.plugin_props.getProperty("pluginName","SocialNetworkApp");

			// read buildId properties:
	        //properties available in revision.txt ( git.branch,git.commit.id, git.build.user.name, 
			//git.build.user.email, git.build.time, git.commit.id,git.commit.id.abbrev
			//, build.user,build.timestamp, build.os, build.java_version, build.number)
	        try {
	            this.build_props = getPropertiesFromClasspath("revision.txt",true);
	        } catch (IOException e) {
	            // TODO: write Warning "Could not load 'buildID.props' - using default settings"
	            this.build_props.setProperty("build.number", "0");
	            this.build_props.setProperty("git.commit.id", "0");
	            this.build_props.setProperty("build.user", "user");
	            //Enrichment_Map_Plugin.build_props.setProperty("build.host", "host");-->can't access with maven implementaion
	            this.build_props.setProperty("git.build.time", "1900/01/01 00:00:00 +0000 (GMT)");
	        }

	        this.buildId = "Build: " + this.build_props.getProperty("build.number") +
	                                        " from GIT: " + this.build_props.getProperty("git.commit.id") +
	                                        " by: " + this.build_props.getProperty("build.user")  ;




	 }
	 
	 private Properties getPropertiesFromClasspath(String propFileName, boolean inMaindir) throws IOException {
	        // loading properties file from the classpath
	        Properties props = new Properties();
	        InputStream inputStream;
	        
	        if(inMaindir)
	        		inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
	        else
	        		inputStream = this.getClass().getResourceAsStream(propFileName);

	        if (inputStream == null) {
	            throw new FileNotFoundException("property file '" + propFileName
	                    + "' not found in the classpath");
	            
	        }

	        props.load(inputStream);
	        return props;
	    }
	
	/**
	 * Notify user of an issue
	 * @param String message
	 * @return null
	 */
	public static  void notifyUser(String message) {
		JOptionPane.showMessageDialog(new JPanel(), "<html><body style='width: 200px'>" + message);
	}
	
}
