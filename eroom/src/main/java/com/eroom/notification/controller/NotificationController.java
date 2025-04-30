package com.eroom.notification.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.notification.CalendarAlarmService;
import com.eroom.notification.dto.CalendarAlarmDto;
import com.eroom.security.EmployeeDetails;

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
	  
	  @PatchMapping("/notification/read/{alarmId}")
	  @ResponseBody
	  public Map<String, String> readNotification(@PathVariable("alarmId") Long alarmId) {
	      Map<String, String> result = new HashMap<>();
	      result.put("res_code", "500");
	      result.put("res_msg", "읽음 처리 실패");

	      try {
	          calendarAlarmService.markAsRead(alarmId);
	          result.put("res_code", "200");
	          result.put("res_msg", "읽음 처리 성공");
	      } catch (Exception e) {
	          e.printStackTrace(); // 서버 콘솔에 에러 출력
	      }

	      return result;
	  }
	  
	  //전체 읽음 처리
	  @PatchMapping("/notification/readall")
	  @ResponseBody
	  public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal EmployeeDetails userDetails) {
	      Long employeeNo = userDetails.getEmployee().getEmployeeNo();
	      calendarAlarmService.markAllAsRead(employeeNo);
	      return ResponseEntity.ok().build();
	  }
}
