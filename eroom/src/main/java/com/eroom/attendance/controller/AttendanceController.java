package com.eroom.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

	@GetMapping("/list")
	public String selectAttendanceAll(Model model) {
		return "attendance/list";
	}
	
	
}
