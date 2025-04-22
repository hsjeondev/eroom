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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalRepository approvalRepository;
	private final EmployeeRepository employeeRepository;
	private final ApprovalLineRepository approvalLineRepository;

	// 내가 올린 결재 리스트 조회 + 신청일 기준으로 최신순 정렬
	public List<Approval> getMyRequestedApprovals(Long employeeNo, String visible) {
		List<Approval> resultList = approvalRepository.findByEmployee_EmployeeNoAndApprovalVisibleYnOrderByApprovalRegDateDesc(employeeNo, visible);
		return resultList;
	}

	// 결재 생성
	@Transactional
	public int createApproval(ApprovalRequestDto dto, Long employeeNo) {
		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			String json = objectMapper .writeValueAsString(dto.getContent());
			Employee emp = employeeRepository.findById(employeeNo).orElse(null);
			
			Approval approval = Approval.builder()
					.employee(emp)
					.approvalTitle(dto.getTitle())
					.approvalFormat(ApprovalFormat.builder().approvalFormatNo(Long.valueOf(dto.getFormat_no())).build())
					.approvalContent(dto.getContent())
					.approvalStatus("S")
					.build();
			
			approvalRepository.save(approval);
			
			
			
			// 1. 결재자 저장
			ApprovalLine approvalLine = null;
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

	// 결재 번호로 결재 조회
	public Approval selectApprovalByApprovalNo(Long approvalNo) {
		Approval approval = approvalRepository.findById(approvalNo).orElse(null);
		return approval;
	}

	// 결재 삭제
	public int updateVisibleYn(Long approvalNo) {
		int result = 0;
		try {
			Approval approval = approvalRepository.findById(approvalNo).orElse(null);
			if (approval != null) {
				if (!approval.getApprovalVisibleYn().equals("N")) {
					approval.setApprovalVisibleYn("N");
					approvalRepository.save(approval);
				}
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		return result;
	}

	// 결재 번호로 결재 리스트 조회
	public List<Approval> getApprovalListByApprovalNo(Long approvalNo, String visible) {
		List<Approval> resultList = approvalRepository.findByApprovalNoAndApprovalVisibleYnOrderByApprovalRegDateDesc(approvalNo, visible);
		return resultList;
	}

	// 결재 회수
	public int updateApprovalStatus(Long approvalNo) {
		int result = 0;
		try {
			Approval approval = approvalRepository.findById(approvalNo).orElse(null);
			if (approval != null) {
				if (!approval.getApprovalStatus().equals("F")) {
					approval.setApprovalStatus("F");
					approvalRepository.save(approval);
				}
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		return result;
	}



}
