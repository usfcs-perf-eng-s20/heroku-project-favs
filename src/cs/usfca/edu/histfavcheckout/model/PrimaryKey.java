package cs.usfca.edu.histfavcheckout.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PrimaryKey {
	@Column(name="userId", nullable = false)
    private int userId;

    @Column(name="productId", nullable = false)
    private int productId;
    
    public PrimaryKey(int userId, int productId) {
    	this.userId = userId;
    	this.productId = productId;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}
    
    @Override
    public String toString() {
    	return "PrimaryKey: { userId: " + userId
    			+ ", productId: " + productId
    			+ "}";
    }
}
