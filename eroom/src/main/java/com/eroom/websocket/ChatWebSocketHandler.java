package com.eroom.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.eroom.chat.dto.ChatMessageDto;
import com.eroom.chat.entity.ChatAlarm;
import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.repository.ChatAlarmRepository;
import com.eroom.chat.repository.ChatMessageRepository;
import com.eroom.chat.repository.ChatroomRepository;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatroomRepository chatroomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EmployeeRepository employeeRepository;
    private final ChatAlarmRepository chatAlarmRepository;
    private final DriveRepository driveRepository;
    
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String senderNo = getQueryParam(session, "senderNo");
        if (senderNo != null) {
            userSessions.put(Long.parseLong(senderNo), session);
            System.out.println("WebSocket 연결: senderNo=" + senderNo);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessageDto dto = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);

        // 저장할 메시지 객체 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .chatroom(Chatroom.builder().chatroomNo(dto.getChatroomNo()).build())
                .senderMember(Employee.builder().employeeNo(dto.getSenderMember()).build())
                .chatMessageContent(dto.getChatMessageContent())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 알림 저장
        if (dto.getReceiverMember() != null) {
            // 1:1 채팅 알림
            saveAlarm(savedMessage, dto.getReceiverMember());
        } else {
            // 그룹 채팅 알림 (보낸 사람 제외)
            Chatroom chatroom = chatroomRepository.findByIdWithAttendees(dto.getChatroomNo());
            for (ChatroomAttendee attendee : chatroom.getChatroomMapping()) {
                Long participantNo = attendee.getAttendee().getEmployeeNo();
                if (!participantNo.equals(dto.getSenderMember())) {
                    saveAlarm(savedMessage, participantNo);
                }
            }
        }

        // 메시지 브로드캐스트
        broadcastMessage(dto, savedMessage);
    }

    private void saveAlarm(ChatMessage message, Long receiverNo) {
        ChatAlarm alarm = ChatAlarm.builder()
                .chatMessage(message)
                .employee(Employee.builder().employeeNo(receiverNo).build())
                .chatAlarmReadYn("N")
                .build();
        chatAlarmRepository.save(alarm);
    }
    public void broadcastMessage(ChatMessageDto dto, ChatMessage savedMessage) throws Exception {
        Employee sender = employeeRepository.findById(dto.getSenderMember())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

        Map<String, Object> sendData = new HashMap<>();
        sendData.put("chatMessageContent", dto.getChatMessageContent());
        sendData.put("senderMember", dto.getSenderMember());
        sendData.put("senderName", sender.getEmployeeName());
        sendData.put("chatroomNo", dto.getChatroomNo());
        sendData.put("receiverMember", dto.getReceiverMember());

        // [파일] 메시지일 경우 drive 정보 추가
        if (dto.getChatMessageContent().startsWith("[파일]")) {
            String fileName = dto.getChatMessageContent().substring(5);
            Long chatroomNo = dto.getChatroomNo();
            List<Drive> drives = driveRepository.findByDriveOriNameAndParam1(fileName, chatroomNo);

            if (!drives.isEmpty()) {
                Drive drive = drives.get(0); // 첫 번째 드라이브 선택 (또는 여러 드라이브 처리)
                sendData.put("drivePath", "/files/drive/chat/" + drive.getDrivePath().replaceFirst("^drive/chat/", ""));
                sendData.put("driveOriName", drive.getDriveOriName());
            }
        }

        String sendPayload = new ObjectMapper().writeValueAsString(sendData);

        if (dto.getReceiverMember() != null) {
            sendToUser(dto.getReceiverMember(), sendPayload);
            sendToUser(dto.getSenderMember(), sendPayload);
        } else {
            Chatroom chatroom = chatroomRepository.findByIdWithAttendees(dto.getChatroomNo());
            for (ChatroomAttendee attendee : chatroom.getChatroomMapping()) {
                Long participantNo = attendee.getAttendee().getEmployeeNo();
                sendToUser(participantNo, sendPayload);
            }
        }
    }



    private void sendToUser(Long userNo, String message) throws Exception {
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
            System.out.println("❌ WebSocket 연결 종료: senderNo=" + senderNo);
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