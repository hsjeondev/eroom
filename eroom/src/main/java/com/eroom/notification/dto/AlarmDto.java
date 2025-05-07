package com.eroom.notification.dto;

import com.eroom.approval.dto.ApprovalAlarmDto;
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
	private MailAlarmDto mailAlarm;
	//private ChatAlarmDto chatAlarm;
	private ApprovalAlarmDto approvalAlarm;
	
	public Alarm toEntity() {
		return Alarm.builder()
				.alarmNo(alarm_no)
				.param1(param1)
				.separatorCode(separator_code)
				.employeeNo(employee_no)			
				.build();
	}
	
	public AlarmDto toDto(Alarm alarm) {
		return AlarmDto.builder()
				.alarm_no(alarm.getAlarmNo())
				.param1(alarm.getParam1())
				.separator_code(alarm.getSeparatorCode())
				.employee_no(alarm.getEmployeeNo())
				.calendarAlarm(alarm.getCalendarAlarm() != null 
		         ? new CalendarAlarmDto().toDto(alarm.getCalendarAlarm())
		         : null)
				.build();
	}

}
