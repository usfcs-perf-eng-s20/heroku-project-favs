package cs.usfca.edu.histfavcheckout.filters;

import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import cs.usfca.edu.histfavcheckout.utils.LoggerHelper;

@Order(1)
public class SplunkLogFilter implements Filter {
	private static final String SPLUNK_SERVICE_NAME = "favs";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(req);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(res);
		responseWrapper.setContentType("application/json");
		long timestamp = System.currentTimeMillis();
		chain.doFilter(requestWrapper, responseWrapper);
		boolean success = (res.getStatus() == HttpURLConnection.HTTP_OK);
		long processingTime = System.currentTimeMillis() - timestamp;
		splunkFormattedLogs(req, res, requestWrapper, responseWrapper, processingTime, success);
	}
	
	private void splunkFormattedLogs(HttpServletRequest req, HttpServletResponse res, ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper,  long processingTime, boolean success) throws IOException {
		String requestBody = new String(requestWrapper.getContentAsByteArray(),requestWrapper.getCharacterEncoding());
		String responseMessage = new String(responseWrapper.getContentAsByteArray(),responseWrapper.getCharacterEncoding());
		if(success) {
			LoggerHelper.makeInfoLog("serviceName=" + SPLUNK_SERVICE_NAME + ", method=" + req.getMethod() 
			+ ", path=" + req.getServletPath() + ", requestParams={" + req.getQueryString() + "}" + ", requestBody=" + requestBody 
			+ ", runTime=" + processingTime + ", status=" + res.getStatus() + ", error=0" + ", message=" + responseMessage);
			
		}
		else {
			LoggerHelper.makeInfoLog("serviceName=" + SPLUNK_SERVICE_NAME + ", method=" + req.getMethod() 
			+ ", path=" + req.getServletPath() + ", requestParams={" + req.getQueryString() + "}" + ", requestBody=" + requestBody 
			+ ", runTime=" + processingTime + ", status=" + res.getStatus() + ", error=1" + ", message=" + responseMessage);
		}
		responseWrapper.copyBodyToResponse();
	}

}
