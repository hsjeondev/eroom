package com.eroom.employee.dto;

import java.time.LocalDateTime;
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
	private Long sort_order;
	private String parent_code;
	private String visible_yn;
	private String creator;
	private String editor;
	private List<Employee> employees;
	
	public Structure toEntity() {
	    return Structure.builder()
	        .structureNo(structure_no)
	        .separatorCode(separator_code)
	        .codeName(code_name)
	        .sortOrder(sort_order)
	        .parentCode(parent_code)
	        .visibleYn(visible_yn)
	        .employees(employees)
	        .creator(creator)
	        .editor(editor)
	        .build();
	}
	
	public static StructureDto toDto(Structure entity) {
	    return StructureDto.builder()
	        .structure_no(entity.getStructureNo())
	        .separator_code(entity.getSeparatorCode())
	        .code_name(entity.getCodeName())
	        .sort_order(entity.getSortOrder())
	        .parent_code(entity.getParentCode())
	        .visible_yn(entity.getVisibleYn())
	        .employees(entity.getEmployees())
	        .creator(entity.getCreator())
	        .editor(entity.getEditor())
	        .build();
	}
}
