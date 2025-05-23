package com.eroom.directory.dto;

import java.util.List;

import com.eroom.employee.dto.StructureDto;

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
public class GetSortableDto {
	
	private String departmentId;    // 부서 ID
    private String departmentName;  // 부서명
    private int order;              // 부서 순서
    private List<GetSortableTaemDto> teams;    // 하위 팀 목록	
	
}
