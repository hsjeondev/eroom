package com.eroom.employee.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eroom.directory.entity.Directory;
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
public class EmployeeUpdateDto {
	private Long employee_no; // 사번
	private String employee_name; // 이름
	private String employee_position; // 직급
	private String employee_birth; // 생년월일
	private String employee_pw; // 생년월일 재암호화 한 비밀번호
	private String employee_hire_date; // 입사일
	private String employee_end_date; // 퇴사일
	private String employee_employment_yn; // 재직여부
	private Long structure_no; // 부서 또는 팀
	private String directory_phone; // 연락처(주소록)
	
	
	// Directory 엔티티 변환
	public Directory toDirectoryEntity(Employee employee) {
		return Directory.builder()
				.employee(employee)
				.directoryPhone(directory_phone)
				.build();
	}
	
	// Employee -> EmployeeUpdateDto 변환
	public static EmployeeUpdateDto toDto(Employee employee) {
		EmployeeUpdateDto dto = new EmployeeUpdateDto();
		dto.setEmployee_no(employee.getEmployeeNo());
		dto.setEmployee_name(employee.getEmployeeName());
		dto.setEmployee_position(employee.getEmployeePosition());
		dto.setEmployee_birth(employee.getEmployeeBirth());
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		// 입사일
		if(employee.getEmployeeHireDate() != null) {
			dto.setEmployee_hire_date(employee.getEmployeeHireDate().format(formatter));
		}
		// 퇴사일
		if(employee.getEmployeeEndDate() != null) {
			dto.setEmployee_end_date(employee.getEmployeeEndDate().format(formatter));
		}
		dto.setEmployee_employment_yn(employee.getEmployeeEmploymentYn());
		if(employee.getStructure() != null) {
			dto.setStructure_no(employee.getStructure().getStructureNo());
		}
		if(employee.getDirectory() != null) {
			dto.setDirectory_phone(employee.getDirectory().getDirectoryPhone());
		}
		return dto;
	}
	
	
	
}
