package phishing_website_detector.Application;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootApplication
public class Website_risk_scoring_system {
 
	public static void main(String[] args) {
	
		SpringApplication.run(Website_risk_scoring_system.class, args);
		log.info("Application started");
		log.info("Starting Phishing Detection Backend Service...");
	}
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                		.allowedOriginPatterns("chrome-extension://*", "http://localhost:[*]")
                        .allowedMethods("GET")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
	}

}
