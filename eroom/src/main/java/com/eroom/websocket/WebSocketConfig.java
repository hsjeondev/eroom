package com.eroom.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;
//환경설정
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer{

	private final BasicWebSocketHandler basicWebSocketHandler;
	private final ChatWebSocketHandler chatWebSocketHandler;
	private final ApprovalWebSocketHandler approvalWebSocketHandler;
	private final CompanyAlarmSocketHandler companyAlarmSocketHandler;
	private final TeamAlarmSocketHandler teamAlarmSocketHandler;
	 private final MailWebSocketHandler mailWebSocketHandler;
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(basicWebSocketHandler, "/ws/basic")
		        .addInterceptors(new CustomHandshakeInterceptor()) // 핸드쉐이크 인터셉터 추가
				.setAllowedOrigins("http://localhost:8080");
		
		registry.addHandler(chatWebSocketHandler, "/ws/chat")
				.addInterceptors(new CustomHandshakeInterceptor())
				.setAllowedOrigins("http://localhost:8080");
		
		registry.addHandler(approvalWebSocketHandler, "/ws/approval")
		.addInterceptors(new CustomHandshakeInterceptor())
		.setAllowedOrigins("http://localhost:8080");
		
		registry.addHandler(companyAlarmSocketHandler, "/ws/company")
        .addInterceptors(new CustomHandshakeInterceptor())
        .setAllowedOrigins("http://localhost:8080");
		
		 registry.addHandler(teamAlarmSocketHandler, "/ws/team")
         .addInterceptors(new CustomHandshakeInterceptor())
         .setAllowedOrigins("http://localhost:8080");
		 
		 registry.addHandler(mailWebSocketHandler, "/ws/mail")
         .addInterceptors(new CustomHandshakeInterceptor())
         .setAllowedOrigins("http://localhost:8080");
		
	}
}
