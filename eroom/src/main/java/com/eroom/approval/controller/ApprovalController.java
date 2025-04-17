package com.eroom.approval.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.service.ApprovalFormatService;
import com.eroom.approval.service.ApprovalLineService;
import com.eroom.approval.service.ApprovalService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ApprovalController {
	
	private final ApprovalService approvalService;
	private final ApprovalLineService approvalLineService;
	private final ApprovalFormatService approvalFormatService;
	private final StructureService structureService;
	
	@GetMapping("/approval/myRequestedApprovals")
	public String selectMyRequestedApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
		// 내가 올린 approval 리스트 조회
		List<Approval> temp = approvalService.getMyRequestedApprovals(employee.getEmployeeNo());
		List<ApprovalDto> resultList = new ArrayList<ApprovalDto>();
		Map<Long, List<ApprovalLineDto>> approvalLineMap = new HashMap<Long, List<ApprovalLineDto>>();
		for (Approval approval : temp) {
			// approval 리스트의 approval_no를 사용해서 approval_line 리스트 조회
			List<ApprovalLine> temp2 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
			List<ApprovalLineDto> approvalLineDtoList = new ArrayList<ApprovalLineDto>();
			for (ApprovalLine approvalLine : temp2) {
				ApprovalLineDto approvalLineDto = new ApprovalLineDto().toDto(approvalLine);
				approvalLineDtoList.add(approvalLineDto);
			}
			// approval_line 리스트를 approval_no를 키로 하는 맵에 저장
			approvalLineMap.put(approval.getApprovalNo(), approvalLineDtoList);
			
			
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(approval);
			resultList.add(dto);
		}
		model.addAttribute("approvalLineMap", approvalLineMap);
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
	public String selectApprovalCreate(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
//		System.out.println("employee : " + employee);
		// 결재 양식 리스트 조회
		List<ApprovalFormat> approvalFormatList = approvalFormatService.getApprovalFormatList();
		model.addAttribute("approvalFormatList", approvalFormatList);
		// 부서 정보 조회
		Structure structure = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(employee.getStructure().getParentCode());
        String departmentName = structure != null ? structure.getCodeName() : "-";
        model.addAttribute("departmentName", departmentName);
		
		return "/approval/create";
	}
	@GetMapping("/approval/detail")
	public String selectApprovalDetail() {
		
		return "/approval/detail";
	}
	
	

	

}
