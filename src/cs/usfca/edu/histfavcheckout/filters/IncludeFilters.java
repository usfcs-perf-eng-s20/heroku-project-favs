package cs.usfca.edu.histfavcheckout.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IncludeFilters {
	@Bean
	public FilterRegistrationBean<AuthenticationFilter> filter() {
		FilterRegistrationBean<AuthenticationFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new AuthenticationFilter());
		bean.addUrlPatterns("/rateMovie", "/totalFavesAndCheckouts", "/checkedOutMovies", 
				"/returnMovie", "/checkOutMovie", "/user", "/favoriteMovie");
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean<SplunkLogFilter> splunkFilter() {
		FilterRegistrationBean<SplunkLogFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new SplunkLogFilter());
		bean.addUrlPatterns("*");
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean<CORSFilter> corsFilter() {
		FilterRegistrationBean<CORSFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new CORSFilter());
		bean.addUrlPatterns("*");
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean<AnalyticsFilter> analyticsFilter() {
		FilterRegistrationBean<AnalyticsFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new AnalyticsFilter());
		bean.addUrlPatterns("*");
		return bean;
	}
	
}
