package cs.usfca.edu.histfavcheckout.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	SELECT p FROM Product p ORDER BY p.numberOfFavorites DESC LIMIT :threshold
	public List<Product> findTopNFavoritedMovies(@Param("threshold") int threshold);
}
