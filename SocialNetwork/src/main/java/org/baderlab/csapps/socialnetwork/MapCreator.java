package main.java.org.baderlab.csapps.socialnetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapCreator {

	public static void main(String[] args) throws IOException {
//		Author a = new Author("kofia;victor;", Category.FACULTY);
//		Author b = new Author("kofia;vincent;", Category.FACULTY);
//		Author c = new Author("kofia;arnold;", Category.FACULTY);
//		
//		Map<Author, Author> authorMap = new HashMap<Author, Author>();
//		authorMap.put(a, a);
//		authorMap.put(c, c);
//		
//		Consortium ha = new Consortium(c, a);
//		
//		authorMap.get(b).setFaculty("INFELICITY");
//		
//		AbstractNode node1 = ha.getNode1();
//		String node1Mess = "THIS IS NODE#1\n" +
//				"FACULTY: " + node1.nodeAttrMap.get("Faculty");
//		
//		AbstractNode node2 = ha.getNode2();
//		String node2Mess = "THIS IS NODE#2\n" +
//				"FACULTY: " + node2.nodeAttrMap.get("Faculty");
//
//		System.out.println(node1Mess + "\n\n" + node2Mess);
		
//		HashSet nasa = new HashSet();
//		nasa.add(a);
//		nasa.add(c);
//		System.out.println(nasa.contains(a));
		
		
//		File a = new File("/Users/viktorkofia/Desktop/locations.txt");
//		Scanner in = new Scanner(a);
//		String line = "";
//		String[] line_array = null;
//		in.nextLine();
//		Map<String, String> locationMap = new HashMap<String, String>();
//		String institution = "", location = "";
//		
//		while (in.hasNext()) {
//			line = in.nextLine();
//			line_array = line.split("\\t");
//			institution = line_array[0];
//			location = line_array[1];
//			locationMap.put(institution, location);
//		}
//		
//		locationMap.put("UNIV TORONTO", "UNIV TORONTO");
//		
//		FileOutputStream fout = new FileOutputStream("/Users/viktorkofia/Desktop/map.sn");
//		ObjectOutputStream oos = new ObjectOutputStream(fout);   
//		oos.writeObject(locationMap);
//		oos.close();
//		
//		System.out.println(locationMap.get("JAPAN ATOM ENERGY AGCY"));

		Pattern pattern = Pattern.compile("([^\\/]+?)(\\.xlsx|\\.txt)$");
		Matcher matcher = pattern.matcher("/Users/viktorkofia/Desktop/CCBR_Bibliometrics Network Data Outline Trial.xlsx");
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		}


	}

}
