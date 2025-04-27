package com.eroom.admin.dto;

import java.time.LocalDateTime;

import com.eroom.directory.entity.Directory;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
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
public class CreateEmployeeDto {
	// 사원 정보
	private String employee_name; // 사원 이름
	private String employee_position; // 사원 직급
	private String employee_birth; // 생년월일
	private String employee_hire_date; // 입사일
	
	// 주소록 정보
	private String directory_phone; // 전화번호
	
	// 소속 정보
	private Long department_no; // 부서 번호
	private Long team_no; // 팀 번호
	
	public Employee toEmployeeEntity(String employeeId, String encodedPw,Structure structure ) {
		return Employee.builder()
				.employeeId(employeeId)
				.employeePw(encodedPw)
				.employeeName(this.employee_name)
				.employeePosition(this.employee_position)
				.employeeHireDate(LocalDateTime.parse(this.employee_hire_date + "T00:00:00"))
				.employeeBirth(this.employee_birth.replaceAll("-", ""))
				.employeeEmploymentYn("Y")
				.structure(structure)
				.build();
	}
	
	public Directory toDirectoryEntity(Employee employee,String separatorCode, String email, String departmentName, String teamName, String creatorName, String companyName) {
		LocalDateTime now = LocalDateTime.now();
		return Directory.builder()
				.directoryPhone(this.directory_phone)
				.directoryEmail(email)
				.directoryName(this.employee_name) // 생성된 사원 이름
				.directoryCompanyName(companyName)
				.directoryCreator(creatorName)
				.visibleYn("Y")
				.directoryRegDate(now)
				.directoryPosition(this.employee_position)
				.directoryDepartment(departmentName)
				.directoryTeam(teamName)
				.employee(employee)
				.separator(Separator.builder().separatorCode(separatorCode).build())
				.build();
		
	}

}
