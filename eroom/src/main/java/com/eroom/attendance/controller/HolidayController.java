//package com.eroom.attendance.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.eroom.attendance.service.HolidayService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/holiday")
//public class HolidayController {
//
//	private final HolidayService holidayService;
//	
//	@GetMapping("/save")
//	public String saveHolidays(@RequestParam("year") int year) {
//		holidayService.saveHolidaysByJson(year);
//		return year + "년 공휴일 저장 완료";
//	}
//}
