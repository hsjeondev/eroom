package com.eroom.mypage.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.attendance.entity.Attendance;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.EmployeeDirectoryService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
	private final StructureService structureService;
	private final EmployeeDirectoryService employeeDirectoryService;
	// 마이페이지
	@GetMapping("/list")
	public String myPageView(Model model, Authentication authentication) {
		
		EmployeeDetails employeeDetail = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		
		// 부서 정보 조회
		Structure structure = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(employee.getStructure().getParentCode());
        String departmentName = structure != null ? structure.getCodeName() : "-";
        model.addAttribute("departmentName", departmentName);
        
        // 주소록 정보 조회
        Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeNo);
        model.addAttribute("directory", directory);
		
		return "mypage/list";
	}
}
