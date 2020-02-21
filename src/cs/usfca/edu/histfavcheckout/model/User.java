package cs.usfca.edu.histfavcheckout.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
public class User {
	@EmbeddedId
    private PrimaryKey id;
	@Column(nullable = false)
	private boolean favourites = false;
	@Column(nullable = false)
	private boolean checkouts = false;
	@Column
	@Min(1)
	@Max(5)
	private int rating;  
	@Temporal(TemporalType.DATE)
    private Date expectedReturnDate;
	@Temporal(TemporalType.DATE)
	private Date actualReturnDate;
	
	public User() {}
	
	public User(PrimaryKey id) {
		this.id = id;
	}

	public PrimaryKey getId() {
		return id;
	}

	public void setId(PrimaryKey id) {
		this.id = id;
	}

	public boolean isFavourites() {
		return favourites;
	}

	public void setFavourites(boolean favourites) {
		this.favourites = favourites;
	}

	public boolean isCheckouts() {
		return checkouts;
	}

	public void setCheckouts(boolean checkouts) {
		this.checkouts = checkouts;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Date getExpectedReturnDate() {
		return expectedReturnDate;
	}

	public void setExpectedReturnDate(Date expectedReturnDate) {
		this.expectedReturnDate = expectedReturnDate;
	}

	public Date getActualReturnDate() {
		return actualReturnDate;
	}

	public void setActualReturnDate(Date actualReturnDate) {
		this.actualReturnDate = actualReturnDate;
	}
	
	@Override
	public String toString() {
		return "User{" +
                "id: " + id +
                ", favourites: " + favourites +
                ", checkouts: " + checkouts +
                ", rating: " + rating +
                ", expectedReturnDate: " + expectedReturnDate +
                ", actualReturnDate: " + actualReturnDate +
                '}';
	}
}
