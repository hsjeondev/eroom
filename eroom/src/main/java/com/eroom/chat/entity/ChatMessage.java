package com.eroom.chat.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.eroom.drive.entity.Drive;
import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	
	@ManyToOne
	@JoinColumn(name="chatroom_no")
	private Chatroom chatroom; // 챗룸번호
	
	@ManyToOne
	@JoinColumn(name="sender_member")
	private Employee senderMember; // 보낸 사람
	
	@Column(name="chat_message_content")
	private String chatMessageContent; // 메시지 내용
	
	@CreationTimestamp
	@Column(updatable=false, name="message_reg_date")
	private LocalDateTime messageRegDate; // 보낸시간
	
	@Builder.Default
	@Column(nullable=false, name="message_is_deleted_yn")
	private String messageIsDeletedYn = "N"; // 삭제여부
	
	@OneToMany(mappedBy = "chatMessage")
	private List<ChatAlarm> chatAlarmList; // 채팅 알림 리스트
	
	@ManyToOne
	@JoinColumn(name = "drive_attach_no")
	private Drive drive;
	
}