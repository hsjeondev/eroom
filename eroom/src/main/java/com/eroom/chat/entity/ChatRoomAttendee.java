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
@Table(name="chatroom_attendee")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomAttendee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chatroom_mapping_no")
	// 채팅방매핑번호
	private Long chatroomMappingNo;
	
	// chatroom_no FK 연결
	
	@CreationTimestamp
	@Column(updatable=false, name="joined_at")
	// 채팅방 참여 일시
	private LocalDateTime joinedAt;
	
	// 사번 FK 받아와야됨
	
}
