package com.eroom.approval.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
		return resultList;
	}
}
