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
@Table(name="chat_alarm")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatAlarm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chat_alarm_no")
	// 채팅알림번호
	private Long chatAlarmNo;
	
	// 채팅메시지 FK 
	
	@Column(nullable=false, name="chat_alarm_read_yn")
	// 확인여부
	private String chatAlarmReadYn;
	
	@CreationTimestamp
	@Column(updatable=false, name="chat_alarm_reg_date")
	// 등록일
	private LocalDateTime chatAlarmRegDate;
	                  
	
	
	
}
