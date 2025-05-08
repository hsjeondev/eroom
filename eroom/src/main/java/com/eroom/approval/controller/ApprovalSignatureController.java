package com.eroom.approval.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.approval.dto.ApprovalSignatureDto;
import com.eroom.approval.entity.ApprovalSignature;
import com.eroom.approval.service.ApprovalSignatureService;
import com.eroom.employee.entity.Employee;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ApprovalSignatureController {
	
	private final ApprovalSignatureService approvalSignatureService;
	
	@PostMapping("/approval/signature/create")
	@ResponseBody
	public Map<String, String> createSignature(@RequestBody ApprovalSignatureDto dto, Authentication authentication){
		EmployeeDetails employeeDetails = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("res_code", "500");
		map.put("res_msg", "저장된 서명이 있습니다.");
		
		dto.setEmployee_no(employeeNo);
		int result = approvalSignatureService.createSignature(dto);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "서명 저장 성공");
		}
		
		return map;
	}
	
//	@PutMapping("/approval/signature/update")
//	@ResponseBody
//	public Map<String, String> updateSignature(@RequestBody ApprovalSignatureDto dto, Authentication authentication){
//		EmployeeDetails employeeDetails = (EmployeeDetails)authentication.getPrincipal();
//		Employee employee = employeeDetails.getEmployee();
//		Long employeeNo = employee.getEmployeeNo();
//		
//		
//		Map<String, String> map = new HashMap<String, String>();
//		
//		map.put("res_code", "500");
//		map.put("res_msg", "서명 저장 실패");
//		map.put("memo", "수정 실패");
//		
//		dto.setEmployee_no(employeeNo);
//		int result = approvalSignatureService.updateSignature(dto);
//		
//		if(result > 0) {
//			map.put("res_code", "200");
//			map.put("res_msg", "서명 저장 성공");
//			map.put("memo", "수정 성공");
//		}
//		
//		return map;
//	}
	@PutMapping("/approval/signature/delete")
	@ResponseBody
	public Map<String, String> deleteSignature(Authentication authentication){
		EmployeeDetails employeeDetails = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("res_code", "500");
		map.put("res_msg", "서명 삭제 실패");
		int result = approvalSignatureService.deleteSignature(employeeNo);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "서명 삭제 성공");
		}
		
		return map;
	}
	
	@GetMapping("/approval/signature/search")
	@ResponseBody
	public Map<String, Object> getSignature(@AuthenticationPrincipal EmployeeDetails user) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("res_code", "500");
			map.put("res_msg", "조회 실패");
			ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(user.getEmployee());
			if (approvalSignature != null) {
				// Base64로 인코딩해서 반환
				// "data:image/png;base64," + approvalSignatureService.encodeToBase64(approvalSignature.getApprovalSignatureBlob());
				ApprovalSignatureDto dto = new ApprovalSignatureDto().toDto(approvalSignature);
				map.put("signatureDto", dto);
			} else {
				// 이미지가 없으면 빈 문자열 반환
				// return "";
				map.put("signatureDto", "");
			}
			map.put("res_code", "200");
			map.put("res_msg", "조회 성공");
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
