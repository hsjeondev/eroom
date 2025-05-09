package com.eroom.home.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
	

}
