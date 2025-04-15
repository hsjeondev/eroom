package com.eroom.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.chat.dto.ChatroomDto;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.service.ChatroomService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatroomService chatroomService;
	private final EmployeeRepository employeeRepository;
	
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
	@PostMapping("/create")
	@ResponseBody
	public Map<String,String> createChatroomApi(ChatroomDto dto){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "채팅방 생성을 실패하였습니다.");
		
		 // 그룹 채팅인데 제목이 비어있을 경우
	    if ("Y".equals(dto.getChatIsGroupYn()) &&
	        (dto.getChatroomName() == null || dto.getChatroomName().trim().isEmpty())) {
	        resultMap.put("res_msg", "그룹 채팅은 제목을 반드시 입력해야 합니다.");
	        return resultMap;
	    }
		// 1:1 채팅인데 제목이 비어있을 경우 -> 상대방 이름 으로 설정
		if ("N".equals(dto.getChatIsGroupYn()) && (dto.getChatroomName() == null || dto.getChatroomName().isEmpty())) {
			if (dto.getParticipantIds() != null && !dto.getParticipantIds().isEmpty()) {
	            Long participantId = dto.getParticipantIds().get(0);
	            // 참여자 정보 조회 (repository 또는 service 사용)
	            Employee participant = employeeRepository.findById(participantId)
	                .orElseThrow(() -> new RuntimeException("참여자 정보를 찾을 수 없습니다."));
	            dto.setChatroomName(participant.getEmployeeName());
	        }
	    }
		
		
		ChatroomDto chatDto = chatroomService.createChatroom(dto);
		if(chatDto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "새로운 채팅방을 등록하였습니다!");
		} 
		return resultMap;
	}

}
