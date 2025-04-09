package com.eroom.directory.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eroom.directory.entity.EmployeeDirectory;
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
public class EmployeeDirectoryDto {
	
	private Long employee_directory_no;
	private String employee_directory_email;
	private String employee_directory_phone;
	private Long employee_no;
	private String employee_name;
	private String department_name;
	private String team_name;
	private LocalDateTime employee_hire_date;
	private LocalDateTime employee_end_date;
	
	public EmployeeDirectory toEntity() {
		return EmployeeDirectory.builder()
				.employeeDirectoryNo(employee_directory_no)
				.employeeDirectoryEmail(employee_directory_email)
				.employeeDirectoryPhone(employee_directory_phone)
				.employee(Employee.builder().employeeNo(employee_no).build())
				.build();
	}
	
	public EmployeeDirectoryDto toDto(EmployeeDirectory entity) {
	    if (entity == null) return null;
	    Employee emp = entity.getEmployee();
	    
	    return EmployeeDirectoryDto.builder()
	            .employee_directory_no(entity.getEmployeeDirectoryNo())
	            .employee_directory_email(entity.getEmployeeDirectoryEmail())
	            .employee_directory_phone(entity.getEmployeeDirectoryPhone())
	            .employee_no(emp != null ? emp.getEmployeeNo() : null)
	            .employee_hire_date((emp != null && emp.getEmployeeHireDate() != null)? emp.getEmployeeHireDate() : null)
	            .employee_end_date((emp != null && emp.getEmployeeEndDate() != null) ? emp.getEmployeeEndDate() : null)
	            .employee_name((emp != null && emp.getEmployeeName() != null) ? emp.getEmployeeName() : null)
	            .department_name((emp != null && emp.getDepartment() != null) ? emp.getDepartment().getDepartmentName() : null)
	            .team_name((emp != null && emp.getTeam() != null) ? emp.getTeam().getTeamName() : null)
	            .build();
	}
	
	public String getFormattedHireDate() {
		if(employee_hire_date != null) {
			return employee_hire_date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
		} else {
			return "-";
		}
	}
	public String getFormattedEndDate() {
		if(employee_end_date != null) {
			return employee_end_date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
		} else {
			return "-";
		}
	}
	
	
}
