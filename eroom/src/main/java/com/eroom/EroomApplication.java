package com.eroom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class EroomApplication implements WebMvcConfigurer {

	@Value("${ffupload.location}")
	private String fileDir;
	
	public static void main(String[] args) {
		SpringApplication.run(EroomApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**")
		.addResourceLocations("file:///" + fileDir);
		 registry.addResourceHandler("/**")
         .addResourceLocations("classpath:/static/");
	}
	
}
