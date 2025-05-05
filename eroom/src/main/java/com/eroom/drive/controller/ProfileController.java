
package com.eroom.drive.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.service.ProfileService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {
	
	private final ProfileService profileService;
	
	// 프로필 업로드
	@PostMapping("/mypage/upload/profile")
	@ResponseBody
	public Map<String,String> uploadProfileImage(DriveDto driveDto,
												@AuthenticationPrincipal EmployeeDetails user){
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "업로드 실패");
		
		int result = profileService.uploadProfileImage(driveDto,user.getEmployee().getEmployeeNo());
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "업로드 성공");
		}
		return resultMap;
		
	}
	
}
