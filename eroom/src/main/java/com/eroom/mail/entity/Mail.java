package com.eroom.mail.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="mail")
@Builder
@Getter
@Setter
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
	
	
	@Column(name="mail_visible_yn")
	private String mailVisibleYn;

	@OneToMany(mappedBy = "mail")
	private List<MailReceiver> receivers; // 수신자 목록
	
	
	// 부서 정보를 위해 필요
	@ManyToOne
	@JoinColumn(name = "employee_no")  // mail 테이블에 외래키 컬럼 생성됨
	private Employee sender;
	

}
