package com.eroom.admin.controller;

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

import com.eroom.admin.dto.CreateEmployeeDto;
import com.eroom.admin.dto.EmployeeManageDto;
import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.service.AnnualLeaveService;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.DirectoryService;
import com.eroom.drive.service.ProfileService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.EmployeeUpdateDto;
import com.eroom.employee.dto.StructureDto;
import com.eroom.employee.dto.TeamDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.facility.dto.FacilityDto;
import com.eroom.facility.entity.Facility;
import com.eroom.facility.service.FacilityService;
import com.eroom.reservation.dto.MeetingRoomDto;
import com.eroom.reservation.dto.VehicleDto;
import com.eroom.reservation.service.MeetingRoomService;
import com.eroom.reservation.service.VehicleService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final EmployeeService employeeService;
	private final DirectoryService employeeDirectoryService;
	private final StructureService structureService;
	private final AttendanceService attendanceService;
	private final ProfileService profileService;
	private final FacilityService facilityService;
	private final MeetingRoomService meetingRoomService;
	private final VehicleService vehicleService;
	private final AnnualLeaveService annualLeaveService;
	
	// 회의실 목록
	@GetMapping("/meetingroom")
	public String selectMeetingroomList(Model model) {
		List<Facility> resultList = facilityService.selectVisibleMeetingRooms();
		// 전체 예약 현황
		List<MeetingRoomDto> reservationList = meetingRoomService.getAllReservations();
		model.addAttribute("meetingroomList",resultList);
		model.addAttribute("meetingroomReservationList",reservationList); // 예약 내역 리스트
		return "admin/meetingroom";
	}
	
	// 회의실 조회
	@GetMapping("/meetingroom/get")
	@ResponseBody
	public Map<String,Object> getMeetingRoomByNo(@RequestParam("facilityNo") Long facilityNo){
		Map<String,Object> resultMap = new HashMap<>();
		FacilityDto dto = facilityService.selectFacilityByNo(facilityNo);
		if(dto == null) {
			resultMap.put("res_code", 404);
			resultMap.put("res_msg", "해당 회의실 정보를 찾을 수 없습니다.");
		}else {
			resultMap.put("res_code",200 );
			resultMap.put("facility_no", dto.getFacility_no());
			resultMap.put("facility_name",dto.getFacility_name() );
			resultMap.put("facility_capacity",dto.getFacility_capacity());
		}
		return resultMap;
	}

	// 회의실 생성
	@PostMapping("/meetingroom/create")
	@ResponseBody
	public Map<String, Object> createMeetingRoom(@RequestParam("room_name") String roomName,
	        									@RequestParam("room_capacity") String roomCapacity,
	        									Authentication authentication) {
	    Map<String, Object> resultMap = new HashMap<>();

	    // 로그인한 사용자 정보
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    String employeeId = employeeDetails.getEmployee().getEmployeeId();
	    // DTO 구성
	    FacilityDto dto = FacilityDto.builder()
	            .facility_name(roomName)
	            .facility_capacity(roomCapacity)
	            .separator_code("F001")
	            .visible_yn("Y")
	            .facility_creator(employeeId)
	            .build();

	    try {
	        facilityService.createMeetingroom(dto);
	        resultMap.put("res_code", 200);
	        resultMap.put("res_msg", "회의실 등록이 성공적으로 완료되었습니다.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        resultMap.put("res_code", 500);
	        resultMap.put("res_msg", "서버 오류로 등록 실패: "+e.getMessage());
	    }
	    return resultMap;
	}
	
	// 회의실 수정
	@PostMapping("/meetingroom/edit")
	@ResponseBody
	public Map<String, Object> updateMeetingRoom(@RequestParam("facility_no") Long facilityNo,
	        									@RequestParam(value = "room_name", required = false) String roomName,
	        									@RequestParam(value = "room_capacity", required = false) String roomCapacity,
	        									Authentication authentication) {
	    Map<String, Object> resultMap = new HashMap<>();
	    // 로그인 사용자
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    String employeeId = employeeDetails.getEmployee().getEmployeeId();
	    // DTO 구성
	    FacilityDto dto = FacilityDto.builder()
	            .facility_no(facilityNo)
	            .facility_name(roomName)
	            .facility_capacity(roomCapacity)
	            .facility_editor(employeeId)
	            .build();
	    try {
	        Facility updated = facilityService.updateMeetingroom(dto);
	        if (updated != null) {
	            resultMap.put("res_code", 200);
	            resultMap.put("res_msg", "회의실 정보가 성공적으로 수정되었습니다.");
	        } else {
	            resultMap.put("res_code", 404);
	            resultMap.put("res_msg", "수정할 회의실을 찾을 수 없습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", 500);
	        resultMap.put("res_msg", "서버 오류로 수정 실패: " + e.getMessage());
	    }
	    return resultMap;
	}
	
	// 회의실 삭제
	@PostMapping("/meetingroom/delete")
	@ResponseBody
	public Map<String, Object> deleteMeetingRoom(@RequestParam("facility_no") Long facilityNo,
												Authentication authentication) {
	    Map<String, Object> resultMap = new HashMap<>();
	    // 로그인 사용자 정보
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    String employeeId = employeeDetails.getEmployee().getEmployeeId();
	    // DTO 구성
	    FacilityDto dto = FacilityDto.builder()
	            .facility_no(facilityNo)
	            .facility_editor(employeeId)
	            .visible_yn("N") // 삭제는 visible_yn = N 처리
	            .build();
	    try {
	        Facility deleted = facilityService.deleteMeetingroom(dto);
	        if (deleted != null) {
	            resultMap.put("res_code", 200);
	            resultMap.put("res_msg", "회의실이 성공적으로 삭제되었습니다.");
	        } else {
	            resultMap.put("res_code", 404);
	            resultMap.put("res_msg", "삭제할 회의실을 찾을 수 없습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", 500);
	        resultMap.put("res_msg", "서버 오류로 삭제 실패: " + e.getMessage());
	    }
	    return resultMap;
	}	
	
	// 회의실명 중복 체크
	@GetMapping("/meetingroom/checkDuplicate")
	@ResponseBody
	public Map<String,Object> checkDuplicateRoomName(@RequestParam("roomName") String roomName){
		Map<String,Object> resultMap = new HashMap<>();
		// String trimmedName = roomName.replaceAll("\\s", "");
		boolean exists = facilityService.existsByNameIgnoringSpaces(roomName);
		resultMap.put("exists", exists);
		resultMap.put("res_code", 200);
		resultMap.put("res_msg", exists ? "중복된 이름입니다." : "사용 가능한 이름입니다.");		
		return resultMap;
	}
	
	// 차량명 중복 체크
	@GetMapping("/vehicle/checkDuplicate")
	@ResponseBody
	public Map<String,Object> checkDuplicateVehicleName(@RequestParam("vehicleName") String vehicleName){
		Map<String,Object> resultMap = new HashMap<>();
		boolean exists = facilityService.existsByNameIgnoringSpaces(vehicleName);
		resultMap.put("exists", exists);
		resultMap.put("res_code", 200);
		resultMap.put("res_msg", exists ? "중복된 이름입니다." : "사용 가능한 이름입니다.");		
		return resultMap;
	}
	
	// 차량 목록
	@GetMapping("/vehicle")
	public String selectVehicleList(Model model) {
		List<Facility> resultList = facilityService.selectVisibleVehicles();
		// 전체 예약 현황
		List<VehicleDto> reservationList = vehicleService.getAllReservations();
		model.addAttribute("vehicleList",resultList);
		model.addAttribute("vehicleReservationList",reservationList); // 예약 내역 리스트
		return "admin/vehicle";
	}
	
	// 차량 조회
	@GetMapping("/vehicle/get")
	@ResponseBody
	public Map<String,Object> getVehicleByNo(@RequestParam("facilityNo") Long facilityNo){
		Map<String,Object> resultMap = new HashMap<>();
		FacilityDto dto = facilityService.selectFacilityByNo(facilityNo);
		if(dto == null) {
			resultMap.put("res_code", 404);
			resultMap.put("res_msg", "해당 차량 정보를 찾을 수 없습니다.");
		}else {
			resultMap.put("res_code",200 );
			resultMap.put("facility_no", dto.getFacility_no());
			resultMap.put("facility_name",dto.getFacility_name() );
			resultMap.put("facility_capacity",dto.getFacility_capacity());
		}
		return resultMap;
	}
	
	// 차량 생성
	@PostMapping("/vehicle/create")
	@ResponseBody
	public Map<String,Object> createVehicle(@RequestParam("facility_name") String vehicleName,
											@RequestParam("facility_capacity") String vehicleCapacity,
											Authentication authentication){
		Map<String,Object> resultMap = new HashMap<>();
		
		EmployeeDetails employee = (EmployeeDetails) authentication.getPrincipal();
		String employeeId = employee.getEmployee().getEmployeeId();
		
		FacilityDto dto = FacilityDto.builder()
						.facility_name(vehicleName)
						.facility_capacity(vehicleCapacity)
						.separator_code("F002")
						.visible_yn("Y")
						.facility_creator(employeeId)
						.build();
		try {
			facilityService.createVehicle(dto);
	        resultMap.put("res_code", 200);
	        resultMap.put("res_msg", "차량 등록이 성공적으로 완료되었습니다.");			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code", 500);
			resultMap.put("res_msg", "서버 오류로 등록 실패:"+e.getMessage());
		}
		return resultMap;
	}
	
	// 차량 수정
	@PostMapping("/vehicle/edit")
	@ResponseBody
	public Map<String,Object> updateVehicle(@RequestParam("facility_no") Long facilityNo,
											@RequestParam(value="facility_name", required=false) String vehicleName,
											@RequestParam(value="facility_capacity", required=false) String vehicleCapacity,
											Authentication authentication) {
		Map<String,Object> resultMap = new HashMap<>();
		EmployeeDetails employee = (EmployeeDetails) authentication.getPrincipal();
		String employeeId = employee.getEmployee().getEmployeeId();
		
		FacilityDto dto = FacilityDto.builder()
						.facility_no(facilityNo)
						.facility_name(vehicleName)
						.facility_capacity(vehicleCapacity)
						.facility_editor(employeeId)
						.build();
		
		try {
			Facility updated = facilityService.updateVehicle(dto);
			if(updated != null) {
		        resultMap.put("res_code", 200);
		        resultMap.put("res_msg", "차량 정보가 성공적으로 수정되었습니다.");
			}else {
		        resultMap.put("res_code", 404);
		        resultMap.put("res_msg", "수정할 차량을 찾을 수 없습니다.");
			}
		}catch(Exception e) {
			e.printStackTrace();
	        resultMap.put("res_code", 500);
	        resultMap.put("res_msg", "서버 오류로 수정 실패: " + e.getMessage());			
		}
		return resultMap;		
	}
	
	// 차량 삭제
	@PostMapping("/vehicle/delete")
	@ResponseBody
	public Map<String,Object> deleteVehicle(@RequestParam("facility_no") Long facilityNo,
											Authentication authentication){
	    Map<String, Object> resultMap = new HashMap<>();
	    // 로그인 사용자 정보
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    String employeeId = employeeDetails.getEmployee().getEmployeeId();
	    // DTO 구성
	    FacilityDto dto = FacilityDto.builder()
	            .facility_no(facilityNo)
	            .facility_editor(employeeId)
	            .visible_yn("N") // 삭제는 visible_yn = N 처리
	            .build();
	    try {
	        Facility deleted = facilityService.deleteVehicle(dto);
	        if (deleted != null) {
	            resultMap.put("res_code", 200);
	            resultMap.put("res_msg", "차량이 성공적으로 삭제되었습니다.");
	        } else {
	            resultMap.put("res_code", 404);
	            resultMap.put("res_msg", "삭제할 차량을 찾을 수 없습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", 500);
	        resultMap.put("res_msg", "서버 오류로 삭제 실패: " + e.getMessage());
	    }
	    return resultMap;		
	}
	
	// 회원 관리
	@GetMapping("/employeeManagement")
	public String selectEmployeeManagementList(@RequestParam(name="deptId",required=false) Long deptId, @RequestParam(name="teamId",required=false) Long teamId,Model model) {
		
		// 모든 부서 목록 조회(부모코드가 null인 것들)
		List<Structure> deptList = structureService.selectDepartmentAll();
		model.addAttribute("deptList", deptList);
		model.addAttribute("deptId",deptId);
		
		// 부서에 속한 팀 목록 조회
		List<Structure> teamList = new ArrayList<>();
		Structure selectedDept = null;
		if(deptId != null) {
			// 부서가 선택된 경우
			for(Structure dept : deptList) {
				if(dept.getStructureNo().equals(deptId)) {
					selectedDept = dept;
					break;
				}
			}
			if(selectedDept != null) {
				// parentCode(=separatorCode)로 팀 목록 조회
				teamList = structureService.selectTeamAllByParentCode(selectedDept.getSeparatorCode());
			}
		}else {
			// 부서가 선택되지 않은 경우
			teamList = structureService.selectAllTeams();
		}
		model.addAttribute("teamList", teamList);
		model.addAttribute("teamId",teamId);
		
		// 드롭다운 버튼 라벨 텍스트 지정
		String deptLabel = "부서";
		if(deptId != null) {
			for(Structure dept : deptList) {
				if(dept.getStructureNo().equals(deptId)) {
					deptLabel = dept.getCodeName();
					break;
				}
			}
		}
		
		String teamLabel = "팀";
		if(teamId != null) {
			for (Structure team : teamList) {
				if (team.getStructureNo().equals(teamId)) {
					teamLabel = team.getCodeName();
					break;
				}
			}
		}
		
		model.addAttribute("deptLabel", deptLabel);
		model.addAttribute("teamLabel", teamLabel);
		
		// 사원의 정보 조회
		List<Employee> employeeList =  new ArrayList<>();; 
		if(teamId != null) {
			// 팀만 선택된 경우
			employeeList = employeeService.findByStructureNo(teamId);
		}else if(deptId != null) {
			// 부서만 선택된 경우 : 그 부서의 모든 팀
			List<Long> teamNos = new ArrayList<>();
			for(Structure t : teamList) {
				teamNos.add(t.getStructureNo());
			}
			
			if(!teamNos.isEmpty()) {
				employeeList = employeeService.findByStructureNoIn(teamNos);
			}
			
		}else {
			// 부서, 팀 모두 선택되지 않은 경우
			employeeList = employeeService.findAllEmployee();
		}
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
			}else {
				annualLeaveDto = new AnnualLeaveDto();
				annualLeaveDto.setAnnual_leave_total(0.0);
				annualLeaveDto.setAnnual_leave_used(0.0);
				annualLeaveDto.setAnnual_leave_remain(0.0);
			}
			
			String profileImageUrl = profileService.getProfileImageUrl(employee.getEmployeeNo());

			// EmployeeManageDto 통합 DTO 생성
			EmployeeManageDto manageDto = EmployeeManageDto.toDto(employeeDto,
					directoryDto,
					structureDto,
					null,
					annualLeaveDto,
					departmentName,
					teamName,
					employeeDto.getEmployee_hire_date() != null ? employeeDto.getEmployee_hire_date().toLocalDate().toString() : "-",
					employeeDto.getEmployee_end_date() != null?  employeeDto.getEmployee_end_date().toLocalDate().toString() : "-",
					profileImageUrl
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
		
		// 프로필 이미지 URL 추가
		String profileImageUrl = profileService.getProfileImageUrl(employeeNo);
		model.addAttribute("profileImageUrl", profileImageUrl);
		
		// 부서 정보
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
		if (directory != null) {
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
	    AttendanceDto dto = attendanceService.findAttendanceByNo(attendanceNo);
	    return dto;
	}
	
	// 회원 근태 정보 수정
	@PostMapping("attendanceUpdate")
	@ResponseBody
	public Map<String, Object> updateAttendance(@RequestParam("attendance_no") Long attendanceNo,
			@RequestParam("attendance_check_in_time") String checkIn,
			@RequestParam("attendance_check_out_time") String checkOut) {
		
		// 출근시간, 퇴근시간 수정
		AttendanceDto updateDto = attendanceService.updateAttendance(attendanceNo, checkIn, checkOut);
		
	    Map<String, Object> resultMap = new HashMap<>();
	    if(updateDto != null) {
	    	resultMap.put("res_code", 200);
	    	resultMap.put("res_msg", "근태 정보가 성공적으로 수정되었습니다.");
	    	resultMap.put("updateAttendance", updateDto);
	    }else {
			resultMap.put("res_code", 500);
			resultMap.put("res_msg", "근태 정보 수정에 실패하였습니다.");
	    }
		return resultMap;
	}
	
	// 회원 이름 중복 확인
	@GetMapping("checkNameDuplicate")
	@ResponseBody
	public Map<String, Object> checkNameDuplicate(@RequestParam("name") String name){
		Map<String,Object> resultMap = new HashMap<>();
		
		boolean isDuplicate = employeeService.existsByEmployeeName(name);
		
		resultMap.put("duplicate",isDuplicate);
		return resultMap;
	}
	
	// 부서 선택에 따른 팀 리스트 반환
	@GetMapping("/findTeamsByDeptId")
	@ResponseBody
	public List<TeamDto> findTeamsByDeptId(@RequestParam("deptId") Long deptId){
		List<TeamDto> resultList = new ArrayList<>();
		
		Structure dept = structureService.getStructureById(deptId);
		
		if(dept != null) {
			List<Structure> teamList = structureService.selectTeamAllByParentCode(dept.getSeparatorCode());
			for(Structure team : teamList) {
				resultList.add(TeamDto.toDto(team)); 
			}
		}
		return resultList;
	}
	
	// 회원 생성
	@PostMapping("/createEmployee")
	@ResponseBody
	public Map<String,Object> createEmployee(@RequestBody CreateEmployeeDto dto){
		Map<String,Object> resultMap = new HashMap<>();
		
		try {
			employeeService.createEmployee(dto);
			resultMap.put("res_code", 200);
			resultMap.put("res_msg", "회원 등록이 완료되었습니다.");
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code",500);
			
			resultMap.put("res_msg", "회원 등록에 실패했습니다.");
		}
		return resultMap;
	}
	
	// 회원 정보 수정
	@PostMapping("/updateEmployee")
	@ResponseBody
	public Map<String,Object> updateEmployee(@RequestBody EmployeeUpdateDto dto){
		Map<String,Object> resultMap = new HashMap<>();
		try {
			employeeService.updateEmployee(dto);
			resultMap.put("res_code", 200);
			resultMap.put("res_msg", "회원 정보가 성공적으로 수정되었습니다.");
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code", 500);
			resultMap.put("res_msg", "회원 정보 수정 중 오류가 발생했습니다.");
		}
		
		return resultMap;
	}
	
	// 회원 삭제 
	@PostMapping("/deleteEmployee")
	@ResponseBody
	public Map<String,Object> deleteEmployee(@RequestBody EmployeeUpdateDto dto){
		Map<String,Object> resultMap = new HashMap<>();
		try {
			employeeService.deleteEmployee(dto);
			resultMap.put("res_code", 200);
			resultMap.put("res_msg", "회원이 정상적으로 퇴사 처리되었습니다.");
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code", 500);
			resultMap.put("res_msg", "회원 퇴사 처리 중 오류가 발생했습니다.");
		}
		return resultMap;
	}
	
	// 회원 정보 가져오기(employee,directory,structure)
	@GetMapping("/getEmployeeDetail")
	@ResponseBody
	public Map<String, Object> getEmployeeDetail(@RequestParam("employeeNo") Long employeeNo){
		Map<String,Object> resultMap = new HashMap<>();
		
		try {
			// employeeNo로 사원 정보 조회
			Employee employee = employeeService.findEmployeeByEmployeeNo(employeeNo);
			
			// 사원 정보가 없을 경우
			if(employee == null) {
				resultMap.put("res_code", 404);
				resultMap.put("res_msg", "회원 정보를 찾을 수 없습니다.");
				return resultMap;
			}
			resultMap.put("employee_name", employee.getEmployeeName());
			resultMap.put("employee_position", employee.getEmployeePosition());
			resultMap.put("employee_hire_date", employee.getEmployeeHireDate() != null? employee.getEmployeeHireDate().toLocalDate().toString() : "");
			resultMap.put("employee_end_date", employee.getEmployeeEndDate() != null ? employee.getEmployeeEndDate().toLocalDate().toString() : "");
			resultMap.put("employee_employment_yn", employee.getEmployeeEmploymentYn() != null ? employee.getEmployeeEmploymentYn() : "Y");
			
			// 생일 문자열 -> 날짜형태 변환
			String birth = employee.getEmployeeBirth();
			if(birth != null && birth.length() == 8) {
				String formettedBirth = birth.substring(0,4) + "-" + birth.substring(4,6) + "-" + birth.substring(6,8);
				resultMap.put("employee_birth", formettedBirth);
			}else {
				resultMap.put("employee_birth", "");
			}
			// 사원의 directory 정보 가져오기
			Directory directory = employee.getDirectory();
			if(directory != null) {
				resultMap.put("directory_phone",directory.getDirectoryPhone());
				resultMap.put("directory_zipcode", directory.getDirectoryZipcode());
				resultMap.put("directory_address", directory.getDirectoryAddress());
			}else {
				resultMap.put("directory_phone", "");
				resultMap.put("directory_zipcode", "");
				resultMap.put("directory_address", "");
			}
			
			// 사원이 소속된 부서/팀 정보 가져오기
			Structure structure = employee.getStructure();
			if(structure != null) {
				// 팀 번호 저장
				resultMap.put("team_no", structure.getStructureNo());
				// 상위 부서 찾기(부모 코드)
				if(structure.getParentCode() != null) {
					Structure parentDept = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(structure.getParentCode());
					if(parentDept != null) {
						resultMap.put("department_no", parentDept.getStructureNo());
					}
				}else {
					// 부모 코드가 없으면 structure 자체가 부서
					resultMap.put("department_no", structure.getStructureNo());
				}
				
			}else {
				resultMap.put("team_no", null);		
				resultMap.put("department_no", null);		
			}
			
			// 부서 리스트 보내기 (드롭다운용)
			List<Structure> deptList = structureService.selectDepartmentAll();
			List<Map<String,Object>> deptResultList = new ArrayList<>();
			for(Structure dept : deptList) {
				Map<String,Object> deptMap = new HashMap<>();
				deptMap.put("structure_no", dept.getStructureNo());
				deptMap.put("code_name", dept.getCodeName());
				deptResultList.add(deptMap);
			}
			resultMap.put("department_list", deptResultList);
			
			// 팀 리스트
			List<Structure> teamList = structureService.selectAllTeams();
			List<Map<String,Object>> teamResultList = new ArrayList<>();
			for(Structure team : teamList) {
				Map<String,Object> teamMap = new HashMap<>();
				teamMap.put("structure_no", team.getStructureNo());
				teamMap.put("code_name", team.getCodeName());
				teamResultList.add(teamMap);
			}
			resultMap.put("team_list", teamResultList);
			
			resultMap.put("res_code", 200);
			resultMap.put("res_msg","회원 정보 조회 성공");
			
			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code", 500);
			resultMap.put("res_msg", "회원 정보를 가져오는 중 오류가 발생했습니다.");
		}
		
		return resultMap;
	}
	
	// 회원 연차정보 불러오기
	@GetMapping("/getAnnualLeave")
	@ResponseBody
	public Map<String,Object> getAnnualLeave(@RequestParam("employeeNo") Long employeeNo){
		Map<String,Object> result = new HashMap<>();
		
		try {
			AnnualLeave leave = annualLeaveService.findByEmployeeNo(employeeNo);
			
			if(leave == null) {
				result.put("res_code", 404);
				result.put("res_msg", "해당 사원의 연차 정보가 없습니다.");
			}else {
				double total = leave.getAnnualLeaveTotal();
				double used = leave.getAnnualLeaveUsed();
				double remain = total - used;
				
	            result.put("res_code", 200);
	            result.put("annual_leave_total", total);
	            result.put("annual_leave_used", used);
	            result.put("annual_leave_remain", remain);				
			}
		}catch(Exception e) {
			e.printStackTrace();
			
			result.put("res_code", 500);
			result.put("res_msg","연차 정보를 가져오는 중 오류가 발생했습니다.");
		}
		
		return result;
	}
	
}