package com.eroom.attendance.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.EmployeeDirectoryService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	
	private final AttendanceService attendanceService;
	private final StructureService structureService;
	private final EmployeeDirectoryService employeeDirectoryService;
	
	// 근태 페이지 목록
	@GetMapping("/list")
	public String selectAttendanceList(@RequestParam(name= "month",required = false) String month,Model model, Authentication authentication) {
		// 로그인한 사용자 정보
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
//        System.out.println("directory 정보 : " + directory);
        if(directory != null) {
        	DirectoryDto directoryDto = new DirectoryDto().toDto(directory);
        	model.addAttribute("directory", directoryDto);
        }
        
        
        // 연차 정보 조회
        AnnualLeave annualLeave = attendanceService.selectAnnualLeaveByEmployeeNo(employeeNo);
		if (annualLeave != null) {
			AnnualLeaveDto annualLeaveDto = new AnnualLeaveDto().toDto(annualLeave);
			model.addAttribute("annualLeave", annualLeaveDto);
		}
        
		// 근태 기록이 있는 월 목록 조회
		List<String> monthList = attendanceService.selectAttendanceMonthList(employeeNo);
		
		// 현재 년월 가져오기
		String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM"));
		
		// 현재 월이 목록에 포함되어 있다면 맨 위로 정렬
		if(monthList.contains(currentMonth)) {
			monthList.remove(currentMonth); // 중복 제거
			monthList.add(0, currentMonth); // 맨 앞에 삽입
		}
		
		model.addAttribute("monthList", monthList);
		
		// selectedMonth -> 사용자가 선택한 월이 없으면 현재 월이 기본값
		String selectedMonth = (month != null && !month.isEmpty()) ? month : currentMonth;
		model.addAttribute("selectedMonth",selectedMonth);
		// 해당 월의 근태 기록 조회
		List<AttendanceDto> attendanceList = attendanceService.selectAttendanceListByMonth(employeeNo, selectedMonth);
		model.addAttribute("attendanceList",attendanceList);
		return "attendance/list";
	}
	
	
}
