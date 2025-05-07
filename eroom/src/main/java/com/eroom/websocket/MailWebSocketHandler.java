package com.eroom.websocket;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.mail.dto.MailDto;
import com.eroom.mail.dto.MailReceiverDto;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailReceiver;
import com.eroom.mail.repository.MailReceiverRepository;
import com.eroom.mail.repository.MailRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailWebSocketHandler extends TextWebSocketHandler {

    private final MailRepository mailRepository;
    private final MailReceiverRepository mailReceiverRepository;
    private final EmployeeRepository employeeRepository;

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	String senderNo = getQueryParam(session, "senderNo");
//        Principal principal = session.getPrincipal();
//        String senderNo = principal.getName();
        if (senderNo != null) {
            userSessions.put(Long.parseLong(senderNo), session);
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MailDto dto = objectMapper.readValue(message.getPayload(), MailDto.class);

        // 저장할 메일 객체 생성
        Mail mail = dto.toEntity();

        // 메일 저장
        Mail savedMail = mailRepository.save(mail);

        // 수신자 및 참조자 처리
        for (Long receiverNo : dto.getReceiver_no()) {
            saveMailReceiver(savedMail, receiverNo);
        }
        for (Long ccNo : dto.getCc_no()) {
            saveMailReceiver(savedMail, ccNo); // 참조자 처리
        }

        // 메시지 브로드캐스트
        broadcastMessage(dto, savedMail);
    }

    private void saveMailReceiver(Mail mail, Long receiverNo) {
        // 수신자 정보 저장
        MailReceiverDto mailReceiverDto = new MailReceiverDto();
        mailReceiverDto.setMail_no(mail.getMailNo());
        mailReceiverDto.setEmployee_no(receiverNo);

        MailReceiver mailReceiver = mailReceiverDto.toEntity();
        mailReceiverRepository.save(mailReceiver);
    }

    private void broadcastMessage(MailDto dto, Mail savedMail) throws Exception {
        Employee sender = employeeRepository.findById(dto.getEmployee_no())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

        Map<String, Object> sendData = new HashMap<>();
        sendData.put("mailTitle", dto.getMail_title());
        sendData.put("mailContent", dto.getMail_content());
        sendData.put("senderMember", dto.getEmployee_no());
        sendData.put("senderName", sender.getEmployeeName());
        sendData.put("mailNo", savedMail.getMailNo());
        sendData.put("receiverNo", dto.getReceiver_no());

        String sendPayload = new ObjectMapper().writeValueAsString(sendData);

        for (Long receiverNo : dto.getReceiver_no()) {
            sendToUser(receiverNo, sendPayload);
        }
    }

    public void sendToUser(Long userNo, String message) throws Exception {
        WebSocketSession session = userSessions.get(userNo);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String senderNo = getQueryParam(session, "senderNo");
        if (senderNo != null) {
            userSessions.remove(Long.parseLong(senderNo));
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
