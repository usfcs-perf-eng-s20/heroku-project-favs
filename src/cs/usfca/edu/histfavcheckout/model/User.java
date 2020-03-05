package cs.usfca.edu.histfavcheckout.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;

import org.springframework.data.annotation.Id;

@Entity
@Table(name = "UserEntity")
public class User {
	@EmbeddedId
	@Id
    private PrimaryKey id;
	@Column(nullable = false)
	private boolean favourites;
	@Column(nullable = false)
	private boolean checkouts;
	@Column(nullable = true)
	@Max(5)
	private int rating;  
	@Temporal(TemporalType.DATE)
    private Date expectedReturnDate;
	@Temporal(TemporalType.DATE)
	private Date actualReturnDate;
	
	public User() {
		this.favourites = false;
		this.checkouts = false;
		this.rating = -1;
	}
	
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
