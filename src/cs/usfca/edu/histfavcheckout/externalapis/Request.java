package cs.usfca.edu.histfavcheckout.externalapis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Request {
	
	public URL url(String url) {
		try {
			return new URL(url);
		}
		catch(MalformedURLException e) {
			System.out.println("MALFORMED EXCEPTION : " + url);
			return null;
		}
	}
	
	public HttpURLConnection connect(URL url, String requestMethod) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(requestMethod);
		} 
		catch (IOException e) {
//			e.printStackTrace();
		}
		return con;
	}
	
	public HttpURLConnection connect(URL url, String requestMethod, String contentType, String authorization) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(requestMethod);
			if(contentType != null && !contentType.isEmpty())
				con.setRequestProperty("Content-Type", contentType);
			if(authorization != null && !authorization.isEmpty())
				con.setRequestProperty("Authorization", authorization);
		} 
		catch (IOException e) {
//			e.printStackTrace();
		}
		return con;
	}
	
	public String getResponse(HttpURLConnection con) {
		try {
			int responseCode = con.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = input.readLine()) != null) {
					response.append(inputLine);
				}
				input.close();
				return response.toString();
			}
		} catch (IOException e) {
			System.out.println("COULD NOT CONNECT OR READ RESPONSE : " + con.getURL());
		}
		
		return null;
	}
	
	public boolean writeToBody(HttpURLConnection con, String putBody) {
		con.setDoOutput(true);
		 try {
			OutputStream out = con.getOutputStream();
			out.write(putBody.getBytes());
			out.flush();
			out.close();
			return true;
		} catch (IOException e) {
			System.out.println("COULD NOT GET OUTPUTSTREAM OF CONNECTION : " + con.getURL());
			return false;
		}
		
	}

}
