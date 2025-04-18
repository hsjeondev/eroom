package com.eroom.websocket;

import java.util.HashMap;
import java.util.Map;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler{

	private final ChatMessageRepository chatMessageRepository;
	
	// WebSocketSession을 저장할 Map
	private static final Map<Long,WebSocketSession> userSessions = new HashMap<Long,WebSocketSession>();
	// 방 번호를 저장할 Map
	private static final Map<Long,Long> userRooms = new HashMap<Long,Long>();
	
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
		// 메시지 처리 로직
		ObjectMapper objectMapper = new ObjectMapper();
		ChatMessageDto chatMessageDto = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);
		
		// 메시지를 DB에 저장
		if(userSessions.containsKey(chatMessageDto.getSenderMember())) {
			// DB에 메시지 저장
			Employee employee = Employee.builder().employeeNo(chatMessageDto.getSenderMember()).build();
			// 채팅방 번호를 가져옴
			Chatroom chatroom = Chatroom.builder().chatroomNo(chatMessageDto.getChatroomNo()).build();
			// 메시지 저장
			ChatMessage entity = ChatMessage.builder()
						                    .senderMember(employee)
						                    .chatroomNo(chatroom)
						                    .chatMessageContent(chatMessageDto.getChatMessageContent())
						                    .build();
			// DB에 저장
			ChatMessage saveMessage = chatMessageRepository.save(entity);
		}
		// 메시지를 받는 사람의 세션을 가져옴
		WebSocketSession receiverSession = userSessions.get(chatMessageDto.getReceiverMember());
		// 받는 사람의 방 번호를 가져옴
		Long receiverRoom = userRooms.get(chatMessageDto.getReceiverMember());
		
		System.out.println("### 수신자 세션 존재 여부: " + (receiverSession != null));
		System.out.println("### 수신자 세션 오픈 여부: " + (receiverSession != null ? receiverSession.isOpen() : "세션없음"));
		System.out.println("### 수신자 방번호: " + receiverRoom + ", 현재 메시지 방번호: " + chatMessageDto.getChatroomNo());

		
		// 메시지를 받는 사람의 세션이 열려있고, 방 번호가 같으면 메시지를 전송
		if (receiverSession != null && receiverSession.isOpen() && receiverRoom != null && receiverRoom.equals(chatMessageDto.getChatroomNo())) {
			receiverSession.sendMessage(new TextMessage(message.getPayload()));
		}
		// 메시지를 보내는 사람의 세션을 가져옴
		WebSocketSession senderSession = userSessions.get(chatMessageDto.getSenderMember());
		// 보내는 사람의 방 번호를 가져옴
		Long senderRoom = userRooms.get(chatMessageDto.getSenderMember());
		// 메시지를 보내는 사람의 세션이 열려있고, 방 번호가 같으면 메시지를 전송
		if (senderSession != null && senderSession.isOpen() && senderRoom.equals(chatMessageDto.getChatroomNo())) {
			senderSession.sendMessage(new TextMessage(message.getPayload()));
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
