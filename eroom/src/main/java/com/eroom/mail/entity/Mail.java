package com.eroom.mail.entity;

import java.time.LocalDateTime;

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
	
	@Column(name="mail_sent_time")
	private LocalDateTime mailSentTime;
	
}
