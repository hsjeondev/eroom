package com.eroom.chat.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.chat.dto.ChatroomDto;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.repository.ChatroomAttendeeRepository;
import com.eroom.chat.repository.ChatroomRepository;
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
	
	public List<Chatroom> selectChatRoomAll() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Employee employee = employeeDetails.getEmployee(); // employeeNo 말고 Employee 객체 통째로!

	    // 1. 내가 만든 채팅방
	    List<Chatroom> createdByMe = repository.findByCreater(employee);

	    // 2. 내가 참여자로 들어간 채팅방
	    List<Chatroom> participatedIn = repository.findByParticipant(employee.getEmployeeNo());

	    // 3. 합치기 (중복제거)
	    Set<Chatroom> allChatrooms = new HashSet<>();
	    allChatrooms.addAll(createdByMe);
	    allChatrooms.addAll(participatedIn);

	    return new ArrayList<>(allChatrooms);
	}



	public ChatroomDto createChatroom(ChatroomDto dto) {
		Chatroom param = dto.toEntity();
		Chatroom result = repository.save(param);
		
		if(dto.getParticipantIds() != null && !dto.getParticipantIds().isEmpty()) {
			for(Long participantid : dto.getParticipantIds()) {
				Employee participant = employeeRepository.findById(participantid).orElse(null);
				ChatroomAttendee attendeeMapping = ChatroomAttendee.builder().chatroomNo(result).attendee(participant).build();
				
				chatroomAttendeeRepository.save(attendeeMapping);
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

}
