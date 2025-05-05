package com.eroom.drive.controller;

import java.util.HashMap;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.security.EmployeeDetails;
import com.eroom.drive.service.ProfileService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {
	
	private final ProfileService profileService;
	
	// 프로필 변경
	@PostMapping("/mypage/update/profile")
	@ResponseBody
	public Map<String,String> updateProfileImage(@RequestParam(value = "profileImage",required = false) MultipartFile profileImage,
												@RequestParam(value="useDefault",required=false) String useDefault,
												@AuthenticationPrincipal EmployeeDetails user){
		Map<String,String> resultMap = new HashMap<>();
		try {
			// useDefault 가 true 이면 기본 프로필로 변경
			boolean isDefault = "true".equals(useDefault);
			
			// 프로필 이미지 변경 요청
			profileService.updateProfileImage(profileImage,user.getEmployee().getEmployeeNo(), isDefault);
			
			resultMap.put("res_code", "200");
            resultMap.put("res_msg", "프로필 사진이 변경되었습니다.");
		}catch(Exception e) {
			resultMap.put("res_code", "500");
            resultMap.put("res_msg", "변경 실패: " + e.getMessage());
		}
		return resultMap;
	}
	
}
