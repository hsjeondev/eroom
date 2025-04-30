package com.eroom.approval.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
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
public class ApprovalDto {
    private Long approval_no; // 결재 번호
    private Long employee_no; // 기안자 번호
    
    private String approval_status; // 결재 상태 (A, S, D, F, 등)
    private String approval_title; // 결재 제목
    private Map<String, String> approval_content; // 결재 내용

    private LocalDateTime approval_reg_date; // 등록일
    private String reg_date; // 등록일 (String)
    private LocalDateTime approval_completed_date; // 완료일
    private String completed_date; // 완료일 (String)
    
    private Employee employee; // 기안자
    private ApprovalFormat approval_format; // 결재 양식
    private String approval_visible_yn; // 결재 사용여부
    
	public Approval toEntity() {
		return Approval.builder()
				.approvalNo(approval_no)
				.employee(Employee.builder().employeeNo(employee_no).build())
				.approvalStatus(approval_status)
				.approvalTitle(approval_title)
				.approvalContent(approval_content)
				.approvalRegDate(approval_reg_date)
				.approvalCompletedDate(approval_completed_date)
				.employee(employee)
				.approvalFormat(approval_format)
				.approvalVisibleYn(approval_visible_yn)
				.approvalRegDate(approval_reg_date)
				.build();
	}
	
	public ApprovalDto toDto(Approval entity) {
		return ApprovalDto.builder()
				.approval_no(entity.getApprovalNo())
				.employee_no(entity.getEmployee().getEmployeeNo())
				.approval_status(entity.getApprovalStatus())
				.approval_title(entity.getApprovalTitle())
				.approval_content(entity.getApprovalContent())
				.reg_date(entity.getApprovalRegDate() != null ? entity.getApprovalRegDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")
				.completed_date(entity.getApprovalCompletedDate() != null ? entity.getApprovalCompletedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")
				.employee(entity.getEmployee())
				.approval_reg_date(entity.getApprovalRegDate())
				.approval_format(entity.getApprovalFormat())
				.approval_visible_yn(entity.getApprovalVisibleYn())
				.build();
	}
}
