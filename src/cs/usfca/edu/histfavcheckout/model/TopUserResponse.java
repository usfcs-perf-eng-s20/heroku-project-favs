package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class TopUserResponse implements Serializable {
	private String userName;
	private String email;
	private long favsCount;
	private long checkoutsCount;
	private long ratingsCount;
	
	public TopUserResponse() {}
	
	public TopUserResponse(String userName, String email, long favsCount, long checkoutsCount, long ratingsCount) {
		this.userName = userName;
		this.email = email;
		this.favsCount = favsCount;
		this.checkoutsCount = checkoutsCount;
		this.ratingsCount = ratingsCount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getFavsCount() {
		return favsCount;
	}

	public void setFavsCount(long favsCount) {
		this.favsCount = favsCount;
	}

	public long getCheckoutsCount() {
		return checkoutsCount;
	}

	public void setCheckoutsCount(long checkoutsCount) {
		this.checkoutsCount = checkoutsCount;
	}

	public long getRatingsCount() {
		return ratingsCount;
	}

	public void setRatingsCount(long ratingsCount) {
		this.ratingsCount = ratingsCount;
	}

	
	
	
}
