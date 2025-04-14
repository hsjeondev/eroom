package com.eroom.calendar.dto;

import java.time.LocalDateTime;

import com.eroom.calendar.entity.EmployeeCalendar;
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
@ToString
@Builder
public class EmployeeCalendarDto {
	private Long calendar_no;  // 캘린더 번호 (수정 시 사용)
    private String calendar_title; // 제목
    private String calendar_location; // 장소
    private LocalDateTime calendar_start_time; // 시작 시간
    private LocalDateTime calendar_end_time; // 종료 시간
    private String calendar_content; // 설명
    private String calendar_creator;
    private Long employee_no; // 사번
    private String separator; // 구분자
    private String visibleYn = "Y";
    private LocalDateTime calendar_reg_date; 
    private LocalDateTime calendar_mod_date;
	
    public EmployeeCalendar toEntity() {
        return EmployeeCalendar.builder()
                .calendarNo(calendar_no)
                .calendarTitle(calendar_title)
                .calendarLocation(calendar_location)
                .calendarStartTime(calendar_start_time)
                .calendarEndTime(calendar_end_time)
                .calendarContent(calendar_content)
                .calendarCreator(calendar_creator)
                .employeeNo(employee_no)
                .separator(separator)
            
                .build();
    }
    
    
    public EmployeeCalendarDto toDto(EmployeeCalendar employeeCalendar) {
        return EmployeeCalendarDto.builder()
                .calendar_no(employeeCalendar.getCalendarNo())
                .calendar_title(employeeCalendar.getCalendarTitle())
                .calendar_location(employeeCalendar.getCalendarLocation())
                .calendar_start_time(employeeCalendar.getCalendarStartTime())
                .calendar_end_time(employeeCalendar.getCalendarEndTime())
                .calendar_content(employeeCalendar.getCalendarContent())
                .calendar_creator(employeeCalendar.getCalendarCreator())
                .employee_no(employeeCalendar.getEmployeeNo())
                .separator(employeeCalendar.getSeparator())
                .calendar_reg_date(employeeCalendar.getCalendarRegDate())
                .calendar_mod_date(employeeCalendar.getCalendarModDate())
                .build();
    }
    
    
    
	/*
	 * public EmployeeCalendarDto toDto(EmployeeCalendar employeeCalendar) { return
	 * EmployeeCalendarDto.builder() .calendar_no(employeeCalendar.getCalendarNo())
	 * .calendar_location(employeeCalendar.getCalendarLocation())
	 * .calendar_start(employeeCalendar.getCalendarStart())
	 * .calendar_end(employeeCalendar.getCalendarEnd())
	 * .calendar_title(employeeCalendar.getCalendarTitle())
	 * .calendar_description(employeeCalendar.getCalendarDescription())
	 * .calendar_reg_date(employeeCalendar.getCalendarRegDate())
	 * .calendar_mod_date(employeeCalendar.getCalendarModDate()) .build(); }
	 */
    
//    public Map<String, Object> toFullCalendarEvent() {
//        Map<String, Object> event = new HashMap<>();
//        event.put("title", this.calendar_title);
//        event.put("start", this.calendar_start.toString());
//        event.put("end", this.calendar_end.toString());
//        event.put("location", this.calendar_location);
//        event.put("description", this.calendar_description);
//        return event;
//    }
	

}
