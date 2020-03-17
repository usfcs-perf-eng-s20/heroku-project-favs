package cs.usfca.edu.histfavcheckout.model;

public class AuthRequest {
	private int userId;
	
	public AuthRequest() {}
	
	public AuthRequest(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	
}
