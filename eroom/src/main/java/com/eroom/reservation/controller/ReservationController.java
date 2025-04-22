package com.eroom.reservation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eroom.facility.entity.Facility;
import com.eroom.reservation.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReservationController {

   
	
	private final VehicleService vehicleService;

  
	
	@GetMapping("/reservation/sleep")
	public String sleepReservationView() {
		return "reservation/sleeprev";
	}
	
	@GetMapping("/reservation/vehicle")
	public String vehicleReservationView(Model model) {
		List<Facility>result = vehicleService.selectVehicleAll();
		//목록이 정상적으로 출력
		//System.out.println(result);
		model.addAttribute("list",result);		
		return "reservation/vehiclerev";
	}
	
	@GetMapping("/reservation/meetingroom")
	public String meetingroomReservationView() {
		return "reservation/meetingroomrev";
	}
}
