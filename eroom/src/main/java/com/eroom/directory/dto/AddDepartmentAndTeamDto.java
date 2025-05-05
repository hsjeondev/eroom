package com.eroom.directory.dto;

import java.time.LocalDateTime;

import com.eroom.employee.dto.EmployeeDto;

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
public class AddDepartmentAndTeamDto {

	private String parentCode; // 상위부서 코드
	private String codeName; // 상위 부서 코드가 null = 부서이름, null != 팀이름
	
}
