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
				"/returnMovies", "/checkOutMovies", "/user", "/favoriteMovie");
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
