package com.eroom.approval.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.repository.ApprovalLineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalLineService {

	private final ApprovalLineRepository approvalLineRepository;
	
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


	@Transactional
	public int approvalLineApproveDeny(ApprovalLine approvalLine) {
		int result = 0;
		try {
			Long approvalNo = approvalLine.getApproval().getApprovalNo();
			Long employeeNo = approvalLine.getEmployee().getEmployeeNo();
			ApprovalLine appLine = approvalLineRepository.findByApproval_ApprovalNoAndEmployee_EmployeeNo(approvalNo, employeeNo);
			if (appLine != null) {
				appLine.setApprovalLineStatus(approvalLine.getApprovalLineStatus());
				approvalLineRepository.save(appLine);
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
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
