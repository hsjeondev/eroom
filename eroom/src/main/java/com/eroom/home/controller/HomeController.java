package com.eroom.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping({"", "/"})
	public String home() {
		System.out.println("홈 컨트롤러 진입");
		return "index";
	}
}
