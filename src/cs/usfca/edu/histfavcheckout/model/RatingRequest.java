package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class RatingRequest implements Serializable {
	
	private int userId;
	private int movieId;
	private int rating;
	
	public RatingRequest() {}
	
	public RatingRequest(int userId, int movieId, int rating) {
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
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

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	@Override
    public String toString() {
    	return "Rating: { userId: " + userId
    			+ ", movieId: " + movieId
    			+ ", ratingId: " + rating
    			+ "}";
    }
}
