package com.eroom.approval.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.service.ApprovalService;
import com.eroom.employee.entity.Employee;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ApprovalController {
	
	private final ApprovalService approvalService;
	
	@GetMapping("/approval/myRequestedApprovals")
	public String selectMyRequestedApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
		// 내가 올린 approval 리스트 조회
		List<Approval> temp = approvalService.getMyRequestedApprovals(employee.getEmployeeNo());
		List<ApprovalDto> resultList = new ArrayList<ApprovalDto>();
		for (Approval approval : temp) {
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(approval);
			resultList.add(dto);
		}
		model.addAttribute("resultList", resultList);
		
		return "/approval/myRequestedApprovals";
	}
	@GetMapping("/approval/agreementApprovals")
	public String selectAgreementApprovalsList() {
		return "/approval/agreementApprovals";
	}
	@GetMapping("/approval/receivedApprovals")
	public String selectReceivedApprovalsList() {
		return "/approval/receivedApprovals";
	}
	@GetMapping("/approval/referencedApprovals")
	public String selectReferencedApprovalsList() {
		return "/approval/referencedApprovals";
	}
	@GetMapping("/approval/withdrawnApprovals")
	public String selectWithdrawnApprovalsList() {
		return "/approval/withdrawnApprovals";
	}
	@GetMapping("/approval/create")
	public String selectApprovalCreate() {
		
		return "/approval/create";
	}
	@GetMapping("/approval/detail")
	public String selectApprovalDetail() {
		
		return "/approval/detail";
	}
	
	

	

}
