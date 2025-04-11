package com.eroom.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationController {
	@GetMapping("/sleeprev")
	public String sleepReservationView() {
		return "reservation/sleeprev";
	}
	
	@GetMapping("/vehiclerev")
	public String vehicleReservationView() {
		return "reservation/vehiclerev";
	}
	
	@GetMapping("/meetingroomrev")
	public String meetingroomReservationView() {
		return "reservation/meetingroomrev";
	}
}
