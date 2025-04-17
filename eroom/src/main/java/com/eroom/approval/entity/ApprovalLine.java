package com.eroom.approval.entity;

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
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

@Entity
@Table(name="approval_line")
public class ApprovalLine {
	@Id
	@Column(name="approval_line_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long approvalLineNo; // 결재 라인 번호
	
	@ManyToOne
	@JoinColumn(name = "approval_no")
	private Approval approval; // 결재 번호
	
	@ManyToOne
	@JoinColumn(name = "employee_no")
	private Employee employee;
	
	@Column(name="approval_line_step")
	private int approvalLineStep; // 결재 라인 순서
	
	@Column(name="approval_line_yn")
	private String approvalLineYn; // 결재 라인 사용 여부
	
	@Column(name="approval_line_signed_date")
	private LocalDateTime approvalLineSignedDate; // 결재 라인 결재일
	


}
