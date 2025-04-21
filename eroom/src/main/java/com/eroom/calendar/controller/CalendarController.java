package com.eroom.calendar.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.calendar.dto.CompanyCalendarDto;
import com.eroom.calendar.dto.EmployeeCalendarDto;
import com.eroom.calendar.dto.TeamCalendarDto;
import com.eroom.calendar.entity.CompanyCalendar;
import com.eroom.calendar.entity.EmployeeCalendar;
import com.eroom.calendar.entity.TeamCalendar;
import com.eroom.calendar.service.CompanyCalendarService;
import com.eroom.calendar.service.EmployeeCalendarService;
import com.eroom.calendar.service.TeamCalendarService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CalendarController {
	
	private final EmployeeCalendarService service;
	private final CompanyCalendarService companyService;
	private final TeamCalendarService teamService;

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
	public String companyCalendarView() {
		return "calendar/companylist";
	}
	
	@GetMapping("/calendar/department")
	public String departMentCalendarView() {
		return "calendar/departlist";
	}
	
	
	// =============================등록 =================================
	
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
	

	//회사 캘린더 등록
	@PostMapping("/companycalendar/add")
	@ResponseBody
	public Map<String,String> addCompanyCalendarApi(CompanyCalendarDto param){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "일정 등록을 실패하였습니다");
		System.out.println(param);
		
		CompanyCalendarDto cdto = companyService.addCompanyCalendar(param);
		if(cdto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정을 등록하였습니다!");
		}
		
		return resultMap;
	}
	
	//팀(해당직원의 팀) 캘린더 등록
	@PostMapping("/teamcalendar/add")
	@ResponseBody
	public Map<String,String> addTeamCalendarApi(TeamCalendarDto param){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "일정 등록을 실패하였습니다");
		
		TeamCalendarDto tdto = teamService.addTeamCalendar(param);
		
		if(tdto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정을 등록하였습니다!");
		}
		
		//System.out.println(param);
		
		return resultMap;
	}
	
	
	// ============================= 목록 조회 =================================

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
	
	//회사 일정의 목록을 조회 -> 구분자로 판별
	@GetMapping("/companycalendar/list/{separator}")
	@ResponseBody
	public List<Map<String, Object>> getCompanyList(@PathVariable("separator") String separator) {
	    List<Map<String, Object>> result = new ArrayList<>();
	    List<CompanyCalendarDto> companyList = companyService.getCompanyList(separator);
	    
	    for (CompanyCalendarDto dto : companyList) {
	        result.add(dto.toFullCalendarEvent());
	    }
	    
	    return result;
	}
	
	//해당 직원의 팀! 목록을 조회 -> 구분자로 판별
	@GetMapping("/teamcalendar/list/{separator}")
	@ResponseBody
	public List<Map<String, Object>> getTeamList(@PathVariable("separator") String separator) {
	    List<Map<String, Object>> result = new ArrayList<>();
	    List<TeamCalendarDto> teamList = teamService.getTeamList(separator);
	    
	    for (TeamCalendarDto dto : teamList) {
	        result.add(dto.toFullCalendarEvent());
	    }
	    
	    return result;
	}

	// ============================= 단일 조회 =================================
	
	//해당 직원 일정 단일 조회 (for 수정 모달)
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
    
    //회사 일정 단일 조회 (for 수정 모달)
    @GetMapping("/companycalendar/detail/{calendarNo}")
    @ResponseBody
    public ResponseEntity<CompanyCalendarDto> selectCompanyOne(@PathVariable("calendarNo") Long calendarNo){
        try {
        	CompanyCalendarDto calendar = companyService.findByCompanyNo(calendarNo);
            if (calendar == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(calendar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    //팀 일정 단일 조회 (for 수정 모달)
    @GetMapping("/teamcalendar/detail/{calendarNo}")
    @ResponseBody
    public ResponseEntity<TeamCalendarDto> selectTeamOne(@PathVariable("calendarNo") Long calendarNo){
    	  try {
          	TeamCalendarDto calendar = teamService.findByTeamNo(calendarNo);
              if (calendar == null) {
                  return ResponseEntity.notFound().build();
              }
              return ResponseEntity.ok(calendar);
          } catch (Exception e) {
              return ResponseEntity.badRequest().body(null);
          }
      }
    
 // ============================= 수정 =================================
    
    //개인 일정 수정 모달에서 데이터 받아와서 저장하는 메소드
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
    
    //회사 일정 수정 모달에서 데이터 받아와서 저장하는 메소드
    @PostMapping("/companycalendar/update/{calendarNo}")
    @ResponseBody
    public Map<String,String> updateCompanyCalendar(CompanyCalendarDto param, @PathVariable("calendarNo") Long calendarNo){
    	Map<String,String> resultMap = new HashMap<String,String>();
    	resultMap.put("res_code", "500");
    	resultMap.put("res_msg", "일정 수정을 실패하였습니다");
    	
    	param.setCalendar_no(calendarNo);
    	
    	
    	CompanyCalendar update = companyService.updateCompanyCalendar(param);
    	
    	if(update != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "수정을 성공하였습니다!");
		}
    	
    	
    	return resultMap;
    }
    
    @PostMapping("/teamcalendar/update/{calendarNo}/{separator}")
    @ResponseBody
    public Map<String,String> updateTeamCalendar(TeamCalendarDto param, @PathVariable("calendarNo") Long calendarNo,@PathVariable("separator") String separator){
    	Map<String,String> resultMap = new HashMap<String,String>();
    	resultMap.put("res_code", "500");
    	resultMap.put("res_msg", "일정 수정을 실패하였습니다");
    	
    	param.setCalendar_no(calendarNo);
    	param.setSeparator(separator);
    	
    	TeamCalendar update = teamService.updateTeamCalendar(param);
    	
    	if(update != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "수정을 성공하였습니다!");
		}
    	
    	
    	return resultMap;
    }
    
    
    // ============================= 삭제 =================================
	
    //일정 삭제 --> calendar 테이블에 사용여부를 Y -> N으로 변경
    @PostMapping("/employeecalendar/delete/{calendarNo}")
    @ResponseBody
    public Map<String,String> deleteCalendar(@PathVariable("calendarNo") Long id){
    	Map<String,String> result = new HashMap<String,String>();
		result.put("res_code", "500");
		result.put("res_msg", "삭제를 실패했습니다");
		
		//System.out.println("==================="+ id + "===================");
		
		EmployeeCalendarDto deleteCalendar = service.deleteCalendar(id);
		
		if(deleteCalendar != null) {
			result.put("res_code", "200");
			result.put("res_msg", "일정을 삭제하였습니다!");
		}
		
		return result;
    }
    
    //회사 일정 삭제 --> calendar 테이블에 사용여부를 Y -> N으로 변경
    @PostMapping("/companycalendar/delete/{calendarNo}")
    @ResponseBody
    public Map<String,String> deleteCompanyCalendar(@PathVariable("calendarNo") Long id){
    	Map<String,String> result = new HashMap<String,String>();
    	result.put("res_code", "500");
		result.put("res_msg", "삭제를 실패했습니다");
    	
		CompanyCalendarDto deleteCompanyCalendar = companyService.deleteCompanyCalendar(id);
    	
		if(deleteCompanyCalendar != null) {
			result.put("res_code", "200");
			result.put("res_msg", "일정을 삭제하였습니다!");
		}
		
		return result;
    	
    	
    }
    
    @PostMapping("/teamcalendar/delete/{calendarNo}")
    @ResponseBody
    public Map<String,String> deleteTeamCalendar(@PathVariable("calendarNo") Long id){
    	Map<String,String> result = new HashMap<String,String>();
    	result.put("res_code", "500");
		result.put("res_msg", "삭제를 실패했습니다");
    	
		TeamCalendarDto deleteCompanyCalendar = teamService.deleteTeamCalendar(id);
    	
		if(deleteCompanyCalendar != null) {
			result.put("res_code", "200");
			result.put("res_msg", "일정을 삭제하였습니다!");
		}
		
		return result;
    }

}
