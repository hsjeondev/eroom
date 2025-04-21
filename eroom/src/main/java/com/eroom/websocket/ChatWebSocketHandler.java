package com.eroom.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.eroom.chat.dto.ChatMessageDto;
import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.repository.ChatMessageRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler{

	private final ChatMessageRepository chatMessageRepository;
	private final EmployeeRepository employeeRepository;
	// 사용자 세션을 저장할 Map
	private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
	// 사용자 방 번호를 저장할 Map
	// ConcurrentHashMap 을 사용하여 멀티스레드 환경에서도 안전하게 사용할 수 있도록 함
	private static final Map<Long, Long> userRooms = new ConcurrentHashMap<>();
	
	// WebSocket 연결 시 호출되는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// WebSocket 연결시 세션을 저장
		String senderNo = getQueryParam(session, "senderNo");
		String roomNo = getQueryParam(session, "roomNo");
				
		// 세션을 저장
		userSessions.put(Long.parseLong(senderNo), session);
		// 방 번호를 저장
		userRooms.put(Long.parseLong(senderNo), Long.parseLong(roomNo));
	}
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	    ObjectMapper objectMapper = new ObjectMapper();
	    ChatMessageDto chatMessageDto = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);

	    // (1) DB 저장
	    if (userSessions.containsKey(chatMessageDto.getSenderMember())) {
	        Employee employee = Employee.builder().employeeNo(chatMessageDto.getSenderMember()).build();
	        Chatroom chatroom = Chatroom.builder().chatroomNo(chatMessageDto.getChatroomNo()).build();
	        ChatMessage entity = ChatMessage.builder()
	                                .senderMember(employee)
	                                .chatroomNo(chatroom)
	                                .chatMessageContent(chatMessageDto.getChatMessageContent())
	                                .build();
	        chatMessageRepository.save(entity);
	    }
	    // (2) DB에서 저장된 메시지 조회
	    Employee sender = employeeRepository.findById(chatMessageDto.getSenderMember())
                .orElseThrow(() -> new IllegalArgumentException("직원이 존재하지 않습니다."));
	    Map<String, Object> sendData = new HashMap<>();
	    sendData.put("chatMessageContent", chatMessageDto.getChatMessageContent());
	    sendData.put("senderMember", chatMessageDto.getSenderMember());
	    sendData.put("senderName", sender.getEmployeeName()); // ⭐ 추가
	    sendData.put("chatroomNo", chatMessageDto.getChatroomNo());
	    sendData.put("receiverMember", chatMessageDto.getReceiverMember());

	    String sendPayload = objectMapper.writeValueAsString(sendData);

	    // 그룹 채팅 or 1:1 채팅 분기
	    if (chatMessageDto.getReceiverMember() == null) {
	        // 그룹 채팅 처리: 방번호 같은 모든 사람에게 보내기
	        for (Map.Entry<Long, Long> entry : userRooms.entrySet()) {
	            Long participantNo = entry.getKey();
	            Long participantRoomNo = entry.getValue();

	            if (participantRoomNo.equals(chatMessageDto.getChatroomNo())) {
	                WebSocketSession participantSession = userSessions.get(participantNo);
	                if (participantSession != null && participantSession.isOpen()) {
	                    participantSession.sendMessage(new TextMessage(sendPayload));
	                }
	            }
	        }
	    } else {
	        // 1:1 채팅 처리
	        WebSocketSession receiverSession = userSessions.get(chatMessageDto.getReceiverMember());
	        Long receiverRoom = userRooms.get(chatMessageDto.getReceiverMember());

	        if (receiverSession != null && receiverSession.isOpen() && receiverRoom != null && receiverRoom.equals(chatMessageDto.getChatroomNo())) {
	            receiverSession.sendMessage(new TextMessage(sendPayload));
	        }
	     // 내가 보낸 메시지도 화면에 보여야 하니까 나한테도 보내기
		    WebSocketSession senderSession = userSessions.get(chatMessageDto.getSenderMember());
		    Long senderRoom = userRooms.get(chatMessageDto.getSenderMember());
		    if (senderSession != null && senderSession.isOpen() && senderRoom.equals(chatMessageDto.getChatroomNo())) {
		        senderSession.sendMessage(new TextMessage(sendPayload));
		    }
	    }
	    
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// WebSocket 연결 종료 시 세션 제거
		String userNo = getQueryParam(session, "senderNo");
		// 세션 제거
		userSessions.remove(Long.parseLong(userNo));
		// 방 번호 제거
		userRooms.remove(Long.parseLong(userNo));
	}
	// WebSocket 연결 시 쿼리 파라미터를 가져오는 메서드
	private String getQueryParam(WebSocketSession session, String param) {
		// URI에서 쿼리 파라미터를 가져옴
		String query = session.getUri().getQuery();
		// query가 null이 아닐 때만 처리
		if(query != null) {
			String[] arr = query.split("&");
			for(String target : arr) {
				String[] keyArr = target.split("=");
				if (keyArr.length == 2 && keyArr[0].equals(param)) {
					return keyArr[1];
				}
			}
		}
		return null;
	}
}
