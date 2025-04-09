package com.eroom.directory.dto;

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
	
	public EmployeeDirectory toEntity() {
		return EmployeeDirectory.builder()
				.employeeDirectoryNo(employee_directory_no)
				.employeeDirectoryEmail(employee_directory_email)
				.employeeDirectoryPhone(employee_directory_phone)
				.employee(Employee.builder().employeeNo(employee_directory_no).build())
				.build();
	}
	
	public EmployeeDirectoryDto toDto(EmployeeDirectory entity) {
		return EmployeeDirectoryDto.builder()
				.employee_directory_no(entity.getEmployeeDirectoryNo())
				.employee_directory_email(entity.getEmployeeDirectoryEmail())
				.employee_directory_phone(entity.getEmployeeDirectoryPhone())
				.build();
	}
	
	
}
