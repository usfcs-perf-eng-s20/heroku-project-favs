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
	
	public class Movie{
		public String movieName;
		public int movieID;
		public String checkoutDate;
		
		public Movie(String name, int Id, String checkoutDate) {
			this.movieName = name;
			this.movieID = Id;
			this.checkoutDate = checkoutDate;
		}
	}
}
