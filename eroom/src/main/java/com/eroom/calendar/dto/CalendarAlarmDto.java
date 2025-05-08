package com.eroom.calendar.dto;

import java.time.LocalDateTime;

import com.eroom.calendar.entity.CalendarAlarm;

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
public class CalendarAlarmDto {
	private Long alarm_id;
	private Long calendar_no;
	private Long employee_no;
	private String alarm_read_yn = "N";
	private LocalDateTime alarm_reg_date;
	private String separator;
	private String calendar_title;
	
	public CalendarAlarm toEntity() {
		return CalendarAlarm.builder()
				.alarmId(alarm_id)
				.calendarNo(calendar_no)
				.employeeNo(employee_no)
				.alarmReadYn(alarm_read_yn)
				.alarmRegDate(alarm_reg_date)
				.separator(separator)
				.build();
	}
	
	public CalendarAlarmDto toDto(CalendarAlarm calendarAlarm) {
		return CalendarAlarmDto.builder()
				.alarm_id(calendarAlarm.getAlarmId())
				.calendar_no(calendarAlarm.getCalendarNo())
				.employee_no(calendarAlarm.getEmployeeNo())
				.alarm_read_yn(calendarAlarm.getAlarmReadYn())
				.alarm_reg_date(calendarAlarm.getAlarmRegDate())
				.separator(calendarAlarm.getSeparator())
				.build();
	}
	

}
