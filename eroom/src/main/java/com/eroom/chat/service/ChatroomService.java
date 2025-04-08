package com.eroom.chat.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.repository.ChatroomRepository;
import com.eroom.chat.specification.ChatroomSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatroomService {

	private final ChatroomRepository repository;
	
	public List<Chatroom> selectChatRoomAll(){
		// 현재 로그인한 사람 설정해야됨
		
		
		// 채팅방 설정
		Specification<Chatroom> spec = (root, query, criteriaBuilder) -> null;
		// 생성자가 나인 경우
		spec = spec.and(ChatroomSpecification.fromUserEquals(null));
		// 생성자가 상대방인 경우
		spec = spec.or(ChatroomSpecification.toUserEquals(null));
		
		List<Chatroom> list = repository.findAll();
		return list;
	}

	
}
