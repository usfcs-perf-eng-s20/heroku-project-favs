package cs.usfca.edu.histfavcheckout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HelloWorldController {
	
	@GetMapping(value = "/", consumes = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody()
	public ResponseEntity<?> registerUser() {
		return ResponseEntity.status(HttpStatus.OK).body("Hello, World!");
	}
}
