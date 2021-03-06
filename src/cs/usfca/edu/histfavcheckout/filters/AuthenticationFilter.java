package cs.usfca.edu.histfavcheckout.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;

import com.google.gson.Gson;

import cs.usfca.edu.histfavcheckout.externalapis.APIClient;
import cs.usfca.edu.histfavcheckout.model.AuthRequest;
import cs.usfca.edu.histfavcheckout.utils.LoggerHelper;
import cs.usfca.edu.histfavcheckout.utils.MultiReadHttpServletRequest;

@Order(0)
public class AuthenticationFilter implements Filter {

	private static Gson gson = new Gson();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest((HttpServletRequest) request);
		String userParam = multiReadRequest.getParameter("userId");
		int userId;
		if(userParam != null) {
			userId = Integer.parseInt(userParam);
		} else {
			BufferedReader input = new BufferedReader(new InputStreamReader(multiReadRequest.getInputStream()));
			String inputLine;
			StringBuffer responseBody = new StringBuffer();

			while ((inputLine = input.readLine()) != null) {
				responseBody.append(inputLine);
			}
			input.close();
			AuthRequest authRequest = gson.fromJson(responseBody.toString(), AuthRequest.class);
			userId = authRequest.getUserId();
		}
		if(APIClient.isAuthenticated(userId)) {
			chain.doFilter(multiReadRequest, response);
		}
		else {
			LoggerHelper.makeWarningLog("GET /isLoggedIn --> " + userId + " is not authorized.");
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
