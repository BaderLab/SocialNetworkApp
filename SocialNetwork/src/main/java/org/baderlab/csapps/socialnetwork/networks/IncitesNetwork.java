package main.java.org.baderlab.csapps.socialnetwork.networks;

public class IncitesNetwork extends SocialNetwork {
	
	private String facultyName = "";

	public IncitesNetwork(int networkType) {
		super(networkType);
	}

	/**
	 * Get faculty name
	 * @param null
	 * @return String facultyName
	 */
	public String getFacultyName() {
		return facultyName;
	}

	/**
	 * Set faculty name
	 * @param String facultyName
	 * @return null
	 */
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

}