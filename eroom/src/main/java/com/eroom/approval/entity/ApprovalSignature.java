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
import jakarta.persistence.OneToOne;
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
@Table(name="Approval_signature")
public class ApprovalSignature {
	@Id
	@Column(name="approval_signature_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long approvalSignatureNo; // 결재 서명 번호
	@OneToOne
	@JoinColumn(name="employee_no")
	private Employee employee;
	@Column(name = "approval_signature_name")
	private String approvalSignatureName; // 결재 서명 이름
	@Column(name = "approval_signature_path")
	private String approvalSignaturePath; // 결재 서명 경로
	@Column(name = "approval_signature_reg_date", insertable = false, updatable = false)
	private LocalDateTime approvalSignatureRegDate; // 결재 서명 등록일
	@Column(name = "approval_signature_mod_date")
	private LocalDateTime approvalSignatureModDate; // 결재 서명 수정일
	
}
