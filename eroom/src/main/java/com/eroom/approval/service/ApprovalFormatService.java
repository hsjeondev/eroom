package com.eroom.approval.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.repository.ApprovalFormatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalFormatService {
	
	private final ApprovalFormatRepository approvalFormatRepository;

	public List<ApprovalFormat> getApprovalFormatList() {
		List<ApprovalFormat> resultList = approvalFormatRepository.findAll();
		return resultList;
	}

	public ApprovalFormat getApprovalFormat(Long id) {
		ApprovalFormat result = approvalFormatRepository.findById(id).orElse(null);
		return result;
	}
	
	
}
