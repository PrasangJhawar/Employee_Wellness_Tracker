package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //logging when CORS configuration is being applied
                logger.info("Configuring CORS mappings...");

                registry.addMapping("/**")  //allowing all endpoints
                        .allowedOrigins("http://localhost:5500")  //allowing frontend origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  //allowing HTTP methods
                        .allowedHeaders("*")  //headers
                        .allowCredentials(true);  //credentials like cookies

                logger.info("CORS configuration applied successfully.");//debugging
            }
        };
    }
}
