package com.eroom.approval.dto;

import java.time.LocalDateTime;

import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalAlarm;
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
public class ApprovalAlarmDto {

	private Long approval_alarm_no; // 결재 알람 번호
	private Long approval_no; // 결재 번호
	private String approval_alarm_comment; // 결재 알람 내용
	private String approval_alarm_read_yn; // 결재 알람 읽음 여부
	private LocalDateTime approval_alarm_reg_date; // 결재 알람 등록일
	
	public ApprovalAlarmDto toDto(ApprovalAlarm entity) {
		return ApprovalAlarmDto.builder()
				.approval_alarm_no(entity.getApprovalAlarmNo())
				.approval_no(entity.getApproval().getApprovalNo())
				.approval_alarm_comment(entity.getApprovalAlarmComment())
				.approval_alarm_read_yn(entity.getApprovalAlarmReadYn())
				.approval_alarm_reg_date(entity.getApprovalAlarmRegDate())
				.build();
	}
	
    public ApprovalAlarm toEntity(Approval approval, Employee employee) {
        return ApprovalAlarm.builder()
                .approvalAlarmNo(this.approval_alarm_no)
                .approval(approval)
                .receiver(employee)
                .approvalAlarmComment(this.approval_alarm_comment)
                .approvalAlarmReadYn(this.approval_alarm_read_yn)
                .approvalAlarmRegDate(this.approval_alarm_reg_date != null ? this.approval_alarm_reg_date : LocalDateTime.now())
                .build();
    }
    public ApprovalAlarm toEntity() {
    	return ApprovalAlarm.builder()
    			.approvalAlarmNo(this.approval_alarm_no)
    			.approvalAlarmComment(this.approval_alarm_comment)
    			.approvalAlarmReadYn(this.approval_alarm_read_yn)
    			.approvalAlarmRegDate(this.approval_alarm_reg_date != null ? this.approval_alarm_reg_date : LocalDateTime.now())
    			.build();
    }
	
}
