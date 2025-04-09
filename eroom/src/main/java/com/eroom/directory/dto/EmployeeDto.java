package com.eroom.directory.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eroom.directory.entity.Department;
import com.eroom.directory.entity.Employee;
import com.eroom.directory.entity.Team;

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
public class EmployeeDto {

	private Long employee_no;
	private String employee_id;
	private String employee_pw;
	private String employee_name;
	private LocalDateTime employee_hire_date;
	private LocalDateTime employee_end_date;
	private String employee_end_yn;
	private String employee_position;
	private Long department_no;
	private Long team_no;
	
	public Employee toEntity() {
		return Employee.builder()
				.employeeNo(employee_no)
				.employeeId(employee_id)
				.employeePw(employee_pw)
				.employeeName(employee_name)
				.employeeHireDate(employee_hire_date)
				.employeeEndDate(employee_end_date)
				.employeeEndYn(employee_end_yn)
				.employeePosition(employee_position)
				.department(Department.builder().departmentNo(department_no).build())
				.team(Team.builder().teamNo(team_no).build())
				.build();
	}
	
	public EmployeeDto toDto(Employee emp) {
		return EmployeeDto.builder()
				.employee_no(emp.getEmployeeNo())
				.employee_id(emp.getEmployeeId())
				.employee_pw(emp.getEmployeePw())
				.employee_hire_date(emp.getEmployeeHireDate())
				.employee_end_date(emp.getEmployeeEndDate())
				.employee_end_yn(emp.getEmployeeEndYn())
				.employee_position(emp.getEmployeePosition())
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
