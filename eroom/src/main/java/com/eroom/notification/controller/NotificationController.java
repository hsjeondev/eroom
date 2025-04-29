package com.eroom.notification.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eroom.notification.CalendarAlarmService;
import com.eroom.notification.dto.CalendarAlarmDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {
	
	private final CalendarAlarmService calendarAlarmService;

	@GetMapping("/notification")
	public String notificationView(Model model) {
		
		//캘린더 알람 목록 가져오기
		List<CalendarAlarmDto> calendarList = calendarAlarmService.getCalendarAlarm();
		model.addAttribute("calendaralarm",calendarList);
		return "notification/notification";
	}
}
