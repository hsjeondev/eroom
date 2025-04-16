package com.eroom.employee.dto;

import java.time.LocalDateTime;

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
	
	public Structure toEntity() {
		return Structure.builder()
				.structureNo(structure_no)
				.separatorCode(separator_code)
				.codeName(code_name)
				.order(order)
				.parentCode(parent_code)
				.visibleYn(visible_yn)
				.build();
	}
	
	public StructureDto toDto(Structure structure) {
		return StructureDto.builder()
				.structure_no(structure.getStructureNo())
				.separator_code(structure.getSeparatorCode())
				.code_name(structure.getCodeName())
				.order(structure.getOrder())
				.parent_code(structure.getParentCode())
				.visible_yn(structure.getVisibleYn())
				.build();
	}
}
