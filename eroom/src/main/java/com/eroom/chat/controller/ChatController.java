package com.eroom.chat.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.service.ChatroomService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatroomService chatroomService;
	
	@GetMapping("/list")
	public String selectChatRoomAll(@RequestParam(name = "department" ,required = false) String department, Model model) {
		List<Chatroom> resultList = chatroomService.selectChatRoomAll();
		model.addAttribute("chatroomList",resultList);
		
		List<SeparatorDto> structureList = chatroomService.findDistinctStructureNames();
		model.addAttribute("structureList", structureList);
		
		return "chat/list";
	}
	
	@GetMapping("/employes")
	@ResponseBody
	public List<EmployeeDto> getEmployeesByDepartment(@RequestParam(name = "separator_code") String separatorCode) {
	String temp = separatorCode.substring(0,1);
	System.out.println(temp + " | substring 자르기 1글자 나와야해");
	if ("T".equals(temp)) {
		// 팀(소속) 선택한 경우: separatorCode 기준 조회
		return chatroomService.findEmployeesByStructureName(separatorCode);
	} else {
		// 부서를 선택한 경우: parentCode 기준 조회
		return chatroomService.findEmployeesByParentCode(separatorCode);
	}
}
	

}
