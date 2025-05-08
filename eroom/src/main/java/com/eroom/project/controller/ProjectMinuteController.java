package com.eroom.project.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.project.dto.ProjectMeetingMinuteDto;
import com.eroom.project.dto.ProjectMeetingMinuteMappingDto;
import com.eroom.project.service.ProjectMeetingMinuteService;
import com.eroom.project.service.ProjectService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project/minute")
public class ProjectMinuteController {
	private final EmployeeService employeeService;
	private final ProjectService projectService;
	private final ProjectMeetingMinuteService projectMeetingMinuteService;
	
	@GetMapping("/summary")
	@ResponseBody
	public String getMeetingSummary(@RequestParam("minuteNo") Long minuteNo) {
	    return projectMeetingMinuteService.getSummaryByMinuteNo(minuteNo);
	}
	
	@GetMapping("/create-page")
	public String createMinutePage(@RequestParam("projectNo") Long projectNo, Model model) {
	    List<Employee> projectEmployees = projectService.findEmployeesByProjectNo(projectNo);

	    model.addAttribute("projectEmployees", projectEmployees);
	    model.addAttribute("projectNo", projectNo);
	    return "project/projectMinuteCreate";
	}
	
	@PostMapping("/create")
	public String createMinute(ProjectMeetingMinuteDto minuteDto,
	                           @RequestParam(value = "participants") List<Long> participants) {

	    // 로그인 사용자 정보 설정
	    EmployeeDetails userDetails = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    minuteDto.setWriter(userDetails.getEmployee().getEmployeeName());

	    // 회의록 및 참여자 저장
	    Long savedMinuteNo = projectMeetingMinuteService.saveMinuteAndMappings(minuteDto, participants);

	    return "redirect:/project/minute/detail?minuteNo=" + savedMinuteNo + "&projectNo=" + minuteDto.getProjectNo();
	}
	
	@GetMapping("/detail")
	public String minuteDetail(@RequestParam("minuteNo") Long minuteNo,
	                           @RequestParam("projectNo") Long projectNo,
	                           Model model) {

	    // 회의록 정보 및 참여자
	    ProjectMeetingMinuteDto dto = projectMeetingMinuteService.findByMinuteNo(minuteNo);
	    List<String> participants = projectMeetingMinuteService.findParticipantNames(minuteNo);

	    // 로그인한 사용자 정보
	    EmployeeDetails userDetails = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Long currentUserNo = userDetails.getEmployee().getEmployeeNo();

	    // 회의록 참여 여부
	    boolean isMinuteParticipant = projectMeetingMinuteService.isMinuteParticipant(minuteNo, currentUserNo);

	    // 모델에 전달
	    model.addAttribute("minute", dto);
	    model.addAttribute("participants", participants);
	    model.addAttribute("projectNo", projectNo);
	    model.addAttribute("isMinuteParticipant", isMinuteParticipant);

	    return "project/projectMinuteDetail";
	}

	@GetMapping("/edit-page")
	public String editMinutePage(@RequestParam("minuteNo") Long minuteNo,
	                             @RequestParam("projectNo") Long projectNo,
	                             Model model) {

	    // 회의록 기본 정보 조회
	    ProjectMeetingMinuteDto dto = projectMeetingMinuteService.findByMinuteNo(minuteNo);

	    // 회의 참여자 번호 목록 조회
	    List<Long> selectedParticipantNos = projectMeetingMinuteService.findParticipantNos(minuteNo);

	    // 프로젝트 멤버 목록 조회
	    List<Employee> projectEmployees = projectService.findEmployeesByProjectNo(projectNo);

	    model.addAttribute("minute", dto);
	    model.addAttribute("selectedParticipantNos", selectedParticipantNos);
	    model.addAttribute("projectEmployees", projectEmployees);
	    model.addAttribute("projectNo", projectNo);

	    return "project/projectMinuteEdit";
	}

	@PostMapping("/edit")
	public String editMinute(ProjectMeetingMinuteDto minuteDto,
	                         @RequestParam("participants") List<Long> participants,
	                         @RequestParam("projectNo") Long projectNo) {

	    projectMeetingMinuteService.updateMinute(minuteDto, participants);

	    return "redirect:/project/minute/detail?minuteNo=" + minuteDto.getMeetingMinuteNo() + "&projectNo=" + projectNo;
	}
	
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, Object> deleteMinute(@RequestBody Map<String, Long> request) {
	    Long minuteNo = request.get("minuteNo");

	    Map<String, Object> response = new HashMap<>();
	    try {
	        projectMeetingMinuteService.deleteMinute(minuteNo);
	        response.put("success", true);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	    }

	    return response;
	}

}
