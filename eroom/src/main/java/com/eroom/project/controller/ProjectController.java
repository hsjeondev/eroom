package com.eroom.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
public class ProjectController {

	@GetMapping("/all")
	public String allProjectView() {
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

}
