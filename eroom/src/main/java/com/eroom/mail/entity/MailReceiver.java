package com.eroom.mail.entity;

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
@Table(name="mail_receiver")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailReceiver {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mail_receiver_no")
	private Long mailReceiverNo;
	
	@Column(name="mail_receiver_type")
	private String mailReceiverType;
	
	@Column(name="mail_receiver_read_yn")
	private String mailReceiverReadYn;
	
	@Column(name="mail_receiver_deleted_yn")
	private String mailReceiverDeletedYn;
	
	@Column(name="mail_receiver_important_yn")
	private String mailReceiverImportantYn;
	
//	@ManyToOne
//	mail_no
//	employee_no
}
