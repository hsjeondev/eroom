package com.eroom.calendar.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.eroom.calendar.entity.CompanyCalendar;

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
public class CompanyCalendarDto {
	private Long calendar_no;
	private String company_title;
	private String company_location;
	private LocalDateTime calendar_start_time; // 시작 시간
	private LocalDateTime calendar_end_time; // 종료 시간
	private String company_content; // 설명
	private String company_creator;
	private String company_editor;
	private Long employee_no; // 사번
	private String separator; // 구분자
	private String visibleYn = "Y";
	private LocalDateTime calendar_reg_date;
	private LocalDateTime calendar_mod_date;
	
	public CompanyCalendar toEntity() {
		return CompanyCalendar.builder()
				.employeeNo(employee_no)
				.calendarNo(calendar_no)
				.companyTitle(company_title)
				.companyLocation(company_location)
				.calendarStartTime(calendar_start_time)
				.calendarEndTime(calendar_end_time)
				.companyContent(company_content)
				.companyCreator(company_creator)
				.companyEditor(company_editor)
				.separator(separator)
				.visibleYn(visibleYn)
				.build();				
	}
	
	public CompanyCalendarDto toDto(CompanyCalendar companyCalendar) {
		return CompanyCalendarDto.builder().calendar_no(companyCalendar.getCalendarNo())
				.company_title(companyCalendar.getCompanyTitle())
				.company_location(companyCalendar.getCompanyLocation())
				.calendar_start_time(companyCalendar.getCalendarStartTime())
				.calendar_end_time(companyCalendar.getCalendarEndTime())
				.company_content(companyCalendar.getCompanyContent())
				.company_creator(companyCalendar.getCompanyCreator())
				.company_editor(companyCalendar.getCompanyEditor())
				.employee_no(companyCalendar.getEmployeeNo())
				.separator(companyCalendar.getSeparator())
				.calendar_reg_date(companyCalendar.getCalendarRegDate())
				.calendar_mod_date(companyCalendar.getCalendarModDate())
				.build();
	}
	
	
	public Map<String, Object> toFullCalendarEvent() {
	    Map<String, Object> event = new HashMap<>();
	    event.put("title", this.company_title != null ? this.company_title : "제목 없음");
	    event.put("start", this.calendar_start_time != null ? this.calendar_start_time.toString() : "");
	    event.put("end", this.calendar_end_time != null ? this.calendar_end_time.toString() : "");
	    event.put("location", this.company_location != null ? this.company_location : "");
	    event.put("description", this.company_content != null ? this.company_content : "");
	    event.put("separator", this.separator != null ? this.separator : "");
	    event.put("calendar_creator", this.company_creator != null ? this.company_creator : "");
	    event.put("calendar_no", this.calendar_no);
	    

	    
	    
	    return event;
	}
}
