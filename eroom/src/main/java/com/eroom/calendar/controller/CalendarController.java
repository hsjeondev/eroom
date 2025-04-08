package com.eroom.calendar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarController {
	
	//캘린더 개인일정 목록으로 화면 전환
	@GetMapping("/calendar")
	public String employeeCalendarView() {
		return "calendar/list";
	}

}
