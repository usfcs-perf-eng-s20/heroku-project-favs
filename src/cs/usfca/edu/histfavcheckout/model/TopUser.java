package cs.usfca.edu.histfavcheckout.model;

public class TopUser {
	
	private int userId;
	private long favsCount;
	private long checkoutsCount;
	private long ratingsCount;
	
	public TopUser() {}
	
	public TopUser(int userId, long favsCount, long checkoutsCount, long ratingsCount) {
		this.userId = userId;
		this.favsCount = favsCount;
		this.checkoutsCount = checkoutsCount;
		this.ratingsCount = ratingsCount;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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
