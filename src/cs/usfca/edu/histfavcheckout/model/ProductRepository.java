package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	@Query("SELECT p FROM Product as p")
	public List<Product> findTopN(Pageable pageable);
	
	@Query("SELECT new cs.usfca.edu.histfavcheckout.model.RatingModel((p.id) as id, (cast(p.sumOfRatings as float)/p.totalCountOfRatings) as averageRating) FROM Product p WHERE p.totalCountOfRatings > 0 ORDER BY averageRating DESC")
	public List<RatingModel> findTopNRating(Pageable pageable);
	
}
