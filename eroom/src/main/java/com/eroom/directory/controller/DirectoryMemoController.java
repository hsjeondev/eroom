package com.eroom.directory.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.directory.dto.DirectoryMemoDto;
import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.directory.service.DirectoryMemoService;
import com.eroom.employee.entity.Employee;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryMemoController {
	
	private final DirectoryMemoService directoryMemoService;
	
	
	// 내 메모 조회
	@GetMapping("/directoryMemo/{id}")
	@ResponseBody
	public Map<String, String> searchMyMemo(@PathVariable("id") Long targetNo, Model model, Authentication authentication){
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "메모 조회 실패");
		map.put("memo", "");
		
		List<DirectoryMemo> directoryMemo = directoryMemoService.selectTargetMemo(employeeNo, targetNo);
		
		
		if(!directoryMemo.isEmpty()) {
			map.put("res_code", "200");
			map.put("res_msg", "입력된 메모 없음");
			if(directoryMemo.get(directoryMemo.size() - 1).getDirectoryMemoContent() != null) {
				String comment = directoryMemo.get(directoryMemo.size() - 1).getDirectoryMemoContent();
				map.put("res_msg", "메모 조회 성공");
				map.put("memo", comment);
			}
		}
		
		
		return map;
	}
	
	// 메모 저장하기
	@PostMapping("directoryMemo/create")
	@ResponseBody
	public Map<String, String> createMemo(@RequestBody DirectoryMemoDto dto, Authentication authentication){
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "메모 저장 실패");
		
//		System.out.println("주소록번호 체크 : " + dto.getDirectory_no());
//		System.out.println("메모 체크 : " + dto.getDirectory_memo_content());
		int result = directoryMemoService.createMemo(dto, employee);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "메모 저장 성공");
		}
		
		return map;
	}
}
