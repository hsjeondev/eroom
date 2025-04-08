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
	// 메시지번호
	private Long chatMessageNo;
	
	// 챗룸번호 FK
	
	@Column(name="chat_message_content")
	// 메시지 내용
	private String chatMessageContent;
	
	@CreationTimestamp
	@Column(updatable=false, name="message_reg_date")
	// 보낸시간
	private LocalDateTime messageRegDate;
	
	@Column(nullable=false, name="message_is_deleted_yn")
	// 삭제여부
	private String messageIsDeletedYn;
	
	// 사번 FK
	
	
	
}
