package com.eroom.mail.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MailDto {
	private Long mail_no;
	private String mail_title;
	private String mail_content;
	private LocalDateTime mail_sent_time;
	private String mail_status;
	
//	private Long employee_no;
	
	
}
