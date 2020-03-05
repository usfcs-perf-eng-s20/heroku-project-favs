package cs.usfca.edu.histfavcheckout.model;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, PrimaryKey> {
	
	@Query("SELECT u FROM User u WHERE u.id.userId = :userId")
	public List<User> findUserWithUserId(@Param("userId") int userId, Sort sort);
	
	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.checkouts = :checkouts, u.expectedReturnDate = :expectedReturnDate WHERE u.id = :id")
	public int updateCheckoutDetails(@Param("checkouts") boolean checkout, @Param("expectedReturnDate") Date expectedReturnDate, 
			@Param("id") PrimaryKey id);
	
	@Query("SELECT u FROM User u WHERE u.id.userId = :userId AND u.checkouts = :checkouts")
	public List<User> findCheckedOutMovies(@Param("userId") int userId, @Param("checkouts") boolean checkout, Pageable pageable);
}