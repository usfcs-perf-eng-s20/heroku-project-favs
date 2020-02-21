package cs.usfca.edu.histfavcheckout.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cs.usfca.edu.histfavcheckout.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("SELECT * FROM User WHERE userId= :userId")
	public List<User> findUserWithUserId(@Param("userId") int userId, Sort sort);
}