package com.eroom.calendar.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.calendar.dto.EmployeeCalendarDto;
import com.eroom.calendar.service.EmployeeCalendarService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CalendarController {
	
	private final EmployeeCalendarService service;

	@GetMapping("/calendar")
	public String calendarView() {
		return "calendar/list";
	}
	
	//캘린더 개인일정 목록으로 화면 전환
	@GetMapping("/calendar/employee")
	public String employeeCalendarView() {
		return "calendar/employeelist";
	}
	
	//캘린더 마이팀일정 목록으로 화면 전환
	@GetMapping("/calendar/myteam")
	public String myTeamCalendarView() {
		return "calendar/myteamlist";
	}

	//캘린더 회사일정 목록으로 화면 전환
	@GetMapping("/calendar/company")
	public String companyCalendarView(Model model) {
		model.addAttribute("separator", "A001");
		return "calendar/companylist";
	}
	
	@GetMapping("/calendar/department")
	public String departMentCalendarView() {
		return "calendar/departlist";
	}
	
	//개인 캘리더 등록
	@PostMapping("/employeecalendar/add")
	@ResponseBody
	public Map<String,String> addEmployeeCalendarApi(EmployeeCalendarDto param){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "일정 등록을 실패하였습니다");
		
		System.out.println(param);
		
		EmployeeCalendarDto edto = service.addEmployeeCalendar(param);
		if(edto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정을 등록하였습니다!");
		}
		
		
		return resultMap;
	}

}
