package com.eroom.reservation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.facility.entity.Facility;
import com.eroom.facility.service.FacilityService;
import com.eroom.reservation.dto.VehicleDto;
import com.eroom.reservation.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReservationController {

 

   
	private final FacilityService facilityService;
	private final VehicleService vehicleService;


 

  
	//========================화면 전환 =============================
	@GetMapping("/reservation/sleep")
	public String sleepReservationView() {
		return "reservation/sleeprev";
	}
	
	//facility 에서 F002 차량만 목록조회
	@GetMapping("/reservation/vehicle")
	public String vehicleReservationView(Model model) {
		List<Facility>result = facilityService.selectVehicleAll();
		//목록이 정상적으로 출력
		//System.out.println(result);
		model.addAttribute("list",result);		
		return "reservation/vehiclerev";
	}
	
	@GetMapping("/reservation/meetingroom")
	public String meetingroomReservationView() {
		return "reservation/meetingroomrev";
	}
	
	
	//========================등록 =============================
	@PostMapping("/resvehicle/reservation")
	@ResponseBody
	public Map<String,String> vehicleReservation(VehicleDto param){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "예약을 실패하였습니다");
		
		//System.out.println(param);
		
		VehicleDto dto = vehicleService.vehicleReservation(param);
		
		if(dto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "예약을 성공적으로 완료했습니다");
		}
		
		return resultMap;
	}
}
