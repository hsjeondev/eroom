package com.eroom.employee.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.chat.service.ChatroomService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
	private final EmployeeService employeeService;

	@GetMapping("/login")
	public String loginView() {
		return "login";
	}

	@GetMapping("/admin")
	public String adminView() {
		return "admin";
	}

	@GetMapping("/employes")
	@ResponseBody
	public List<EmployeeDto> getEmployeesByDepartment(@RequestParam(name = "separator_code") String separatorCode) {
		String temp = separatorCode.substring(0, 1);
		if ("T".equals(temp)) {
			// 팀(소속) 선택한 경우: separatorCode 기준 조회
			return employeeService.findEmployeesByStructureName(separatorCode);
		} else {
			// 부서를 선택한 경우: parentCode 기준 조회
			return employeeService.findEmployeesByParentCode(separatorCode);
		}

	}
}
