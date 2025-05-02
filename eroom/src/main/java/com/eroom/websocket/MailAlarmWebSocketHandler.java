package com.eroom.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class MailAlarmWebSocketHandler extends TextWebSocketHandler{

	
	 private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

	    @Override
	    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	        String senderNo = getQueryParam(session, "employeeNo");
	        if (senderNo != null) {
	            userSessions.put(Long.parseLong(senderNo), session);
	            System.out.println("📬 메일 알림 연결 완료: " + senderNo);
	        }
	    }

	    public void sendMailAlarm(Long receiverEmployeeNo, String message) throws Exception {
	        WebSocketSession session = userSessions.get(receiverEmployeeNo);
	        if (session != null && session.isOpen()) {
	            session.sendMessage(new TextMessage(message));
	        }
	    }

	    @Override
	    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	        String senderNo = getQueryParam(session, "employeeNo");
	        if (senderNo != null) {
	            userSessions.remove(Long.parseLong(senderNo));
	            System.out.println("❌ 메일 알림 연결 종료: " + senderNo);
	        }
	    }

	    private String getQueryParam(WebSocketSession session, String param) {
	        String query = session.getUri().getQuery();
	        if (query != null) {
	            for (String pair : query.split("&")) {
	                String[] kv = pair.split("=");
	                if (kv.length == 2 && kv[0].equals(param)) {
	                    return kv[1];
	                }
	            }
	        }
	        return null;
	    }
}
