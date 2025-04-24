package com.eroom.drive.contorller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.service.DriveService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/drive")
@RequiredArgsConstructor
public class DriveController {

	private final DriveService driveService;
	
	// ------------------------------------------ 드라이브 메인 ------------------------------------------
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
		Long employeeNo = user.getEmployee().getEmployeeNo();
	    List<DriveDto> fileList = driveService.findPersonalDriveFiles(employeeNo);
	    model.addAttribute("fileList", fileList);
		return "drive/personal";
	}
	// ------------------------------------------ 파일 업로드 ------------------------------------------
	// 개인 드라이브 파일 업로드
	@PostMapping("/upload/personal")
	@ResponseBody
	public Map<String,String> uploadPersonalDriveFiles(DriveDto driverDto,@RequestParam("driveDescriptions") List<String> driveDescriptions, @AuthenticationPrincipal EmployeeDetails user){
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "업로드 실패");
		
		driverDto.setDriveDescriptions(driveDescriptions);
		int result = driveService.uploadDriveFiles(driverDto,user.getEmployee().getEmployeeNo());
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "업로드 성공");
		} 
		return resultMap;
	}
	
	
}
