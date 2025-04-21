package com.eroom.mail.dto;


import com.eroom.employee.entity.Employee;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailReceiver;

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
@ToString
@Builder
public class MailReceiverDto {

	// mail_receiver PK값
	private Long mail_receiver_no;
	// type 원래 to, cc 했다가 그냥 to 넣어서 "발신자-수신자" 로만
	private String mail_recervier_type;
	
	// 읽음 여부
	private String mail_receiver_read_yn="N";
	// 삭제 여부
	private String mail_receiver_deleted_yn="N";
	// 중요 여부
	private String mail_receiver_important_yn="N";
	
	private Mail mail;
	// 메일 번호
	private Long mail_no;
	// 사용자 정보
	private Long employee_no;
	
	
//	public MailReceiver toEntity() {
//		return MailReceiver.builder()
//				.mailReceiverNo(mail_receiver_no)
//				.mail(Mail.builder().mailNo(mail_no).build())
//				.receiver(Employee.builder().employeeNo(employee_no).build())
//				.mailReceiverReadYn(mail_receiver_read_yn)
//				.mailReceiverDeletedYn(mail_receiver_deleted_yn)
//				.mailReceiverImportantYn(mail_receiver_important_yn)
//				.build();
//	}
	public MailReceiver toEntity(Mail mail, Employee receiver) {
	    return MailReceiver.builder()
	        .mail(mail)
	        .receiver(receiver)
	        .mailReceiverType(mail_recervier_type)
	        .mailReceiverReadYn(mail_receiver_read_yn)
	        .mailReceiverDeletedYn(mail_receiver_deleted_yn)
	        .mailReceiverImportantYn(mail_receiver_important_yn)
	        .build();
	}
	
	
}
