package com.eroom.chat.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="chat_message")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chat_message_no")
	private Long chatMessageNo; // 메시지번호
	
	// 챗룸번호 FK
	
	@Column(name="chat_message_content")
	private String chatMessageContent; // 메시지 내용
	
	@CreationTimestamp
	@Column(updatable=false, name="message_reg_date")
	private LocalDateTime messageRegDate; // 보낸시간
	
	@Column(nullable=false, name="message_is_deleted_yn")
	private String messageIsDeletedYn; // 삭제여부
	
	// 사번 FK
	
	
	
}
