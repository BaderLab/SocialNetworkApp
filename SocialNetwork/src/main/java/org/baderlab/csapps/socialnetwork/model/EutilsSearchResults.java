package org.baderlab.csapps.socialnetwork.model;

public class EutilsSearchResults {
	private int retStart =0;
    private int retMax = 0;
    private String queryKey = "";
    private String webEnv = "";

    public EutilsSearchResults(int retStart, int retMax, String queryKey,
			String webEnv) {
		super();
		this.retStart = retStart;
		this.retMax = retMax;
		this.queryKey = queryKey;
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
    
    

}
