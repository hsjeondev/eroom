package com.eroom.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/filesdrive/chat/**")  // URL 접근 경로
                .addResourceLocations("file:///C:/dev/eroom/upload/drive/chat/"); // 실제 로컬 경로
    }
}
