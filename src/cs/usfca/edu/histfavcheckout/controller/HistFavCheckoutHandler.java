package cs.usfca.edu.histfavcheckout.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cs.usfca.edu.histfavcheckout.externalapis.APIClient;
import cs.usfca.edu.histfavcheckout.model.ConfigRequest;
import cs.usfca.edu.histfavcheckout.model.FavesAndCheckOuts;
import cs.usfca.edu.histfavcheckout.model.Favorites;
import cs.usfca.edu.histfavcheckout.model.Favourites;
import cs.usfca.edu.histfavcheckout.model.GetUserCheckoutsResponse;
import cs.usfca.edu.histfavcheckout.model.GetUserCheckoutsResponse.Movie;
import cs.usfca.edu.histfavcheckout.model.Inventory;
import cs.usfca.edu.histfavcheckout.model.InventoryRepository;
import cs.usfca.edu.histfavcheckout.model.OperationalRequest;
import cs.usfca.edu.histfavcheckout.model.OperationalResponse;
import cs.usfca.edu.histfavcheckout.model.PrimaryKey;
import cs.usfca.edu.histfavcheckout.model.Product;
import cs.usfca.edu.histfavcheckout.model.ProductRepository;
import cs.usfca.edu.histfavcheckout.model.RatingModel;
import cs.usfca.edu.histfavcheckout.model.RatingRequest;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse.MovieData;
import cs.usfca.edu.histfavcheckout.model.TopRatedResponse;
import cs.usfca.edu.histfavcheckout.model.TopUser;
import cs.usfca.edu.histfavcheckout.model.TopUserResponse;
import cs.usfca.edu.histfavcheckout.model.User;
import cs.usfca.edu.histfavcheckout.model.UserInfoResponse;
import cs.usfca.edu.histfavcheckout.model.UserRepository;
import cs.usfca.edu.histfavcheckout.utils.Config;
import cs.usfca.edu.histfavcheckout.utils.LoggerHelper;


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

	public ResponseEntity<?> addInventory(Inventory inventory, String prefix) {
		Optional<Product> product = productRepository.findById(inventory.getProductId());
		if(!product.isPresent()) {
			LoggerHelper.makeInfoLog(prefix + "Invalid movie Id: " + inventory.getProductId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request unsuccessful. Invalid movie Id.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(inventoryRepository.save(inventory));
	}

	public ResponseEntity<?> getInventory(int movieId, String prefix) {
		Optional<Inventory> inv = inventoryRepository.findById(movieId);
		if(!inv.isPresent()) {
			LoggerHelper.makeInfoLog(prefix + "Record does not exist at movieId: " + movieId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record does not exist!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(inv.get());
	}

	public ResponseEntity<?> addMovie(Product movie, String prefix) {
		Optional<Product> product = productRepository.findById(movie.getId());
		if(product.isPresent()) {
			LoggerHelper.makeInfoLog(prefix + String.format("MovieId: %d was added previously", movie.getId()));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie has been added previously.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(movie));
	}

	public ResponseEntity<?> getMovie(int movieId, String prefix) {
		Optional<Product> product = productRepository.findById(movieId);
		if(!product.isPresent()) {
			LoggerHelper.makeInfoLog(prefix + String.format("MovieId: %d does not exist", movieId));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie does not exist!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(product.get());
	}

	public ResponseEntity<?> getTopFavs(int page, int nums, String prefix) {
		List<Product> products = productRepository.findTopN(PageRequest.of(page, nums, Sort.by("numberOfFavorites").descending()));
		if(products == null || products.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new OperationalResponse(false, "No movie records are available!"));
		}
		LinkedHashMap<Integer, Favourites> favsMap = new LinkedHashMap<Integer, Favourites>();
		for(Product product : products) {
			Favourites favourite = new Favourites();
			favourite.setMovieId(product.getId());
			favourite.setFavourites(product.getNumberOfFavorites());
			favsMap.put(product.getId(), favourite);
		}
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(favsMap.keySet());
		if(searchAPIResp == null) {
			LoggerHelper.makeWarningLog(prefix + "Search returned no information for ids: " + favsMap.keySet());
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Search returned no information for ids: " + favsMap.keySet());
		}
		List<Favourites> res =  new LinkedList<Favourites>();
		List<MovieData> moviesData = searchAPIResp.getResults();
		for(MovieData movie : moviesData) {
			Favourites fav = favsMap.get(movie.getID());
			fav.setMovieName(movie.getTitle());
			res.add(fav);
		}
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	public ResponseEntity<?> getCheckouts(int userId, int page, int nums, String prefix) {
		List<User> userCheckedOutMovies = userRepository.findCheckedOutMovies(userId, true,
				PageRequest.of(page, nums, Sort.by("expectedReturnDate").descending()));
		OperationalResponse confirm = new OperationalResponse();
		GetUserCheckoutsResponse checkouts = new GetUserCheckoutsResponse();
		if(userCheckedOutMovies.size() == 0) {
			LoggerHelper.makeInfoLog(prefix + "Returning! No valid data for user");
			return ResponseEntity.status(HttpStatus.OK).body(checkouts);
		}
		LinkedHashMap<Integer, User> movieMap = new LinkedHashMap();
		for(User u: userCheckedOutMovies) {
			movieMap.put(u.getId().getProductId(), u);
		}
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(movieMap.keySet());
		if(searchAPIResp == null) {
			LoggerHelper.makeWarningLog(prefix + "Returning! Search API had no data for user checkedout movies");
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

	public ResponseEntity<?> getTopRated(int page, int nums, String prefix) {
		List<RatingModel> ratings = productRepository.findTopNRating(PageRequest.of(page, nums));
		if(ratings == null || ratings.isEmpty()) {
			LoggerHelper.makeInfoLog(prefix + "No movie records are available!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new OperationalResponse(false, "No movie records are available!"));
		}
		LinkedHashMap<Integer, RatingModel> ratingMap = new LinkedHashMap<Integer, RatingModel>();
		for(RatingModel ratingModel : ratings) {
			ratingMap.put(ratingModel.getId(), ratingModel);
		}
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(ratingMap.keySet());
		if(searchAPIResp == null) {
			LoggerHelper.makeWarningLog(prefix + "Search returned no information for ids: " + ratingMap.keySet());
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

	public ResponseEntity<?> getTopUsers(String selected, int page, int nums, String prefix) {
		List<TopUser> users = new ArrayList<>();
		if(selected.equals("checkouts")) {
			users = userRepository.getTopUserCheckouts(PageRequest.of(page, nums));
		}
		else if(selected.equals("favs")) {
			users = userRepository.getTopUserFavourites(PageRequest.of(page, nums));
		}
		else if(selected.equals("ratings")) {
			users = userRepository.getTopRaters(PageRequest.of(page, nums));
		}
		else {
			LoggerHelper.makeWarningLog(prefix + "selected should be one of favs, checkouts or ratings");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new OperationalResponse(false, "selected should be one of favs, checkouts or ratings"));
		}
		LinkedHashMap<Integer, TopUser> topUserMap = new LinkedHashMap<Integer, TopUser>();
		List<TopUserResponse> response = new LinkedList<TopUserResponse>();
		if(!users.isEmpty()) {
			for(TopUser topUser : users) {
				topUserMap.put(topUser.getUserId(), topUser);
			}
			List<UserInfoResponse.UserInfo> userInfos = APIClient.getTopUsers(topUserMap.keySet());
			for(UserInfoResponse.UserInfo userInfo : userInfos) {
				response.add(new TopUserResponse(userInfo.getUserName(),
						userInfo.getEmail(), topUserMap.get(userInfo.getUserId()).getFavsCount(),
						topUserMap.get(userInfo.getUserId()).getCheckoutsCount(),
						topUserMap.get(userInfo.getUserId()).getRatingsCount()));
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	public OperationalResponse rate(RatingRequest request, String prefix) {
		if(request.getRating() > 5 || request.getRating() < 0) {
			LoggerHelper.makeInfoLog(prefix + "Invalid rating! Rating must be between 0 and 5");
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

	public ResponseEntity<?> checkout(OperationalRequest request, String prefix) {
		int movieId = request.getMovieId();
		int userId = request.getUserId();
		PrimaryKey pk = new PrimaryKey(userId, movieId);
		Optional<Inventory> record = inventoryRepository.findById(movieId);
		Optional<User> user = userRepository.findById(pk);
		User u;
		OperationalResponse resp = new OperationalResponse(false);
		if(!record.isPresent()) {
			HashSet<Integer> s = new HashSet<Integer>();
			s.add(movieId);
			SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(s);
			if(searchAPIResp == null) {
				LoggerHelper.makeInfoLog(prefix + "Search API has no record for Movie with Id " + movieId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
			}
		}
		Inventory inv = (record.isPresent()) ? record.get() : new Inventory(movieId, DEFAULT_NUM_OF_MOVIES, DEFAULT_NUM_OF_MOVIES);
		if(inv.getAvailableCopies() < 1) {
			LoggerHelper.makeInfoLog(prefix + "All copies of movie with Id " + movieId + " have been rented to others");
			resp.setMessage("All copies of movie with Id " + movieId + " have been rented to others");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
		}
		inv.setAvailableCopies(inv.getAvailableCopies() - 1);
		if(user.isPresent()) {
			u = user.get();
			if(!u.isCheckouts()) {
				u.setCheckouts(true);
				u.setExpectedReturnDate(getExpectedReturnDate());
			}
			else {
				LoggerHelper.makeInfoLog(prefix + "user " + userId + " must return this movie before they can check it out again.");
				resp.setMessage("user " + userId + " must return this movie before they can check it out again.");
				return ResponseEntity.status(HttpStatus.OK).body(resp);
			}
		} else {
			u = new User(pk);
			u.setCheckouts(true);
			u.setExpectedReturnDate(getExpectedReturnDate());
		}
		inventoryRepository.save(inv);
		userRepository.save(u);
		return ResponseEntity.status(HttpStatus.OK).body(new OperationalResponse(true, "Movie Checked Out Successfully"));
	}

	public ResponseEntity<?> totalFavesAndCheckouts(int userId, int page, int nums, String prefix) {
		OperationalResponse confirm = new OperationalResponse();
		if(userRepository.findUserWithUserId(userId, Sort.by("id.productId")).size() == 0) {
			LoggerHelper.makeInfoLog(prefix + "User with userId: " + userId + " doesn't exist");
			confirm.setMessage( "User does not exist");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(confirm);
		}
		int totalCheckouts = userRepository.getCheckoutCount(userId);
		List<User> userFavorites = userRepository.findFavoriteMovies(userId, true, PageRequest.of(page, nums, Sort.by("id.productId")));
		List<Favorites> favorites = userFavorites.size() > 0 ? curateFavorites(userFavorites) : new ArrayList<Favorites>();

		if(favorites == null) {
			LoggerHelper.makeInfoLog(prefix + "Could not retrieve movie title for userId:" + userId);
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


	public ResponseEntity<?> returnMovie(int userId, int movieId, String prefix) {
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
				LoggerHelper.makeInfoLog(prefix + "Could not update inventory table");
				confirm.setMessage("Could not update inventory table");
			} else {
				if(!u.isCheckouts()) {
					LoggerHelper.makeInfoLog(prefix + String.format("user has not checked out the concerned movie : %d", movieId));
					confirm.setMessage(String.format("user has not checked out the concerned movie : %d", movieId));
				}
				else {
					LoggerHelper.makeInfoLog(prefix + "Could not update user table");
					confirm.setMessage("Could not update user table");
				}
			}
		}
		else {
			LoggerHelper.makeInfoLog(prefix + "Either user or movie is not present/invalid");
			confirm.setMessage("Either user or movie is not present/invalid");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(confirm);
	}

	public ResponseEntity<?> updateConfig(ConfigRequest request, String prefix) {
		LoggerHelper.makeInfoLog(prefix + "Updating Config. login:" + request.getLogin() 
		+ " search:" + request.getSearch() + " analytics:" + request.getAnalytics());
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
