package com.eroom.approval.dto;

import java.time.LocalDateTime;

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
	private Long approval_line_no; // 결재 라인 번호
	private String approval_alarm_comment; // 결재 알람 내용
	private String approval_alarm_read_yn; // 결재 알람 읽음 여부
	private LocalDateTime approval_alarm_reg_date; // 결재 알람 등록일
	
}
