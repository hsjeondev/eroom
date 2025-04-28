package com.eroom.reservation.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.service.EmployeeService;
import com.eroom.facility.entity.Facility;
import com.eroom.facility.service.FacilityService;
import com.eroom.reservation.dto.MeetingRoomDto;
import com.eroom.reservation.dto.VehicleDto;
import com.eroom.reservation.entity.Vehicle;
import com.eroom.reservation.service.MeetingRoomService;
import com.eroom.reservation.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReservationController {

	private final FacilityService facilityService;
	private final VehicleService vehicleService;
	private final EmployeeService employeeService;
	private final MeetingRoomService meetingRoomService;

	// ========================화면 전환 =============================
	@GetMapping("/reservation/sleep")
	public String sleepReservationView() {
		return "reservation/sleeprev";
	}

	// facility 에서 F002 차량만 목록조회
	@GetMapping("/reservation/vehicle")
	public String vehicleReservationView(Model model) {
		List<Facility> result = facilityService.selectVehicleAll();
		// 목록이 정상적으로 출력
		// System.out.println(result);
		model.addAttribute("list", result);		
		//오늘 예약 현황
		List<VehicleDto> todayReservations = vehicleService.getTodayReservations();
		model.addAttribute("todayReservations",todayReservations);
		return "reservation/vehiclerev";
	}

	@GetMapping("/reservation/meetingroom")
	public String meetingroomReservationView(@RequestParam(name = "department" ,required = false) String department, Model model) {
		List<Facility>result = facilityService.selectMeetingRoomAll();
		List<MeetingRoomDto>todayReservations = meetingRoomService.getTodayReservations();
	    model.addAttribute("structureList", employeeService.findDistinctStructureNames());
		model.addAttribute("list",result);
		model.addAttribute("todayReservations",todayReservations);
		
		return "reservation/meetingroomrev";
	}
	
	//회의실 예약 참가자 선택
	@GetMapping("/reservation/employees")
	@ResponseBody
	public List<EmployeeDto> getEmployeesByDepartment(@RequestParam("separator_code") String separatorCode) {
	    String temp = separatorCode.substring(0,1);
	    if ("T".equals(temp)) {
	        return employeeService.findEmployeesByStructureName(separatorCode);
	    } else {
	        return employeeService.findEmployeesByParentCode(separatorCode);
	    }
	}

	// ========================차량 등록 =============================
	
	@PostMapping("/resvehicle/reservation")
	public ResponseEntity<Map<String, Object>> reserveVehicle(@ModelAttribute VehicleDto dto) {
	    LocalDateTime start = dto.getReservation_start();
	    LocalDateTime end = dto.getReservation_end();
	    Long facilityNo = dto.getFacility_no();

	    if (vehicleService.isConflict(facilityNo, start, end)) {
	        return ResponseEntity.ok(Map.of(
	            "res_code", 400,
	            "res_msg", "이미 예약된 시간이 존재합니다!"
	        ));
	    }

	    vehicleService.vehicleReservation(dto);

	    return ResponseEntity.ok(Map.of(
	        "res_code", 200,
	        "res_msg", "차량 예약이 완료되었습니다!"
	    ));
	}
//	@PostMapping("/resvehicle/reservation")
//	@ResponseBody
//	public Map<String, String> vehicleReservation(VehicleDto param) {
//		Map<String, String> resultMap = new HashMap<String, String>();
//		resultMap.put("res_code", "500");
//		resultMap.put("res_msg", "예약을 실패하였습니다");
//
//		// System.out.println(param);
//
//		VehicleDto dto = vehicleService.vehicleReservation(param);
//
//		if (dto != null) {
//			resultMap.put("res_code", "200");
//			resultMap.put("res_msg", "예약을 성공적으로 완료했습니다");
//		}
//
//		return resultMap;
//	}
	
	// ===================회의실 등록===============================
	@PostMapping("/reservation/meetingroom/reserve")
	@ResponseBody
	public Map<String, String> reserveMeetingRoom(
	        MeetingRoomDto param,
	        @RequestParam("participants") List<Long> participantIds) {

	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "회의실 예약을 실패하였습니다.");

	    // 1. 중복 예약 검사
	    if (meetingRoomService.isConflict(
	            param.getFacility_no(),
	            param.getReservation_start(),
	            param.getReservation_end())) {

	        resultMap.put("res_code", "400");
	        resultMap.put("res_msg", "이미 예약된 시간이 존재합니다!");
	        return resultMap;
	    }

	    // 2. 예약 등록
	    MeetingRoomDto savedDto = meetingRoomService.meetingRoomServiceReservation(param);

	    if (savedDto != null) {
	        meetingRoomService.saveParticipants(savedDto.getReservation_no(), participantIds);
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "회의실 예약이 완료되었습니다!");
	    }

	    return resultMap;
	}
	
	
	
	
	
//	@PostMapping("/reservation/meetingroom/reserve")
//	@ResponseBody
//	public Map<String, String> reserveMeetingRoom(
//			MeetingRoomDto param,
//	    @RequestParam("participants") List<Long> participantIds) {
//
//	    Map<String, String> resultMap = new HashMap<>();
//	    resultMap.put("res_code", "500");
//	    resultMap.put("res_msg", "예약을 실패하였습니다");
//
//	    // 회의실 예약 등록
//	    MeetingRoomDto savedDto = meetingRoomService.meetingRoomServiceReservation(param);
//
//	    if (savedDto != null) {
//	        // 참여자 매핑 저장 
//	        meetingRoomService.saveParticipants(savedDto.getReservation_no(), participantIds);
//
//	        resultMap.put("res_code", "200");
//	        resultMap.put("res_msg", "회의실 예약이 완료되었습니다!");
//	    }
//
//	    return resultMap;
//	}
	

	// ====================차량 목록 조회 ==========================
	@GetMapping("/resvehicle/list/{separator}")
	@ResponseBody
	public List<Map<String, Object>> getVehicleList(@PathVariable("separator") String separator) {
		List<Map<String, Object>> result = new ArrayList<>();
		// System.out.println(separator);
		List<VehicleDto> list = vehicleService.getVehicleList(separator);

		for (VehicleDto dto : list) {
			result.add(dto.toFullCalendarEvent());
		}

		return result;
	}
	//=======================회의실 목록 조회============================
	@GetMapping("/meetingroom/list/{separator}")
	@ResponseBody
	public List<Map<String, Object>> getMeetingRoomList(@PathVariable("separator") String separator) {
		List<Map<String, Object>> result = new ArrayList<>();
		// System.out.println(separator);
		List<MeetingRoomDto> list = meetingRoomService.getMeetingRoomList(separator);

		for (MeetingRoomDto dto : list) {
			result.add(dto.toFullCalendarEvent());
		}

		return result;
	}
	
	//======================차량 예약 수정 모달에 값 넣기 ===========================
	@GetMapping("/resvehicle/detail/{reservationNo}")
	@ResponseBody
	public ResponseEntity<VehicleDto> selectVehicleOne(@PathVariable("reservationNo") Long reservationNo){
		try {
			VehicleDto vehicle = vehicleService.findByReservationNo(reservationNo);
				if(vehicle == null) {
					return ResponseEntity.notFound().build();
				}
				 return ResponseEntity.ok(vehicle);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	//========================차량 예약 수정 ==========================
	@PostMapping("/resvehicle/update/{reservationNo}")
	@ResponseBody
	public Map<String,String> updateVehicle(VehicleDto param, @PathVariable("reservationNo") Long id){
    	Map<String,String> resultMap = new HashMap<String,String>();
    	resultMap.put("res_code", "500");
    	resultMap.put("res_msg", "예약 수정을 실패하였습니다");
    	
    	param.setReservation_no(id);
    	
    	
    	Vehicle update = vehicleService.updateVehicle(param);
    	
    	if(update != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "수정을 성공하였습니다!");
		}
    	
    	
    	return resultMap;
	}
	
	//====================차량 예약 삭제 ================================
	@PostMapping("/resvehicle/delete/{reservationNo}")
	@ResponseBody
	public Map<String,String> deleteVehicle(@PathVariable("reservationNo") Long id){
		Map<String,String> result = new HashMap<String,String>();
		result.put("res_code", "500");
		result.put("res_msg", "취소를 실패했습니다");
		
		//System.out.println("==================="+ id + "===================");
		
		VehicleDto deleteVehicle = vehicleService.deleteCalendar(id);
		
		if(deleteVehicle != null) {
			result.put("res_code", "200");
			result.put("res_msg", "예약을 취소하였습니다!");
		}
		
		return result;
	}
	
	//=====================회의실 예약 삭제=============================
	@PostMapping("/meetingroom/delete/{reservationNo}")
	@ResponseBody
	public Map<String,String> deleteMeetingRoom(@PathVariable("reservationNo") Long id){
		Map<String,String> result = new HashMap<String,String>();
		result.put("res_code", "500");
		result.put("res_msg", "취소를 실패했습니다");
		
		//System.out.println("==================="+ id + "===================");
		
		MeetingRoomDto deleteVehicle = meetingRoomService.deleteMeetingRoom(id);
		
		if(deleteVehicle != null) {
			result.put("res_code", "200");
			result.put("res_msg", "예약을 취소하였습니다!");
		}
		
		return result;
	}

	// =================차량 예약 시간 막기==============================
	// 예약 시간 막기
//	@GetMapping("/resvehicle/booked-times")
//	@ResponseBody
//	public List<String> getBookedTimes(@RequestParam("date") String date,
//			@RequestParam("facilityNo") String facilityNo) {
//		return vehicleService.getBookedTimes(date, facilityNo);
//	}
	
	//==================회의실 예약 시간 막기===============================
	@GetMapping("/meetingroom/booked-times")
	@ResponseBody
	public List<String> getMeetingRoomTimes(@RequestParam("date") String date,
			@RequestParam("facilityNo") String facilityNo) {
		return meetingRoomService.getMeetingRoomTimes(date,facilityNo);
	}
	


}
