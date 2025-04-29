package com.eroom.approval.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.approval.dto.ApprovalSignatureDto;
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
		map.put("res_msg", "등록 실패");
		
		dto.setEmployee_no(employeeNo);
		int result = approvalSignatureService.createSignature(dto);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "등록 성공");
		}
		
		return map;
	}
	
	@PutMapping("/approval/signature/update")
	@ResponseBody
	public Map<String, String> updateSignature(@RequestBody ApprovalSignatureDto dto, Authentication authentication){
		EmployeeDetails employeeDetails = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("res_code", "500");
		map.put("res_msg", "수정 실패");
		
		dto.setEmployee_no(employeeNo);
		int result = approvalSignatureService.updateSignature(dto);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "수정 성공");
		}
		
		return map;
	}

}
