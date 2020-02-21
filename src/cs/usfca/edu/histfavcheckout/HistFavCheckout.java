package cs.usfca.edu.histfavcheckout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication 
@EnableSwagger2
@EnableJpaRepositories(basePackages="cs.usfca.edu.histfavcheckout")
@EntityScan(basePackages="cs.usfca.edu.histfavcheckout")
@ComponentScan(basePackages="cs.usfca.edu.histfavcheckout")
public class HistFavCheckout {
	public static void main(String[] args) {
		SpringApplication.run(HistFavCheckout.class, args);
	}
}
