package cs.usfca.edu.histfavcheckout;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import cs.usfca.edu.histfavcheckout.utils.Config;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication 
@EnableSwagger2
@EnableJpaRepositories(basePackages="cs.usfca.edu.histfavcheckout")
@EntityScan(basePackages="cs.usfca.edu.histfavcheckout")
@ComponentScan(basePackages="cs.usfca.edu.histfavcheckout")
public class HistFavCheckout {
	private static Config config;
	public static void main(String[] args) {
		try {
			setConfig(Config.readConfig("Config.json"));
		} catch (IOException e) {
			System.out.println("Error in opening configuration file!");
			e.printStackTrace();
		}
		SpringApplication.run(HistFavCheckout.class, args);
	}
	public static Config getConfig() {
		return config;
	}
	public static void setConfig(Config config) {
		HistFavCheckout.config = config;
	}
}
