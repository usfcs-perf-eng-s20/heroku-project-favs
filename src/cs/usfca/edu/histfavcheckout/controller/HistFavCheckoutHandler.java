package cs.usfca.edu.histfavcheckout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cs.usfca.edu.histfavcheckout.model.PrimaryKey;
import cs.usfca.edu.histfavcheckout.model.User;
import cs.usfca.edu.histfavcheckout.model.UserRepository;

@Component
public class HistFavCheckoutHandler {
	
	@Autowired
	private UserRepository userRepository; 
	
	public User addUser(PrimaryKey id) {
		return userRepository.save(new User(id));
	}
	
	public List<User> getUser(int userId) {
		return userRepository.findUserWithUserId(userId, Sort.by("id.productId"));
	}

}
