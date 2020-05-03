package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

public class UserInfoResponse {
	private List<UserInfo> users;
	
	public void setUsers(List<UserInfo> users) {
		this.users = users;
	}
	
	public List<UserInfo> getUsers() {
		return users;
	}
	
	public UserInfo newUserInfo() {
		return new UserInfo();
	}
	
	@RedisHash(value = "UserInfoData", timeToLive = 300)
	public static class UserInfo {
		@Id
		private int userId;
		private String userName;
		private String email;
		
		public UserInfo() {}
		
		public UserInfo(int userId, String userName, String email) {
			this.userId = userId;
			this.userName = userName;
			this.email = email;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		
		
	}
}
