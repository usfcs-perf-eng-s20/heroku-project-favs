package cs.usfca.edu.histfavcheckout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HistFavCheckoutController {
	
	@GetMapping(value = "/ping")
	@ResponseBody()
	public ResponseEntity<?> ping() {
		return ResponseEntity.status(HttpStatus.OK).body("Pong");
	}
	
	@GetMapping(value = "/getTopFavs")
	@ResponseBody()
	public ResponseEntity<?> getTopFavs(@RequestParam int start, @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}

	@GetMapping(value = "/getTopRated")
	@ResponseBody()
	public ResponseEntity<?> getTopRated(@RequestParam int start, @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@GetMapping(value = "/getTopUsers")
	@ResponseBody()
	public ResponseEntity<?> getTopUsers(@RequestParam String selected, @RequestParam int start, @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@GetMapping(value = "/totalFavesAndCheckouts")
	@ResponseBody()
	public ResponseEntity<?> totalFavesAndCheckouts(@RequestParam int userId) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@GetMapping(value = "/checkedOutMovies")
	@ResponseBody()
	public ResponseEntity<?> getCheckedOutMovies(@RequestParam int userId, @RequestParam int start, @RequestParam int nums) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@PutMapping(value = "/returnMovies")
	@ResponseBody()
	public ResponseEntity<?> returnMovies(@RequestParam int userId, @RequestParam int movieId) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
	
	@PutMapping(value = "/checkedOutMovies")
	@ResponseBody()
	public ResponseEntity<?> checkedOutMovies(@RequestParam int userId, @RequestParam int movieId) {
		return ResponseEntity.status(HttpStatus.OK).body("Endpoint not implemented!");
	}
}
