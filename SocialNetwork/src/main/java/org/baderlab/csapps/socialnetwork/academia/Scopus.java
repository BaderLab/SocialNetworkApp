package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
  
/**
 * Methods and fields for manipulating Scopus data
 * @author Victor Kofia
 */
public class Scopus {
 
	//private final String USER_AGENT = "Mozilla/5.0";
 
	public static void main(String[] args) throws Exception {
		Scopus http = new Scopus();
		System.out.println("Testing 1 - Send Http GET request");
		http.sendGet();
	}
 
	// HTTP GET request
	private void sendGet() throws Exception {
		String url = "http://api.elsevier.com/content/abstract/SCOPUS_ID:0027359827";
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
		// optional default is GET
		con.setRequestMethod("GET");
 
		// add request headers
		//con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("X-ELS-APIKey", "500b9b98416478bea7c01eff40c0ecea");
		con.setRequestProperty("X-ELS-ResourceVersion", "XOCS");
		con.setRequestProperty("Accept", "text/xml, application/atom+xml");

 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		System.out.println(con.getHeaderFields());
		System.out.println("X-ELS-STATUS:" + con.getHeaderFields().get("X-ELS-Status") + "\n");
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		// print result
		System.out.println(response.toString());
 
	}
 
}
