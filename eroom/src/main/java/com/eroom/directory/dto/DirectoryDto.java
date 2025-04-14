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
@ToString
@Builder
public class DirectoryDto {
	
	private Long directory_no;
	private String directory_email;
	private String directory_phone;
	private String directory_name;
	private String directory_company_name;
	private String directory_creator;
	private String directory_editor;
	private String visible_yn;
	private LocalDateTime directory_reg_date;
	private LocalDateTime directory_mod_date;
	private Long employee_no;
	private String separator_code;
	
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
				.directoryRegDate(directory_reg_date)
				.directoryModDate(directory_mod_date)
				.employee(Employee.builder().employeeNo(employee_no).build())
				.separator(Separator.builder().separatorCode(separator_code).build())
				.build();
	}
	
	public DirectoryDto toDto(Directory entity) {
	    return DirectoryDto.builder()
	            .directory_no(entity.getDirectoryNo())
	            .directory_email(entity.getDirectoryEmail())
	            .directory_phone(entity.getDirectoryPhone())
	            .directory_name(entity.getDirectoryName())
	            .directory_company_name(entity.getDirectoryCompanyName())
	            .directory_creator(entity.getDirectoryCreator())
				.directory_editor(entity.getDirectoryEditor())
				.visible_yn(entity.getVisibleYn())
				.directory_reg_date(entity.getDirectoryRegDate())
				.directory_mod_date(entity.getDirectoryModDate())
				.employee_no(entity.getEmployee().getEmployeeNo())
				.separator_code(entity.getSeparator().getSeparatorCode())
	            .build();
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
