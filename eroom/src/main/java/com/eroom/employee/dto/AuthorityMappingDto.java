package com.eroom.employee.dto;


import com.eroom.employee.entity.Authority;
import com.eroom.employee.entity.AuthorityMapping;
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
public class AuthorityMappingDto {
	private Long authority_mapping_no;
	private Long employee_no;
	private Long authority_no;
	
	public AuthorityMapping toEntity() {
		return AuthorityMapping.builder()
				.authorityMappingNo(authority_no)
				.employee(Employee.builder().employeeNo(employee_no).build())
				.authority(Authority.builder().authorityNo(authority_no).build())
				.build();
	}
	public AuthorityMappingDto toDto(AuthorityMapping entity) {
		return AuthorityMappingDto.builder()
				.authority_mapping_no(entity.getAuthorityMappingNo())
				.build();
	}
}
