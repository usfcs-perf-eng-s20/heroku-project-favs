package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

public class SearchMoviesResponse {
	
	private List<MovieData> results;
	private boolean success;
	
	public void setResults(List<MovieData> results) {
		this.results = results;
	}
	
	public List<MovieData> getResults() {
		return results;
	}
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public MovieData newMovieData() {
		return new MovieData();
	}

	public static class MovieData{
		private String Title;
		private String Studio;
		private String Price;
		private String Rating;
		private String Year;
		private String Genre;
		private String Upc;
		private int ID;
		
		public String getTitle() {
			return Title;
		}
		public String getStudio() {
			return Studio;
		}
		public String getPrice() {
			return Price;
		}
		public String getRating() {
			return Rating;
		}
		public String getYear() {
			return Year;
		}
		public String getGenre() {
			return Genre;
		}
		public String getUpc() {
			return Upc;
		}
		public int getID() {
			return ID;
		}
		public void setTitle(String title) {
			Title = title;
		}
		public void setStudio(String studio) {
			Studio = studio;
		}
		public void setPrice(String price) {
			Price = price;
		}
		public void setRating(String rating) {
			Rating = rating;
		}
		public void setYear(String year) {
			Year = year;
		}
		public void setGenre(String genre) {
			Genre = genre;
		}
		public void setUpc(String upc) {
			Upc = upc;
		}
		public void setID(int iD) {
			ID = iD;
		}
	}
}
