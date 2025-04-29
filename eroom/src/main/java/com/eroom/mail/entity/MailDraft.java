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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="mail_draft")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailDraft {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mail_draft_no")
	private Long mailDraftNo;
	
	@Column(name="mail_draft_time")
	private LocalDateTime mailDraftTime;
	@Column(name="mail_draft_visible_yn")
	private String mailDraftVisibleYn;
	
	@OneToOne
    @JoinColumn(name = "mail_no")  // mail 테이블과 연결되는 외래키
    private Mail mail;
	
	@ManyToOne
    @JoinColumn(name = "employee_no")  // employee 테이블의 employee_no 컬럼 참조
    private Employee employee;  // 초안을 저장한 작성자
	
}
