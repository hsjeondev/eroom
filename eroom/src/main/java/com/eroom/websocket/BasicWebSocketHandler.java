package com.eroom.websocket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class BasicWebSocketHandler extends TextWebSocketHandler{

	private static final List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();
	
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 클라이언트와 연결이 되었을 때
		sessionList.add(session);
	}
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 클라이언트에서 메시지를 보냈을 때
		String payload = message.getPayload();
		System.out.println("서버 에서 받은 메시지 : " + payload);
		
		for(WebSocketSession temp : sessionList) {
			temp.sendMessage(new TextMessage(payload));
		}
	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 클라이언트와 연결이 종료되었을 때
		sessionList.remove(session);
	}
	
}
