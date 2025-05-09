package com.eroom.chat.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.chat.dto.ChatroomDto;
import com.eroom.chat.entity.ChatAlarm;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.repository.ChatAlarmRepository;
import com.eroom.chat.repository.ChatroomAttendeeRepository;
import com.eroom.chat.repository.ChatroomRepository;
import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.drive.service.DriveService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.security.EmployeeDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatroomService {

	private final ChatroomRepository repository;
	private final EmployeeRepository employeeRepository;
	private final ChatroomAttendeeRepository chatroomAttendeeRepository;
	private final ChatAlarmRepository chatAlarmRepository;
	private final DriveRepository driveRepository;
	private final DriveService driveService;
	// 파일 저장 경로 
		 @Value("${ffupload.location}")
		 private String fileDir;
		
	
		 public List<ChatroomDto> selectChatRoomAll() {
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		    Employee me = employeeDetails.getEmployee(); // 현재 로그인한 직원

		    List<Chatroom> createdByMe = repository.findByCreater(me);
		    List<Chatroom> participatedIn = repository.findByParticipant(me.getEmployeeNo());

		    Set<Chatroom> allChatrooms = new HashSet<>();
		    allChatrooms.addAll(createdByMe);
		    allChatrooms.addAll(participatedIn);

		    List<ChatroomDto> result = new ArrayList<>();
		    for (Chatroom chatroom : allChatrooms) {
		        ChatroomDto dto = ChatroomDto.toDto(chatroom);

		        // ✅ 1:1 채팅방이면 상대방 이름으로 설정
		        if ("N".equals(chatroom.getChatIsGroupYn())) {
		            String opponentName = "알 수 없음";
		            for (ChatroomAttendee attendee : chatroom.getChatroomMapping()) {
		                if (!attendee.getAttendee().getEmployeeNo().equals(me.getEmployeeNo())) {
		                    opponentName = attendee.getAttendee().getEmployeeName();
		                    break;
		                }
		            }
		            dto.setChatroomName(opponentName);
		        }

		        result.add(dto);
		    }

		    return result;
		}




	public ChatroomDto createChatroom(ChatroomDto dto) {
	    Chatroom param = dto.toEntity();
	    Chatroom result = repository.save(param);

	    // 생성자 본인을 참여자에 추가
	    Employee creater = employeeRepository.findById(dto.getCreater())
	        .orElseThrow(() -> new RuntimeException("생성자 정보를 찾을 수 없습니다."));
	    ChatroomAttendee creatorMapping = ChatroomAttendee.builder()
	        .chatroomNo(result)
	        .attendee(creater)
	        .build();
	    chatroomAttendeeRepository.save(creatorMapping);

	    // 나머지 참여자 추가
	    if (dto.getParticipantIds() != null && !dto.getParticipantIds().isEmpty()) {
	        for (Long participantId : dto.getParticipantIds()) {
	            Employee participant = employeeRepository.findById(participantId).orElse(null);
	            if (participant != null) {
	                ChatroomAttendee attendeeMapping = ChatroomAttendee.builder()
	                    .chatroomNo(result)
	                    .attendee(participant)
	                    .build();
	                chatroomAttendeeRepository.save(attendeeMapping);
	            }
	        }
	    }

	    return ChatroomDto.toDto(result);
	}


	public Chatroom selectChatroomOne(Long id) {
		return repository.findById(id).orElse(null);
	}

	public Chatroom renameChatroom(ChatroomDto param) {
		Chatroom result = null;
		Chatroom target = repository.findById(param.getChatroomNo()).orElse(null);
		if(target != null) {
			target.setChatroomName(param.getChatroomName());
			result = repository.save(target);
		}
		return result;
	}
	public boolean existsOneToOneChatroom(Long myId, Long otherId) {
	    List<Long> existingRooms = chatroomAttendeeRepository.findOneToOneChatroomNos(myId, otherId);
	    return !existingRooms.isEmpty();
	}
	
	// 채팅방 참여자 추가
	@Transactional
	public void addParticipants(Long chatroomNo, List<Long> participantIds) {
	    Chatroom chatroom = repository.findById(chatroomNo)
	        .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

	    List<Employee> newParticipants = employeeRepository.findAllById(participantIds);
	    // 참여자 정보 조회
	    for (Employee participant : newParticipants) {
	        boolean alreadyExists = chatroom.getChatroomMapping().stream()
	            .anyMatch(mapping -> mapping.getAttendee().getEmployeeNo().equals(participant.getEmployeeNo()));
	        // 참여자가 이미 존재하는지 확인
	        if (!alreadyExists) {
	            ChatroomAttendee newMapping = ChatroomAttendee.builder()
	                .chatroomNo(chatroom)
	                .attendee(participant)
	                .build();
	            chatroomAttendeeRepository.save(newMapping);
	        }
	    }
	}
	// 채팅방 삭제
	@Transactional
	public void deleteChatroom(Long chatroomNo) {
		Chatroom chatroom = repository.findById(chatroomNo)
				.orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
		
		chatroom.setVisibleYn("N"); // 채팅방 삭제
		repository.save(chatroom);
	}
	public void updateReadStatus(Long chatroomNo, Long senderNo) {
		List<ChatAlarm> alarms = chatAlarmRepository.findUnreadAlarms(senderNo, chatroomNo);

		for (ChatAlarm alarm : alarms) {
		    alarm.setChatAlarmReadYn("Y");
		    alarm.setChatAlarmReadDate(LocalDateTime.now());
		}

		chatAlarmRepository.saveAll(alarms);
	}
	@Transactional
	public List<Drive> uploadChatFilesAndReturnDrives(DriveDto driveDto, Long employeeNo) {
	    List<Drive> savedDrives = new ArrayList<>();

	    if (!"FL003".equals(driveDto.getSeparatorCode())) {
	        throw new IllegalArgumentException("채팅 업로드에는 separatorCode=FL003 이 필수입니다.");
	    }

	    if (driveDto.getParam1() == null) {
	        throw new IllegalArgumentException("chatroomNo (param1)이 누락되었습니다.");
	    }

	    List<MultipartFile> files = driveDto.getDriveFiles();
	    List<String> descriptions = driveDto.getDriveDescriptions();

	    Employee uploader = employeeRepository.findById(employeeNo)
	        .orElseThrow(() -> new RuntimeException("업로더를 찾을 수 없습니다."));

	    for (int i = 0; i < files.size(); i++) {
	        MultipartFile file = files.get(i);
	        try {
	            String oriName = file.getOriginalFilename();
	            String ext = oriName.substring(oriName.lastIndexOf("."));
	            String newName = UUID.randomUUID().toString().replace("-", "") + ext;

	            String path = fileDir + "drive/chat/" + newName;
	            File savedFile = new File(path);
	            if (!savedFile.getParentFile().exists()) {
	                savedFile.getParentFile().mkdirs();
	            }
	            file.transferTo(savedFile);

	            String description = (descriptions != null && descriptions.size() > i) ? descriptions.get(i) : null;

	            Drive drive = Drive.builder()
	                .uploader(uploader)
	                .separatorCode("FL003")
	                .driveOriName(oriName)
	                .driveNewName(newName)
	                .driveType(ext)
	                .driveSize(file.getSize())
	                .drivePath("drive/chat/" + newName)
	                .driveDescription(description)
	                .param1(driveDto.getParam1())
	                .downloadCount(0L)
	                .visibleYn("Y")
	                .build();

	            savedDrives.add(driveRepository.save(drive));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return savedDrives;
	}
	public Long findOrCreateOneToOneChatroom(Long myEmpNo, Long targetEmpNo) {
	    // 1:1 채팅방 존재 여부 확인
	    Optional<Chatroom> existingChatroom = repository.findOneToOneRoom(myEmpNo, targetEmpNo);

	    if (existingChatroom.isPresent()) {
	        return existingChatroom.get().getChatroomNo();
	    }

	    // 없으면 새로 생성
	    ChatroomDto dto = new ChatroomDto();
	    dto.setCreater(myEmpNo);
	    dto.setChatIsGroupYn("N");
	    dto.setParticipantIds(List.of(targetEmpNo));

	    ChatroomDto createdChatroom = this.createChatroom(dto);

	    if (createdChatroom == null || createdChatroom.getChatroomNo() == null) {
	        throw new RuntimeException("1:1 채팅방 생성 실패");
	    }

	    return createdChatroom.getChatroomNo();
	}

	public List<DriveDto> getChatFilesByRoom(Long chatroomNo, int page, int size) {
	    return driveService.findChatFilesByRoom(chatroomNo, page, size);
	}






}