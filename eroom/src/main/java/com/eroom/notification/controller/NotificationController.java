package com.eroom.notification.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.calendar.dto.CalendarAlarmDto;
import com.eroom.calendar.service.CalendarAlarmService;
import com.eroom.notification.dto.AlarmDto;
import com.eroom.notification.entity.Alarm;
import com.eroom.notification.service.AlarmService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {
	
	private final AlarmService alarmService;

	
	//알림 페이지 목록조회
	@GetMapping("/notification")
	public String notificationView(Model model) {
		//오늘,어제 기준으로만 날짜 목록 조회
		List<AlarmDto> alarmList = alarmService.getMyAlarms();
		//날짜별 그룹화로 알람 보기
		Map<String,List<AlarmDto>> groupedAlarms = alarmService.getGroupedAlarms();
		model.addAttribute("alarmList", alarmList);
		model.addAttribute("groupedAlarms",groupedAlarms);
		return "notification/notification";
	}
	
	//헤더 종 누르면 내려오는 목록 가져오기
	@GetMapping("/notification/data")
	@ResponseBody
	public List<AlarmDto> getNotificationData(@AuthenticationPrincipal EmployeeDetails userDetails) {
	    Long loginEmployeeNo = userDetails.getEmployee().getEmployeeNo();
	    //System.out.println("로그인한 사번: " + loginEmployeeNo);
	    List<AlarmDto> list = alarmService.getUnreadAlarms(loginEmployeeNo);
	    return list;
	    
	}
	  
	//헤더 종 누르고, 목록 하나 하나 누르면 N에서 Y로 처리
	@PatchMapping("/notification/read/{alarmId}")
	@ResponseBody
	public Map<String, Object> readNotification(@PathVariable("alarmId") Long alarmId) {
	    Map<String, Object> result = new HashMap<>();
	    result.put("res_code", "500");
	    result.put("res_msg", "읽음 처리 실패");

	    try {
	    	Map<String, Object> tempMap = alarmService.markAsRead(alarmId);  // ← calendarAlarmService → alarmService
	    	if(tempMap != null && !tempMap.isEmpty()) {
	    		result.put("separator", tempMap.get("separator"));
	    		result.put("pk", tempMap.get("pk"));
	    		result.put("separator_code", tempMap.get("separator_code"));
	    	}
	    	if (tempMap.containsKey("roomNo")) {
	            result.put("roomNo", tempMap.get("roomNo"));
	        }
	        result.put("res_code", "200");
	        result.put("res_msg", "읽음 처리 성공");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}
	  
	  
	  //전체 읽음 처리
	  @PatchMapping("/notification/readall")
	  @ResponseBody
	  public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal EmployeeDetails userDetails) {
	      Long employeeNo = userDetails.getEmployee().getEmployeeNo();
	      alarmService.markAllAsRead(employeeNo);
	      return ResponseEntity.ok().build();
	  }

	  
	//================================================================================================================  
		//calendaralarm , calendarList
		//알림 페이지에서 목록 가져오기
//		@GetMapping("/notification")
//		public String notificationView(Model model) {		
//			//캘린더 알람 목록 가져오기
//			List<CalendarAlarmDto> calendarList = calendarAlarmService.getCalendarAlarm();
//			List<CalendarAlarmDto> teamList = calendarAlarmService.getTeamAlarm();
//			model.addAttribute("calendaralarm",calendarList);
//			model.addAttribute("teamList",teamList);
//			return "notification/notification";
//		}
		
//		@GetMapping("/notification")
//		public String notificationView(Model model) {
//		    List<CalendarAlarmDto> calendarList = calendarAlarmService.getMyAlarms();
//		    model.addAttribute("calendaralarm", calendarList);
//		    return "notification/notification";
//		}  
	  
	  //전체 읽음 처리
//	  @PatchMapping("/notification/readall")
//	  @ResponseBody
//	  public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal EmployeeDetails userDetails) {
//	      Long employeeNo = userDetails.getEmployee().getEmployeeNo();
//	      calendarAlarmService.markAllAsRead(employeeNo);
//	      return ResponseEntity.ok().build();
//	  }
}