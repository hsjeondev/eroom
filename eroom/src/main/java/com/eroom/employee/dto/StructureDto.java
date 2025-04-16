package com.eroom.employee.dto;

import java.util.List;

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
public class StructureDto {
	private Long structure_no;
	private String separator_code;
	private String code_name;
	private Long order;
	private String parent_code;
	private String visible_yn;
	private List<Employee> employees;
	
	public Structure toEntity() {
	    return Structure.builder()
	        .structureNo(structure_no)
	        .separatorCode(separator_code)
	        .codeName(code_name)
	        .order(order)
	        .parentCode(parent_code)
	        .visibleYn(visible_yn)
	        .employees(employees)
	        .build();
	}
	
	public static StructureDto toDto(Structure entity) {
	    return StructureDto.builder()
	        .structure_no(entity.getStructureNo())
	        .separator_code(entity.getSeparatorCode())
	        .code_name(entity.getCodeName())
	        .order(entity.getOrder())
	        .parent_code(entity.getParentCode())
	        .visible_yn(entity.getVisibleYn())
	        .employees(entity.getEmployees())
	        .build();
	}
}
