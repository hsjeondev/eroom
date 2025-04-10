package com.eroom.employee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

	@GetMapping("/login")
	public String loginView() {
		return "login";
	}
	
	@GetMapping("/admin")
	public String adminView() {
		return "admin";
	}
}
