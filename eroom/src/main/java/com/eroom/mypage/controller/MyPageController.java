package com.eroom.mypage.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.service.ApprovalService;
import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.service.AnnualLeaveService;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.common.AnnualPolicyUtil;
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
import com.eroom.report.service.ReportService;
import com.eroom.security.EmployeeDetails;

import jakarta.servlet.http.HttpServletResponse;
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
	private final ApprovalService approvalService;
	private final ReportService reportService;
	private final AnnualLeaveService annualLeaveService;
	private final AnnualPolicyUtil annualPolicyUtil;
	
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
		String teamName = "-";
		// 부서 정보 조회
		if(employeeStructure != null) {
			if(employeeStructure.getParentCode() == null) {
				// 팀이 없을 경우
				departmentName = employeeStructure.getCodeName();
				teamName = "-";
			}else {
				// 팀이 있는 경우
				teamName = employeeStructure.getCodeName();
				Structure department = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(employeeStructure.getParentCode());
				if(department != null) {
					departmentName = department.getCodeName();
				}
			}

		}
        model.addAttribute("departmentName", departmentName);
        model.addAttribute("teamName",teamName);
        
        // 주소록 정보 조회
        Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeNo);
//        System.out.println("directory 정보 : " + directory);
        if(directory != null) {
        	DirectoryDto directoryDto = new DirectoryDto().toDto(directory);
        	model.addAttribute("directory", directoryDto);
        }
        // 현재 년도
		// Long currentYear = Long.valueOf(LocalDate.now().getYear());
		// 기준일 기반 연차 연도 계산
        Long targetYear = annualPolicyUtil.getTargetYearByPolicy();
        // 연차 정보 조회
        AnnualLeave annualLeave = annualLeaveService.selectAnnualLeaveByEmployeeNoAndYear(employee.getEmployeeNo(),targetYear);
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

	    if (employee == null) {
	        result.put("res_code", 404);
	        result.put("res_msg", "회원 정보를 찾을 수 없습니다.");
	        return result;
	    }

	    // 기본 정보 설정
	    result.put("res_code", 200);
	    result.put("employee_name", employee.getEmployeeName());
	    result.put("employee_position", employee.getEmployeePosition());
	    result.put("employee_birth", employee.getEmployeeBirth() != null ? employee.getEmployeeBirth().toString() : "");
	    result.put("employee_hire_date", employee.getEmployeeHireDate() != null ? employee.getEmployeeHireDate().toLocalDate().toString() : "");
	    result.put("employee_end_date", employee.getEmployeeEndDate() != null ? employee.getEmployeeEndDate().toLocalDate().toString() : "");

	    // Directory 정보는 있을 때만 추가
	    Directory directory = employeeDirectoryService.selectDirectoryByEmployeeNo(employeeNo);
	    if (directory != null) {
	        result.put("directory_phone", directory.getDirectoryPhone());
	        result.put("directory_zipcode", directory.getDirectoryZipcode());
	        result.put("directory_address", directory.getDirectoryAddress());
	    }

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
	
	// 진행중인 결재 목록
	@GetMapping("/approval/ongoing")
	@ResponseBody
	public List<Map<String,Object>> getOngoingApprovals(Authentication authentication){
		EmployeeDetails employee = (EmployeeDetails) authentication.getPrincipal();
		List<ApprovalDto> dtoList = approvalService.myPageMyApprovalsStatusIsS(employee.getEmployee().getEmployeeNo());
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		List<Map<String,Object>> resultList = new ArrayList<>();
		for(ApprovalDto dto : dtoList) {
			Map<String,Object> map = new HashMap<>();
			map.put("approval_no", dto.getApproval_no());
			map.put("approval_title", dto.getApproval_title());
			if(dto.getApproval_reg_date() != null) {
				map.put("approval_display_date", dto.getApproval_reg_date().format(formatter));
			}else {
				map.put("approval_display_date", "-");
			}
			resultList.add(map);
		}
		return resultList;
	}
	
	// 승인된 결재 목록
	@GetMapping("/approval/completed")
	@ResponseBody
	public List<Map<String,Object>> getCompletedApprovals(Authentication authentication){
		EmployeeDetails employee = (EmployeeDetails) authentication.getPrincipal();
		List<ApprovalDto> dtoList = approvalService.myPageMyApprovalsStatusIsA(employee.getEmployee().getEmployeeNo());
		
		List<Map<String,Object>> resultList = new ArrayList<>();
		for(ApprovalDto dto : dtoList) {
			Map<String,Object> map = new HashMap<>();
			map.put("approval_no",dto.getApproval_no());
			map.put("approval_title",dto.getApproval_title());
			
			map.put("approval_display_date", dto.getCompleted_date());
			resultList.add(map);
			
		}
		return resultList;
	}
	
	// 근태 기록 엑셀로 다운로드
	@GetMapping("/attendanceExcel")
	public void exportAttendanceExcel(@RequestParam("month") String month,
									Authentication authentication,
									HttpServletResponse response) throws Exception{
		EmployeeDetails user = (EmployeeDetails) authentication.getPrincipal();
		Long employeeNo = user.getEmployee().getEmployeeNo();		
		// 해당 월의 근태 기록
		List<AttendanceDto> records = attendanceService.selectAttendanceListByMonth(employeeNo, month);
		// 엑셀 생성
		ByteArrayOutputStream excelFile = reportService.generateAttendanceExcelReport(records, month);
		
		// 응답 설정
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=attendance_"+month+".xls");
		response.getOutputStream().write(excelFile.toByteArray());
		response.getOutputStream().flush();
		
	}


}
