package com.eroom.websocket;

import java.io.IOException;
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
public class MailAlarmWebSocketHandler extends TextWebSocketHandler {
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>(); // 사용자별 세션 관리

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userNo = getUserNoFromSession(session); // 세션에서 사용자 번호 추출
        if (userNo != null) {
            userSessions.put(userNo, session); // 해당 사용자 세션을 저장
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userNo = getUserNoFromSession(session); // 세션에서 사용자 번호 추출
        if (userNo != null) {
            userSessions.remove(userNo); // 세션이 종료되면 해당 사용자 세션을 제거
        }
    }

    // 특정 사용자에게 메일 알림 전송
    public void sendToUser(Long userNo, String message) throws IOException {
        WebSocketSession session = userSessions.get(userNo); // 사용자 세션 가져오기
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message)); // 세션에 메시지 전송
        }
    }

    // 사용자의 번호를 세션에서 추출하는 방법 (예시)
    private Long getUserNoFromSession(WebSocketSession session) {
        // 예시: 세션의 principal 또는 다른 정보를 사용하여 사용자 번호 추출
        String userNoStr = session.getPrincipal() != null ? session.getPrincipal().getName() : null;
        return userNoStr != null ? Long.parseLong(userNoStr) : null;
    }
}
