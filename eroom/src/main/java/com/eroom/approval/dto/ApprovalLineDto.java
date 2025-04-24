package com.eroom.approval.dto;

import java.time.LocalDateTime;

import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.employee.entity.Employee;

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
@Builder
@ToString
public class ApprovalLineDto {
	private Long approval_line_no; // 결재 라인 번호
	private Long approval_no; // 결재 번호
	private Long employee_no; // 결재자 번호
	private int approval_line_step; // 결재 라인 순서
	private String approval_line_status; // 결재여부(A승인, S대기, D반려)
	private LocalDateTime approval_line_signed_date; // 결재 라인 결재일
	private Employee employee; // 결재자
	private String approval_line_deny_reason; // 결재 반려 사유
	private int delimeter; // 결재 구분자
	
	public ApprovalLine toEntity() {
		return ApprovalLine.builder()
				.approvalLineNo(approval_line_no)
				.approval(Approval.builder().approvalNo(approval_no).build())
				.employee(employee)
				.employee(Employee.builder().employeeNo(employee_no).build())
				.approvalLineStep(approval_line_step)
				.approvalLineStatus(approval_line_status)
				.approvalLineSignedDate(approval_line_signed_date)
				.approvalLineDenyReason(approval_line_deny_reason)
				.build();
	}
	
	public ApprovalLineDto toDto(ApprovalLine entity) {
        return ApprovalLineDto.builder()
                .approval_line_no(entity.getApprovalLineNo())
                .approval_no(entity.getApproval().getApprovalNo())
                .employee(entity.getEmployee())
                .employee_no(entity.getEmployee().getEmployeeNo())
                .approval_line_step(entity.getApprovalLineStep())
                .approval_line_status(entity.getApprovalLineStatus())
                .approval_line_signed_date(entity.getApprovalLineSignedDate())
                .approval_line_deny_reason(entity.getApprovalLineDenyReason())
                .build();
	}
}
