package com.eroom.notification.dto;

import java.time.LocalDateTime;

import com.eroom.approval.dto.ApprovalAlarmDto;
import com.eroom.calendar.dto.CalendarAlarmDto;
import com.eroom.chat.dto.ChatAlarmDto;
import com.eroom.mail.dto.MailAlarmDto;
import com.eroom.notification.entity.Alarm;

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
public class AlarmDto {
	private Long alarm_no;
	private Long param1;
	private String separator_code;
	private Long employee_no;
	private CalendarAlarmDto calendarAlarm;
	private String read_yn = "N";
	private MailAlarmDto mailAlarm;
	private ChatAlarmDto chatAlarm;
	private ApprovalAlarmDto approvalAlarm;
	private LocalDateTime reg_date;
	private String comment;
	private String calendarAlarm_separator;
	
	public Alarm toEntity() {
		return Alarm.builder()
				.alarmNo(alarm_no)
				.param1(param1)
				.separatorCode(separator_code)
				.employeeNo(employee_no)
				.readYn(read_yn)
				.regDate(reg_date)
				.build();
	}
	
	public AlarmDto toDto(Alarm alarm) {
		String separatorCode = alarm.getSeparatorCode();
		if(("R001").equals(separatorCode)) {
			return AlarmDto.builder()
					.alarm_no(alarm.getAlarmNo())
					.param1(alarm.getParam1())
					.separator_code(alarm.getSeparatorCode())
					.employee_no(alarm.getEmployeeNo())
					.read_yn(alarm.getReadYn())
					.reg_date(alarm.getRegDate())
					.calendarAlarm_separator((alarm.getCalendarAlarm() != null && alarm.getCalendarAlarm().getSeparator() != null) ? alarm.getCalendarAlarm().getSeparator() : "")
					.build();
		} else if(("R002").equals(separatorCode)) {
			return null;
		} else if(("R003").equals(separatorCode)) {
			return null;
		} else if(("R004").equals(separatorCode)) {
			return AlarmDto.builder()
					.alarm_no(alarm.getAlarmNo())
					.param1(alarm.getParam1())
					.separator_code(alarm.getSeparatorCode())
					.employee_no(alarm.getEmployeeNo())
					.read_yn(alarm.getReadYn())
					.reg_date(alarm.getRegDate())
					.comment((alarm.getApprovalAlarm() != null && alarm.getApprovalAlarm().getApprovalAlarmComment() != null) ? alarm.getApprovalAlarm().getApprovalAlarmComment() : "")
					.build();
		} else {
			return null;
		}
	}

}
