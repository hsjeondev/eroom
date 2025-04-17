package com.eroom.approval.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.approval.entity.Approval;
import com.eroom.approval.repository.ApprovalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalRepository approvalRepository;

	public List<Approval> getMyRequestedApprovals(Long employeeNo) {
		List<Approval> resultList = approvalRepository.findByEmployee_EmployeeNo(employeeNo);
		return resultList;
	}

}
