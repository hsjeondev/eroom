package com.eroom.chat.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name="chatroom")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chatroom_no")
	// 채팅방번호
	private Long chatroomNo;
	
	@Column(name="chatroom_name")
	// 채팅방 이름
	private String chatroomName;
	
	@Column(nullable=false, name="chat_is_group")
	// 그룹채팅 여부
	private String chatIsGroup;
	
	// creater(직원번호) FK 연결
	
	
	@CreationTimestamp
	@Column(updatable=false, name="chatroom_reg_date")
	// 생성일
	private LocalDateTime chatroomRegDate;
	
	@UpdateTimestamp
	@Column(insertable=false ,name="chatroom_mod_date")
	// 수정일
	private LocalDateTime chatroomModDate;
	
	
	
	
}
