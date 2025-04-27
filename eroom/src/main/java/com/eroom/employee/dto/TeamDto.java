package com.eroom.employee.dto;

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
public class TeamDto {
	private Long structure_no; // 팀 번호 (Structure PK)
	private String code_name; // 팀 이름
	
	
	// DTO -> Entity
	public Structure toEntity() {
			return Structure.builder()
					.structureNo(structure_no)
					.codeName(code_name)
					.build();
	}
	
	// Entity -> DTO
	public static TeamDto toDto(Structure structure) {
		return new TeamDto(
				structure.getStructureNo(),
				structure.getCodeName()
			);
	}
	
}
