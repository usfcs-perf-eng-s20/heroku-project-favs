package cs.usfca.edu.histfavcheckout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cs.usfca.edu.histfavcheckout.model.OperationalRequest;
import cs.usfca.edu.histfavcheckout.model.OperationalResponse;
import cs.usfca.edu.histfavcheckout.model.Inventory;
import cs.usfca.edu.histfavcheckout.model.PrimaryKey;
import cs.usfca.edu.histfavcheckout.model.Product;
import cs.usfca.edu.histfavcheckout.model.RatingRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/")
@Api(value = "Video Rental System", description = "APIs owned by the Faves Team")
public class HistFavCheckoutController {
	
	@Autowired
	HistFavCheckoutHandler handler = new HistFavCheckoutHandler();
	
	@ApiOperation(value = "Check if System is alive", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Pong"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@GetMapping(value = "/ping")
	@ResponseBody()
	public ResponseEntity<?> ping() {
		return ResponseEntity.status(HttpStatus.OK).body("Pong");
	}
	
	@ApiOperation(value = "Get Top Liked Movies", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved top liked movies"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@GetMapping(value = "/getTopFavs")
	@ResponseBody()
	public ResponseEntity<?> getTopFavs(@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int page, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return handler.getTopFavs(page, nums);
	}
	
	@ApiOperation(value = "Rate a Movie")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully rated movie"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
	@PostMapping(value = "/rateMovie")
	@ResponseBody()
	public ResponseEntity<?> rateMovie(@ApiParam(value = "RatingRequest", required = true) 
		@RequestBody RatingRequest request) {
		OperationalResponse response = handler.rate(request);
		if(response.isConfirm()) {
			return ResponseEntity.status(HttpStatus.OK).body(handler.rate(request));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handler.rate(request));
	}

	@ApiOperation(value = "Get Top Rated Movies", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved top rated movies"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@GetMapping(value = "/getTopRated")
	@ResponseBody()
	public ResponseEntity<?> getTopRated(@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int page, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body(handler.getTopRated(page, nums));
	}
	
	@ApiOperation(value = "Favorite a Movie")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully favorited movie"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
	@PostMapping(value = "/favoriteMovie")
	@ResponseBody()
	public ResponseEntity<?> favoriteMovie(@ApiParam(value = "OperationalRequest", required = true) 
		@RequestBody OperationalRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(handler.favoriteMovie(request));
	}
	
	@ApiOperation(value = "Get Top Users", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved top users"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@GetMapping(value = "/getTopUsers")
	@ResponseBody()
	public ResponseEntity<?> getTopUsers(@ApiParam(value = "Checkouts, Faves and Ratings per user can be selected", required = true) @RequestParam String selected, 
			@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int page, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return handler.getTopUsers(selected, page, nums);
	}
	
	@ApiOperation(value = "Returns all the favorite movies and the total number of movies checked out for the user", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully favorite movies and total checkouts"),
        @ApiResponse(code = 400, message = "User does not exist"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@GetMapping(value = "/totalFavesAndCheckouts")
	@ResponseBody()
	public ResponseEntity<?> totalFavesAndCheckouts(@ApiParam(value = "id of user", required = true) @RequestParam int userId) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@ApiOperation(value = "Returns details of all movies a user has checked out", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved movies"),
        @ApiResponse(code = 400, message = "User does not exist"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@GetMapping(value = "/checkedOutMovies")
	@ResponseBody()
	public ResponseEntity<?> getCheckedOutMovies(@ApiParam(value = "id of user", required = true) @RequestParam int userId, 
			@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int page, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return handler.getCheckouts(userId, page, nums);
	}
	
	@ApiOperation(value = "Lets a user return a movie", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully returned movie"),
        @ApiResponse(code = 400, message = "User or movie does not exist"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@PutMapping(value = "/returnMovie")
	@ResponseBody()
	public ResponseEntity<?> returnMovies(@ApiParam(value = "id of user", required = true) @RequestParam int userId, 
			@ApiParam(value = "id of movie", required = true) @RequestParam int movieId) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@ApiOperation(value = "Lets a user checkout a movie", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully checked out movie"),
        @ApiResponse(code = 400, message = "User or movie does not exist"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@PutMapping(value = "/checkOutMovies")
	@ResponseBody()
	public ResponseEntity<?> checkOutMovies(@RequestBody OperationalRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(handler.checkout(request));
	}
	
	@GetMapping(value = "/user")
	@ResponseBody()
	public ResponseEntity<?> getUser(@RequestParam int userId) {
		return ResponseEntity.status(HttpStatus.OK).body(handler.getUser(userId));
	}
	
	@PostMapping(value = "/user")
	@ResponseBody()
	public ResponseEntity<?> postUser(@RequestBody PrimaryKey id) {
		return ResponseEntity.status(HttpStatus.OK).body(handler.addUser(id));
	}
	
	@GetMapping(value = "/inventory")
	@ResponseBody()
	public ResponseEntity<?> getInventory(@RequestParam int movieId) {
		return handler.getInventory(movieId);
	}
	
	@PostMapping(value = "/inventory")
	@ResponseBody()
	public ResponseEntity<?> postInventory(@RequestBody Inventory inventory) {
		return handler.addInventory(inventory);
	}
	
	@GetMapping(value = "/movie")
	@ResponseBody()
	public ResponseEntity<?> getMovie(@RequestParam int movieId) {
		return handler.getMovie(movieId);
	}
	
	@PostMapping(value = "/movie")
	@ResponseBody()
	public ResponseEntity<?> postMovie(@RequestBody Product movie) {
		return handler.addMovie(movie);
	}
}
