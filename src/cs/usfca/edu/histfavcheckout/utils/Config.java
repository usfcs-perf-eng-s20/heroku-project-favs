package cs.usfca.edu.histfavcheckout.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class Config {
	public static Config config;
	private String searchMoviesURL;
	private String authURL;
	private String analyticsURL;
	private boolean ignoreExternalAPIs;
	
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
	
	public boolean getIgnoreExternalAPIs() {
		return this.ignoreExternalAPIs;
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
