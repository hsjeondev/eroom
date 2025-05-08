package com.eroom.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 채팅 첨부파일
        registry.addResourceHandler("/files/drive/chat/**")
                .addResourceLocations("file:///C:/dev/eroom/upload/drive/chat/");

        // 프로필 이미지
        registry.addResourceHandler("/files/profile/**")
                .addResourceLocations("file:///C:/dev/eroom/upload/profile/");
    }
}
