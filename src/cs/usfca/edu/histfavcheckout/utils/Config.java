package cs.usfca.edu.histfavcheckout.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class Config {
	public static Config config;
	private String searchMoviesURL;
	private String authURL;
	private String analyticsURL;
	private String userInfoURL;
	private boolean useFavesAPIs;
	private boolean useSearchAPIs;
	private boolean useLoginAPIs;
	private boolean useAnalyticsAPIs;
	//getters
	public String getSearchMoviesURL() {
		return this.searchMoviesURL;
	}
	
	public String getAuthURL() {
		return this.authURL;
	}
	
	public String getAnalyticsURL() {
		return this.analyticsURL;
	}
	
	public String getUserInfoURL() {
		return this.userInfoURL;
	}
	
	public boolean getUseFavesAPIs() {
		return this.useFavesAPIs;
	}
	
	public void setUseFavesAPIs(boolean value) {
		config.useFavesAPIs = value;
	}
	
	public boolean getUseSearchAPIs() {
		return this.useSearchAPIs;
	}
	
	public void setUseSearchAPIs(boolean value) {
		config.useSearchAPIs = value;
	}
	public boolean getUseLoginAPIs() {
		return this.useLoginAPIs;
	}
	
	public void setUseLoginAPIs(boolean value) {
		config.useLoginAPIs = value;
	}
	public boolean getUseAnalyticsAPIs() {
		return this.useAnalyticsAPIs;
	}
	
	public void setUseAnalyticsAPIs(boolean value) {
		config.useAnalyticsAPIs = value;
	}
	
	//read config file and create class object
	public static Config readConfig(String path) throws IOException {
		Gson gson = new GsonBuilder().create();
		BufferedReader reader = null;
	
		try {
			reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.ISO_8859_1);
			config = gson.fromJson(reader, Config.class);
		}
		catch (NoSuchFileException i) {
			System.out.printf("MESSAGE : NO SUCH FILE : %s\n",path);
			System.exit(1);
		}
		catch (JsonSyntaxException i) {
			System.out.println("MESSAGE : JsonSyntaxException");
		}
		return config;
		
	}		

}
