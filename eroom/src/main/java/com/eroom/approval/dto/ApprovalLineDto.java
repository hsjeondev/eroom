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
	private String approval_line_yn; // 결재 라인 사용 여부
	private LocalDateTime approval_line_signed_date; // 결재 라인 결재일
	
	public ApprovalLine toEntity() {
		return ApprovalLine.builder()
				.approvalLineNo(approval_line_no)
				.approval(Approval.builder().approvalNo(approval_no).build())
				.employee(Employee.builder().employeeNo(employee_no).build())
				.approvalLineStep(approval_line_step)
				.approvalLineYn(approval_line_yn)
				.approvalLineSignedDate(approval_line_signed_date)
				.build();
	}
	
	public ApprovalLineDto toDto(ApprovalLine entity) {
        return ApprovalLineDto.builder()
                .approval_line_no(entity.getApprovalLineNo())
                .approval_no(entity.getApproval().getApprovalNo())
                .employee_no(entity.getEmployee().getEmployeeNo())
                .approval_line_step(entity.getApprovalLineStep())
                .approval_line_yn(entity.getApprovalLineYn())
                .approval_line_signed_date(entity.getApprovalLineSignedDate())
                .build();
	}
}
