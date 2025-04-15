package com.eroom.project.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.eroom.project.entity.Project;
import com.eroom.project.entity.ProjectMember;

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
public class ProjectDto {

	private Long project_no;
	private String project_name;
	private LocalDateTime project_start;
	private LocalDateTime project_end;
	private String proceed;
	private List<ProjectMemberDto> project_members;

	public Project toEntity() {
		Project project = Project.builder()
				.projectNo(project_no)
				.projectName(project_name)
				.projectStart(project_start)
				.projectEnd(project_end)
				.proceed(proceed)
				.build();



		return project;
	}

	public ProjectDto toDto(Project project) {
		List<ProjectMemberDto> dtoList = new ArrayList<>();
		if (project.getProjectMembers() != null) {
			for (ProjectMember member : project.getProjectMembers()) {
				dtoList.add(new ProjectMemberDto().toDto(member));
			}
		}

		return ProjectDto.builder()
				.project_no(project.getProjectNo())
				.project_name(project.getProjectName())
				.project_start(project.getProjectStart())
				.project_end(project.getProjectEnd())
				.proceed(project.getProceed())
				.project_members(dtoList)
				.build();
	}
}
