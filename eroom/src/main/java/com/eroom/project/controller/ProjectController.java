package com.eroom.project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.project.dto.ProjectDto;
import com.eroom.project.entity.Project;
import com.eroom.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
	
	private final ProjectService projectService;

	@GetMapping("/all")
	public String allProjectView(Model model) {
		
		List<ProjectDto> projectList = projectService.findAllProject();
		Long projectCount = projectService.getProjectCount();
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectCount", projectCount);
		
		return "project/allProject";
	}
	
	@GetMapping("/doing")
	public String doingProjectView() {
		return "project/doingProject";
	}
	
	@GetMapping("/done")
	public String doneProjectView() {
		return "project/doneProject";
	}
	
	@GetMapping("/hold")
	public String holdProjectView() {
		return "project/holdProject";
	}
	
	@GetMapping("/yet")
	public String yetProjectView() {
		return "project/yetProject";
	}
	
	@GetMapping("/detail/{project_no}")
	public String detailProjectView(@PathVariable("project_no") Long project_no, Model model) {
		
		ProjectDto project = projectService.findByProjectNo(project_no);
		
		model.addAttribute("project", project);
		return "project/projectDetail";
	}
	
	// github api 테스트 
	@PostMapping("/create")
	public String createProjectView(ProjectDto projectDto) {
		System.out.println(projectDto.getProject_no());
		System.out.println(projectDto.getProject_github_url());
		System.out.println(projectDto.getProject_github_token());
		
		// projectService.createProject(projectDto);
		
		projectService.fetchPullRequests(projectDto.getProject_no());
		
		return "project/allProject";
	}
	
}
