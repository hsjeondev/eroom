package com.eroom.project.dto;

import com.eroom.employee.entity.Employee;
import com.eroom.project.entity.Project;
import com.eroom.project.entity.ProjectMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDto {

	private Long project_member_no;
	
	private Project project;
	
	private Employee project_member;
	
	private String project_manager;
	
	private String github_username;
	
	private String is_manager;
	
	
	public ProjectMemberDto toDto(ProjectMember entity) {
	    return ProjectMemberDto.builder()
	            .project_member_no(entity.getProjectMemberNo())
	            .project(entity.getProject())
	            .project_member(entity.getEmployee())
	            .project_manager(entity.getProjectManager())
	            .github_username(entity.getGithubUsername())
	            .is_manager(entity.getIsManager())
	            .build();
	}

	public ProjectMember toEntity() {
	    return ProjectMember.builder()
	            .projectMemberNo(project_member_no)
	            .project(project)
	            .employee(project_member)
	            .projectManager(project_manager)
	            .githubUsername(github_username)
	            .isManager(is_manager)
	            .build();
	}

}
