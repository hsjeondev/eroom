package com.eroom.attendance.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.attendance.entity.Attendance;
import com.eroom.attendance.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	
	private final AttendanceService attendanceService;

	@GetMapping("/list")
	public String selectAttendanceList(Model model) {
//		 List<Attendance> resultList = attendanceService.selectAttendanceList();
//		model.addAttribute("attendanceList",resultList);
		return "attendance/list";
	}
	
	@GetMapping("/checkinout")
	public String checkInOut() {
		return "checkinout";
	}

	
	
}
