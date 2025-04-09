package com.eroom.chat.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String selectChatRoomAll(Model model) {
		List<Chatroom> resultList = chatroomService.selectChatRoomAll();
		model.addAttribute("chatroomList",resultList);
		
		List<Employee> employeeList = chatroomService.selectEmployeeAll();
		model.addAttribute("employeeList",employeeList);
		
		return "chat/list";
	}
	
}
