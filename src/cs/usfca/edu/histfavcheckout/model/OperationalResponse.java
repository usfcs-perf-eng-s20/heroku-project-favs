package cs.usfca.edu.histfavcheckout.model;

import java.io.Serializable;

public class OperationalResponse implements Serializable  {

	private boolean confirm;
	private String message;
	
	public OperationalResponse() {}
	
	public OperationalResponse(boolean confirm) {
		this.confirm = confirm;
	}
	
	public OperationalResponse(boolean confirm, String message) {
		this.confirm = confirm;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	
	
}
