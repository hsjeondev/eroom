package com.eroom.admin.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.admin.dto.EmployeeManageDto;
import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.EmployeeDirectoryService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.StructureDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final EmployeeService employeeService;
	private final EmployeeDirectoryService employeeDirectoryService;
	private final StructureService structureService;
	private final AttendanceService attendanceService;
	
	// 회의실 목록
	@GetMapping("/meetingroom")
	public String selectMeetingroomList(Model model) {
		// 이름 바꿀 예정
		return "admin/meetingroom";
	}
	
	// 차량 목록
	@GetMapping("/vehicle")
	public String selectVehicleList(Model model) {
		// 이름 바꿀 예정
		return "admin/vehicle";
	}
	
	// 숙직실 목록
	@GetMapping("/sleep")
	public String selectSleepList(Model model) {
		// 이름 바꿀 예정 -> selectSleepManagementList
		return "admin/sleep";
	}
	
	// 주소록 관리
	@GetMapping("/directoryManagement")
	public String selectDirectoryManagementList(Model model) {
		return "admin/directoryManagement";
	}
	
	// 회원 관리
	@GetMapping("/employeeManagement")
	public String selectEmployeeManagementList(Model model) {
		// 사원의 정보 조회
		List<Employee> employeeList = employeeService.findAllEmployee();
		
		// 반환용 DTO리스트
		List<EmployeeManageDto> manageDtoList = new ArrayList<>();
	
		
		for(Employee employee : employeeList) {
			// 사원 dto
			EmployeeDto employeeDto = new EmployeeDto().toDto(employee);
			
			// 주소록 조회, dto
			Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeDto.getEmployee_no());
			DirectoryDto directoryDto = (directory != null) ? new DirectoryDto().toDto(directory) : null;
			
			// 부서 정보 가져오기
			Structure structure = employee.getStructure();
			String departmentName = "-";
			String teamName = "-";
			StructureDto structureDto = null;
			
			if(structure != null) {
				structureDto = StructureDto.toDto(structure);
				// parent_code가 없으면 structure는 부서
				if(structure.getParentCode() == null) {
					departmentName = structure.getCodeName(); // 부서명
					teamName = "-";
				}else {
					// 팀 (부모코드 있음)
					teamName = structure.getCodeName(); // 팀명
					
					// 부서 이름은 parent_code == separator_code 로 조회
					Structure parent = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(structure.getParentCode());
					if(parent != null) {
						departmentName = parent.getCodeName();
						
					}
				}
			}
			
			AnnualLeave annualLeave = attendanceService.selectAnnualLeaveByEmployeeNo(employee.getEmployeeNo());
			
			// 연차 정보 조회
			AnnualLeaveDto annualLeaveDto = null;
			if(annualLeave != null) {
				annualLeaveDto = new AnnualLeaveDto().toDto(annualLeave);
			}
			
			// EmployeeManageDto 통합 DTO 생성
			EmployeeManageDto manageDto = EmployeeManageDto.toDto(employeeDto,
					directoryDto,
					structureDto,
					null,
					annualLeaveDto,
					departmentName,
					teamName,
					employeeDto.getEmployee_hire_date() != null ? employeeDto.getEmployee_hire_date().toLocalDate().toString() : "-",
					employeeDto.getEmployee_end_date() != null?  employeeDto.getEmployee_end_date().toLocalDate().toString() : "-"
					);
			
			manageDtoList.add(manageDto);
			
			
		}
		model.addAttribute("employeeList",manageDtoList);
		return "admin/employeeManagement";
	}
	
	// 회원 근태 상세정보 
	@GetMapping("/employeeInfoView")
	public String selectEmployeeInfoView(@RequestParam("employeeNo") Long employeeNo,
										@RequestParam(name="month", required = false) String month,
										Model model) {
		// 현재 로그인한 사용자 정보
		EmployeeDetails employeeDetail = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Employee loginEmployee = employeeDetail.getEmployee();
		// 로그인한 사용자가 관리자(admin)인지 여부
		boolean isAdmin = "admin".equals(loginEmployee.getEmployeeId());

		model.addAttribute("isAdmin", isAdmin);
		
		// employeeNo로 사원 정보 조회
		Employee employee = employeeService.findEmployeeByEmployeeNo(employeeNo);
		model.addAttribute("employee", employee);
		
		// 부서 정보
		String departmentName = "-"; // 기본값
		Structure employeeStructure = employee.getStructure();
		if (employeeStructure != null) {
			// 부서 정보가 있을 경우에만 parentCode를 사용하여 부서명 조회
			String parentCode = employeeStructure.getParentCode();
			Structure structure = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(parentCode);
			if (structure != null) {
				departmentName = structure.getCodeName();
			} else {
				// 부서 정보가 없을 경우 기본값 사용
				departmentName = "-";
			}
		}
		model.addAttribute("departmentName", departmentName);

		// 주소록 정보 조회
		Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeNo);
		if (directory != null) {
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
		if (monthList.contains(currentMonth)) {
			monthList.remove(currentMonth); // 중복 제거
			monthList.add(0, currentMonth); // 맨 앞에 삽입
		}
		model.addAttribute("monthList", monthList);
		// selectedMonth -> 사용자가 선택한 월이 없으면 현재 월이 기본값
		String selectedMonth = (month != null && !month.isEmpty()) ? month : currentMonth;
		model.addAttribute("selectedMonth", selectedMonth);
		// 해당 월의 근태 기록 조회
		List<AttendanceDto> attendanceList = attendanceService.selectAttendanceListByMonth(employeeNo, selectedMonth);
		model.addAttribute("attendanceList", attendanceList);
		
		// 근태 차트 요약 데이터
		Map<String, Object> chartData = attendanceService.getAttendanceChartDataByEmployeeNo(employeeNo);
		// 근무시간 문자열만 가져오기
		String totalWorkTime = (String) chartData.get("totalWorkTime");
		model.addAttribute("totalWorkTime", totalWorkTime);
		model.addAttribute("summaryData",chartData);
		
		return "admin/employeeInfoView";
	}
	
	// 회원의 근태 차트
	@GetMapping("/employeeChartData")
	@ResponseBody
	public Map<String,Object> getEmployeeChartData(@RequestParam("employeeNo") Long employeeNo) {
		return attendanceService.getAttendanceChartDataByEmployeeNo(employeeNo);
	}
	
	// 회원의 근태 기록 상세
	@GetMapping("/attendanceDetail")
	@ResponseBody
	public AttendanceDto getAttendanceDetail(@RequestParam("attendanceNo") Long attendanceNo){
	    System.out.println("받은 attendanceNo: " + attendanceNo);
	    AttendanceDto dto = attendanceService.findAttendanceByNo(attendanceNo);
	    System.out.println("반환할 DTO: " + dto);
	    return dto;
	}
	
	@PostMapping("attendanceUpdate")
	@ResponseBody
	public Map<String, Object> updateAttendance(@RequestParam("attendanceNo") Long attendanceNo,
			@RequestParam("attendanceCheckInTime") String attendanceCheckInTime,
			@RequestParam("attendanceCheckOutTime") String attendanceCheckOutTime) {
		// 출근시간, 퇴근시간 수정
		// return attendanceService.updateAttendance(attendanceNo, attendanceCheckInTime, attendanceCheckOutTime);
		return null;
	}
	
	
}
