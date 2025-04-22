package com.eroom.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.eroom.chat.dto.ChatMessageDto;
import com.eroom.chat.entity.ChatAlarm;
import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.repository.ChatAlarmRepository;
import com.eroom.chat.repository.ChatMessageRepository;
import com.eroom.chat.repository.ChatroomRepository;
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

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private static final Map<Long, Long> userRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object senderNoObj = session.getAttributes().get("senderNo");
        if (senderNoObj != null) {
            Long senderNo = Long.parseLong(senderNoObj.toString());
            System.out.println("연결된 senderNo: " + senderNo);
            userSessions.put(senderNo, session);
        } else {
            System.out.println("senderNo를 찾을 수 없습니다!");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessageDto chatMessageDto = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);
        System.out.println("📦 수신된 메시지 DTO: " + chatMessageDto);
        System.out.println("📦 DTO senderMember=" + chatMessageDto.getSenderMember() + ", chatroomNo=" + chatMessageDto.getChatroomNo());
        // userRooms.put(chatMessageDto.getSenderMember(), chatMessageDto.getChatroomNo());
        // 1. DB 저장
        ChatMessage savedMessage = null;
        if (userSessions.containsKey(chatMessageDto.getSenderMember())) {
            Employee employee = Employee.builder().employeeNo(chatMessageDto.getSenderMember()).build();
            Chatroom chatroom = Chatroom.builder().chatroomNo(chatMessageDto.getChatroomNo()).build();
            ChatMessage entity = ChatMessage.builder()
                    .senderMember(employee)
                    .chatroom(chatroom)
                    .chatMessageContent(chatMessageDto.getChatMessageContent())
                    .build();
            savedMessage = chatMessageRepository.save(entity);
        }

        if (savedMessage != null) {
            if (chatMessageDto.getReceiverMember() != null) {
                // 1:1 채팅 알람 저장
                ChatAlarm alarm = ChatAlarm.builder()
                        .chatMessage(savedMessage)
                        .employee(Employee.builder().employeeNo(chatMessageDto.getReceiverMember()).build())
                        .chatAlarmReadYn("N")
                        .build();
                chatAlarmRepository.save(alarm);
            } else {
                // 그룹 채팅 알람 저장 (자기 제외)
                List<Employee> participants = getParticipants(chatMessageDto.getChatroomNo());
                for (Employee participant : participants) {
                    if (!participant.getEmployeeNo().equals(chatMessageDto.getSenderMember())) {
                        ChatAlarm alarm = ChatAlarm.builder()
                                .chatMessage(savedMessage)
                                .employee(participant)
                                .chatAlarmReadYn("N")
                                .build();
                        chatAlarmRepository.save(alarm);
                    }

                }
            }
            System.out.println("🔵 현재 userSessions: " + userSessions.keySet());
            System.out.println("🟣 현재 userRooms: " + userRooms);
            System.out.println("🟡 메시지 보낼 방 번호: " + chatMessageDto.getChatroomNo());

        }

        // 2. WebSocket 브로드캐스트
        Employee sender = employeeRepository.findById(chatMessageDto.getSenderMember())
                .orElseThrow(() -> new IllegalArgumentException("직원이 존재하지 않습니다."));

        Map<String, Object> sendData = new HashMap<>();
        sendData.put("chatMessageContent", chatMessageDto.getChatMessageContent());
        sendData.put("senderMember", chatMessageDto.getSenderMember());
        sendData.put("senderName", sender.getEmployeeName());
        sendData.put("chatroomNo", chatMessageDto.getChatroomNo());
        sendData.put("receiverMember", chatMessageDto.getReceiverMember());

        String sendPayload = objectMapper.writeValueAsString(sendData);
        if (chatMessageDto.getReceiverMember() == null) {
            // 그룹 채팅
            List<Employee> participants = getParticipants(chatMessageDto.getChatroomNo());
            for (Employee participant : participants) {
                WebSocketSession participantSession = userSessions.get(participant.getEmployeeNo());
                if (participantSession != null && participantSession.isOpen()) {
                    participantSession.sendMessage(new TextMessage(sendPayload));
                }
            }
        } else {
            // 1:1 채팅: 상대방 + 나 모두에게
            WebSocketSession receiverSession = userSessions.get(chatMessageDto.getReceiverMember());
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(sendPayload));
            }
            WebSocketSession senderSession = userSessions.get(chatMessageDto.getSenderMember());
            if (senderSession != null && senderSession.isOpen()) {
                senderSession.sendMessage(new TextMessage(sendPayload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userNo = getQueryParam(session, "senderNo");
        userSessions.remove(Long.parseLong(userNo));
        userRooms.remove(Long.parseLong(userNo));
    }

    private String getQueryParam(WebSocketSession session, String param) {
        String query = session.getUri().getQuery();
        if (query != null) {
            String[] arr = query.split("&");
            for (String target : arr) {
                String[] keyArr = target.split("=");
                if (keyArr.length == 2 && keyArr[0].equals(param)) {
                    return keyArr[1];
                }
            }
        }
        return null;
    }
 // WebSocket 외부에서도 방 등록할 수 있도록 static 메서드 추가
    public static void registerUserRoom(Long senderNo, Long chatroomNo) {
        userRooms.put(senderNo, chatroomNo);
        System.out.println("ChatWebSocketHandler.registerUserRoom: senderNo=" + senderNo + ", chatroomNo=" + chatroomNo);
    }
    private List<Employee> getParticipants(Long chatroomNo) {
        Chatroom chatroom = chatroomRepository.findByIdWithAttendees(chatroomNo);
        if (chatroom == null) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }
        return chatroom.getChatroomMapping().stream()
                .map(mapping -> mapping.getAttendee())
                .collect(Collectors.toList());
    }
}