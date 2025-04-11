package com.eroom.mail.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="mail")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mail_no")
	private Long mailNo;
	
	@Column(name="mail_title")
	private String mailTitle;
	
	@Column(name="mail_content")
	private String mailContent;
	
	@CreationTimestamp
	@Column(name="mail_sent_time")
	private LocalDateTime mailSentTime;
	
	@Column(name="mail_status")
	private String mailStatus;
	

	@OneToMany(mappedBy = "mail")
	private List<MailReceiver> receivers; // 수신자 목록
}
