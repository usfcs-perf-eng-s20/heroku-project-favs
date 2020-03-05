package cs.usfca.edu.histfavcheckout.model;

public class TopRatedResponse {
	private String movieName;
	private int movieId;
	private float averageRating;
	
	public TopRatedResponse() {}
	
	public TopRatedResponse(String movieName, int movieId, float averageRating) {
		this.movieName = movieName;
		this.movieId = movieId;
		this.averageRating = averageRating;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

	public float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}
	
	
}
