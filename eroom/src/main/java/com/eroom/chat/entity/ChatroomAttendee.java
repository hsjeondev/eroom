package com.eroom.chat.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.eroom.directory.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class ChatroomAttendee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chatroom_mapping_no")
	private Long chatroomMappingNo; // 채팅방매핑번호
	
	@ManyToOne
	@JoinColumn(name="chatroom_no")
	private Chatroom chatroomNo; // 채팅방이랑 조인
	
	@CreationTimestamp
	@Column(updatable=false, name="joined_at")
	private LocalDateTime joinedAt; // 채팅방 참여 일시
	
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee formMember; // 사번이랑 조인
}
