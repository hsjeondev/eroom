package com.eroom.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompanyAlarmSocketHandler extends TextWebSocketHandler {
	 private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet(); // 1. 세션 저장

	    @Override
	    public void afterConnectionEstablished(WebSocketSession session) {
	        sessions.add(session); // 2. 연결되면 추가
	    }

	    @Override
	    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
	        sessions.remove(session); // 3. 연결 끊기면 제거
	    }

	    public void broadcast(String message) { // 4. 메시지 뿌리기
	        for (WebSocketSession session : sessions) {
	            try {
	                session.sendMessage(new TextMessage(message));
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}


