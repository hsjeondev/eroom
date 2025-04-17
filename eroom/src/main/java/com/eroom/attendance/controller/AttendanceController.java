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
	
	// 출근 페이지
	@GetMapping("/checkinout")
	public String checkInOut() {
		return "checkinout";
	}

	// 출퇴근 기록
	@PostMapping("/log")
	@ResponseBody
	public Map<String,String> recordAttendance(AttendanceDto dto){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "처리에 실패했습니다.");
		
		Attendance result = attendanceService.recordAttendance(dto);
		if(result != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "checkIn".equals(dto.getAttendanceType()) ? "출근 완료! 오늘도 힘내세요~" : "퇴근 완료! 수고하셨습니다");
		}
		
		return resultMap;
	}
	
	// 출근 여부
	@GetMapping("/status")
	@ResponseBody
	public Map<String, String> getTodayAttendanceStatus(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		EmployeeDetails employeeDetail = (EmployeeDetails) authentication.getPrincipal();
		Long employeeNo = employeeDetail.getEmployee().getEmployeeNo();
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
		LocalDateTime todayEnd = todayStart.plusDays(1);
		
		Attendance checkIn = attendanceRepository.findLastCheckInToday(employeeNo, todayStart, todayEnd);
		
		Map<String,String> result = new HashMap<>();
		
		if(checkIn == null) {
			result.put("status", "notCheckedIn");
		}else if(checkIn.getAttendanceCheckOutTime() == null) {
			result.put("status", "checkedIn");
		}else {
			result.put("status", "checkedOut");
		}
		
		return result;
	}
	
}
