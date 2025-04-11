package com.eroom.chat.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.service.ChatroomService;
import com.eroom.employee.entity.Employee;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatroomService chatroomService;
	
	@GetMapping("/list")
	public String selectChatRoomAll(@RequestParam(name = "department" ,required = false) String department,Model model) {
		List<Chatroom> resultList = chatroomService.selectChatRoomAll();
		model.addAttribute("chatroomList",resultList);
		
		List<String> departmentList = chatroomService.findDistinctDepartmentNames();
		model.addAttribute("departmentList", departmentList);

		List<Employee> employeeList = (department != null && !department.isEmpty())
		    ? chatroomService.findEmployeesByDepartmentName(department)
		    : chatroomService.selectEmployeeAll();
		model.addAttribute("employeeList", employeeList);
		
		return "chat/list";
	}
	
}
