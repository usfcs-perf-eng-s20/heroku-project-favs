package cs.usfca.edu.histfavcheckout.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Optional;


import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import cs.usfca.edu.histfavcheckout.externalapis.APIClient;
import cs.usfca.edu.histfavcheckout.model.ConfigRequest;
import cs.usfca.edu.histfavcheckout.model.GetUserCheckoutsResponse;
import cs.usfca.edu.histfavcheckout.model.GetUserCheckoutsResponse.Movie;
import cs.usfca.edu.histfavcheckout.model.OperationalResponse;
import cs.usfca.edu.histfavcheckout.model.Inventory;
import cs.usfca.edu.histfavcheckout.model.InventoryRepository;
import cs.usfca.edu.histfavcheckout.model.OperationalRequest;
import cs.usfca.edu.histfavcheckout.model.PrimaryKey;
import cs.usfca.edu.histfavcheckout.model.Product;
import cs.usfca.edu.histfavcheckout.model.ProductRepository;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse.MovieData;
import cs.usfca.edu.histfavcheckout.utils.Config;
import cs.usfca.edu.histfavcheckout.model.TopRatedResponse;
import cs.usfca.edu.histfavcheckout.model.TopUser;
import cs.usfca.edu.histfavcheckout.model.TopUserResponse;
import cs.usfca.edu.histfavcheckout.model.RatingRequest;
import cs.usfca.edu.histfavcheckout.model.RatingModel;
import cs.usfca.edu.histfavcheckout.model.User;
import cs.usfca.edu.histfavcheckout.model.UserInfoResponse;
import cs.usfca.edu.histfavcheckout.model.UserRepository;
import cs.usfca.edu.histfavcheckout.model.Favorites;
import cs.usfca.edu.histfavcheckout.model.FavesAndCheckOuts;


@Component
public class HistFavCheckoutHandler {
	
	public static int NUMBER_OF_DAYS_TO_BORROW = 15;
	public static int DEFAULT_NUM_OF_MOVIES = 10000;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private InventoryRepository inventoryRepository;
	
	public User addUser(PrimaryKey id) {
		return userRepository.save(new User(id));
	}
	
	public List<User> getUser(int userId) {
		return userRepository.findUserWithUserId(userId, Sort.by("id.productId"));
	}
	
	public ResponseEntity<?> addInventory(Inventory inventory) {
		Optional<Product> product = productRepository.findById(inventory.getProductId());
		if(!product.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request unsuccessful. Invalid movie Id.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(inventoryRepository.save(inventory));
	}
	
	public ResponseEntity<?> getInventory(int movieId) {
		Optional<Inventory> inv = inventoryRepository.findById(movieId);
		if(!inv.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record does not exist!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(inv.get());
	}

	public ResponseEntity<?> addMovie(Product movie) {
		Optional<Product> product = productRepository.findById(movie.getId());
		if(product.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie has been added previously.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(movie));
	}
	
	public ResponseEntity<?> getMovie(int movieId) {
		Optional<Product> product = productRepository.findById(movieId);
		if(!product.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie does not exist!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(product.get());
	}
	
	public ResponseEntity<?> getTopFavs(int page, int nums) {
		List<Product> products = productRepository.findTopN(PageRequest.of(page, nums, Sort.by("numberOfFavorites").descending()));
		return ResponseEntity.status(HttpStatus.OK).body(products);		
	}
	
	public ResponseEntity<?> getCheckouts(int userId, int page, int nums) {
		List<User> userCheckedOutMovies = userRepository.findCheckedOutMovies(userId, true, 
				PageRequest.of(page, nums, Sort.by("expectedReturnDate").descending()));
		OperationalResponse confirm = new OperationalResponse();
		GetUserCheckoutsResponse checkouts = new GetUserCheckoutsResponse();
		if(userCheckedOutMovies.size() == 0) {
			return ResponseEntity.status(HttpStatus.OK).body(checkouts);
		}
		LinkedHashMap<Integer, User> movieMap = new LinkedHashMap();
		for(User u: userCheckedOutMovies) {
			movieMap.put(u.getId().getProductId(), u);
		}
		//System.out.println("Calling search APIs");
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(movieMap.keySet());
		if(searchAPIResp == null) {
			confirm.setMessage("Search returned no information for ids: " + movieMap.keySet());
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(confirm);
		}
		for(MovieData m : searchAPIResp.getResults()) {
			User usr = movieMap.get(m.getID());
			String checkoutDate = getCheckoutDate(usr.getExpectedReturnDate());
			Movie mov = checkouts.newMovie(m.getTitle(), m.getID(), checkoutDate);
			checkouts.addMovie(mov);
		}
		return ResponseEntity.status(HttpStatus.OK).body(checkouts);		
	}
	
	public ResponseEntity<?> getTopRated(int page, int nums) {
		List<RatingModel> ratings = productRepository.findTopNRating(PageRequest.of(page, nums));
		if(ratings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No movie records are available!");
		}
		LinkedHashMap<Integer, RatingModel> ratingMap = new LinkedHashMap<Integer, RatingModel>();
		for(RatingModel ratingModel : ratings) {
			ratingMap.put(ratingModel.getId(), ratingModel);
		}
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(ratingMap.keySet());
		if(searchAPIResp == null) {
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Search returned no information for ids: " + ratingMap.keySet());
		}
		List<TopRatedResponse> res =  new LinkedList<TopRatedResponse>();
		List<MovieData> moviesData = searchAPIResp.getResults();
		for(MovieData movie : moviesData) {
			res.add(new TopRatedResponse(movie.getTitle(), movie.getID(),
					ratingMap.getOrDefault(movie.getID(), new RatingModel()).getAverageRating()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}
	
	public ResponseEntity<?> getTopUsers(String selected, int page, int nums) {
		List<TopUser> users = new ArrayList<>();
		if(selected.equals("checkouts")) {
			users = userRepository.getTopUserCheckouts(PageRequest.of(page, nums));
		}
		else if(selected.equals("favs")) {
			users = userRepository.getTopUserFavourites(PageRequest.of(page, nums));
		}
		else if(selected.equals("ratings")){
			users = userRepository.getTopRaters(PageRequest.of(page, nums));
		}
		LinkedHashMap<Integer, TopUser> topUserMap = new LinkedHashMap<Integer, TopUser>();
		for(TopUser topUser : users) {
			topUserMap.put(topUser.getUserId(), topUser);
		}
		List<UserInfoResponse.UserInfo> userInfos = APIClient.getTopUsers(topUserMap.keySet());
		List<TopUserResponse> response = new LinkedList<TopUserResponse>();
		for(UserInfoResponse.UserInfo userInfo : userInfos) {
			response.add(new TopUserResponse(userInfo.getUserName(), 
					userInfo.getEmail(), topUserMap.get(userInfo.getUserId()).getFavsCount(), 
					topUserMap.get(userInfo.getUserId()).getCheckoutsCount(),
					topUserMap.get(userInfo.getUserId()).getRatingsCount()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	public OperationalResponse rate(RatingRequest request) {
		if(request.getRating() > 5 || request.getRating() < 0) {
			// log here: invalid rating.
			return new OperationalResponse(false, "Invalid rating! rating must be between 0 and 5");
		}
		User user;
		Product product;
		PrimaryKey key = new PrimaryKey(request.getUserId(), request.getMovieId());
		if(productRepository.existsById(request.getMovieId())) {
			product = productRepository.getOne(request.getMovieId());
		} 
		else {
			product = new Product(request.getMovieId());
		}
		if(userRepository.existsById(key)) {
			user = userRepository.getOne(key);
			if(user.getRating() == -1) {
				product.setTotalCountOfRatings(product.getTotalCountOfRatings() + 1);
			} else {
				product.setSumOfRatings(product.getSumOfRatings() + request.getRating() - user.getRating());
			}
		}
		else {
			user = new User(key);
			product.setSumOfRatings(product.getSumOfRatings() + request.getRating());
			product.setTotalCountOfRatings(product.getTotalCountOfRatings() + 1);
		}
		user.setRating(request.getRating());
		userRepository.save(user);
		productRepository.save(product);
		return new OperationalResponse(true);
	}
	
	public OperationalResponse favoriteMovie(OperationalRequest request) {
		User user;
		Product product;
		PrimaryKey key = new PrimaryKey(request.getUserId(), request.getMovieId());
		if(productRepository.existsById(request.getMovieId())) {
			product = productRepository.getOne(request.getMovieId());
		} 
		else {
			product = new Product(request.getMovieId());
		}
		if(userRepository.existsById(key)) {
			user = userRepository.getOne(key);
			if(!user.isFavourites()) {
				product.setNumberOfFavorites(product.getNumberOfFavorites() + 1);
			}
		}
		else {
			user = new User(key);
			product.setNumberOfFavorites(product.getNumberOfFavorites() + 1);
		}
		user.setFavourites(true);
		userRepository.save(user);
		productRepository.save(product);
		return new OperationalResponse(true);
	}
	
	public ResponseEntity<?> checkout(OperationalRequest request) {
		int movieId = request.getMovieId();
		int userId = request.getUserId();
		Inventory record = null;
		User user;
		PrimaryKey pk = new PrimaryKey(userId, movieId);
		OperationalResponse resp = new OperationalResponse(false);
		if(!inventoryRepository.existsById(movieId)) {
			HashSet<Integer> s = new HashSet<Integer>();
			s.add(movieId);
			SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(s);
			if(searchAPIResp == null) {
				resp.setMessage("Search API has no record for Movie with Id " + movieId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
			}
			record = new Inventory(movieId, DEFAULT_NUM_OF_MOVIES, DEFAULT_NUM_OF_MOVIES);
		}
		record = (record == null) ? inventoryRepository.getOne(movieId) : record;
		if(record.getAvailableCopies() < 1) {
			resp.setMessage("All copies of movie with Id " + movieId + " have been rented to others");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
		}
		record.setAvailableCopies(record.getAvailableCopies() - 1);
		if(userRepository.existsById(pk)) {
			user = userRepository.getOne(pk);
			if(!user.isCheckouts()) {
				user.setCheckouts(true);
				user.setExpectedReturnDate(getExpectedReturnDate());
			}
			else {
				resp.setMessage("user " + userId + " must return this movie before they can check it out again.");
				return ResponseEntity.status(HttpStatus.OK).body(resp);
			}
		} else {
			user = new User(pk);
			user.setCheckouts(true);
			user.setExpectedReturnDate(getExpectedReturnDate());
		}
		inventoryRepository.save(record);
		userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK).body(new OperationalResponse(true, "Movie Checked Out Successfully"));
	}

	public ResponseEntity<?> totalFavesAndCheckouts(int userId, int page, int nums) {
		OperationalResponse confirm = new OperationalResponse();
		if(userRepository.findUserWithUserId(userId, Sort.by("id.productId")).size() == 0) {
			confirm.setMessage( "User does not exist");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(confirm);
		}
		int totalCheckouts = userRepository.getCheckoutCount(userId);
		List<User> userFavorites = userRepository.findFavoriteMovies(userId, true, PageRequest.of(page, nums, Sort.by("id.productId")));
		List<Favorites> favorites = userFavorites.size() > 0 ? curateFavorites(userFavorites) : new ArrayList<Favorites>();

		if(favorites == null) {
			confirm.setMessage("Could not retrieve movie title");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(confirm);
		}
		FavesAndCheckOuts favesAndCheckOuts = new FavesAndCheckOuts();
		favesAndCheckOuts.setCheckouts(totalCheckouts);
		favesAndCheckOuts.setFavorites(favorites);
		return ResponseEntity.status(HttpStatus.OK).body(favesAndCheckOuts);
	}

	private List<Favorites> curateFavorites(List<User> userFavorites) {
		List<Favorites> favorites = new ArrayList<>();
		HashMap<Integer, User> idToUser = new HashMap<>();

		for(User u : userFavorites) {
			idToUser.put(u.getId().getProductId(), u);
		}

		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(idToUser.keySet());
		if(searchAPIResp == null) {
			return null;
		}
		for(MovieData movieData : searchAPIResp.getResults()) {
			User u = idToUser.get(movieData.getID());
			Favorites favorite = new Favorites();
			favorite.setMovieId(movieData.getID());
			favorite.setMovieName(movieData.getTitle());
			favorite.setRating(u.getRating());
			favorites.add(favorite);
		}
		return favorites;
	}

	
	public ResponseEntity<?> returnMovie(int userId, int movieId) {
		Optional<User> user = userRepository.findById(new PrimaryKey(userId, movieId));
		Optional<Inventory> inventory = inventoryRepository.findById(movieId);
		OperationalResponse confirm = new OperationalResponse();
		if(user.isPresent() && inventory.isPresent()) {
			User u = user.get();
			if (u.isCheckouts() && userRepository.updateCheckoutReturn(u.getId(), getCurrentDate()) == 1) {
				Inventory record = inventory.get();
				record.setAvailableCopies(record.getAvailableCopies() + 1);
				if (inventoryRepository.updateAvailableCopies(record.getAvailableCopies(), record.getProductId()) == 1) {
					confirm.setConfirm(true);
					confirm.setMessage("Movie returned successfully");
					return ResponseEntity.status(HttpStatus.OK).body(confirm);
				}
				confirm.setMessage("Could not update inventory table");
			} else {
				if(!u.isCheckouts()) {
					confirm.setMessage(String.format("user has not checked out the concerned movie : %d", movieId));
				}
				else {
					confirm.setMessage("Could not update user table");
				}
			}
		}
		else {
			confirm.setMessage("Either user or movie is not present/invalid");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(confirm);
	}

	public ResponseEntity<?> updateConfig(ConfigRequest request) {
		Config.config.setUseLoginAPIs(request.getLogin());
		Config.config.setUseSearchAPIs(request.getSearch());
		Config.config.setUseAnalyticsAPIs(request.getAnalytics());
		return ResponseEntity.status(HttpStatus.OK).body(new OperationalResponse(true, "Config updated successfully"));
	}
	
	/**
	 * gives the expected return date
	 * expected return date = current date + default number of days to borrow movie  
	 * @return
	 */
	private Date getExpectedReturnDate() {
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, NUMBER_OF_DAYS_TO_BORROW);
        return c.getTime();
	}
	
	private static String getCheckoutDate(Date expectedReturnDate) {
		String pattern = "MM/dd/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        Calendar c = Calendar.getInstance();
        c.setTime(expectedReturnDate);
        c.add(Calendar.DATE, -NUMBER_OF_DAYS_TO_BORROW);
        String reportDate = df.format(c.getTime());
        return reportDate;
	}
  
	/**
	 * gives the current date
	 * @return
	 */
	private Date getCurrentDate() {
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		return c.getTime();
	}
}
