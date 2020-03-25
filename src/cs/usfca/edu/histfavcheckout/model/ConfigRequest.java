package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class ConfigRequest implements Serializable {
	private boolean faves;
	private boolean search;
	private boolean login;
	private boolean analytics;
	
	public boolean getFaves() {
		return faves;
	}
	
	public boolean getSearch() {
		return search;
	}
	
	public boolean getLogin() {
		return login;
	}
	
	public boolean getAnalytics() {
		return analytics;
	}
}
