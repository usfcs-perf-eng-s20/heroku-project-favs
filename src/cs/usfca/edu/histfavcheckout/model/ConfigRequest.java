package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class ConfigRequest implements Serializable {
	private boolean ignoreExternalAPIs;
	
	public boolean getIgnoreExternalAPIs() {
		return ignoreExternalAPIs;
	}
}
