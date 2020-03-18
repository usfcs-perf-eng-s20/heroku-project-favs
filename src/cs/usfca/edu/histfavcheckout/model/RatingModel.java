package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class RatingModel implements Serializable {
	private int id;
	private float averageRating;
	
	public RatingModel() {}
	
	public RatingModel(int id, float averageRating) {
		this.id = id;
		this.averageRating = averageRating;
	}

	public int getId() {
		return id;
	}

	public void setProductId(int id) {
		this.id = id;
	}

	public float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(this == o) {
			return true;
		}
		if(o instanceof RatingModel) {
			RatingModel obj = (RatingModel) o;
			if(obj.getId() == id && obj.getAverageRating() == averageRating)
				return true;
			else 
				return false;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}

}
