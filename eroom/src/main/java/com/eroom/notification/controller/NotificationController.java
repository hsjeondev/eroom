package com.eroom.notification.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.notification.CalendarAlarmService;
import com.eroom.notification.dto.CalendarAlarmDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {
	
	private final CalendarAlarmService calendarAlarmService;
	
	//알림 페이지에서 목록 가져오기
	@GetMapping("/notification")
	public String notificationView(Model model) {		
		//캘린더 알람 목록 가져오기
		List<CalendarAlarmDto> calendarList = calendarAlarmService.getCalendarAlarm();
		model.addAttribute("calendaralarm",calendarList);
		return "notification/notification";
	}
	
	//종 누르면 내려오는 목록 가져오기
	  @GetMapping("/notification/data")
	  @ResponseBody
	    public List<CalendarAlarmDto> getNotificationData() {
	        return calendarAlarmService.getUnreadCompanyAlarms();
	    }
}
