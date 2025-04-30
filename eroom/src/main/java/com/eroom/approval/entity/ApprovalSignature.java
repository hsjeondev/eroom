package com.eroom.approval.entity;


import java.sql.Blob;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(name="approval_signature")
public class ApprovalSignature {
	@Id
	@Column(name="approval_signature_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long approvalSignatureNo; // 결재 서명 번호
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee employee;
	@Column(name = "approval_signature_name")
	private String approvalSignatureName; // 결재 서명 이름
	@Lob
	@Column(name = "approval_signature_blob")
	private byte[] approvalSignatureBlob; // 결재 서명 이미지 바이너리저장
	@Column(name = "approval_signature_reg_date", insertable = false, updatable = false)
	private LocalDateTime approvalSignatureRegDate; // 결재 서명 등록일
	@Column(name = "approval_signature_mod_date", insertable = false, updatable = false)
	private LocalDateTime approvalSignatureModDate; // 결재 서명 수정일
	@Column(name = "approval_signature_visible_yn")
	private String approvalSignatureVisibleYn; // 사용 여부
	
}
