package com.eroom.chat.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.eroom.employee.entity.Employee;

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
	private Long chatAlarmNo; // 채팅알림번호
	 
	@ManyToOne
	@JoinColumn(name="chat_message_no")
	private ChatMessage chatMessage; // 채팅메시지번호 FK
	
	@Builder.Default
	@Column(nullable=false, name="chat_alarm_read_yn")
	private String chatAlarmReadYn = "N"; // 확인여부
	
	@CreationTimestamp
	@Column(updatable=false, name="chat_alarm_reg_date")
	private LocalDateTime chatAlarmRegDate; // 알림 등록일
	                  
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee employee; // 알림 받을 사원번호 FK
	
	@Column(name="chat_alarm_read_date")
	private LocalDateTime chatAlarmReadDate; // 읽은 시간

}
