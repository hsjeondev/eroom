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
	private Long calendar_no;
    private String calendar_location;
    private LocalDateTime calendar_start;
    private LocalDateTime calendar_end;
    private String calendar_title;
    private String calendar_description;
    private LocalDateTime calendar_reg_date;
    private LocalDateTime calendar_mod_date;
    private Long employee_no;
	
    public EmployeeCalendar toEntity() {
    	return EmployeeCalendar.builder()
    			.calendarNo(calendar_no)
    			.calendarLocation(calendar_location)
    			.calendarStart(calendar_start)
    			.calendarEnd(calendar_end)
    			.calendarTitle(calendar_title)
    			.calendarDescription(calendar_description)
    			.employee(Employee.builder().employeeNo(employee_no).build())
    			.build();		
    }
    
    public EmployeeCalendarDto toDto(EmployeeCalendar employeeCalendar) {
    	return EmployeeCalendarDto.builder()
    			.calendar_no(employeeCalendar.getCalendarNo())
    			.calendar_location(employeeCalendar.getCalendarLocation())
    			.calendar_start(employeeCalendar.getCalendarStart())
    			.calendar_end(employeeCalendar.getCalendarEnd())
    			.calendar_title(employeeCalendar.getCalendarTitle())
    			.calendar_description(employeeCalendar.getCalendarDescription())
    			.calendar_reg_date(employeeCalendar.getCalendarRegDate())
    			.calendar_mod_date(employeeCalendar.getCalendarModDate())
    			.build();
    }
    
    public Map<String, Object> toFullCalendarEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("title", this.calendar_title);
        event.put("start", this.calendar_start.toString());
        event.put("end", this.calendar_end.toString());
        event.put("location", this.calendar_location);
        event.put("description", this.calendar_description);
        return event;
    }
	

}
