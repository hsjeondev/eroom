package com.eroom.directory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryController {

	@GetMapping("/directory")
	public String selectDirectory01() {
		return "directory/list";
	}
	@GetMapping("/directory2")
	public String selectDirectory02(Model model) {
		
		
		return "directory/list2";
	}
//	@GetMapping("/directory/{id}")
//	public String selectDirectory03(@PathVariable("id") Long id, Model model) {
//		
//		
//		return "directory/list2";
//	}
	
}
