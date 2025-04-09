package com.eroom.directory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryController {

	@GetMapping("/directory")
	public String selectDirectory01() {
		return "directory/list";
	}
	@GetMapping("/directory2")
	public String selectDirectory02() {
		return "directory/list2";
	}
	
}
