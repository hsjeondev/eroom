package com.eroom.attendance.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.Attendance;
import com.eroom.attendance.repository.AttendanceRepository;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	
	private final AttendanceService attendanceService;
	private final AttendanceRepository attendanceRepository;
	
	// 근태 페이지 목록
	@GetMapping("/list")
	public String selectAttendanceList(Model model) {
//		 List<Attendance> resultList = attendanceService.selectAttendanceList();
//		model.addAttribute("attendanceList",resultList);
		return "attendance/list";
	}
	
	
}
