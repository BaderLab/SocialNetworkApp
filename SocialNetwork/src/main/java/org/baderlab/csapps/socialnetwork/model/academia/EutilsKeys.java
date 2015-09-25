package org.baderlab.csapps.socialnetwork.model.academia;

public class EutilsKeys {

	 private String queryKey = "";
	 private String webEnv = "";
	 private int retStart = 0;
	 private int retMax = 0;
	 private int totalPubs = 0;
	 
	public EutilsKeys(String queryKey, String webEnv, int retStart, int retMax,
			int totalPubs) {
		super();
		this.queryKey = queryKey;
		this.webEnv = webEnv;
		this.retStart = retStart;
		this.retMax = retMax;
		this.totalPubs = totalPubs;
	}
	
	public String getQueryKey() {
		return queryKey;
	}
	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}
	public String getWebEnv() {
		return webEnv;
	}
	public void setWebEnv(String webEnv) {
		this.webEnv = webEnv;
	}
	public int getRetStart() {
		return retStart;
	}
	public void setRetStart(int retStart) {
		this.retStart = retStart;
	}
	public int getRetMax() {
		return retMax;
	}
	public void setRetMax(int retMax) {
		this.retMax = retMax;
	}
	public int getTotalPubs() {
		return totalPubs;
	}
	public void setTotalPubs(int totalPubs) {
		this.totalPubs = totalPubs;
	}
	 
	 
	
}
