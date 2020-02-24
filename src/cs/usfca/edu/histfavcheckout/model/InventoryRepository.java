package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

	@Transactional
	@Modifying
	@Query("UPDATE Inventory i SET i.availableCopies = :availableCopies WHERE i.productId = :productId")
	public int updateAvailableCopies(@Param("availableCopies") int availableCopies, @Param("productId") int productId);
}
