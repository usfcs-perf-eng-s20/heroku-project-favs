package cs.usfca.edu.histfavcheckout.externalapis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

import cs.usfca.edu.histfavcheckout.utils.Config;
import cs.usfca.edu.histfavcheckout.model.AuthResponse;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse;

public class APIClient {
	
	private static Request request = new Request();
	private static Gson gson = new Gson();
	
	public static SearchMoviesResponse getAllMovies(Set<Integer> movies) {
		URL url = request.url(Config.config.getSearchMoviesURL() + movieIds(movies));
		HttpURLConnection con = request.connect(url, "GET");
		
		String response = request.getResponse(con);
		if (response != null) {
			System.out.println("Received : " + response.toString());
			SearchMoviesResponse resp = gson.fromJson(response.toString(), SearchMoviesResponse.class);
			return resp;
		}
		else {
			System.out.println("No response from " + url.toString());
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
			return authResponse.isUserLoggedIn();
		}
		return false;
	}
}
