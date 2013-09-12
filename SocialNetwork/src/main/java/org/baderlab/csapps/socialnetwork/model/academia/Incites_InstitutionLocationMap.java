package org.baderlab.csapps.socialnetwork.model.academia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JRadioButton;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;


/**
 * Tools for manipulating Incites data
 * @author Victor Kofia
 */
public class Incites_InstitutionLocationMap {
	
	/**
	 * Author location map
	 */
	private  Map<String, String> locationMap = null;

	/**
	 * Location ranking map.
	 * <br> key: <i>location</i>
	 * <br> value: <i>rank</i>
	 */
	private  Map<String, Integer> locationRankingMap = null;
	
	/*
	 * Initialize the map with values from a file stored in the jar
	 * File is currently stored as binary hashmap
	 * TODO:Convert to text file so it is easily updatable.
	 */
	public Incites_InstitutionLocationMap(){
		if (this.locationMap == null) {
			try {
				InputStream in = this.getClass().getResourceAsStream("locationsmap.txt");
				this.locationMap = new HashMap<String,String>();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String sCurrentLine = null;
				while((sCurrentLine = br.readLine()) != null) {
					//tokenize the line
					String[] tokens = sCurrentLine.split("\t");
					//properly formed line
					if(tokens.length == 2){
						this.locationMap.put(tokens[0], tokens[1]);
					}
					else{
						System.out.println("misformed line in locationmap file\n \"" + sCurrentLine + "\n");
					}										
				}				
				/*ObjectInputStream ois = new ObjectInputStream(in);
				this.setLocationMap((Map<String, String>) ois.readObject());*/
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				CytoscapeUtilities.notifyUser("Failed to load location map. FileNotFoundException.");
			} catch (IOException e) {
				e.printStackTrace();
				CytoscapeUtilities.notifyUser("Failed to load location map. IOException.");
			} 
		}
	}
	
	/**
	 * Get location map
	 * @param null
	 * @return Map locationMap
	 */
	public  Map<String, String> getLocationMap() {		
		return this.locationMap;
	}
		

	/**
	 * Set location map
	 * @param Map locationMap
	 * @return null
	 */
	public  void setLocationMap(Map<String, String> locationMap) {
		this.locationMap = locationMap;
	}
	
	/**
	 * Set location ranking map
	 * @param Map locationRankingMap
	 * @return null
	 */
	private  void setLocationRankingMap(Map<String, Integer> map) {
		this.locationRankingMap = map;
	}
	
	/**
	 * Get location ranking map
	 * @param null
	 * @return Map locationRankingMap
	 */
	public  Map<String, Integer> getLocationRankingMap() {
		if (this.locationRankingMap == null) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			String[] locations = new String[] {"UNIV TORONTO", "Ontario", "Canada", 
                                                 "United States", "International", "Other"};
			for (int i = 0; i < 6; i++) {
				map.put(locations[i], 6 - i);
			}
			this.setLocationRankingMap(map);
		}
		return this.locationRankingMap;
	}


}