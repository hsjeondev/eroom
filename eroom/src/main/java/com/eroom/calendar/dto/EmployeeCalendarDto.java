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
	private Long calendar_no;
    private String calendar_location;
    private LocalDateTime calendar_date;
    private String calendar_description;
    private LocalDateTime calendar_reg_date;
    private LocalDateTime calendar_mod_date;
    private Long employee_no;
	
    public EmployeeCalendar toEntity() {
    	return EmployeeCalendar.builder()
    			.calendarNo(calendar_no)
    			.calendarLocation(calendar_location)
    			.calendarDate(calendar_date)
    			.calendarDescription(calendar_description)
    			.employee(Employee.builder().employeeNo(employee_no).build())
    			.build();		
    }
    
    public EmployeeCalendarDto toDto(EmployeeCalendar employeeCalendar) {
    	return EmployeeCalendarDto.builder()
    			.calendar_no(employeeCalendar.getCalendarNo())
    			.calendar_location(employeeCalendar.getCalendarLocation())
    			.calendar_date(employeeCalendar.getCalendarDate())
    			.calendar_description(employeeCalendar.getCalendarDescription())
    			.calendar_reg_date(employeeCalendar.getCalendarRegDate())
    			.calendar_mod_date(employeeCalendar.getCalendarModDate())
    			.build();
    }
	

}
