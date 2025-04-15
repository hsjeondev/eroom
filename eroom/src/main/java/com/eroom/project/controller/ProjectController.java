package com.eroom.project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.project.dto.GithubPullRequestDto;
import com.eroom.project.dto.ProjectDto;
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
		
		// 이 2줄은 나중에 프로젝트 디테일의 개발탭으로 이동
	    List<GithubPullRequestDto> pullRequests = projectService.fetchPullRequests(project_no);
	    model.addAttribute("pullRequests", pullRequests);
		
		model.addAttribute("project", project);
		return "project/projectDetail";
	}
	
	@PostMapping("/create")
	public String createProjectView(ProjectDto projectDto, Model model) {
		System.out.println(projectDto.getProject_no());
		
		// projectService.createProject(projectDto);
		
	    return "";
	}
	
}
