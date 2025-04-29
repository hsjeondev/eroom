package com.eroom.mypage.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.attendance.entity.Attendance;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.DirectoryService;
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
	private final DirectoryService employeeDirectoryService;
	// 마이페이지
	@GetMapping("/list")
	public String myPageView(Model model, Authentication authentication) {
		// 로그인한 사용자 정보
		EmployeeDetails employeeDetail = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		
		Structure employeeStructure = employee.getStructure();  
		String departmentName = "-"; // 기본값
		// 부서 정보 조회
		if(employeeStructure != null) {
			// 부서 정보가 있을 경우에만 parentCode를 사용하여 부서명 조회
			String parentCode = employeeStructure.getParentCode();
			Structure structure = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(parentCode);
			if(structure != null) {
				departmentName = structure.getCodeName();
			} else {
				// 부서 정보가 없을 경우 기본값 사용
				departmentName = "-";
			}
		}
        model.addAttribute("departmentName", departmentName);
        
        // 주소록 정보 조회
        Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeNo);
//	    System.out.println("directory 정보 : " + directory);
        if(directory != null) {
        	DirectoryDto directoryDto = new DirectoryDto().toDto(directory);
        	model.addAttribute("directory", directoryDto);
        }

		return "mypage/list";
	}
}
