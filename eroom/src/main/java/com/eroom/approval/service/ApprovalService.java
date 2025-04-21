package com.eroom.approval.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.approval.dto.ApprovalRequestDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.repository.ApprovalLineRepository;
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
	private final ApprovalLineRepository approvalLineRepository;

	public List<Approval> getMyRequestedApprovals(Long employeeNo) {
		List<Approval> resultList = approvalRepository.findByEmployee_EmployeeNoOrderByApprovalRegDateDesc(employeeNo);
		return resultList;
	}

	@Transactional
	public int createApproval(ApprovalRequestDto dto, Long employeeNo) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper .writeValueAsString(dto.getContent());
			Employee emp = employeeRepository.findById(employeeNo).orElse(null);
			
			Approval approval = Approval.builder()
					.employee(emp)
					.approvalTitle(dto.getTitle())
					.approvalFormat(ApprovalFormat.builder().approvalFormatNo(Long.valueOf(dto.getFormat_no())).build())
					.approvalContent(json)
					.approvalStatus("S")
					.build();
			
			approvalRepository.save(approval);
			
			
			
			// 1. 결재자 저장
			ApprovalLine approvalLine = null;
//			System.out.println(dto.getApproverIds() + " : 결재자들id");
//			System.out.println(dto.getApproverSteps() + " : 결재자들step");
//			System.out.println(dto.getAgreerIds() + " : 합의자들id");
//			System.out.println(dto.getAgreerSteps() + " : 합의자들step");
//			System.out.println(dto.getRefererIds() + " : 참조자들id");
//			System.out.println(dto.getRefererSteps() + " : 참조자들step");
			for (int i = 0; i < dto.getApproverIds().size(); i++) {
				
				approvalLine = ApprovalLine.builder()
						.approval(approval)
						.approvalLineStatus("S")
						.employee(Employee.builder().employeeNo(dto.getApproverIds().get(i)).build())
						.approvalLineStep(dto.getApproverSteps().get(i)) // 결재자 순서 step 1,2,3...
						.build();
					approvalLineRepository.save(approvalLine);
			}

			// 2. 합의자 저장
			for (int i = 0; i < dto.getAgreerIds().size(); i++) {
				
				approvalLine = ApprovalLine.builder()
						.approval(approval)
						.approvalLineStatus("S")
						.employee(Employee.builder().employeeNo(dto.getAgreerIds().get(i)).build())
						.approvalLineStep(0) // 합의자 고정값 0
						.build();
					approvalLineRepository.save(approvalLine);
			}

			// 3. 참조자 저장
			for (int i = 0; i < dto.getRefererIds().size(); i++) {
				
						approvalLine = ApprovalLine.builder()
						.approval(approval)
						.approvalLineStatus("A")
						.employee(Employee.builder().employeeNo(dto.getRefererIds().get(i)).build())
						.approvalLineStep(-1) // 참조자 고정값 -1
						.build();
					approvalLineRepository.save(approvalLine);
			}
			
			
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}



}
