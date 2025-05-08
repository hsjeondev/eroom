package com.eroom.directory.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eroom.directory.entity.Directory;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;

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
@Builder
@ToString(exclude = "employee")
public class DirectoryDto {
	
	private Long directory_no;
	private String directory_email;
	private String directory_phone;
	private String directory_name;
	private String directory_company_name;
	private String directory_creator;
	private String directory_editor;
	private String visible_yn;
	private String directory_position;
	private String directory_department;
	private String directory_team;
	private LocalDateTime directory_reg_date;
	private LocalDateTime directory_mod_date;
	private Long employee_no;
	private String separator_code;
	private String employee_name;
	private String employee_position;
	private String code_name;
	private Employee employee;
	
	// structure 변수 담기
	private String department_name;
	private String team_name;
	// 입사일 퇴사일 String 저장
	private String formatted_hire_date;
	private String formatted_end_date;
	
	private Long drive_attach_no;
	
	// 주소 정보
	private String directory_zipcode; // 우편번호
	private String directory_address; // 주소
	
//	private String employee_name;
//	private String department_name;
//	private String team_name;
//	private LocalDateTime employee_hire_date;
//	private LocalDateTime employee_end_date;
	
	public Directory toEntity() {
		return Directory.builder()
				.directoryNo(directory_no)
				.directoryEmail(directory_email)
				.directoryPhone(directory_phone)
				.directoryName(directory_name)
				.directoryCompanyName(directory_company_name)
				.directoryCreator(directory_creator)
				.directoryEditor(directory_editor)
				.visibleYn(visible_yn)
				.directoryPosition(directory_position)
				.directoryDepartment(directory_department)
				.directoryTeam(directory_team)
				.directoryRegDate(directory_reg_date)
				.directoryModDate(directory_mod_date)
				.driveAttachNo(drive_attach_no)
				.directoryZipcode(directory_zipcode)
				.directoryAddress(directory_address)
				.employee(Employee.builder().employeeNo(employee_no).build())
				.separator(Separator.builder().separatorCode(separator_code).build())
				.build();
	}
	public Directory toEntity2() {
		return Directory.builder()
				.directoryNo(directory_no)
				.directoryEmail(directory_email)
				.directoryPhone(directory_phone)
				.directoryName(directory_name)
				.directoryCompanyName(directory_company_name)
				.directoryCreator(directory_creator)
				.directoryEditor(directory_editor)
				.visibleYn(visible_yn)
				.directoryPosition(directory_position)
				.directoryDepartment(directory_department)
				.directoryTeam(directory_team)
				.directoryRegDate(directory_reg_date)
				.directoryModDate(directory_mod_date)
				.driveAttachNo(drive_attach_no)
				.directoryZipcode(directory_zipcode)
				.directoryAddress(directory_address)
				.separator(Separator.builder().separatorCode(separator_code).build())
				.build();
	}
	
	public DirectoryDto toDto(Directory entity) {
	    Employee emp = entity.getEmployee();
		
	    return DirectoryDto.builder()
	            .directory_no(entity.getDirectoryNo())
	            .directory_email(entity.getDirectoryEmail())
	            .directory_phone(entity.getDirectoryPhone())
	            .directory_name(entity.getDirectoryName())
	            .directory_company_name(entity.getDirectoryCompanyName())
	            .directory_creator(entity.getDirectoryCreator())
				.directory_editor(entity.getDirectoryEditor())
				.visible_yn(entity.getVisibleYn())
				.directory_department(entity.getDirectoryDepartment())
				.directory_position(entity.getDirectoryPosition())
				.directory_team(entity.getDirectoryTeam())
				.directory_reg_date(entity.getDirectoryRegDate())
				.directory_mod_date(entity.getDirectoryModDate())
				.employee_no(entity.getEmployee().getEmployeeNo() != null ? entity.getEmployee().getEmployeeNo() : null)
				.drive_attach_no(entity.getDriveAttachNo())
				.directory_zipcode(entity.getDirectoryZipcode())
				.directory_address(entity.getDirectoryAddress())
				.employee_name(entity.getEmployee().getEmployeeName())
				.employee_position(entity.getEmployee().getEmployeePosition())
				.code_name(entity.getSeparator().getSeparatorName())
				.separator_code(entity.getSeparator().getSeparatorCode())
				.employee(entity.getEmployee() != null ? entity.getEmployee() : new Employee())
				.formatted_hire_date(emp.getEmployeeHireDate() != null ? emp.getEmployeeHireDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")
				.formatted_end_date(emp.getEmployeeEndDate() != null ? emp.getEmployeeEndDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")
	            .build();
	}
	public DirectoryDto toDto2(Directory entity) {
	    Employee emp = entity.getEmployee();
	    Separator separator = entity.getSeparator();

	    return DirectoryDto.builder()
	            .directory_no(entity.getDirectoryNo())
	            .directory_email(entity.getDirectoryEmail())
	            .directory_phone(entity.getDirectoryPhone())
	            .directory_name(entity.getDirectoryName())
	            .directory_company_name(entity.getDirectoryCompanyName())
	            .directory_creator(entity.getDirectoryCreator())
	            .directory_editor(entity.getDirectoryEditor())
	            .visible_yn(entity.getVisibleYn())
	            .directory_department(entity.getDirectoryDepartment())
	            .directory_position(entity.getDirectoryPosition())
	            .directory_team(entity.getDirectoryTeam())
	            .directory_reg_date(entity.getDirectoryRegDate())
	            .directory_mod_date(entity.getDirectoryModDate())
	            
	            .employee_no(emp != null ? emp.getEmployeeNo() : null)
	            .employee_name(emp != null ? emp.getEmployeeName() : null)
	            .employee_position(emp != null ? emp.getEmployeePosition() : null)
	            .employee(emp != null ? emp : new Employee())
	            
	            .code_name(separator != null ? separator.getSeparatorName() : null)
	            .separator_code(separator != null ? separator.getSeparatorCode() : null)
	            
	            .formatted_hire_date(emp != null && emp.getEmployeeHireDate() != null ? emp.getEmployeeHireDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")
	            .formatted_end_date(emp != null && emp.getEmployeeEndDate() != null ? emp.getEmployeeEndDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")
	            
	            .drive_attach_no(entity.getDriveAttachNo())
				.directory_zipcode(entity.getDirectoryZipcode())
				.directory_address(entity.getDirectoryAddress())
	            .build();
	}
	
	public DirectoryDto toDto3(Directory entity) {
	    Employee emp = entity.getEmployee();
	    Separator separator = entity.getSeparator();

	    return DirectoryDto.builder()
	            .directory_no(entity.getDirectoryNo())
	            .directory_email(entity.getDirectoryEmail())
	            .directory_phone(entity.getDirectoryPhone())
	            .directory_name(entity.getDirectoryName())
	            .directory_company_name(entity.getDirectoryCompanyName())
	            .directory_creator(entity.getDirectoryCreator())
	            .directory_editor(entity.getDirectoryEditor())
	            .visible_yn(entity.getVisibleYn())
	            .directory_department(entity.getDirectoryDepartment())
	            .directory_position(entity.getDirectoryPosition())
	            .directory_team(entity.getDirectoryTeam())
	            .directory_reg_date(entity.getDirectoryRegDate())
	            .directory_mod_date(entity.getDirectoryModDate())
	            
	            .employee_no(emp != null ? emp.getEmployeeNo() : null)
	            .employee_name(emp != null ? emp.getEmployeeName() : null)
	            .employee_position(emp != null ? emp.getEmployeePosition() : null)
	            .code_name(separator != null ? separator.getSeparatorName() : null)
	            .separator_code(separator != null ? separator.getSeparatorCode() : null)

	            .formatted_hire_date(emp != null && emp.getEmployeeHireDate() != null ? 
	                                  emp.getEmployeeHireDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")
	            .formatted_end_date(emp != null && emp.getEmployeeEndDate() != null ? 
	                                  emp.getEmployeeEndDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "-")

	            .drive_attach_no(entity.getDriveAttachNo())
	            .directory_zipcode(entity.getDirectoryZipcode())
	            .directory_address(entity.getDirectoryAddress())
	            .build();
	}

	
	
}
