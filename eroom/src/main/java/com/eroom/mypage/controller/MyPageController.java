package com.eroom.mypage.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.DirectoryService;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.drive.service.ProfileService;
import com.eroom.employee.dto.EmployeeUpdateDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
	private final StructureService structureService;
	private final DirectoryService employeeDirectoryService;
	private final AttendanceService attendanceService;
	private final EmployeeService employeeService;
	private final DriveRepository driveRepository;
	private final ProfileService profileService;
	
	// 마이페이지
	@GetMapping("/list")
	public String myPageView(@RequestParam(name= "month",required = false) String month,Model model, Authentication authentication) {
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
//        System.out.println("directory 정보 : " + directory);
        if(directory != null) {
        	DirectoryDto directoryDto = new DirectoryDto().toDto(directory);
        	model.addAttribute("directory", directoryDto);
        }
        
        // 연차 정보 조회
        AnnualLeave annualLeave = attendanceService.selectAnnualLeaveByEmployeeNo(employeeNo);
        AnnualLeaveDto annualLeaveDto;
        if (annualLeave != null) {
        	annualLeaveDto = new AnnualLeaveDto().toDto(annualLeave);
		}else {
			annualLeaveDto = new AnnualLeaveDto();
			annualLeaveDto.setAnnual_leave_total(0.0);
			annualLeaveDto.setAnnual_leave_used(0.0);
			annualLeaveDto.setAnnual_leave_remain(0.0);
		}
        model.addAttribute("annualLeave",annualLeaveDto);
        
        // 프로필 이미지 조회
        Drive latestProfile = driveRepository.findTop1ByUploader_EmployeeNoAndSeparatorCodeAndVisibleYnOrderByDriveRegDateDesc(
        		employeeNo, "FL008", "Y");

        String profileImgUrl = (latestProfile != null) ?
        	"/upload/" + latestProfile.getDrivePath() :
        	"/assets/img/team/avatar.webp"; // 기본 프로필 이미지
        model.addAttribute("profileImage",profileImgUrl);
        
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
		
		// 근태 차트 요약 데이터
		Map<String, Object> chartData = attendanceService.getAttendanceChartData(employeeDetail);
		// 근무시간 문자열만 가져오기
		String totalWorkTime = (String) chartData.get("totalWorkTime");
		model.addAttribute("totalWorkTime", totalWorkTime);
		
		// 근태요약 정보 가져오기
		Map<String,Object> summaryData = attendanceService.getAttendanceChartData(employeeDetail);
		model.addAttribute("summaryData", summaryData);        

		return "mypage/list";
	}
	@GetMapping("/chartData")
	@ResponseBody
	public Map<String,Object> getChartData(){
		// 현재 로그인 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		
		// 차트 데이터
		return attendanceService.getAttendanceChartData(employeeDetail);
		
	}
	// 회원 정보 가져오기
	@GetMapping("/getEmployeeDetail")
	@ResponseBody
	public Map<String, Object> getEmployeeDetail(@RequestParam("employeeNo") Long employeeNo) {
	    Map<String, Object> result = new HashMap<>();
	    Employee employee = employeeService.findEmployeeByEmployeeNo(employeeNo);
	    Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeNo);

	    if (employee == null || directory == null) {
	        result.put("res_code", 404);
	        result.put("res_msg", "정보를 찾을 수 없습니다.");
	        return result;
	    }

	    result.put("res_code", 200);
	    result.put("employee_name", employee.getEmployeeName());
	    result.put("directory_phone", directory.getDirectoryPhone());
	    result.put("directory_zipcode", directory.getDirectoryZipcode());
	    result.put("directory_address", directory.getDirectoryAddress());
	    return result;
	}
	
	
	// 회원 정보 수정하기
	@PostMapping("/updateEmployee")
	@ResponseBody
	public Map<String, Object> updateMyInfo(@RequestBody EmployeeUpdateDto dto, Authentication authentication) {
	    Map<String, Object> resultMap = new HashMap<>();
	    try {
	        // 로그인한 사용자 정보 가져오기
	        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	        Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	        // 본인의 정보만 수정 가능하게 설정
	        dto.setEmployee_no(employeeNo);

	        // 서비스 호출
	        employeeService.updateEmployeeFromMypage(dto);

	        resultMap.put("res_code", 200);
	        resultMap.put("res_msg", "회원 정보가 성공적으로 수정되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", 500);
	        resultMap.put("res_msg", "회원 정보 수정 중 오류가 발생했습니다.");
	    }

	    return resultMap;
	}
}
