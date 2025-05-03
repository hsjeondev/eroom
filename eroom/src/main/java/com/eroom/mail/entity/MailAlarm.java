package com.eroom.mail.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="mail_alarm")
public class MailAlarm {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mail_alarm_no")
    private Long mailAlarmNo;

    @ManyToOne
    @JoinColumn(name = "mail_receiver_no")
    private MailReceiver mailReceiver;  // 수신자 정보 (MailReceiver 엔티티와 매핑)

    @Column(name="mail_alarm_read_yn")
    private String mailAlarmReadYn;  // 읽음 여부 (Y/N)
    
    @CreationTimestamp
	@Column(name="mail_alarm_reg_date")
    private LocalDateTime mailAlarmRegDate;  // 생성 날짜

    // Getter, Setter, Constructor 등
}
