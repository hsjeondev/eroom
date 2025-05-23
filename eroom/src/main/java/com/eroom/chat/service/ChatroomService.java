package com.eroom.chat.service;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    // 채팅방 나가기
    public void leaveChatroom(Long chatroomNo, Long employeeNo) {
        // 채팅방과 해당 참여자 확인
        ChatroomAttendee attendee = chatroomAttendeeRepository.findByChatroomNoAndAttendeeEmployeeNo(chatroomNo, employeeNo);

        if (attendee != null) {
            // 참여자 제거
            chatroomAttendeeRepository.delete(attendee);

            // 만약 채팅방에 참여자가 1명도 없으면 채팅방 삭제
            if (chatroomAttendeeRepository.countByChatroomNo(chatroomNo) == 0) {
                repository.deleteById(chatroomNo);
            }
        } else {
            throw new RuntimeException("참여자가 아니거나 잘못된 채팅방입니다.");
        }
	}

    @Transactional
 // 채팅방 파일 다운로드 처리
    public ResponseEntity<Resource> downloadFile(Long driveAttachNo) {
        try {
            // DB에서 파일 정보 가져오기 (driveAttachNo로 파일 조회)
            Drive drive = driveService.findByDriveAttachNo(driveAttachNo);
            if (drive == null) {
                System.out.println("해당 파일을 찾을 수 없음");
                return ResponseEntity.notFound().build(); // 파일을 찾을 수 없으면 404 응답
            }

            // 파일이 FL003 세퍼레이터 코드인지 확인
            if (!"FL003".equals(drive.getSeparatorCode())) {
                System.out.println("이 파일은 다운로드할 수 없습니다. 세퍼레이터 코드가 맞지 않습니다.");
                return ResponseEntity.badRequest().build(); // 잘못된 파일 요청
            }

            // 실제 파일 경로
            Path filePath = Paths.get(fileDir + drive.getDrivePath()); // 파일 경로 생성
            System.out.println("파일 경로: " + filePath.toString());
            System.out.println("파일 존재 여부: " + Files.exists(filePath));

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build(); // 파일이 존재하지 않으면 404 응답
            }

            // 파일명 한글 깨짐 방지 (UTF-8 인코딩)
            String encodedFileName = URLEncoder.encode(drive.getDriveOriName(), "UTF-8")
                                               .replaceAll("\\+", "%20");

            // 파일 스트림 준비
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));

            // 다운로드 응답
            return ResponseEntity.ok()
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                                 .contentType(MediaType.APPLICATION_OCTET_STREAM) // 파일 종류에 상관없이 모든 파일을 처리
                                 .body(resource);

        } catch (Exception e) {
            System.out.println("파일 다운로드 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build(); // 예외가 발생하면 400 오류 반환
        }
    }

}