package com.eroom.mail.entity;

import java.time.LocalDateTime;

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

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="mail_receiver")
public class MailReceiver {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mail_receiver_no")
	private Long mailReceiverNo;
	
	// 사용 여부로 쓸 컬럼
	@Column(name="mail_receiver_type")
	private String mailReceiverType;
	
	@Column(name="mail_receiver_read_yn")
	private String mailReceiverReadYn;
	
//	@Column(name="mail_receiver_deleted_yn")
//	private String mailReceiverDeletedYn;
//	
//	@Column(name="mail_receiver_important_yn")
//	private String mailReceiverImportantYn;
//	
//	@Column(name="mail_receiver_deleted_time")
//	private LocalDateTime mailReceiverDeletedTime;
	
	@ManyToOne
	@JoinColumn(name = "mail_no")  // FK 컬럼명 명시
	private Mail mail;
	
	// 부서 정보를 위해 서치 필요
	@ManyToOne
	@JoinColumn(name = "employee_no")  // 수신자 FK 컬럼
	private Employee receiver;
	
//    @ManyToOne
//    @JoinColumn(name = "directory_no")
//    private Directory directory; // Directory와 연결
}
