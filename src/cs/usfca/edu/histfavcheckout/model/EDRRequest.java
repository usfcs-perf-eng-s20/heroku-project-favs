package cs.usfca.edu.histfavcheckout.model;

public class EDRRequest {
	
	private String method;
	private String path;
	private long processingTimeInMiliseconds;
	private String responseCode; // Will be changed to INT once changed by EDR team.
	private String serviceName;
	private boolean success; 
	private String timestamp; // Will be changed to LONG once changed by EDR team.
	private String username; // Set null for now will be deprecated by EDR team soon.
	
	public EDRRequest() {}
	
	public EDRRequest(String method, String path, long processingTimeInMiliseconds,
			String responseCode, String serviceName, boolean success, String timestamp, String username) {
		this.method = method;
		this.path = path;
		this.processingTimeInMiliseconds = processingTimeInMiliseconds;
		this.responseCode = responseCode;
		this.serviceName = serviceName;
		this.success = success;
		this.timestamp = timestamp;
		this.username = username;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getProcessingTimeInMiliseconds() {
		return processingTimeInMiliseconds;
	}

	public void setProcessingTimeInMiliseconds(long processingTimeInMiliseconds) {
		this.processingTimeInMiliseconds = processingTimeInMiliseconds;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
