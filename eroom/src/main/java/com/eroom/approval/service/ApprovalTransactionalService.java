package com.eroom.approval.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalTransactionalService {
	
	private final ApprovalService approvalService;
	private final ApprovalLineService approvalLineService;
	private final EmployeeService employeeService;
	
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> approvalApproveDenyTransaction(ApprovalLineDto dto, Long employeeNo) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> delimeterMap = new HashMap<String, String>();
		String delimeter = String.valueOf(dto.getDelimeter());
		delimeterMap.put("0", "합의");
		delimeterMap.put("1", "결재");
		map.put("res_code", "500");
		map.put("res_msg", delimeterMap.get(delimeter)+ " 승인 실패");
		
		
		ApprovalLine approvalLine = dto.toEntity();
		Employee employee = employeeService.findEmployeeByEmployeeNo(dto.getEmployee_no());
		Approval approval = approvalService.selectApprovalByApprovalNo(dto.getApproval_no());
		approvalLine.setApproval(approval);
		if (employee == null) {
			throw new IllegalStateException("Employee is null");
		}
		approvalLine.setEmployee(employee);
		int result = approvalLineService.approvalLineApproveDeny(approvalLine);
		String res_msg_success = "";
		String res_msg_fail = "";
		List<ApprovalLine> approvalLines = approvalLineService.getApprovalLineByApprovalNo(dto.getApproval_no());
		int cnt = 0;
		int myApprovalLineStep = -1;
		Boolean isFinalApprovalLineisMe = false;
		for(int i = 0; i < approvalLines.size(); i++) {
			if(approvalLines.get(i).getApprovalLineStep() >= 1) {
				cnt++;
			}
			if(approvalLines.get(i).getEmployee().getEmployeeNo() == employeeNo) {
				myApprovalLineStep = approvalLines.get(i).getApprovalLineStep();
			}
		}
		
		int approveResult = 0;
		int denyResult = 0;
		if(cnt == myApprovalLineStep) {
			isFinalApprovalLineisMe = true;
		}
		
		if(dto.getApproval_line_status().equals("A")) {
			approveResult = approvalService.approvalApproveDeny(approvalLine, isFinalApprovalLineisMe);
			res_msg_success = delimeterMap.get(delimeter)+ " 승인 성공";
			res_msg_fail = delimeterMap.get(delimeter)+ " 승인 실패(서버 오류)";
		} else if(dto.getApproval_line_status().equals("D")) {
			denyResult = approvalService.approvalApproveDeny(approvalLine, isFinalApprovalLineisMe);
			res_msg_success = delimeterMap.get(delimeter)+ " 반려 성공";
			res_msg_fail = delimeterMap.get(delimeter)+ " 반려 실패(서버 오류)";
		}
		
		cnt = 0;
		isFinalApprovalLineisMe = false;
//		System.out.println(result + "result");
//		System.out.println(approveResult + "approveResult");
//		System.out.println(denyResult + "denyResult");
		if(result > 0 && (approveResult > 0 || denyResult > 0)) {
			map.put("res_code", "200");
			map.put("res_msg", res_msg_success);
		} else {
			map.put("res_code", "500");
			map.put("res_msg", res_msg_fail);
		}
		
		
		return map;
	}

}
