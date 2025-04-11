package com.eroom.directory.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eroom.directory.dto.EmployeeDirectoryDto;
import com.eroom.directory.entity.EmployeeDirectory;
import com.eroom.directory.service.EmployeeDirectoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryController {
	
	private final EmployeeDirectoryService employeeDirectoryService;
	

	@GetMapping("/directory/employee")
	public String selectDirectoryEmployeeList(Model model) {
		List<EmployeeDirectoryDto> resultList = new ArrayList<EmployeeDirectoryDto>();
		List<EmployeeDirectory> temp = employeeDirectoryService.selectDirectoryAll();
		
		for(EmployeeDirectory t : temp) {
			EmployeeDirectoryDto dto = new EmployeeDirectoryDto();
			resultList.add(dto.toDto(t));
		}
		
		model.addAttribute("resultList", resultList);
		
		return "directory/employeeList";
	}
	@GetMapping("/directory/partner")
	public String selectDirectoryPartnerList(Model model) {
//		List<EmployeeDirectoryDto> resultList = new ArrayList<EmployeeDirectoryDto>();
//		List<EmployeeDirectory> temp = employeeDirectoryService.selectDirectoryAll();
//		
//		for(EmployeeDirectory t : temp) {
//			EmployeeDirectoryDto dto = new EmployeeDirectoryDto();
//			resultList.add(dto.toDto(t));
//		}
//		
//		model.addAttribute("resultList", resultList);
		
		return "directory/treetest";
	}
	
}
