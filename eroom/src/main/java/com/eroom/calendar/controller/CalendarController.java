package com.eroom.calendar.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.calendar.dto.EmployeeCalendarDto;
import com.eroom.calendar.entity.EmployeeCalendar;
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
//		EmployeeCalendarDto ecd = service.oneCalendar();
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
	
	//해당 유저의 일정 목록을 조회
	@GetMapping("/employeecalendar/list/{employeeNo}")
	@ResponseBody
	public List<Map<String, Object>> getCalendarList(@PathVariable("employeeNo") Long employeeNo) {
	    List<Map<String, Object>> result = new ArrayList<>();
	    List<EmployeeCalendarDto> calendarList = service.getCalendarList(employeeNo);
	    for (EmployeeCalendarDto dto : calendarList) {
	   
	        result.add(dto.toFullCalendarEvent());
	    }
	    
	    return result;
	}
	
	/*@PostMapping("/calendar/update/{calendarNo}")
	public Map<String,String>updateCalendar(@PathVariable Long calendarNo, Model model){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "일정 수정을 실패하였습니다");
		
		System.out.println(calendarNo);*/
		
//		EmployeeCalendarDto edto = service.updateCalendar(calendarNo);
//		if(edto != null) {
//			resultMap.put("res_code", "200");
//			resultMap.put("res_msg", "수정을 성공하였습니다!");
//		}
//		return resultMap;
	
    @GetMapping("/employeecalendar/detail/{calendarNo}")
    @ResponseBody
    public ResponseEntity<EmployeeCalendarDto> selectCalendarOne(@PathVariable("calendarNo") Long calendarNo) {
        try {
            EmployeeCalendarDto calendar = service.findByCalendarNo(calendarNo);
            if (calendar == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(calendar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    //일정 수정 모달에서 데이터 받아와서 저장하는 메소드
    @PostMapping("/employeecalendar/update/{calendarNo}")
    @ResponseBody
    public Map<String,String> updateCalendar(EmployeeCalendarDto param,@PathVariable("calendarNo") Long calendarNo){
    	Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "일정 수정을 실패하였습니다");
		
		param.setCalendar_no(calendarNo);
		
		//System.out.println(calendarNo);
		//System.out.println(param);
		
		EmployeeCalendar update = service.updateCalendar(param);
		
	
		if(update != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "수정을 성공하였습니다!");
		}
		return resultMap;
    }
	

}
