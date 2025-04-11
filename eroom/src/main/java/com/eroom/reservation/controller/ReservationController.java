package com.eroom.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationController {
	@GetMapping("/reservation/sleep")
	public String sleepReservationView() {
		return "reservation/sleeprev";
	}
	
	@GetMapping("/reservation/vehicle")
	public String vehicleReservationView() {
		return "reservation/vehiclerev";
	}
	
	@GetMapping("/reservation/meetingroom")
	public String meetingroomReservationView() {
		return "reservation/meetingroomrev";
	}
}
