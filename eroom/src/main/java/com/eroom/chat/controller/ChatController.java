package com.eroom.chat.controller;


import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.chat.dto.ChatMessageDto;
import com.eroom.chat.dto.ChatroomDto;
import com.eroom.chat.entity.ChatAlarm;
import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.repository.ChatAlarmRepository;
import com.eroom.chat.service.ChatMessageService;
import com.eroom.chat.service.ChatroomService;
import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.service.DriveService;
import com.eroom.drive.service.ProfileService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;
import com.eroom.websocket.ChatWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatroomService chatroomService;
	private final EmployeeService employeeService;
	private final EmployeeRepository employeeRepository;
	private final ChatMessageService chatMessageService;
	private final ChatAlarmRepository chatAlarmRepository;
	private final ChatWebSocketHandler webSocketHandler; 
	private final ProfileService profileService;
	private final DriveService driveService;
	private final StructureService structureService;
	
	
	@Value("${ffupload.location}")
	private String fileDir;
	
	@GetMapping("/list")
	public String selectChatRoomAll(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long myEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    // 1. 채팅방 목록 + 프로필 이미지 처리
	    List<ChatroomDto> chatroomDtos = chatroomService.selectChatRoomAll();
	    for (ChatroomDto dto : chatroomDtos) {
	        int unreadCount = chatAlarmRepository.countUnreadAlarms(myEmployeeNo, dto.getChatroomNo());
	        dto.setUnreadCount(unreadCount);

	        Long targetEmployeeNo = null;
	        if ("N".equals(dto.getChatIsGroupYn())) {
	            List<Long> participants = dto.getParticipantIds();
	            if (participants != null) {
	                for (Long no : participants) {
	                    if (!no.equals(myEmployeeNo)) {
	                        targetEmployeeNo = no;
	                        break;
	                    }
	                }
	            }
	        } else {
	            targetEmployeeNo = dto.getCreater();
	        }

	        String profileUrl = profileService.getProfileImageUrl(targetEmployeeNo);
	        dto.setProfileImageUrl(profileUrl);
	    }
	    chatroomDtos.sort((a, b) -> {
	        if (a.getLastMessageRegDate() == null) return 1;
	        if (b.getLastMessageRegDate() == null) return -1;
	        return b.getLastMessageRegDate().compareTo(a.getLastMessageRegDate());
	    });
	    model.addAttribute("chatroomList", chatroomDtos);

	    // 2. 부서 / 팀 / 사원 정보 트리 구조 세팅
	    Map<String, List<EmployeeDto>> teamEmployeeMap = new HashMap<>();
	    List<Employee> empEntityList = employeeService.findAllEmployee_EmployeeEmploymentYn();
	    List<Structure> departmentList = structureService.selectDepartmentAll();
	    Map<String, List<Structure>> teamMap = new HashMap<>();

	    for (Structure dept : departmentList) {
	        List<Structure> teamList = structureService.selectTeamAllByParentCode(dept.getSeparatorCode());
	        teamMap.put(dept.getCodeName(), teamList);

	        for (Structure team : teamList) {
	            List<EmployeeDto> employees = new ArrayList<>();
	            for (Employee emp : empEntityList) {
	                if (emp.getStructure() != null &&
	                    emp.getStructure().getSeparatorCode().equals(team.getSeparatorCode())) {
	                    employees.add(new EmployeeDto().toDto(emp));
	                }
	            }
	            teamEmployeeMap.put(team.getSeparatorCode(), employees);
	        }
	    }

	    // 3. 무소속 사원 처리
	    List<EmployeeDto> noTeamList = new ArrayList<>();
	    for (Employee emp : empEntityList) {
	        if (emp.getStructure() == null) {
	            noTeamList.add(new EmployeeDto().toDto(emp));
	        }
	    }
	    teamEmployeeMap.put("noTeam", noTeamList);

	    // 4. 부서는 있지만 팀이 없는 사원 처리
	    for (Structure dept : departmentList) {
	        List<EmployeeDto> notAssignedList = new ArrayList<>();
	        for (Employee emp : empEntityList) {
	            Structure struct = emp.getStructure();
	            if (struct == null) continue;

	            if (struct.getSeparatorCode().equals(dept.getSeparatorCode())) {
	                notAssignedList.add(new EmployeeDto().toDto(emp));
	            } else if (struct.getParentCode() != null &&
	                       struct.getParentCode().equals(dept.getSeparatorCode())) {
	                List<Structure> teamList = teamMap.get(dept.getCodeName());
	                boolean isTeam = teamList.stream()
	                                         .anyMatch(t -> t.getSeparatorCode().equals(struct.getSeparatorCode()));
	                if (!isTeam) {
	                    notAssignedList.add(new EmployeeDto().toDto(emp));
	                }
	            }
	        }

	        if (!notAssignedList.isEmpty()) {
	            teamEmployeeMap.put(dept.getSeparatorCode() + "_notAssigned", notAssignedList);
	        }
	    }

	    // 5. 트리 데이터 전달
	    model.addAttribute("departmentList", departmentList);
	    model.addAttribute("teamMap", teamMap);
	    model.addAttribute("teamEmployeeMap", teamEmployeeMap);

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
		
		
		Long creater = dto.getCreater();
	    List<Long> original = dto.getParticipantIds();
	    List<Long> cleanedList = new ArrayList<>();
	    
	    for (Long id : original) {
	        if (!id.equals(creater) && !cleanedList.contains(id)) {
	            cleanedList.add(id);
	        }
	    }
	    dto.setParticipantIds(cleanedList);
		// 채팅방 생성 시 참여자에 본인 ID가 포함되어 있을 경우
		if ("N".equals(dto.getChatIsGroupYn()) && dto.getParticipantIds().contains(dto.getCreater())) {
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
	    // 현재 로그인한 사용자 정보 가져오기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long myEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    // 1. 안 읽은 알림 조회 및 읽음 처리
	    List<ChatAlarm> unreadAlarms = chatAlarmRepository.findUnreadAlarms(myEmployeeNo, roomNo);
	    for (ChatAlarm alarm : unreadAlarms) {
	        alarm.setChatAlarmReadYn("Y");
	    }
	    chatAlarmRepository.saveAll(unreadAlarms);

	    // 2. 채팅방 조회
	    Chatroom chatroom = chatroomService.selectChatroomOne(roomNo);
	    if (chatroom == null) {
	        throw new RuntimeException("채팅방 정보를 찾을 수 없습니다.");
	    }

	    // 3. 메시지 리스트 DTO 변환
	    List<ChatMessage> messageList = chatMessageService.selectMessageByRoomNo(roomNo);
	    List<ChatMessageDto> messageDtoList = new ArrayList<>();
	    
	    for (ChatMessage message : messageList) {
	    	Long senderEmployeeNo = message.getSenderMember().getEmployeeNo();
	        String profileUrl = profileService.getProfileImageUrl(senderEmployeeNo); 
	        messageDtoList.add(ChatMessageDto.toDto(message,profileUrl));
	    }

	    // 4. 채팅방 DTO 생성
	    ChatroomDto chatroomDto = ChatroomDto.toDto(chatroom, messageDtoList);

	    // 5. 1:1 채팅일 경우 상대방 이름으로 덮어쓰기
	    if ("N".equals(chatroom.getChatIsGroupYn())) {
	        String opponentName = chatroom.getChatroomMapping().stream()
	            .map(ChatroomAttendee::getAttendee)
	            .filter(e -> !e.getEmployeeNo().equals(myEmployeeNo))
	            .map(Employee::getEmployeeName)
	            .findFirst()
	            .orElse("알 수 없음");
	        chatroomDto.setChatroomName(opponentName);
	    }

	    return chatroomDto;
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
	public List<Map<String, Object>> getParticipants(@RequestParam("chatroomNo") Long chatroomNo) {
	    Chatroom chatroom = chatroomService.selectChatroomOne(chatroomNo);
	    if (chatroom == null) {
	        throw new RuntimeException("채팅방 정보를 찾을 수 없습니다.");
	    }

	    List<Map<String, Object>> participantList = new ArrayList<>();
	    for (ChatroomAttendee mapping : chatroom.getChatroomMapping()) {
	        Employee attendee = mapping.getAttendee();
	        Map<String, Object> info = new HashMap<>();
	        info.put("employeeNo", attendee.getEmployeeNo());
	        info.put("employeeName", attendee.getEmployeeName());
	        info.put("profileImageUrl", profileService.getProfileImageUrl(attendee.getEmployeeNo()));
	        participantList.add(info);
	    }
	    return participantList;
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
	// 채팅방 참여자 조회
	@GetMapping("/receiver")
	@ResponseBody
	public Map<String, Object> getReceiver(@RequestParam("chatroomNo") Long chatroomNo) {
	    Map<String, Object> resultMap = new HashMap<>();

	    try {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	        Long myEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();

	        Chatroom chatroom = chatroomService.selectChatroomOne(chatroomNo);
	        if (chatroom != null && "N".equals(chatroom.getChatIsGroupYn())) {
	            for (ChatroomAttendee attendee : chatroom.getChatroomMapping()) {
	                if (!attendee.getAttendee().getEmployeeNo().equals(myEmployeeNo)) {
	                    resultMap.put("receiverNo", attendee.getAttendee().getEmployeeNo());
	                    return resultMap;
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 꼭 출력해서 서버 로그 확인
	    }

	    resultMap.put("receiverNo", null); // 항상 응답 보장
	    return resultMap;
	}



	@PostMapping("/read")
	@ResponseBody
	public Map<String, String> readChat(@RequestBody Map<String, Long> payload) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "읽음 처리를 실패했습니다.");

	    try {
	        Long chatroomNo = payload.get("chatroomNo");
	        Long senderNo = payload.get("senderNo");

	        chatroomService.updateReadStatus(chatroomNo, senderNo);

	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "읽음 처리가 완료되었습니다!");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return resultMap;
	}
	
	// 채팅방 알림 개수 조회
	@GetMapping("/unreadCount")
	@ResponseBody
	public Map<String, Integer> getUnreadCount(@RequestParam("chatroomNo") Long chatroomNo) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long myEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();
	    
	    int count = chatAlarmRepository.countUnreadAlarms(myEmployeeNo, chatroomNo);
	    Map<String, Integer> result = new HashMap<>();
	    result.put("count", count);
	    return result;
	}
	// 채팅 파일 업로드
	@PostMapping("/upload/chat")
	@ResponseBody
	public Map<String, Object> uploadChatDriveFiles(
	        DriveDto driveDto,
	        @RequestParam("driveFiles") List<MultipartFile> files,
	        @RequestParam("chatroomNo") Long chatroomNo,
	        @RequestParam("driveDescriptions") List<String> driveDescriptions,
	        @AuthenticationPrincipal EmployeeDetails user) {

	    Map<String, Object> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "업로드 실패");

	    try {
	        // 필수 값 세팅
	        driveDto.setDriveFiles(files);
	        driveDto.setSeparatorCode("FL003");
	        driveDto.setUploaderNo(user.getEmployee().getEmployeeNo());
	        driveDto.setDriveDescriptions(driveDescriptions);
	        driveDto.setParam1(chatroomNo);

	        // 파일 저장 및 Drive 객체 반환
	        List<Drive> savedDrives = chatroomService.uploadChatFilesAndReturnDrives(driveDto, user.getEmployee().getEmployeeNo());

	        List<String> fileNames = new ArrayList<>();
	        Map<String, String> drivePaths = new HashMap<>();
	        for (Drive drive : savedDrives) {
	            String oriName = drive.getDriveOriName();
	            String path = drive.getDrivePath(); // 실제 다운로드 경로
	            fileNames.add(oriName);
	            drivePaths.put(oriName, path);
	            ChatMessageDto msg = ChatMessageDto.builder()
	                    .chatMessageContent("[파일] " + oriName)
	                    .chatroomNo(chatroomNo)
	                    .senderMember(user.getEmployee().getEmployeeNo())
	                    .receiverMember(null) // or 상대방 번호 if 1:1
	                    .build();

	                webSocketHandler.broadcastMessage(msg, null);
	        }

	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "업로드 성공");
	        resultMap.put("fileNames", fileNames);
	        resultMap.put("drivePaths", drivePaths);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_msg", "예외 발생: " + e.getMessage());
	    }

	    return resultMap;
	}
	@GetMapping("/room/{targetEmpNo}")
	public String goOneToOneChat(@PathVariable("targetEmpNo") Long targetEmpNo, Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long myEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    // 본인과의 채팅은 불가하도록 예외 처리
	    if (myEmployeeNo.equals(targetEmpNo)) {
	        return "redirect:/chat/list";
	    }

	    // 1:1 채팅방이 없으면 생성, 있으면 기존 채팅방 번호 반환
	    Long chatroomNo = chatroomService.findOrCreateOneToOneChatroom(myEmployeeNo, targetEmpNo);

	    return "redirect:/chat/list?roomNo=" + chatroomNo;
	}
	@GetMapping("/roomDetailView")
	public String roomDetailView(@RequestParam("roomNo") Long roomNo, Model model) {
	    model.addAttribute("roomNo", roomNo);
	    return "chat/list"; // 또는 실제 HTML/JSP 경로에 맞게 수정
	}
	@GetMapping("/files")
	@ResponseBody
	public List<DriveDto> findFiles(
	    @RequestParam("chatroomNo") Long chatroomNo,
	    @RequestParam("page") int page,
	    @RequestParam("size") int size
	) {
	    return driveService.findChatFilesByRoom(chatroomNo, page, size);
	}
	// 채팅방 나가기
	@PostMapping("/leave")
	@ResponseBody
	public Map<String, Object> leaveChatroom(@RequestBody Map<String, Object> request) {
	    // 로그인한 사용자 정보 추출
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long myEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();  // 로그인한 사용자의 employeeNo

	    // 채팅방 번호 가져오기
	    Long chatroomNo = Long.valueOf(request.get("chatroomNo").toString());

	    // 채팅방 나가기 처리
	    chatroomService.leaveChatroom(chatroomNo, myEmployeeNo);

	    return Map.of("res_code", "200", "res_msg", "채팅방에서 나갔습니다.");
	}
	// 채팅방 파일 다운로드 API
	@GetMapping("/download/chat/{driveAttachNo}")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@PathVariable("driveAttachNo") Long driveAttachNo) {
	    try {
	        // DB에서 파일 정보 가져오기
	        Drive drive = driveService.findByDriveAttachNo(driveAttachNo); 
	        if (drive == null) {
	            return ResponseEntity.notFound().build(); // 파일을 찾을 수 없으면 404 응답
	        }

	        // 실제 파일 경로
	        String filePathString = "/files/drive/chat/" + drive.getDriveNewName();
	        Path filePath = Paths.get(filePathString);
	        System.out.println("파일 경로: " + filePath.toString());

	        if (!Files.exists(filePath)) {
	            return ResponseEntity.notFound().build(); // 파일이 존재하지 않으면 404 응답
	        }

	        // 파일명 한글 깨짐 방지 (UTF-8 인코딩)
	        String encodedFileName = URLEncoder.encode(drive.getDriveOriName(), "UTF-8").replaceAll("\\+", "%20");

	        // 파일 스트림 준비
	        Resource resource = new InputStreamResource(Files.newInputStream(filePath));

	        return ResponseEntity.ok()
	                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
	                             .contentType(MediaType.APPLICATION_OCTET_STREAM) // 파일 종류에 상관없이 모든 파일을 처리
	                             .body(resource);

	    } catch (Exception e) {
	        return ResponseEntity.badRequest().build(); // 예외 발생시 400 오류 반환
	    }
	}


}