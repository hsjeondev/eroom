package com.eroom.project.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

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
	private String description;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate project_start;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate project_end;
	private String project_github_url;
	private String project_github_token;
	private String proceed;
	private int progress;
	private List<ProjectMemberDto> project_members;

	public Project toEntity() {
		Project project = Project.builder()
				.projectNo(project_no)
				.projectName(project_name)
				.description(description)
				.projectStart(project_start)
				.projectEnd(project_end)
				.projectGithubUrl(project_github_url)
				.projectGithubToken(project_github_token)
				.proceed(proceed)
				.progress(progress)
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
				.description(project.getDescription())
				.project_start(project.getProjectStart())
				.project_end(project.getProjectEnd())
				.proceed(project.getProceed())
				.project_github_url(project.getProjectGithubUrl())
				.project_github_token(project.getProjectGithubToken())
				.progress(project.getProgress())
				.project_members(dtoList)
				.build();
	}
}
