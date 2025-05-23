package com.eroom.directory.dto;

import java.util.List;

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
public class DeleteDepartmentOrTeamDto {

	private String deleteDeptCode;
	private String deleteTeamCode;
	private String remainTeamCode;
	private List<Long> employeeNoList;
	
	
}
