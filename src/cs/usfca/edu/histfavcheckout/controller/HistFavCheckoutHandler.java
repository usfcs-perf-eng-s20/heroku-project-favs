package cs.usfca.edu.histfavcheckout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import cs.usfca.edu.histfavcheckout.model.PrimaryKey;
import cs.usfca.edu.histfavcheckout.model.User;
import cs.usfca.edu.histfavcheckout.repository.UserRepository;

public class HistFavCheckoutHandler {
	
	@Autowired
	private static UserRepository userRepository; 
	
	public static User addUser(PrimaryKey id) {
		return userRepository.save(new User(id));
	}
	
	public static List<User> getUser(int userId) {
		return userRepository.findUserWithUserId(userId, Sort.by("productId"));
	}

}
