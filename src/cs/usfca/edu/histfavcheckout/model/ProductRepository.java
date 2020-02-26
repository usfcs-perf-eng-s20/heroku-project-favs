package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	@Query("SELECT p FROM Product p ORDER BY p.numberOfFavorites DESC LIMIT :threshold")
	public List<Product> findTopNFavoritedMovies(@Param("threshold") int threshold);
}
