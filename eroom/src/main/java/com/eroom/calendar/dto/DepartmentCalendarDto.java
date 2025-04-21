package com.eroom.calendar.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.eroom.calendar.entity.DepartmentCalendar;

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
public class DepartmentCalendarDto {
	private Long calendar_no; // 캘린더 번호 (수정 시 사용)
	private String calendar_title; // 제목
	private String calendar_location; // 장소
	private LocalDateTime calendar_start_time; // 시작 시간
	private LocalDateTime calendar_end_time; // 종료 시간
	private String calendar_content; // 설명
	private String calendar_creator;
	private String calendar_editor;
	private Long employee_no; // 사번
	private String separator; // 구분자
	private String visibleYn = "Y";
	private String teamName;
	private LocalDateTime calendar_reg_date;
	private LocalDateTime calendar_mod_date;

	public DepartmentCalendar toEntity() {
		return DepartmentCalendar.builder()
				.calendarNo(calendar_no)
				.calendarTitle(calendar_title)
				.calendarLocation(calendar_location)
				.calendarStartTime(calendar_start_time)
				.calendarEndTime(calendar_end_time)
				.calendarContent(calendar_content).calendarCreator(calendar_creator)
				.employeeNo(employee_no)
				.separator(separator)
				.visibleYn(visibleYn)
				.calendarEditor(calendar_editor)
			
				.build();
	}

	public DepartmentCalendarDto toDto(DepartmentCalendar departmentCalendar) {
		return DepartmentCalendarDto.builder().calendar_no(departmentCalendar.getCalendarNo())
				.calendar_title(departmentCalendar.getCalendarTitle())
				.calendar_location(departmentCalendar.getCalendarLocation())
				.calendar_start_time(departmentCalendar.getCalendarStartTime())
				.calendar_end_time(departmentCalendar.getCalendarEndTime())
				.calendar_content(departmentCalendar.getCalendarContent())
				.calendar_creator(departmentCalendar.getCalendarCreator())
				.calendar_editor(departmentCalendar.getCalendarEditor())
				.employee_no(departmentCalendar.getEmployeeNo())
				.separator(departmentCalendar.getSeparator())
				.calendar_reg_date(departmentCalendar.getCalendarRegDate())
				.calendar_mod_date(departmentCalendar.getCalendarModDate())
				.teamName(
					    departmentCalendar.getEmployee() != null &&
					    departmentCalendar.getEmployee().getStructure() != null
					        ? departmentCalendar.getEmployee().getStructure().getCodeName()
					        : null
					)
				.build();
			
	}

	


	public Map<String, Object> toFullCalendarEvent() {
	    Map<String, Object> event = new HashMap<>();
	    event.put("title", this.calendar_title != null ? this.calendar_title : "제목 없음");
	    event.put("start", this.calendar_start_time != null ? this.calendar_start_time.toString() : "");
	    event.put("end", this.calendar_end_time != null ? this.calendar_end_time.toString() : "");
	    event.put("location", this.calendar_location != null ? this.calendar_location : "");
	    event.put("description", this.calendar_content != null ? this.calendar_content : "");
	    event.put("separator", this.separator != null ? this.separator : "");
	    event.put("calendar_creator", this.calendar_creator != null ? this.calendar_creator : "");
	    event.put("calendar_no", this.calendar_no);
	    event.put("teamName", this.teamName != null ? this.teamName : "");
	    
	    

	    
	    
	    return event;
	}

}
