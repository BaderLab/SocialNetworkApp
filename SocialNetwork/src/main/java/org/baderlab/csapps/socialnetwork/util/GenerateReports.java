package org.baderlab.csapps.socialnetwork.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;

/*
 * Given a set of publications generate all relevant reports and save the reports into a temporary directory
 * Reports:
 *  1. NetworkName_pubByLocation.txt
 *  2. NetworkName_citationByLoaction.txt
 *  3. NetworkName_pubByLocation_unique.txt
 *  4. NetworkName_citationByLoaction_unique.txt
 *  
 *  And the google chart representation of the above data
 *  5. NetworkName_pubByLocation.html
 *  6. NetworkName_citationByLoaction.html
 *  7. NetworkName_pubByLocation_unique.html
 *  8. NetworkName_citationByLoaction_unique.html
 */

public class GenerateReports {
	
	private ArrayList<Publication> publications= null;
	
	private String outputDir = "";
	private String networkName = "";

	//location -> number of publication
	private HashMap<String,Integer> pubByLocation = new HashMap<String, Integer>();
	//location -> number of publication (unique location for each publication
	private HashMap<String,Integer> pubByLocation_unique = new HashMap<String, Integer>();
	//location -> number of publication (count each distinct location for each publication once.
	//i.e. publication has a total of 10 authors, 2 from US, 5 from Canada and 3 international add publication count of 1 to US, Canada and international)
	private HashMap<String,Integer> pubByLocation_distinct = new HashMap<String, Integer>();
	//location -> number of total citations
	private HashMap<String,Integer> citationsByLocation = new HashMap<String, Integer>();
	//location -> number of total citations (unique location for each publication
	private HashMap<String,Integer> citationsByLocation_unique = new HashMap<String, Integer>();
	//location -> number of total citations (count each distinct location for each publication once. 
	//i.e. publication has a total of 10 authors, 2 from US, 5 from Canada and 3 international add publication count of 1 to US, Canada and international)
	private HashMap<String,Integer> citationsByLocation_distinct = new HashMap<String, Integer>();
		
	
	public GenerateReports(ArrayList<Publication> pubs, String outputDir,String networkName) {
		super();
		this.publications = pubs;
		this.networkName = networkName;
		this.outputDir = outputDir;
	}
	
	public GenerateReports(ArrayList<Publication> pubs, String networkName) {
		super();
		this.publications = pubs;
		this.networkName = networkName;
		//create an outputDir in the temp directory
		this.outputDir = System.getProperty("java.io.tmpdir");
	}
	
	
	public HashMap<String,String> createReports(){
	
		this.calculateTotals();
		this.calculateTotals_unique();
		this.calculateTotals_distinct();
		HashMap<String, String> filenames = new HashMap<String, String>();
		
		String basename = this.outputDir + System.getProperty("file.separator") + this.networkName;
		
		this.generateFiles(this.outputDir,this.networkName + "_pubByLocation_distinct", this.pubByLocation_distinct,"Publications by Location - counting publications once for every distinct location");
		filenames.put("1a. Pub by location(txt) - distinct locations publication counts", basename + "_pubByLocation_distinct.txt");
		filenames.put("1b. Pub by location(html) - distinct locations publication counts", basename + "_pubByLocation_distinct.html");		
		this.generateFiles(this.outputDir,this.networkName + "_pubByLocation", this.pubByLocation,"Publications by Location");
		filenames.put("2a. Pub by location(txt)", basename + "_pubByLocation.txt");
		filenames.put("2b. Pub by location(html)", basename + "_pubByLocation.html");
		this.generateFiles(this.outputDir,this.networkName + "_pubByLocation_unique", this.pubByLocation_unique,"Publications by Location - counting publications only once");
		filenames.put("3a. Pub by location(txt) - unique publication counts",basename + "_pubByLocation_unique.txt");
		filenames.put("3b. Pub by location(html) - unique publication counts", basename + "_pubByLocation_unique.html");
		this.generateFiles(this.outputDir,this.networkName + "_citationByLocation_distinct", this.citationsByLocation_distinct,"Citations by Location - counting citations once for every distinct location");
		filenames.put("4a. Citation by location(txt) - distinct locations citation count",basename + "_citationByLocation_distinct.txt");
		filenames.put("4b. Citation by location(html) - distinct locations citation counts", basename + "_citationByLocation_distinct.html");
		this.generateFiles(this.outputDir,this.networkName + "_citationByLocation", this.citationsByLocation,"Citations by Location");
		filenames.put("5a. Citation by location(txt)",basename + "_citationByLocation.txt");
		filenames.put("5b. Citation by location(html)", basename + "_citationByLocation.html");
		this.generateFiles(this.outputDir,this.networkName + "_citationByLocation_unique", this.citationsByLocation_unique,"Citations by Location - counting publications only once");
		filenames.put("6a. Citation by location(txt)- unique publication counts",  basename + "_citationByLocation_unique.txt");
		filenames.put("6b. Citation by location(html)- unique publication counts",  basename + "_citationByLocation_unique.html");
		
		return filenames;
	}
	
	/*
	 * for each publication in the set of publications for this network
	 * For each publication get its set of Authors.  For each Author get their location and add  
	 * a count to that location (publication counts will be duplicated based on this method)
	 * Do the same for the times cited
	 * Add up the publications based on location.
	 */
	private void calculateTotals(){
		HashMap<String,Integer> pubs_summary = new HashMap<String, Integer>();
		HashMap<String,Integer> citations_summary = new HashMap<String, Integer>();
		
		if(this.publications != null){
			for(Publication pub:this.publications){
				
				
				//for each publication the set of Authors
				for(Author author:pub.getAuthorList()){
									
					String current_location = pub.getLocation();
				
					if(pubs_summary.containsKey(current_location) && citations_summary.containsKey(current_location)){
						Integer pub_count = pubs_summary.get(current_location) +1;
						pubs_summary.put(current_location, pub_count);
						Integer cit_count = citations_summary.get(current_location) + pub.getTimesCited();
						citations_summary.put(current_location, cit_count);
					
					}
					else{
						pubs_summary.put(current_location, 1);
						citations_summary.put(current_location,pub.getTimesCited());
					}
				}
			}			
		}
		this.citationsByLocation = citations_summary;
		this.pubByLocation = pubs_summary;
		
	}
	/*
	 * for each publication in the set of publications for this network
	 * For each publication get its set of Authors.  For each Author get their location 
	 * For each unique location on the given publication add a count to that location
	 * (counts will be duplicated but only once per location for each publication.) 
	 * Do the same for the times cited
	 * Add up the publications based on distinct locations of a given publication.
	 */
	private void calculateTotals_distinct(){
		HashMap<String,Integer> pubs_summary = new HashMap<String, Integer>();
		HashMap<String,Integer> citations_summary = new HashMap<String, Integer>();
		
		if(this.publications != null){
			for(Publication pub:this.publications){
				
				
				//for each publication the set of Authors
				HashSet<String> all_locations = new HashSet<String>();
				for(Author author:pub.getAuthorList()){
									
					String current_location = author.getLocation();
					if(!all_locations.contains(current_location))  
						all_locations.add(current_location);
				}
				
				for(String current:all_locations){
					if(pubs_summary.containsKey(current) && citations_summary.containsKey(current)){
						Integer pub_count = pubs_summary.get(current) +1;
						pubs_summary.put(current, pub_count);
						Integer cit_count = citations_summary.get(current) + pub.getTimesCited();
						citations_summary.put(current, cit_count);
					
					}
					else{
						pubs_summary.put(current, 1);
						citations_summary.put(current,pub.getTimesCited());
					}
				}
			}			
		}
		this.citationsByLocation_distinct = citations_summary;
		this.pubByLocation_distinct = pubs_summary;
		
	}
	
	
	/*
	 * for each publication in the set of publications for this network
	 * Each publication is assigned a majority rules location
	 * Add up the publications based on location.
	 * Return hashamp of location --> total publications
	 */
	private void calculateTotals_unique(){
		HashMap<String,Integer> pubs_summary = new HashMap<String, Integer>();
		HashMap<String,Integer> citations_summary = new HashMap<String, Integer>();
		
		if(this.publications != null){
			for(Publication pub:this.publications){
				//for each publication get its most common location, add to counts
				//get author locations
				String current_location = pub.getLocation();
				
				if(pubs_summary.containsKey(current_location) && citations_summary.containsKey(current_location)){
					Integer pub_count = pubs_summary.get(current_location) +1;
					pubs_summary.put(current_location, pub_count);
					Integer cit_count = citations_summary.get(current_location) + pub.getTimesCited();
					citations_summary.put(current_location, cit_count);
					
				}
				else{
					pubs_summary.put(current_location, 1);
					citations_summary.put(current_location,pub.getTimesCited());
				}
			}			
		}
		this.pubByLocation_unique = pubs_summary;
		this.citationsByLocation_unique = citations_summary;
		

	}
	
	/*
	 * Given : a directory, file name and map
	 *  - generate an html file with header and footer
	 *  - and a text file with the information
	 *  
	 */
	private void generateFiles(String directory, String fileName, HashMap<String,Integer> data,String title){
		
		String googleChartHeader = "";
		String googleChartFooter = "";
		
		//load in the header and footer for the google charts files
				try {
					//read in header
					InputStream in = this.getClass().getResourceAsStream("googleChartheader.txt");
					
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String sCurrentLine = null;
					while((sCurrentLine = br.readLine()) != null) {
						googleChartHeader += sCurrentLine + "\n";						
					}
					
					//read in footer
					in = this.getClass().getResourceAsStream("googleChartfooter.txt");
					
					br = new BufferedReader(new InputStreamReader(in));
					sCurrentLine = null;
					while((sCurrentLine = br.readLine()) != null) {
						googleChartFooter += sCurrentLine + "\n";						
					}			
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					CytoscapeUtilities.notifyUser("Failed to load google chart template. FileNotFoundException.");
				} catch (IOException e) {
					e.printStackTrace();
					CytoscapeUtilities.notifyUser("Failed to load google chart template. IOException.");
				} 
				
				//replace the title in the base footer file with the one supplied
				googleChartFooter = googleChartFooter.replace("TITLEPLACEHOLDER", title);
				
				String base_name = directory + System.getProperty("file.separator") + fileName ;
				//generate html file
				try{
					
					PrintWriter html_writer = new PrintWriter(base_name +".html","UTF-8");
					PrintWriter txt_writer = new PrintWriter(base_name +".txt","UTF-8");
					
					//Add the html_header
					html_writer.println(googleChartHeader);
					
					for(Iterator<String> i = data.keySet().iterator();i.hasNext();){
						String current_key = (String)i.next();
						Integer current_value = data.get(current_key);
						if(i.hasNext())
							html_writer.println("['" +current_key+"'," + current_value + "],");
						else
							html_writer.println("['" +current_key+"'," + current_value + "]");
						txt_writer.println(current_key + "\t" + current_value);
					}
					
					html_writer.println(googleChartFooter);
					html_writer.close();
					txt_writer.close();
					
				}catch (IOException e) {
					e.printStackTrace();
					CytoscapeUtilities.notifyUser("Failed to create file. IOException. - " + base_name );
				} 
		//generate txt file
				
	}
		
}
