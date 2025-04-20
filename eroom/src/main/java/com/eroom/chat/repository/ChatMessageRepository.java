package com.eroom.chat.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.employee.entity.Employee;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
	// 채팅 메시지 전체 조회
	List<ChatMessage> findAll(Specification<ChatMessage> spec);
	
	// 메시지 번호로 1개 조회
    ChatMessage findByChatMessageNo(Long chatMessageNo);

    // 채팅방으로 여러 메시지 조회
    List<ChatMessage> findByChatroomNo(Chatroom chatroom);

    // 보낸 사람으로 여러 메시지 조회
    List<ChatMessage> findBySenderMember(Employee senderMember);

    // 메시지 내용으로 메시지 검색 (1개)
    ChatMessage findByChatMessageContent(String chatMessageContent);
	// 메시지 내용으로 여러 메시지 검색
    @Query("SELECT m FROM ChatMessage m WHERE m.chatroomNo.chatroomNo = :roomNo ORDER BY m.messageRegDate ASC")
    List<ChatMessage> findByChatroomNo(@Param("roomNo") Long roomNo);
}
