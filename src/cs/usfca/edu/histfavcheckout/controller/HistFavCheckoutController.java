package cs.usfca.edu.histfavcheckout.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping("/")
@Api(value = "Video Rental System", description = "APIs owned by the Faves Team")
public class HistFavCheckoutController {
	
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
	public ResponseEntity<?> getTopFavs(@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int start, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
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
	public ResponseEntity<?> getTopRated(@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int start, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
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
			@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int start, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
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
			@ApiParam(value = "index to start fetching movies", required = true) @RequestParam int start, 
			@ApiParam(value = "number of movies per page to return", required = true) @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@ApiOperation(value = "Lets a user return a movie", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully returned movie"),
        @ApiResponse(code = 400, message = "User or movie does not exist"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@PutMapping(value = "/returnMovies")
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
	public ResponseEntity<?> checkedOutMovies(@ApiParam(value = "id of user", required = true) @RequestParam int userId, 
			@ApiParam(value = "id of movie", required = true) @RequestParam int movieId) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
}