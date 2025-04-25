package com.eroom.approval.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.repository.ApprovalLineRepository;
import com.eroom.approval.repository.ApprovalRepository;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalLineService {

	private final ApprovalLineRepository approvalLineRepository;
	private final ApprovalRepository approvalRepository;
	private final EmployeeService employeeService;
	
	// 결재 번호로 결재 라인 조회
	public List<ApprovalLine> getApprovalLineByApprovalNo(Long approvalNo) {
		List<ApprovalLine> resultList = approvalLineRepository.findApprovalLines(approvalNo);
//		for (ApprovalLine app : resultList) {
//			System.out.println(app.getApprovalLineNo());
//		}
		return resultList;
	}

	
	// 회원 번호로 결재 라인 조회
	public List<ApprovalLine> getApprovalLineByEmployeeNo(Long employeeNo) {
		List<ApprovalLine> resultList = approvalLineRepository.findApprovalLinesByEmployee_EmployeeNo(employeeNo);
		return resultList;
	}

	// 결재라인 합의자라인의 승인,반려 처리
	@Transactional(rollbackFor = Exception.class)
	public int approvalLineApproveDeny(ApprovalLine approvalLine) {
		int result = 0;
		try {
			Long approvalNo = approvalLine.getApproval().getApprovalNo();
			Long employeeNo = approvalLine.getEmployee().getEmployeeNo();
			
			System.out.println("결재 번호 : " + approvalNo);
			System.out.println("내 직원번호 나와야함 2 : " + employeeNo);
			
			ApprovalLine appLine = approvalLineRepository.findByApproval_ApprovalNoAndEmployee_EmployeeNo(approvalNo, employeeNo);
			System.out.println("결재 번호 : " + appLine.getApproval().getApprovalNo());
			System.out.println("내 직원번호 나와야함 2 : " + appLine.getEmployee().getEmployeeNo());
			appLine.setApprovalLineSignedDate(LocalDateTime.now());
			appLine.setApprovalLineStatus(approvalLine.getApprovalLineStatus());
			System.out.println("이거이거 A 맞아?" + appLine.getApprovalLineStatus());
			if (appLine != null) {
				appLine.setApprovalLineStatus(approvalLine.getApprovalLineStatus());
				approvalLineRepository.save(appLine);
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
			throw e;
		}
		return result;
	}

	public Boolean isMyLineApproval(Long approval_no, Long employee_no) {
		Boolean isMyLineApproval = false;
		List<ApprovalLine> approvalLines = approvalLineRepository.findApprovalLines(approval_no);
		for (ApprovalLine app : approvalLines) {
			if (app.getEmployee().getEmployeeNo() == employee_no) {
				isMyLineApproval = true;
				break;
			}
		}
		return isMyLineApproval;
	}

	
	
}
