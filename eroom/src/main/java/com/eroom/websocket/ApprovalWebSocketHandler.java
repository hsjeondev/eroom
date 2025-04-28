package com.eroom.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalAlarm;
import com.eroom.approval.service.ApprovalAlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApprovalWebSocketHandler extends TextWebSocketHandler {

    // employeeNo -> 세션 매핑
    private static Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ApprovalAlarmService approvalAlarmService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결될 때 employeeNo 가져오기
        Long employeeNo = (Long) session.getAttributes().get("employeeNo");
        if (employeeNo != null) {
            sessions.put(employeeNo, session);
            log.info("웹소켓 연결됨: employeeNo={}", employeeNo);
        } else {
            log.warn("employeeNo가 세션에 없음. 연결 세션ID={}", session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("메시지 수신: {}", message.getPayload());
        // 현재는 수신 메시지 따로 처리할 필요 없음 (필요하면 나중에 추가 가능)
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // 연결 끊길 때 employeeNo 기준으로 세션 정리
        Long employeeNo = (Long) session.getAttributes().get("employeeNo");
        if (employeeNo != null) {
            sessions.remove(employeeNo);
            log.info("웹소켓 연결 종료: employeeNo={}", employeeNo);
        }
    }

    // ApprovalService에서 호출할 메소드
    public void sendApprovalNotification(Long receiverNo, String message, Approval approval) {
        WebSocketSession session = sessions.get(receiverNo);

        if (session != null && session.isOpen()) {
            try {
            	saveNotificationToDatabase(receiverNo, message, approval);
                session.sendMessage(new TextMessage(message));
                log.info("알림 전송 성공: employeeNo={}", receiverNo);
            } catch (Exception e) {
                log.error("알림 전송 실패: employeeNo=" + receiverNo, e);
            }
        } else {
            log.warn("알림 전송 실패: 세션이 존재하지 않거나 닫힘. employeeNo={}", receiverNo);
        }
    }
    
    // 알람 저장용
    private void saveNotificationToDatabase(Long receiverNo, String message, Approval approval) {
    	approvalAlarmService.alarmSaveMethod(receiverNo, message, approval);
    }
}
