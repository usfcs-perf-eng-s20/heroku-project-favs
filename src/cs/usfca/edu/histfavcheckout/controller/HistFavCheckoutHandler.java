package cs.usfca.edu.histfavcheckout.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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


@Component
public class HistFavCheckoutHandler {

	public static int NUMBER_OF_DAYS_TO_BORROW = 15;

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
			System.out.println("Invalid movie Id: " + inventory.getProductId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request unsuccessful. Invalid movie Id.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(inventoryRepository.save(inventory));
	}

	public ResponseEntity<?> getInventory(int movieId) {
		Optional<Inventory> inv = inventoryRepository.findById(movieId);
		if(!inv.isPresent()) {
			System.out.println("Record does not exist at movieId: " + movieId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record does not exist!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(inv.get());
	}

	public ResponseEntity<?> addMovie(Product movie) {
		Optional<Product> product = productRepository.findById(movie.getId());
		if(product.isPresent()) {
			System.out.println(String.format("MovieId: %d was added previously", movie.getId()));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie has been added previously.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(movie));
	}

	public ResponseEntity<?> getMovie(int movieId) {
		Optional<Product> product = productRepository.findById(movieId);
		if(!product.isPresent()) {
			System.out.println(String.format("MovieId: %d does not exist", movieId));
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
		GetUserCheckoutsResponse checkouts = new GetUserCheckoutsResponse();
		if(userCheckedOutMovies.size() == 0) {
			System.out.println("Returning! No valid data for user");
			return ResponseEntity.status(HttpStatus.OK).body(checkouts);
		}
		LinkedHashMap<Integer, User> movieMap = new LinkedHashMap();
		for(User u: userCheckedOutMovies) {
			movieMap.put(u.getId().getProductId(), u);
		}
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(movieMap.keySet());
		if(searchAPIResp == null) {
			System.out.println("Returning! Search API had no data for user checkedout movies");
			return ResponseEntity.status(HttpStatus.OK).body(checkouts);
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
			System.out.println("No movie records are available!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No movie records are available!");
		}
		LinkedHashMap<Integer, RatingModel> ratingMap = new LinkedHashMap<Integer, RatingModel>();
		for(RatingModel ratingModel : ratings) {
			ratingMap.put(ratingModel.getId(), ratingModel);
		}
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(ratingMap.keySet());
		if(searchAPIResp == null) {
			System.out.println("Search returned no information for ids: " + ratingMap.keySet());
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
		if(users.isEmpty()) {
			System.out.println("No users are available");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users are available");
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
			System.out.println("Invalid rating! Rating must be between 0 and 5");
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

	public OperationalResponse checkout(OperationalRequest request) {
		int movieId = request.getMovieId();
		int userId = request.getUserId();
		OperationalResponse resp = new OperationalResponse(false);
		Optional<Inventory> inventory = inventoryRepository.findById(movieId);
		if(!inventory.isPresent()) {
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie with Id " + movieId + " does not exist!");
			return resp;
		}
		Inventory record = inventory.get();
		if(record.getAvailableCopies() < 1) {
			//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No more copies of this movie available for rent. Please try again later.");
			return resp;
		}
		int availableCopies = record.getAvailableCopies() - 1;
		int updated = 0;
		updated = inventoryRepository.updateAvailableCopies(availableCopies, record.getProductId());
		if(updated < 1) {
			System.out.println("Server unable to reduce available copies.");
			//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to complete transaction. Please try again later.");
			return resp;
		}

		// update user table here. Or insert new record
		PrimaryKey pk = new PrimaryKey(userId, movieId);
		Optional<User> user = userRepository.findById(pk);
		User theUser = null;

		if(user.isPresent()) {
			theUser = user.get();
			if(!theUser.isCheckouts()) {
				theUser.setCheckouts(true);
				theUser.setExpectedReturnDate(getExpectedReturnDate());
				updated = userRepository.updateCheckoutDetails(theUser.isCheckouts(), theUser.getExpectedReturnDate(), pk);
				if(updated < 1) {
					System.out.println("Server unable to reduce available copies.");
					//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to complete transaction. Please try again later.");
					return resp;
				}
			}
		} else {
			theUser = new User(pk);
			theUser.setCheckouts(true);
			theUser.setExpectedReturnDate(getExpectedReturnDate());
			userRepository.save(theUser);
		}
		resp.setConfirm(true);
		return resp;
	}

	public ResponseEntity<?> totalFavesAndCheckouts(int userId, int page, int nums) {
		OperationalResponse confirm = new OperationalResponse();
		if(userRepository.findUserWithUserId(userId, Sort.by("id.productId")).size() == 0) {
		    System.out.println("User with userId: " + userId + " doesn't exist");
			confirm.setMessage( "User does not exist");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(confirm);
		}
		int totalCheckouts = userRepository.getCheckoutCount(userId);
		List<User> userFavorites = userRepository.findFavoriteMovies(userId, true, PageRequest.of(page, nums, Sort.by("id.productId")));
		List<Favorites> favorites = userFavorites.size() > 0 ? curateFavorites(userFavorites) : new ArrayList<Favorites>();

		if(favorites == null) {
			System.out.println("Could not retrieve movie title for userId:" + userId);
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
				System.out.println("Could not update inventory table");
				confirm.setMessage("Could not update inventory table");
			} else {
				if(!u.isCheckouts()) {
					System.out.println(String.format("user has not checked out the concerned movie : %d", movieId));
					confirm.setMessage(String.format("user has not checked out the concerned movie : %d", movieId));
				}
				else {
					System.out.println("Could not update user table");
					confirm.setMessage("Could not update user table");
				}
			}
		}
		else {
			System.out.println("Either user or movie is not present/invalid");
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
