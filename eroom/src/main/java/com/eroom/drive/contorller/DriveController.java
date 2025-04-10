package com.eroom.drive.contorller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/drive")
@RequiredArgsConstructor
public class DriveController {

	// 회사 드라이브 
	@GetMapping("/company")
	public String selectDriveCompany() {
		return "drive/company";
	}
	// 부서 드라이브
	@GetMapping("/department")
	public String selectDriveDepartment() {
		return "drive/department";
	}
	// 팀 드라이브 
	@GetMapping("/team")
	public String selectDriveTeam() {
		return "drive/team";
	}
	// 개인
	@GetMapping("/personal")
	public String selectDrivePersonal() {
		return "drive/personal";
	}
}
