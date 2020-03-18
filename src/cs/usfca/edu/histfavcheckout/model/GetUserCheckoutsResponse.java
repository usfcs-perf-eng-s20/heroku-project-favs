package cs.usfca.edu.histfavcheckout.model;

import java.util.ArrayList;
import java.util.List;

public class GetUserCheckoutsResponse {
	
	public List<Movie> movies;
	
	public GetUserCheckoutsResponse() {
		movies = new ArrayList<>();
	}
	
	public void addMovie(Movie m) {
		movies.add(m);
	}
	
	public Movie newMovie(String name, int id, String checkoutDate) {
		return new Movie(name, id, checkoutDate);
	}
	
	public static class Movie {
		private String movieName;
		private int movieID;
		private String checkoutDate;
		
		public Movie(String name, int id, String checkoutDate) {
			this.movieName = name;
			this.movieID = id;
			this.checkoutDate = checkoutDate;
		}

		public String getMovieName() {
			return movieName;
		}

		public void setMovieName(String movieName) {
			this.movieName = movieName;
		}

		public int getMovieID() {
			return movieID;
		}

		public void setMovieID(int movieID) {
			this.movieID = movieID;
		}

		public String getCheckoutDate() {
			return checkoutDate;
		}

		public void setCheckoutDate(String checkoutDate) {
			this.checkoutDate = checkoutDate;
		}
		
		
	}
}
