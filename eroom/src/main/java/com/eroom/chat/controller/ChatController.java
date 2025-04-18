package com.eroom.chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.chat.dto.ChatroomDto;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.service.ChatroomService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatroomService chatroomService;
	private final EmployeeService employeeService;
	private final EmployeeRepository employeeRepository;
	
	@GetMapping("/test")
	public String test123() {
		return "chat/test";
	}
	
	@GetMapping("/list")
	public String selectChatRoomAll(@RequestParam(name = "department" ,required = false) String department, Model model) {
		List<Chatroom> resultList = chatroomService.selectChatRoomAll();
		model.addAttribute("chatroomList",resultList);
		
		List<SeparatorDto> structureList = employeeService.findDistinctStructureNames();
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
		return employeeService.findEmployeesByStructureName(separatorCode);
	} else {
		// 부서를 선택한 경우: parentCode 기준 조회
		return employeeService.findEmployeesByParentCode(separatorCode);
	}
}
	@PostMapping("/create")
	@ResponseBody
	public Map<String,String> createChatroomApi(ChatroomDto dto){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "채팅방 생성을 실패하였습니다.");
		
		// 채팅방 생성 시 참여자에 본인 ID가 포함되어 있을 경우
		if (dto.getParticipantIds().contains(dto.getCreater())) {
			resultMap.put("res_msg", "본인은 참여자로 선택할 수 없습니다.");
			return resultMap;
		}
		// 채팅방 생성 시 참여자 ID가 비어있을 경우
		if ("N".equals(dto.getChatIsGroupYn())) {
			// 1:1 채팅방 생성 시 참여자 ID가 비어있을 경우
			if (dto.getParticipantIds() == null || dto.getParticipantIds().isEmpty()) {
				resultMap.put("res_msg", "1:1 채팅방은 반드시 참여자를 선택해야 합니다.");
				return resultMap;
			}
			// 1:1 채팅방 생성시 이미 존재하는 채팅방인지 체크
			if (chatroomService.existsOneToOneChatroom(dto.getCreater(), dto.getParticipantIds().get(0))) {
	            resultMap.put("res_msg", "이미 존재하는 1:1 채팅방입니다.");
	            return resultMap;
	        }
		}
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
	@GetMapping("/roomDetail")
	@ResponseBody
	public ChatroomDto roomDetail(@RequestParam("roomNo") Long roomNo) {
	    Chatroom chatroom = chatroomService.selectChatroomOne(roomNo);
	    if (chatroom == null) {
	        throw new RuntimeException("채팅방 정보를 찾을 수 없습니다.");
	    }
	    // ChatroomDto의 toDto() 메서드를 통해 필요한 데이터를 DTO에 담아서 반환
	    return ChatroomDto.toDto(chatroom);
	}
	
	// 채팅방 업데이트
	@PostMapping("/rename")
	@ResponseBody
	public Map<String ,String> renameChatroom(@RequestBody ChatroomDto param){
		
		System.out.println(param);
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "채팅방 이름변경을 실패하였습니다.");
		
		Chatroom update = chatroomService.renameChatroom(param);
		if(update != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "수정을 성공하였습니다!");
		}
		return resultMap;
	}
	// 그룹 채팅방 참여자 추가
	@PostMapping("/addParticipants")
	@ResponseBody
	public Map<String, String> addParticipants(@ModelAttribute ChatroomDto param) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "채팅방 참여자 추가를 실패하였습니다.");

	    chatroomService.addParticipants(param.getChatroomNo(), param.getParticipantIds());

	    resultMap.put("res_code", "200");
	    resultMap.put("res_msg", "참여자를 추가하였습니다!");
	    return resultMap;
	}
	// 채팅방 참여자 조회
	@GetMapping("/participants")
	@ResponseBody
	public List<String> getParticipants(@RequestParam("chatroomNo") Long chatroomNo) {
	    Chatroom chatroom = chatroomService.selectChatroomOne(chatroomNo);
	    if (chatroom == null) {
	        throw new RuntimeException("채팅방 정보를 찾을 수 없습니다.");
	    }
	    // ChatroomAttendee에서 참여자 정보 가져오기
	    List<String> participantNames = new ArrayList<String>();
	    for (ChatroomAttendee mapping : chatroom.getChatroomMapping()) {
	        participantNames.add(mapping.getAttendee().getEmployeeName());
	    }
	    
	    return participantNames;
	}
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> deleteChatroom(@RequestBody ChatroomDto param) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "채팅방 삭제를 실패하였습니다.");

		chatroomService.deleteChatroom(param.getChatroomNo());

		resultMap.put("res_code", "200");
		resultMap.put("res_msg", "채팅방을 삭제하였습니다!");

		return resultMap;
	}
}
