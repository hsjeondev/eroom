package com.eroom.mail.dto;


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

	private Long mail_receiver_no;
	private String mail_recervier_type;
	private String mail_recervier_read_yn;
	private String mail_recervier_deleted_yn;
	private String mail_recervier_important_yn;
	
	
	private Long mail_no;
	
	
	private Long employee_no;
	
	
	
}
