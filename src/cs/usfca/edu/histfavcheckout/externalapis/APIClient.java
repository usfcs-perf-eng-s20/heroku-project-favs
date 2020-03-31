package cs.usfca.edu.histfavcheckout.externalapis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;

import com.google.gson.Gson;

import cs.usfca.edu.histfavcheckout.utils.Config;
import cs.usfca.edu.histfavcheckout.model.AuthResponse;
import cs.usfca.edu.histfavcheckout.model.EDRRequest;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse.MovieData;
import cs.usfca.edu.histfavcheckout.model.UserInfoResponse;

public class APIClient {
	
	private static Request request = new Request();
	private static Gson gson = new Gson();
	
	public static SearchMoviesResponse getAllMovies(Set<Integer> movies) {
		if(!Config.config.getUseSearchAPIs()) {
			SearchMoviesResponse resp = new SearchMoviesResponse();
			resp.setSuccess(true);
			resp.setResults(Collections.nCopies(movies.size(), mockMovie(movies.iterator().next())));
			return resp;
		}
		URL url = request.url(Config.config.getSearchMoviesURL() + generateRequestList(movies));
		HttpURLConnection con = request.connect(url, "GET");
		String response = request.getResponse(con);
		if (response != null) {
			//TODO: Log this: System.out.println("Received : " + response.toString());
			SearchMoviesResponse resp = gson.fromJson(response.toString(), SearchMoviesResponse.class);
			if(resp.getResults() != null) {
				return resp;
			}
		}
		con.disconnect();
		return null;
	}
	
	public static List<UserInfoResponse.UserInfo> getTopUsers(Set<Integer> userIds) {
		if(!Config.config.getUseLoginAPIs()) {
			return mockUsers(userIds);
		}
		URL url = request.url(Config.config.getUserInfoURL() + generateRequestList(userIds));
		HttpURLConnection con = request.connect(url, "GET");
		String response = request.getResponse(con);
		if (response != null) {
			//TODO: Log this: System.out.println("Received : " + response.toString());
			UserInfoResponse users = gson.fromJson(response.toString(), UserInfoResponse.class);
			return users.getUsers();
		}
		con.disconnect();
		return null;
	}
	
	public static String generateRequestList(Set<Integer> ids) {
		StringBuilder result = new StringBuilder();
		if(!ids.isEmpty()) {
			List<Integer> list = new ArrayList<>();
			list.addAll(ids);
			int i = 0;
			for(; i<list.size() - 1; i++) {
				result.append(list.get(i));
				result.append(",");
			}
			result.append(list.get(i));
		}
		return result.toString();
	}
	
	public static boolean isAuthenticated(int userId) throws IOException {
		if(!Config.config.getUseLoginAPIs()) {
			return true;
		}
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
	
	public static boolean sendEDR(EDRRequest edrRequest) throws IOException {
		if(!Config.config.getUseAnalyticsAPIs()) {
			return true;
		}
		URL url = new URL(Config.config.getAnalyticsURL());
		HttpURLConnection con = request.connect(url, "POST", MediaType.APPLICATION_JSON_VALUE, null);
		request.writeToBody(con, gson.toJson(edrRequest));
		int responseCode = con.getResponseCode();
		con.disconnect();
		return responseCode == HttpURLConnection.HTTP_OK;
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
	
	private static List<UserInfoResponse.UserInfo> mockUsers(Set<Integer> userIds) {
		List<UserInfoResponse.UserInfo> users = new LinkedList<UserInfoResponse.UserInfo>();
		for(int i : userIds) {
			UserInfoResponse.UserInfo user = new UserInfoResponse.UserInfo(i, "MockName", "mock@email.com");
			users.add(user);
		}
		return users;
	}
	
	
}
