package cs.usfca.edu.histfavcheckout.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping("/history")
@Api(value = "Video Rental System", description = "Description of Video Rental System")
public class HelloWorldController {
	
    @ApiOperation(value = "Register a user", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved list"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")

    })
	@GetMapping(value = "/", consumes = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> registerUser() {
		return ResponseEntity.status(HttpStatus.OK).body("Hello, World!");
	}
}
