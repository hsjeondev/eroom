package com.eroom.employee.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;

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
	private String employee_employment_yn;
	private String employee_position;
	private Long department_no;
	private Long team_no;
	private String department_name;
	private String team_name;
	private Long structure_no;
	
	public Employee toEntity() {
		return Employee.builder()
				.employeeNo(employee_no)
				.employeeId(employee_id)
				.employeePw(employee_pw)
				.employeeName(employee_name)
				.employeeHireDate(employee_hire_date)
				.employeeEndDate(employee_end_date)
				.employeeEmploymentYn(employee_employment_yn)
				.employeePosition(employee_position)
				.structure(Structure.builder().structureNo(structure_no).build())
				.build();
	}
	
	public EmployeeDto toDto(Employee emp) {
		return EmployeeDto.builder()
				.employee_name(emp.getEmployeeName())
				.employee_no(emp.getEmployeeNo())
				.employee_id(emp.getEmployeeId())
				.employee_pw(emp.getEmployeePw())
				.employee_hire_date(emp.getEmployeeHireDate())
				.employee_end_date(emp.getEmployeeEndDate())
				.employee_employment_yn(emp.getEmployeeEmploymentYn())
				.employee_position(emp.getEmployeePosition())
				.structure_no(emp.getStructure() != null ? emp.getStructure().getStructureNo() : null)
				.build();
	}
	public EmployeeDto(Long employeeNo, String employeeName) {
		this.employee_no = employeeNo;
		this.employee_name = employeeName;
	}
	
//	public String getFormattedHireDate() {
//		if(employee_hire_date != null) {
//			return employee_hire_date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
//		} else {
//			return "-";
//		}
//	}
//	public String getFormattedEndDate() {
//		if(employee_end_date != null) {
//			return employee_end_date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
//		} else {
//			return "-";
//		}
//	}
	
}
