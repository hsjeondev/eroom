package com.eroom.attendance.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.attendance.entity.Attendance;
import com.eroom.attendance.repository.AttendanceRepository;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.EmployeeDirectoryService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	
	private final AttendanceService attendanceService;
	private final AttendanceRepository attendanceRepository;
	private final EmployeeDirectoryService employeeDirectoryService;
	
	// 근태 페이지 목록
	@GetMapping("/list")
	public String selectAttendanceList(Model model, Authentication authentication) {
		
		EmployeeDetails employeeDetail = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		
		// 부서 정보 조회
		Structure structure = employeeDirectoryService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(employee.getStructure().getParentCode());
        String departmentName = structure != null ? structure.getCodeName() : "-";
        model.addAttribute("departmentName", departmentName);
        
        // 주소록 정보 조회
        // Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeNo);
		
		List<Attendance> resultList = attendanceService.selectAttendanceList();
		model.addAttribute("attendanceList",resultList);
		return "attendance/list";
	}
	
	
}
