package cs.usfca.edu.histfavcheckout.externalapis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;

import com.google.gson.Gson;

import cs.usfca.edu.histfavcheckout.utils.Config;
import cs.usfca.edu.histfavcheckout.model.AuthResponse;
import cs.usfca.edu.histfavcheckout.model.EDRRequest;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse.MovieData;

public class APIClient {
	
	private static Request request = new Request();
	private static Gson gson = new Gson();
	
	public static SearchMoviesResponse getAllMovies(Set<Integer> movies) {
		if(Config.config.getIgnoreExternalAPIs()) {
			//TODO: Log this: System.out.println("Mocking response from search API");
			SearchMoviesResponse resp = new SearchMoviesResponse();
			resp.setSuccess(true);
			resp.setResults(Collections.nCopies(movies.size(), mockMovie(movies.iterator().next())));
			return resp;
		}
		URL url = request.url(Config.config.getSearchMoviesURL() + movieIds(movies));
		HttpURLConnection con = request.connect(url, "GET");
		String response = request.getResponse(con);
		if (response != null) {
			//TODO: Log this: System.out.println("Received : " + response.toString());
			SearchMoviesResponse resp = gson.fromJson(response.toString(), SearchMoviesResponse.class);
			return resp;
		}
		con.disconnect();
		return null;
	}
	
	public static String movieIds(Set<Integer> movieIds) {
		StringBuilder result = new StringBuilder();
		List<Integer> list = new ArrayList<>();
		list.addAll(movieIds);
		for(int i=0; i<list.size() - 1; i++) {
			result.append(list.get(i));
			result.append(",");
		}
		result.append(list.get(list.size() - 1));
		return result.toString();
	}
	
	public static boolean isAuthenticated(int userId) throws IOException {
		URL url = new URL(Config.config.getAuthURL() + userId);
		HttpURLConnection con = request.connect(url, "GET");
		String response = request.getResponse(con);
		if(response != null) {
			AuthResponse authResponse = gson.fromJson(response, AuthResponse.class);
			con.disconnect();
			return authResponse.isUserLoggedIn();
		}
		con.disconnect();
		return false;
	}
	
	public static int sendEDR(EDRRequest edrRequest) throws IOException {
		URL url = new URL(Config.config.getAnalyticsURL());
		HttpURLConnection con = request.connect(url, "POST", MediaType.APPLICATION_JSON_VALUE, null);
		request.writeToBody(con, gson.toJson(edrRequest));
		int responseCode = con.getResponseCode();
		con.disconnect();
		return responseCode;
	}
	
	private static MovieData mockMovie(int validMovieID) {
		final MovieData m = new MovieData();
		m.setID(validMovieID);
		m.setGenre("Action");
		m.setPrice("10000");
		m.setRating("5.0");
		m.setStudio("Hist Fave Team Studios");
		m.setTitle("Mocking You");
		m.setUpc("AX637228");
		m.setYear("5500");
		return m;
	}
}
