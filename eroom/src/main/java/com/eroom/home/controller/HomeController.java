package com.eroom.home.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.service.ApprovalLineService;
import com.eroom.approval.service.ApprovalService;
import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.Attendance;
import com.eroom.attendance.service.AttendanceService;
import com.eroom.employee.entity.Employee;
import com.eroom.reservation.dto.MeetingRoomDto;
import com.eroom.reservation.dto.VehicleDto;
import com.eroom.reservation.service.MeetingRoomService;
import com.eroom.reservation.service.VehicleService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final AttendanceService attendanceService;
	private final VehicleService vehicleService;
	private final MeetingRoomService meetingRoomService;
	private final ApprovalService approvalService;
	private final ApprovalLineService approvalLineService;

	@GetMapping({"", "/"})
	public String home(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetail = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		// html에서 #authentication.principal.employee.employeeName 대신
		// ${employee.employeeName}으로 사용 가능
		
	    List<VehicleDto> todayVehicleReservations = vehicleService.getTodayReservations();
	    model.addAttribute("todayReservations", todayVehicleReservations);

	    List<MeetingRoomDto> todayRoomReservations = meetingRoomService.getTodayReservations();
	    model.addAttribute("todayRoomReservations", todayRoomReservations);
		
		Map<String,String> statusMap = attendanceService.getTodayAttendanceStatusAndTime(employeeNo);
		model.addAttribute("attendanceStatus",statusMap.get("attendanceStatus"));
		model.addAttribute("checkInTime",statusMap.get("checkInTime"));
		model.addAttribute("checkOutTime",statusMap.get("checkOutTime"));
		
//		System.out.println("attendanceStatus : " + statusMap.get("attendanceStatus"));
//		System.out.println("attendanceTime : " + statusMap.get("attendanceTime"));
		
		return "index";
	}
	
	// 출퇴근 기록
	@PostMapping("/log")
	@ResponseBody
	public Map<String,String> recordAttendance(AttendanceDto dto){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "처리에 실패했습니다.");
		
		Attendance result = attendanceService.recordAttendance(dto);
		if(result != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "checkIn".equals(dto.getAttendanceType()) ? "출근 완료! 오늘도 힘내세요~" : "퇴근 완료! 수고하셨습니다");
		}
		
		return resultMap;
	}
	
	// 내가 올린 진행중인 결재
	@GetMapping("/mainpage/approval/ongoing")
	@ResponseBody
	public List<Map<String, Object>> getOngoingApprovals(Authentication authentication) {
	    EmployeeDetails employee = (EmployeeDetails) authentication.getPrincipal();
	    List<ApprovalDto> dtoList = new ArrayList<ApprovalDto>();
	    List<Approval> list = approvalService.getOngoingApprovals(employee.getEmployee().getEmployeeNo());
	    for(Approval l : list) {
	    	dtoList.add(new ApprovalDto().toDto(l));
	    }
	    
	    List<Map<String, Object>> resultList = new ArrayList<>();
	    for (ApprovalDto dto : dtoList) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("approval_no", dto.getApproval_no());
	        map.put("approval_title", dto.getApproval_title());
	        map.put("approval_display_date", dto.getReg_date());
	        map.put("approval_completed_date", dto.getCompleted_date());
	        map.put("approval_status", dto.getApproval_status());
	        map.put("approval_toMe", false);
	        resultList.add(map);
	    }
	    return resultList;
	}
	// 내가 결재 또는 합의 해야하는 결재
	@GetMapping("/mainpage/approval/pending")
	@ResponseBody
	public List<Map<String, Object>> getPendingApprovals(Authentication authentication) {
	    EmployeeDetails employee = (EmployeeDetails) authentication.getPrincipal();
	    Long employeeNo = employee.getEmployeeNo();
	    List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();

		for (ApprovalLine a : var1Entity) {
			if(!a.getApprovalLineStatus().equals("S")) {
				continue;
			}
			
			
			// 결재라인용 - 내가 결재 라인에 있는 approval들의 결재번호로 approval 리스트 조회
			List<Approval> approvalList = approvalService.getApprovalListByApprovalNo(a.getApproval().getApprovalNo(), "Y");
			if (approvalList != null) {
				// 결재라인용 - 결재라인에 있는 결재번호로 approval 리스트 entity -> Dto 변환
				for (Approval approval : approvalList) {
					List<ApprovalLine> var1 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
					List<ApprovalLineDto> tempDtoList = new ArrayList<>();
				    for(int i = 0; i < var1.size(); i++) {
				    	tempDtoList.add(new ApprovalLineDto().toDto(var1.get(i)));
				    }
				    var1Map.put(approval.getApprovalNo(), tempDtoList);
					
				    // 결재 리스트옹 - 내가 해당 결재의 현재 순번이 맞는 결재들만 보내기
					List<ApprovalLine> temp = approval.getApprovalLines();
					Boolean bool = false;
					Boolean stackBool = false;
					int count = 0;
					// step이 0인 사람(합의자) 중 결재 상태가 A 아닌 사람이 한명이라도 있으면 stackBool = false처리
					for(int i = 0; i < temp.size(); i++) {
						if(temp.get(i).getApprovalLineStep() != 0) {
							continue;
						}
						if(temp.get(i).getApprovalLineStep() == 0) {
							count++;
							// 내가 합의인 경우 추가 - home
							if(temp.get(i).getEmployee().getEmployeeNo() == employeeNo && temp.get(i).getApprovalLineStatus().equals("S")) {
								temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
								resultApprovalList.add(temp2);
							}
							if(!temp.get(i).getApprovalLineStatus().equals("A")) {
								stackBool = false;
								break;
							} else if(temp.get(i).getApprovalLineStatus().equals("A")) {
								stackBool = true;
							}
						}
						
						
				    }
					
					
					// 결재 라인에 있는 사람들 중~
					for(int i = 0; i < temp.size(); i++) {
						bool = false;
						// 내가 결재 라인!
						if(temp.get(i).getApprovalLineStep() >= 1) {
							// 나보다 앞에서 결재 하는 사람의 status확인
							if (i > 0) {
								if(temp.get(i - 1).getApprovalLineStatus().equals("A") && temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
									bool = true;
								}
							// 내가 제일 처음 결재자인 경우
							} else if (i == 0) {
								if(temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
									bool = true;
								}
							}
						}
						
						
						
						// 모든 합의자가 status = A, 나의 결재 차례 이후인 경우에만 approval을 담아서 보내기
						if(count == 0) {
							if(bool && !approval.getApprovalStatus().equals("F")) {
								temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
								resultApprovalList.add(temp2);
							}
						} else {
							if(bool && stackBool && !approval.getApprovalStatus().equals("F")) {
								temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
								resultApprovalList.add(temp2);
							}
						}
					}
				}
			}
		}
		resultApprovalList.sort((a1, a2) -> a2.getApprovalRegDate().compareTo(a1.getApprovalRegDate()));
		for (Approval t : resultApprovalList) {
			if(!t.getApprovalStatus().equals("S")){
				continue;
			}
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			dto.setEmployee_name(t.getEmployee().getEmployeeName());
			resultApprovalListDto.add(dto);
		}
		// - home
	    List<Map<String, Object>> resultList = new ArrayList<>();
	    for (ApprovalDto dto : resultApprovalListDto) {
	    		Map<String, Object> map = new HashMap<>();
	    		map.put("approval_no", dto.getApproval_no());
	    		map.put("approval_title", dto.getApproval_title());
	    		map.put("approval_display_date", dto.getReg_date());
	    		map.put("approval_completed_date", dto.getCompleted_date());
	    		map.put("approval_status", dto.getApproval_status());
	    		map.put("approval_toMe", true);
	    		map.put("approval_writer", dto.getEmployee().getEmployeeName());
	    		resultList.add(map);
				if (resultList.size() >= 5) {
					break;
				}
	    }
	    return resultList;
	}
	// 내가 올린 승인, 반려 처리 된 결재
	@GetMapping("/mainpage/approval/completed")
	@ResponseBody
	public List<Map<String, Object>> getCompletedApprovals(Authentication authentication) {
	    EmployeeDetails employee = (EmployeeDetails) authentication.getPrincipal();
	    List<ApprovalDto> dtoList = new ArrayList<ApprovalDto>();
	    List<Approval> list = approvalService.getCompletedApprovals(employee.getEmployee().getEmployeeNo());
	    for(Approval l : list) {
	    	dtoList.add(new ApprovalDto().toDto(l));
	    }
	    
	    List<Map<String, Object>> resultList = new ArrayList<>();
	    for (ApprovalDto dto : dtoList) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("approval_no", dto.getApproval_no());
	        map.put("approval_title", dto.getApproval_title());
	        map.put("approval_display_date", dto.getReg_date());
	        map.put("approval_completed_date", dto.getCompleted_date());
	        map.put("approval_status", dto.getApproval_status());
	        map.put("approval_toMe", false);
	        resultList.add(map);
	    }
	    return resultList;
	}
	
	

}
