package com.eroom.chat.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.repository.ChatMessageRepository;
import com.eroom.chat.repository.ChatroomRepository;
import com.eroom.chat.specification.ChatMessageSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
	
	private final ChatroomRepository repository;
	private final ChatMessageRepository chatMessageRepository;
	
	public List<ChatMessage> selectMessageByRoomNo(Long roomNo) {
		// 채팅방 번호로 채팅 메시지 조회
		Chatroom chatroom = repository.findById(roomNo).orElse(null);
		// 채팅방이 존재하지 않으면 null 반환
		Specification<ChatMessage> spec = (root, query, CriteriaBuilder) -> null;
		// 채팅방이 존재하면 조건 추가
		spec = spec.and(ChatMessageSpecification.roomNoEquals(chatroom));
		return chatMessageRepository.findAll(spec);
	}

	
	
	
}
