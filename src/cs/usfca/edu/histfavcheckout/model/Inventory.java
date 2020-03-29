package cs.usfca.edu.histfavcheckout.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "Inventory")
public class Inventory {
	@Id
    private Integer productId;
	@Column(nullable = false)
	private int totalNumberOfCopies;
	@Column(nullable = false)
	private int availableCopies;
	
	public Inventory() {}
	
	public Inventory(int productId, int totalCopies, int availableCopies) {
		this.productId = productId;
		this.totalNumberOfCopies = totalCopies;
		this.availableCopies = availableCopies;
	}
	
	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getTotalNumberOfCopies() {
		return totalNumberOfCopies;
	}

	public void setTotalNumberOfCopies(int totalNumberOfCopies) {
		this.totalNumberOfCopies = totalNumberOfCopies;
	}

	public int getAvailableCopies() {
		return availableCopies;
	}
	
	public void setAvailableCopies(int availableCopies) {
		this.availableCopies = availableCopies;
	}
	
	@Override
	public String toString() {
		return "User{" +
                "id: " + productId +
                ", totalNumberOfCopies: " + totalNumberOfCopies +
                ", availableCopies: " + availableCopies +
                '}';
	}

}
