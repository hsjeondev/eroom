package com.eroom.project.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.project.dto.ProjectMeetingMinuteDto;
import com.eroom.project.dto.ProjectMeetingMinuteMappingDto;
import com.eroom.project.service.ProjectMeetingMinuteService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project/minute")
public class ProjectMinuteController {
	private final EmployeeService employeeService;
	private final ProjectMeetingMinuteService projectMeetingMinuteService;
	
	@GetMapping("/create-page")
	public String createMinutePage(@RequestParam("projectNo") Long projectNo, Model model) {
	    EmployeeDetails userDetails = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Employee writer = userDetails.getEmployee();
	    Long structureNo = writer.getStructure().getStructureNo();

	    List<Employee> structureEmployees = employeeService.findByStructureNo(structureNo);
	    List<Employee> filteredEmployees = new ArrayList<>();
	    for (Employee emp : structureEmployees) {
	        if (!emp.getEmployeeNo().equals(writer.getEmployeeNo())) {
	            filteredEmployees.add(emp);
	        }
	    }

	    model.addAttribute("structureEmployees", filteredEmployees);
	    model.addAttribute("projectNo", projectNo);
	    return "project/projectMinuteCreate";
	}
	
	@PostMapping("/create")
	public String createMinute(ProjectMeetingMinuteDto minuteDto,
	                           @RequestParam(value = "participants") List<Long> participants) {

	    // 로그인 사용자 정보 설정
	    EmployeeDetails userDetails = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    minuteDto.setWriter(userDetails.getEmployee().getEmployeeName());
	    minuteDto.setMeetingDate(LocalDateTime.now());

	    // 회의록 저장
	    Long savedMinuteNo = projectMeetingMinuteService.saveMinute(minuteDto);

	    // 참여자 매핑 저장
	    ProjectMeetingMinuteMappingDto mappingDto = new ProjectMeetingMinuteMappingDto();
	    mappingDto.setMeetingMinuteNo(savedMinuteNo);
	    mappingDto.setParticipants(participants);
	    projectMeetingMinuteService.saveMappings(mappingDto);

	    // 저장 완료 후 회의록 탭으로 리다이렉트 (projectId는 추후 동적으로 반영)
	    return "redirect:/project/detail/1/minutes";
	}

}
