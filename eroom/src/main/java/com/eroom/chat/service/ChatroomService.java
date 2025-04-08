package com.eroom.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.repository.ChatroomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatroomService {

	private final ChatroomRepository repository;
	
	public List<Chatroom> selectChatRoomAll(){
		List<Chatroom> list = repository.findAll();
		return list;
	}

	
}
