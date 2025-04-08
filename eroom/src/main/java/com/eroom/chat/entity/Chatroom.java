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
public class Chatroom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chatroom_no")
	private Long chatroomNo; // 채팅방번호
	
	@Column(name="chatroom_name")
	private String chatroomName; // 채팅방 이름
	
	@Column(nullable=false, name="chat_is_group")
	private String chatIsGroup; // 그룹채팅 여부
	
	// creater(직원번호) FK 연결
	
	
	@CreationTimestamp
	@Column(updatable=false, name="chatroom_reg_date")
	private LocalDateTime chatroomRegDate; // 생성일
	
	@UpdateTimestamp
	@Column(insertable=false ,name="chatroom_mod_date")
	private LocalDateTime chatroomModDate; // 수정일
	
	
	
	
}
