package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Map creation starter code.
 * May be modified at developer's discretion. 
 * Primary goal here is to create a map where each key represents an institution
 * and each value the institution's geographic location. 
 * @author Victor Kofia
 */
public class MapCreator {

	/**
	 * Create location map
	 * @param String[] args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File a = new File("--INSERT PATH--");
		Scanner in = new Scanner(a);
		String line = "";
		String[] line_array = null;
		Map<String, String> locationMap = new HashMap<String, String>();
		String institution = "";
		
		System.out.println("\n\nONTARIO");
		while (in.hasNext()) {
			line = in.nextLine();
			line_array = line.split("\\t");
			institution = line_array[0].trim();
			System.out.println(institution);
			locationMap.put(institution, "Ontario");
		}
		
		a = new File("--INSERT PATH--");
		in = new Scanner(a);
		line = "";
		line_array = null;
		institution = "";
		
		System.out.println("\n\nUNITED STATES");
		while (in.hasNext()) {
			line = in.nextLine();
			line_array = line.split("\\t");
			institution = line_array[0].trim();
			System.out.println(institution);
			locationMap.put(institution, "United States");
		}
		
		a = new File("--INSERT PATH--");
		in = new Scanner(a);
		line = "";
		line_array = null;
		institution = "";
		
		System.out.println("\n\nINTERNATIONAL");
		while (in.hasNext()) {
			line = in.nextLine();
			line_array = line.split("\\t");
			institution = line_array[line_array.length - 1].trim();
			System.out.println(institution);
			locationMap.put(institution, "International");
		}

		locationMap.put("UNIV TORONTO", "UNIV TORONTO");
		
		FileOutputStream fout = new FileOutputStream("--INSERT PATH--");
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(locationMap);
		oos.close();
	}

}
