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

	@Query("SELECT u FROM User u WHERE u.id.userId = :userId AND u.favourites = :favorite")
	public List<User> findFavoriteMovies(@Param("userId") int userId, @Param("favorite") boolean favorite, Pageable pageable);

	@Query("SELECT COUNT(*) FROM User u WHERE (u.checkouts = true) AND (u.id.userId = :userId)")
	public int getCheckoutCount(@Param("userId") int userId);

	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.actualReturnDate = :actualReturnDate WHERE u.id = :id")
	public int updateCheckoutReturn(@Param("id") PrimaryKey id, @Param("actualReturnDate") Date actualReturnDate);
	
	@Query("SELECT new cs.usfca.edu.histfavcheckout.model.TopUser(u.id.userId, "
			+ "count(case when favourites=TRUE then 1 end) as favs, "
			+ "count(case when checkouts=TRUE then 1 end) as checks, "
			+ "count(case when rating > 0 then 1 end) as rates) "
			+ "FROM User u GROUP BY u.id.userId ORDER BY favs DESC")
	public List<TopUser> getTopUserFavourites(Pageable pageable);
	
	@Query("SELECT new cs.usfca.edu.histfavcheckout.model.TopUser(u.id.userId, "
			+ "count(case when favourites=TRUE then 1 end) as favs, "
			+ "count(case when checkouts=TRUE then 1 end) as checks, "
			+ "count(case when rating > 0 then 1 end) as rates) "
			+ "FROM User u GROUP BY u.id.userId ORDER BY checks DESC")
	public List<TopUser> getTopUserCheckouts(Pageable pageable);
	
	@Query("SELECT new cs.usfca.edu.histfavcheckout.model.TopUser(u.id.userId, "
			+ "count(case when favourites=TRUE then 1 end) as favs, "
			+ "count(case when checkouts=TRUE then 1 end) as checks, "
			+ "count(case when rating > 0 then 1 end) as rates) "
			+ "FROM User u GROUP BY u.id.userId ORDER BY rates DESC")
	public List<TopUser> getTopRaters(Pageable pageable);

}