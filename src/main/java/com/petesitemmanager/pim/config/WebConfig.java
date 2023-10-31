package com.petesitemmanager.pim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://pimfr.s3-website.us-east-2.amazonaws.com/",
                        "https://pim-callback-7a00565e0d62.herokuapp.com/")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
        // .allowedOrigins("http://pimfr.s3-website.us-east-2.amazonaws.com/")
    }
}
