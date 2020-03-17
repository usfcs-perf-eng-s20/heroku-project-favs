package cs.usfca.edu.histfavcheckout.model;

public class AuthResponse {
	private boolean userLoggedIn;
	
	public AuthResponse() {}
	
	public AuthResponse(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}

	public boolean isUserLoggedIn() {
		return userLoggedIn;
	}

	public void setUserLoggedIn(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}
	
}
