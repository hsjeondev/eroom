package com.eroom.approval.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.approval.dto.ApprovalRequestDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.repository.ApprovalRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalRepository approvalRepository;
	private final EmployeeRepository employeeRepository;

	public List<Approval> getMyRequestedApprovals(Long employeeNo) {
		List<Approval> resultList = approvalRepository.findByEmployee_EmployeeNoOrderByApprovalRegDateDesc(employeeNo);
		return resultList;
	}

	public int createApproval(ApprovalRequestDto dto) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper .writeValueAsString(dto.getContent());
			Employee emp = employeeRepository.findById(dto.getWriter().getEmployee_no()).orElse(null);
			
			Approval approval = Approval.builder()
					.employee(emp)
					.approvalTitle(dto.getTitle())
					.approvalFormat(ApprovalFormat.builder().approvalFormatNo(Long.valueOf(dto.getFormat_no())).build())
					.approvalContent(json)
					.approvalStatus("S")
					.build();
			
			approvalRepository.save(approval);
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}



}
