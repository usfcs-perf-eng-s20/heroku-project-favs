package cs.usfca.edu.histfavcheckout.externalapis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import cs.usfca.edu.histfavcheckout.model.AuthResponse;
import cs.usfca.edu.histfavcheckout.model.EDRRequest;
import cs.usfca.edu.histfavcheckout.model.MovieInfoCacheRepository;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse.MovieData;
import cs.usfca.edu.histfavcheckout.model.UserInfoResponse;
import cs.usfca.edu.histfavcheckout.utils.Config;

@Component
public class APIClient {
	
	@Autowired
	private MovieInfoCacheRepository movieRepository;
	
	private static Request request = new Request();
	private static Gson gson = new Gson();

	public SearchMoviesResponse getAllMovies(Set<Integer> movies) {
		if(!Config.config.getUseSearchAPIs()) {
			SearchMoviesResponse resp = new SearchMoviesResponse();
			resp.setResults(Collections.nCopies(movies.size(), mockMovie(movies.iterator().next())));
			return resp;
		}
		LinkedHashMap<Integer, SearchMoviesResponse.MovieData> cachedMovies = new LinkedHashMap<Integer, SearchMoviesResponse.MovieData>();
		movieRepository.findAllById(movies).forEach((movie) -> {
			cachedMovies.put(movie.getID(), movie);
		});
		if(movies.size() == cachedMovies.size()) {
			SearchMoviesResponse resp = new SearchMoviesResponse();
			resp.setResults(new LinkedList<MovieData>(cachedMovies.values()));
			return resp;
		}
		Set<Integer> restOfMovies = new HashSet<Integer>();
		for(Integer i : movies) {
			if(!cachedMovies.containsKey(i)) {
				restOfMovies.add(i);
			}
		}
		URL url = request.url(Config.config.getSearchMoviesURL() + generateRequestList(restOfMovies));
		HttpURLConnection con = request.connect(url, "GET");
		String response = request.getResponse(con);
		if (response != null) {
			SearchMoviesResponse resp = gson.fromJson(response.toString(), SearchMoviesResponse.class);
			if(resp != null && resp.getResults() != null) {
				List<SearchMoviesResponse.MovieData> result = resp.getResults();
				LinkedHashMap<Integer, SearchMoviesResponse.MovieData> receivedMovies = new LinkedHashMap<Integer, SearchMoviesResponse.MovieData>();
				for(SearchMoviesResponse.MovieData movieData : resp.getResults()) {
					receivedMovies.put(movieData.getID(), movieData);
				}
				movieRepository.saveAll(receivedMovies.values());
				resp.setResults(curateMovieResponse(movies, cachedMovies, receivedMovies));
				return resp;
			}
			else if(!cachedMovies.isEmpty()) {
				SearchMoviesResponse res = new SearchMoviesResponse();
				res.setResults(new LinkedList<MovieData>(cachedMovies.values()));
				return res;
			}
		}
		con.disconnect();
		return null;
	}
	
	private List<SearchMoviesResponse.MovieData> curateMovieResponse(Set<Integer> movies, 
			LinkedHashMap<Integer, SearchMoviesResponse.MovieData> cachedMovies, 
			LinkedHashMap<Integer, SearchMoviesResponse.MovieData> receivedMovies) {
		List<SearchMoviesResponse.MovieData> res = new LinkedList<SearchMoviesResponse.MovieData>();
		for(Integer i : movies) {
			if(cachedMovies.getOrDefault(i, null) != null) {
				res.add(cachedMovies.get(i));
			}
			else if(receivedMovies.getOrDefault(i, null) != null) {
				res.add(receivedMovies.get(i));
			}
		}
		System.out.println("Movies size: " + movies.size() + " CachedSize: " + cachedMovies.size() + " Received Movies: " + receivedMovies.size());
		return res;
	}

	public List<UserInfoResponse.UserInfo> getTopUsers(Set<Integer> userIds) {
		if(!Config.config.getUseLoginAPIs()) {
			return mockUsers(userIds);
		}
		URL url = request.url(Config.config.getUserInfoURL() + generateRequestList(userIds));
		HttpURLConnection con = request.connect(url, "GET");
		String response = request.getResponse(con);
		if (response != null) {
			UserInfoResponse users = gson.fromJson(response.toString(), UserInfoResponse.class);
			return users.getUsers();
		}
		con.disconnect();
		return null;
	}

	public String generateRequestList(Set<Integer> ids) {
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

	public boolean isAuthenticated(int userId) throws IOException {
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

	public boolean sendEDR(EDRRequest edrRequest) throws IOException {
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

	private MovieData mockMovie(int validMovieID) {
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

	private List<UserInfoResponse.UserInfo> mockUsers(Set<Integer> userIds) {
		List<UserInfoResponse.UserInfo> users = new LinkedList<UserInfoResponse.UserInfo>();
		for(int i : userIds) {
			UserInfoResponse.UserInfo user = new UserInfoResponse.UserInfo(i, "MockName", "mock@email.com");
			users.add(user);
		}
		return users;
	}


}