package com.eroom.approval.entity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import com.eroom.employee.entity.Employee;
import com.eroom.approval.JsonToMapConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
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
@Table(name="approval")
public class Approval {
	@Id
	@Column(name="approval_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long approvalNo; // 결재 번호
	@Column(name="approval_status")
	private String approvalStatus; // 결재 상태(A, S, D, F)
	@Column(name="approval_title")
	private String approvalTitle; // 결재 제목
	@Convert(converter = JsonToMapConverter.class)
	@Column(name = "approval_content", columnDefinition = "TEXT")
	private Map<String, String> approvalContent; // 결재 내용
	@Column(name="approval_deny_reason")
	private String approvalDenyReason; // 결재 반려 사유
	@Column(name="approval_reg_date", insertable = false, updatable = false)
	private LocalDateTime approvalRegDate; // 결재 등록일
	@Column(name="approval_completed_date")
	private LocalDateTime approvalCompletedDate; // 결재 수정일
	@Column(name="approval_visible_yn")
	private String approvalVisibleYn; // 결재 사용여부
	
	@ManyToOne
	@JoinColumn(name = "employee_no")
	private Employee employee; // 기안자
	@OneToOne
	@JoinColumn(name="approval_format_no")
	private ApprovalFormat approvalFormat; // 결재 양식
	
	@OneToMany(mappedBy = "approval")
	@OrderBy("approvalLineNo ASC")
	private Set<ApprovalLine> approvalLines;



	
}
