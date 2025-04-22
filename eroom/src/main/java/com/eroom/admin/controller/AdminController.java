package com.eroom.admin.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.admin.dto.EmployeeManageDto;
import com.eroom.attendance.dto.AnnualLeaveDto;
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
	
	// 회원 상세정보 
	@GetMapping("/employeeInfoView")
	public String selectEmployeeInfo(Model model) {
		return "admin/employeeInfoView";
	}
	
	
}
