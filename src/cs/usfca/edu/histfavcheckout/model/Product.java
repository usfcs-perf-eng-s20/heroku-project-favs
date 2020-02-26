package cs.usfca.edu.histfavcheckout.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "Product")
public class Product {

	@Id
    private Integer id;
	@Column(nullable = false)
	private int sumOfRatings;
	@Column(nullable = false)
	private int totalCountOfRatings;
	@Column(nullable = false)
	private int numberOfFavorites;
	
	public Product() {}
	
	public Product(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getSumOfRatings() {
		return sumOfRatings;
	}

	public void setSumOfRatings(int sumOfRatings) {
		this.sumOfRatings = sumOfRatings;
	}

	public int getTotalCountOfRatings() {
		return totalCountOfRatings;
	}

	public void setTotalCountOfRatings(int totalCountOfRatings) {
		this.totalCountOfRatings = totalCountOfRatings;
	}

	public int getNumberOfFavorites() {
		return numberOfFavorites;
	}

	public void setNumberOfFavorites(int numberOfFavorites) {
		this.numberOfFavorites = numberOfFavorites;
	}
	
	@Override
	public String toString() {
		return "User{" +
                "id: " + id +
                ", sumOfRatings: " + sumOfRatings +
                ", totalCountOfRatings: " + totalCountOfRatings +
                ", numberOfFavorites: " + numberOfFavorites +
                '}';
	}
	
}
