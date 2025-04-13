package com.eroom.calendar.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    private LocalDateTime calendar_start; // 시작 시간
    private LocalDateTime calendar_end; // 종료 시간
    private String calendar_description; // 설명
    private Long employee_no; // 사번
    private String separator; // 구분자 (ex: E001 - 개인)

    // 자동 생성 필드는 생략 (DB에서 처리)
    private LocalDateTime calendar_reg_date; 
    private LocalDateTime calendar_mod_date;
	
    public EmployeeCalendar toEntity() {
        return EmployeeCalendar.builder()
                .calendarNo(calendar_no)
                .calendarTitle(calendar_title)
                .calendarLocation(calendar_location)
                .calendarStart(calendar_start)
                .calendarEnd(calendar_end)
                .calendarDescription(calendar_description)
                .employee(Employee.builder().employeeNo(employee_no).build()) // 연관 객체는 사번만으로 구성
                .separator(separator)
                .visibleYn("Y") // 기본값 설정
                .build();
    }
    
    
    public EmployeeCalendarDto toDto(EmployeeCalendar employeeCalendar) {
        return EmployeeCalendarDto.builder()
                .calendar_no(employeeCalendar.getCalendarNo())
                .calendar_title(employeeCalendar.getCalendarTitle())
                .calendar_location(employeeCalendar.getCalendarLocation())
                .calendar_start(employeeCalendar.getCalendarStart())
                .calendar_end(employeeCalendar.getCalendarEnd())
                .calendar_description(employeeCalendar.getCalendarDescription())
                .employee_no(employeeCalendar.getEmployee().getEmployeeNo())
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
