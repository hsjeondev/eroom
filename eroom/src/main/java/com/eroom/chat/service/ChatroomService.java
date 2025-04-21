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
	    Employee employee = employeeDetails.getEmployee(); // 현재 로그인한 직원 정보

	    // 1. 만든 채팅방
	    List<Chatroom> createdByMe = repository.findByCreater(employee);

	    // 2. 참여한 채팅방
	    List<Chatroom> participatedIn = repository.findByParticipant(employee.getEmployeeNo());

	    // 3. 합치기 (자바 Set으로 중복 제거)
	    Set<Chatroom> allChatrooms = new HashSet<>();
	    allChatrooms.addAll(createdByMe);
	    allChatrooms.addAll(participatedIn);

	    return new ArrayList<>(allChatrooms);
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

}