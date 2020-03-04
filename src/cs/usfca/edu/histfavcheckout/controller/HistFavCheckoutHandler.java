package cs.usfca.edu.histfavcheckout.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cs.usfca.edu.histfavcheckout.model.OperationalResponse;
import cs.usfca.edu.histfavcheckout.model.Inventory;
import cs.usfca.edu.histfavcheckout.model.InventoryRepository;
import cs.usfca.edu.histfavcheckout.model.OperationalRequest;
import cs.usfca.edu.histfavcheckout.model.PrimaryKey;
import cs.usfca.edu.histfavcheckout.model.Product;
import cs.usfca.edu.histfavcheckout.model.ProductRepository;
import cs.usfca.edu.histfavcheckout.model.RatingRequest;
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
}
