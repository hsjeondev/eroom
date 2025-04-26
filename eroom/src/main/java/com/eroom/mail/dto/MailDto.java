package com.eroom.mail.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.eroom.employee.entity.Employee;
import com.eroom.mail.entity.Mail;

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
	private String mail_status="N";
	
	// directory 데이터로 바꿀 예정
	private Long employee_no;
	// 받는 사람 나중에 List로 바꿀 예정
	//private Employee receiver;
	
	private List<Long> receiverNos; // 수신자 no값
	private List<Long> ccNos;  // 참조자 no값
	
	public Mail toEntity() {
		return Mail.builder()
				.mailNo(mail_no)
				.sender(Employee.builder().employeeNo(employee_no).build()) // 보낸 사람
				.mailTitle(mail_title)
				.mailContent(mail_content)
				.mailSentTime(mail_sent_time)
				.mailStatus(mail_status)
				.build();
	}
	
	
}
