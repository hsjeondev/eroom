package com.eroom.chat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.repository.ChatroomRepository;
import com.eroom.chat.specification.ChatroomSpecification;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatroomService {

	private final ChatroomRepository repository;
	private final EmployeeRepository employeeRepository;
	
	
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
	 // Repository에서 엔티티로 중복 제거된 부서(소속) 목록을 반환한 후, DTO로 변환
	public List<SeparatorDto> findDistinctStructureNames() {
	    List<Structure> structures = employeeRepository.findDistinctStructures();
	    return structures.stream()
	        .map(entity -> SeparatorDto.builder()
	                .separator_code(entity.getSeparatorCode())
	                .separator_name(entity.getCodeName())
	                .build())
	        .collect(Collectors.toList());
	}
	public List<EmployeeDto> findEmployeesByStructureName(String separatorCode) {
	    // 부서명으로 직원을 조회
		System.out.println("현재 찾고 있는 부서 코드: " + separatorCode);
	    return employeeRepository.findByStructure_SeparatorCode(separatorCode)
	        .stream()
	        .map(emp -> new EmployeeDto(emp.getEmployeeNo(), emp.getEmployeeName()))
	        .collect(Collectors.toList());
	}

	public List<EmployeeDto> findEmployeesByParentCode(String parentCode) {
	    // 부모 부서코드로 직원을 조회
	    return employeeRepository.findByStructureParentCode(parentCode)
	        .stream()
	        .map(emp -> new EmployeeDto(emp.getEmployeeNo(), emp.getEmployeeName()))
	        .collect(Collectors.toList());
	}

}
