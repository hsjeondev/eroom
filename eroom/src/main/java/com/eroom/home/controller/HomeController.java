package com.eroom.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

	@GetMapping({"", "/"})
	public String home() {
		return "index";
	}

}
