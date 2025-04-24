package com.eroom.drive.contorller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.drive.dto.DriverDto;
import com.eroom.security.EmployeeDetails;

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
	// 개인 드라이브
	@GetMapping("/personal")
	public String selectDrivePersonal(Model model, @AuthenticationPrincipal EmployeeDetails user) {
		return "drive/personal";
	}
	// 드라이브 업로드
//	@GetMapping("/create")
//	@ResponseBody
//	public Map<String,String> createDrive(DriverDto driverDto @RequestParam String){
//		
//	}
}
