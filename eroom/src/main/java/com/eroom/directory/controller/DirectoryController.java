package com.eroom.directory.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.eroom.directory.dto.EmployeeDirectoryDto;
import com.eroom.directory.entity.EmployeeDirectory;
import com.eroom.directory.service.EmployeeDirectoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryController {
	
	private final EmployeeDirectoryService employeeDirectoryService;
	

//	@GetMapping("/directory")
//	public String selectDirectory01() {
//		return "directory/list";
//	}
	@GetMapping("/directory")
	public String selectDirectoryList(Model model) {
		List<EmployeeDirectoryDto> resultList = new ArrayList<EmployeeDirectoryDto>();
		List<EmployeeDirectory> temp = employeeDirectoryService.selectDirectoryAll();
		
		for(EmployeeDirectory t : temp) {
			EmployeeDirectoryDto dto = new EmployeeDirectoryDto();
			resultList.add(dto.toDto(t));
		}
		
		model.addAttribute("resultList", resultList);
		
		return "directory/list2";
	}
//	@GetMapping("/directory/{id}")
//	public String selectDirectory03(@PathVariable("id") Long id, Model model) {
//		
//		
//		return "directory/list2";
//	}
	
}
