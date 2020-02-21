package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, PrimaryKey> {
	
	@Query("SELECT u FROM User u WHERE u.id.userId= :userId")
	public List<User> findUserWithUserId(@Param("userId") int userId, Sort sort);
	
}