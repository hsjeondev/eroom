package com.eroom.chat.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eroom.chat.dto.ChatMessageDto;
import com.eroom.chat.entity.ChatAlarm;
import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.repository.ChatAlarmRepository;
import com.eroom.chat.repository.ChatMessageRepository;
import com.eroom.chat.repository.ChatroomRepository;
import com.eroom.chat.specification.ChatMessageSpecification;
import com.eroom.drive.entity.Drive;
import com.eroom.employee.entity.Employee;
import com.eroom.websocket.ChatWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
	
	private final ChatroomRepository repository;
	private final ChatMessageRepository chatMessageRepository;
	private final ChatAlarmRepository	chatAlarmRepository;
	private final ChatWebSocketHandler chatWebSocketHandler;
	
	public List<ChatMessage> selectMessageByRoomNo(Long roomNo) {
		// 채팅방 번호로 채팅 메시지 조회
		Chatroom chatroom = repository.findById(roomNo).orElse(null);
		// 채팅방이 존재하지 않으면 null 반환
		Specification<ChatMessage> spec = (root, query, CriteriaBuilder) -> null;
		// 채팅방이 존재하면 조건 추가
		spec = spec.and(ChatMessageSpecification.roomNoEquals(chatroom));
		return chatMessageRepository.findAll(spec);
	}

	public void sendAndBroadcast(ChatMessageDto dto) {
	    // 메시지 저장
	    ChatMessage chatMessage = chatMessageRepository.save(
	        ChatMessage.builder()
	            .chatroom(Chatroom.builder().chatroomNo(dto.getChatroomNo()).build())
	            .senderMember(Employee.builder().employeeNo(dto.getSenderMember()).build())
	            .chatMessageContent(dto.getChatMessageContent())
	            .drive(dto.getDriveAttachNo() != null 
	            ? Drive.builder().driveAttachNo(dto.getDriveAttachNo()).build() 
	            : null)
	            .build()
	    );

	    // 알림 저장
	    if (dto.getReceiverMember() != null) {
	        chatAlarmRepository.save(ChatAlarm.builder()
	            .chatMessage(chatMessage)
	            .employee(Employee.builder().employeeNo(dto.getReceiverMember()).build())
	            .chatAlarmReadYn("N")
	            .build());
	    } else {
	        Chatroom chatroom = repository.findByIdWithAttendees(dto.getChatroomNo());
	        for (ChatroomAttendee attendee : chatroom.getChatroomMapping()) {
	            Long participantNo = attendee.getAttendee().getEmployeeNo();
	            if (!participantNo.equals(dto.getSenderMember())) {
	                chatAlarmRepository.save(ChatAlarm.builder()
	                    .chatMessage(chatMessage)
	                    .employee(Employee.builder().employeeNo(participantNo).build())
	                    .chatAlarmReadYn("N")
	                    .build());
	            }
	        }
	    }
	    // WebSocket 전송
	    try {
	        chatWebSocketHandler.broadcastMessage(dto, chatMessage);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	
	
}