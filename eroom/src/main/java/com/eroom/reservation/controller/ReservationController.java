package com.eroom.reservation.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.facility.entity.Facility;
import com.eroom.facility.service.FacilityService;
import com.eroom.reservation.dto.VehicleDto;
import com.eroom.reservation.entity.Vehicle;
import com.eroom.reservation.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReservationController {

	private final FacilityService facilityService;
	private final VehicleService vehicleService;

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
		return "reservation/vehiclerev";
	}

	@GetMapping("/reservation/meetingroom")
	public String meetingroomReservationView() {
		return "reservation/meetingroomrev";
	}

	// ========================등록 =============================
	@PostMapping("/resvehicle/reservation")
	@ResponseBody
	public Map<String, String> vehicleReservation(VehicleDto param) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "예약을 실패하였습니다");

		// System.out.println(param);

		VehicleDto dto = vehicleService.vehicleReservation(param);

		if (dto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "예약을 성공적으로 완료했습니다");
		}

		return resultMap;
	}

	// ====================목록 조회 =========================
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
	
	//======================예약 수정 모달에 값 넣기 ===========================
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
	
	//========================예약 수정 ==========================
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

	// =================예약 시간 막기==============================
	// 예약 시간 막기
	@GetMapping("/resvehicle/booked-times")
	@ResponseBody
	public List<String> getBookedTimes(@RequestParam("date") String date,
			@RequestParam("facilityNo") String facilityNo) {
		return vehicleService.getBookedTimes(date, facilityNo);
	}

}
