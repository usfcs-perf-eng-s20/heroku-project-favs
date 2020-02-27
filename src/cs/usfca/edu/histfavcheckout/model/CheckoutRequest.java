package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class CheckoutRequest implements Serializable {

	private int userId;
	private int movieId;
	
	public CheckoutRequest() {}
	
	public CheckoutRequest(int userId, int movieId) {
		this.userId = userId;
		this.movieId = movieId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	
	@Override
    public String toString() {
    	return "CheckoutRequest: { userId: " + userId
    			+ ", movieId: " + movieId
    			+ "}";
    }
	
}
