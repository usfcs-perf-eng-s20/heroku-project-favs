package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class CheckoutResponse implements Serializable  {

	private boolean confirm;
	
	public CheckoutResponse() {}
	
	public CheckoutResponse(boolean confirm) {
		this.confirm = confirm;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	
	
}
