package com.eroom.directory.dto;

import com.eroom.directory.entity.Department;
import com.eroom.directory.entity.Team;

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
	private Long team_no;
	private String team_name;
	private Long team_order;
	private Long department_no;
	
	public Team toEntity() {
		return Team.builder()
				.teamNo(team_no)
				.teamName(team_name)
				.teamOrder(team_order)
				.department(Department.builder().departmentNo(department_no).build())
				.build();
	}
	
	public TeamDto toDto(Team team) {
		return TeamDto.builder()
				.team_no(team.getTeamNo())
				.team_name(team.getTeamName())
				.team_order(team.getTeamOrder())
				.build();
	}
}
