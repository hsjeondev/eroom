package com.eroom.notification.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.eroom.approval.entity.ApprovalAlarm;
import com.eroom.calendar.entity.CalendarAlarm;
import com.eroom.chat.entity.ChatAlarm;
import com.eroom.mail.entity.MailAlarm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alarm")
@Builder
@Getter
public class Alarm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_no")
	private Long alarmNo;
	
	@Column(name = "employee_no")
	private Long employeeNo;
	
	@Column(name = "param1")
	private Long param1;
	
	@Column(name = "separator_code")
	private String separatorCode;
	
	@Column(name = "read_yn")
	private String readYn;
	
	@Column(name = "reg_date")
	@CreationTimestamp
	private LocalDateTime regDate;
	
    
    //메일 알람
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param1", referencedColumnName = "mail_alarm_no", insertable = false, updatable = false)
    private MailAlarm mailAlarm;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param1", referencedColumnName = "alarm_id", insertable = false, updatable = false)
    private CalendarAlarm calendarAlarm;

    //채팅 알람
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param1", referencedColumnName = "chat_alarm_no", insertable = false, updatable = false)
    private ChatAlarm chatAlarm;

    //결재 알람
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param1", referencedColumnName = "approval_alarm_no", insertable = false, updatable = false)
    private ApprovalAlarm approvalAlarm;
}