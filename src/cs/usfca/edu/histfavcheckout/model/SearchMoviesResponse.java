package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

public class SearchMoviesResponse {
	
	private List<MovieData> results;
	private boolean success;
	public List<MovieData> getResults() {
		return results;
	}
	public boolean isSuccess() {
		return success;
	}

	public class MovieData{
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
	}
}
