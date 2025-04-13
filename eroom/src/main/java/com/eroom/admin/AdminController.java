package com.eroom.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	// 회의실 목록
	@GetMapping("/meetingroom")
	public String selectMeetingroomList(Model model) {
		// 이름 바꿀 예정
		return "admin/meetingroom";
	}
	
	// 차량 목록
	@GetMapping("/vehicle")
	public String selectVehicleList(Model model) {
		// 이름 바꿀 예정
		return "admin/vehicle";
	}
	
	// 숙직실 목록
	@GetMapping("/sleep")
	public String selectSleepList(Model model) {
		// 이름 바꿀 예정 -> selectSleepManagementList
		return "admin/sleep";
	}
	
	// 주소록 관리
	@GetMapping("/directoryManagement")
	public String selectDirectoryManagementList(Model model) {
		return "admin/directoryManagement";
	}
	
	// 회원 관리
	@GetMapping("/employeeManagement")
	public String selectEmployeeManagementList(Model model) {
		return "admin/employeeManagement";
	}
	
	// 회원 상세정보 
	@GetMapping("/employeeInfoView")
	public String selectEmployeeInfo(Model model) {
		return "admin/employeeInfoView";
	}
	
	
}
