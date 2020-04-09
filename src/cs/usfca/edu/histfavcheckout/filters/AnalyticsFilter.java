package cs.usfca.edu.histfavcheckout.filters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;

import cs.usfca.edu.histfavcheckout.externalapis.APIClient;
import cs.usfca.edu.histfavcheckout.model.EDRRequest;
import cs.usfca.edu.histfavcheckout.utils.LoggerHelper;

@Order(1)
public class AnalyticsFilter implements Filter {
	private static final String SERVICE_NAME = "HIST-FAV-CHECKOUT";
	private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		long timestamp = System.currentTimeMillis();
		chain.doFilter(request, response);
		boolean success = (res.getStatus() == HttpURLConnection.HTTP_OK);
		sequentialEDR(req,res,timestamp,success);
// 		executor.execute(new Runnable() {
// 			@Override
// 			public void run() {
// 				EDRRequest edrRequest = new EDRRequest(req.getMethod(), req.getPathInfo(),
// 						System.currentTimeMillis() - timestamp, String.valueOf(res.getStatus()), SERVICE_NAME,
// 						success, String.valueOf(timestamp), "");
// 				try {
// 					boolean success = APIClient.sendEDR(edrRequest);
// 					if(!success) {
// 						LoggerHelper.makeInfoLog("POST /saveEDR --> " + "EDR Event responded with responseCode: " + res.getStatus());
// 					}
// 				} catch (IOException e) {
// 					LoggerHelper.makeSevereLog("POST /saveEDR --> error! Message: " + e.getMessage());
// 					e.printStackTrace();
// 				}
// 			}
// 		});
// 	}

	public void sequentialEDR(HttpServletRequest req, HttpServletResponse res, long timestamp, boolean success) {
		EDRRequest edrRequest = new EDRRequest(req.getMethod(), req.getPathInfo(),
				System.currentTimeMillis() - timestamp, String.valueOf(res.getStatus()), SERVICE_NAME,
				success, String.valueOf(timestamp), "");
		try {
			success = APIClient.sendEDR(edrRequest);
			if(!success) {
				LoggerHelper.makeInfoLog("POST /saveEDR --> " + "EDR Event responded with responseCode: " + res.getStatus());
			}
		} catch (IOException e) {
			LoggerHelper.makeSevereLog("POST /saveEDR --> error! Message: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
