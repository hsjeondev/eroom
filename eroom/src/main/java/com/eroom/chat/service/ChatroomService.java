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
	// EmployeeRepository 에 있는 부서명 중복 제거 목록
	public List<String> findDistinctDepartmentNames() {
		return employeeRepository.findDistinctDepartmentNames();
	}
	// EmployeeRepository 에 있는 특정 부서에 속한 직원 가져오기
	public List<EmployeeDto> findEmployeesByDepartmentName(String departmentName) {
	    return employeeRepository.findByStructure_CodeName(departmentName)
	    		.stream()
	    		.map(emp -> new EmployeeDto(emp.getEmployeeNo(), emp.getEmployeeName()))
	    		.collect(Collectors.toList());
	}


}
