package cs.usfca.edu.histfavcheckout.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cs.usfca.edu.histfavcheckout.externalapis.APIClient;
import cs.usfca.edu.histfavcheckout.model.CheckoutRequest;
import cs.usfca.edu.histfavcheckout.model.CheckoutResponse;
import cs.usfca.edu.histfavcheckout.model.GetUserCheckoutsResponse;
import cs.usfca.edu.histfavcheckout.model.GetUserCheckoutsResponse.Movie;
import cs.usfca.edu.histfavcheckout.model.Inventory;
import cs.usfca.edu.histfavcheckout.model.InventoryRepository;
import cs.usfca.edu.histfavcheckout.model.PrimaryKey;
import cs.usfca.edu.histfavcheckout.model.Product;
import cs.usfca.edu.histfavcheckout.model.ProductRepository;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse;
import cs.usfca.edu.histfavcheckout.model.SearchMoviesResponse.MovieData;
import cs.usfca.edu.histfavcheckout.model.User;
import cs.usfca.edu.histfavcheckout.model.UserRepository;

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
		List<Product> products = productRepository.findTopNFavoritedMovies(PageRequest.of(page, nums, Sort.by("numberOfFavorites").descending()));
		return ResponseEntity.status(HttpStatus.OK).body(products);		
	}
	
	public ResponseEntity<?> getCheckouts(int userId, int page, int nums) {
		List<User> userCheckedOutMovies = userRepository.findCheckedOutMovies(userId, true, 
				PageRequest.of(page, nums, Sort.by("numberOfFavorites").descending()));
		HashMap<Integer, User> movieMap = new HashMap();
		for(User u: userCheckedOutMovies) {
			movieMap.put(u.getId().getProductId(), u);
		}
		//TODO: call search team's API here
		SearchMoviesResponse searchAPIResp = APIClient.getAllMovies(movieMap.keySet());
		GetUserCheckoutsResponse checkouts = new GetUserCheckoutsResponse();
		for(MovieData m : searchAPIResp.getResults()) {
			User usr = movieMap.get(m.getID());
			String checkoutDate = getCheckoutDate(usr.getExpectedReturnDate());
			Movie mov = checkouts.new Movie(m.getTitle(), m.getID(), checkoutDate);
			checkouts.addMovie(mov);
		}
		return ResponseEntity.status(HttpStatus.OK).body(checkouts);		
	}
	
	public CheckoutResponse checkout(CheckoutRequest request) {
		int movieId = request.getMovieId();
		int userId = request.getUserId();
		CheckoutResponse resp = new CheckoutResponse(false);
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
		record.setAvailableCopies(record.getAvailableCopies() - 1);
		
		int updated = 0;
		updated = inventoryRepository.updateAvailableCopies(record.getAvailableCopies(), record.getProductId());
		if(updated < 1) {
			// TODO: Add logs here saying for some reason server was unable to reduce available copies
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
					// TODO: Add logs here saying for some reason server was unable to reduce available copies
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
}
