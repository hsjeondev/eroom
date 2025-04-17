package com.eroom.chat.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.chat.dto.ChatroomDto;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.repository.ChatroomAttendeeRepository;
import com.eroom.chat.repository.ChatroomRepository;
import com.eroom.chat.specification.ChatroomSpecification;
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
	
	public List<Chatroom> selectChatRoomAll(){
		// 현재 로그인한 사람 설정해야됨
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		EmployeeDetails employee = (EmployeeDetails)authentication.getPrincipal();
		
		// 채팅방 설정
		Specification<Chatroom> spec = (root, query, criteriaBuilder) -> null;
		// 생성자가 나인 경우
		spec = spec.and(ChatroomSpecification.fromUserEquals(employee.getEmployee()));
		// 생성자가 상대방인 경우
		spec = spec.or(ChatroomSpecification.toUserEquals(employee.getEmployee()));
		
		List<Chatroom> list = repository.findAll(spec);
		return list;
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
