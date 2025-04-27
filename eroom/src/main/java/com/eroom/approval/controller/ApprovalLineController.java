package com.eroom.approval.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.service.ApprovalFormatService;
import com.eroom.approval.service.ApprovalLineService;
import com.eroom.approval.service.ApprovalService;
import com.eroom.approval.service.ApprovalTransactionalService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ApprovalLineController {

	private final ApprovalService approvalService;
	private final ApprovalLineService approvalLineService;
	private final ApprovalFormatService approvalFormatService;
	private final StructureService structureService;
	private final EmployeeService employeeService;
	private final ApprovalTransactionalService approvalTransactionalService;
	
	// 결재 승인/반려
	@PostMapping("/approvalLine/approveDenyAgreement")
	@ResponseBody
	public Map<String, String> ApprovalApproveDeny(Authentication authentication, @RequestBody ApprovalLineDto dto){
//		System.out.println(dto.getApproval_no());
//		System.out.println(dto.getApproval_line_status());
//		System.out.println(dto.getEmployee_no());
		
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		Map<String, String> map = new HashMap<String, String>();
		if (dto == null || dto.getApproval_no() == null || dto.getEmployee_no() == null) {
			map.put("res_code", "500");
			map.put("res_msg", "페이지오류. 관리자에게 문의하세요.");
			return map;
		}
		
//		Map<Integer, String> delimeterMap = new HashMap<Integer, String>();
//		int delimeter = dto.getDelimeter();
//		delimeterMap.put(0, "합의");
//		delimeterMap.put(1, "결재");
//		map.put("res_code", "500");
//		map.put("res_msg", delimeterMap.get(delimeter)+ " 승인 실패");
		
		Boolean isMyReceived = approvalLineService.isMyLineApproval(dto.getApproval_no(), dto.getEmployee_no());
		if (!isMyReceived) {
			map.put("res_code", "500");
			map.put("res_msg", "실패(권한 없음)");
		} else {
//			ApprovalLine approvalLine = dto.toEntity();
			// 결재 라인에서 나의 승인/반려 반영
			// 트랜잭션 필요
//			int result = approvalLineService.approvalLineApproveDeny(approvalLine);
//			String res_msg_success = "";
//			String res_msg_fail = "";
			
			// 결재자 중 마지막 결재자이면 결재 완료 처리
			// 결재 번호로 결재 라인 조회
//			List<ApprovalLine> approvalLines = approvalLineService.getApprovalLineByApprovalNo(dto.getApproval_no());
//			int cnt = 0;
//			int myApprovalLineStep = -1;
//			Boolean isFinalApprovalLineisMe = false;
//			for(int i = 0; i < approvalLines.size(); i++) {
//				if(approvalLines.get(i).getApprovalLineStep() >= 1) {
//					cnt++;
//				}
//				if(approvalLines.get(i).getEmployee().getEmployeeNo() == employeeNo) {
//					myApprovalLineStep = approvalLines.get(i).getApprovalLineStep();
//				}
//			}
//			
//			int approveResult = 0;
//			int denyResult = 0;
//			if(cnt == myApprovalLineStep) {
//				isFinalApprovalLineisMe = true;
//			}
			
			// 트랜잭션 필요
			// 트랜잭션 필요
			Map<String, String> mapTemp = approvalTransactionalService.approvalApproveDenyTransaction(dto, employeeNo);
			// 트랜잭션 필요
			// 트랜잭션 필요
			// 트랜잭션 필요
			
//			if(dto.getApproval_line_status().equals("A")) {
//				approveResult = approvalService.approvalApproveDeny(approvalLine, isFinalApprovalLineisMe);
//				res_msg_success = delimeterMap.get(delimeter)+ " 승인 성공";
//				res_msg_fail = delimeterMap.get(delimeter)+ " 승인 실패(서버 오류)";
//			} else if(dto.getApproval_line_status().equals("D")) {
//				denyResult = approvalService.approvalApproveDeny(approvalLine, isFinalApprovalLineisMe);
//				res_msg_success = delimeterMap.get(delimeter)+ " 반려 성공";
//				res_msg_fail = delimeterMap.get(delimeter)+ " 반려 실패(서버 오류)";
//			}
//			
//			cnt = 0;
//			isFinalApprovalLineisMe = false;
//			System.out.println(approveResult + "approveResult");
//			System.out.println(denyResult + "denyResult");
//			if(result > 0 && (approveResult > 0 || denyResult > 0)) {
//				map.put("res_code", "200");
//				map.put("res_msg", res_msg_success);
//			} else {
//				map.put("res_code", "500");
//				map.put("res_msg", res_msg_fail);
//			}
//		}
		
		return mapTemp;
		
	}
//	// 결재 합의/거절
//	@PostMapping("/approvalLine/approveDeny")
//	@ResponseBody
//	public Map<String, String> Approvalagreement(Authentication authentication, @RequestBody ApprovalLineDto dto){
////		System.out.println(dto.getApproval_no());
////		System.out.println(dto.getApproval_line_status());
////		System.out.println(dto.getEmployee_no());
//		
//		// 로그인한 사용자 정보 가져오기
//		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
//		Employee employee = employeeDetails.getEmployee();
//		Long employeeNo = employee.getEmployeeNo();
//		
//		
//		
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("res_code", "500");
//		map.put("res_msg", "결재 합의 실패");
//		
//		Boolean isMyAgreemented = approvalLineService.isMyLineApproval(dto.getApproval_no(), dto.getEmployee_no());
//		if (!isMyAgreemented) {
//			map.put("res_code", "500");
//			map.put("res_msg", "결재 합의 실패(권한 없음)");
//		} else {
//			ApprovalLine approvalLine = dto.toEntity();
//			int result = approvalLineService.approvalLineApproveDeny(approvalLine);
//			String res_msg_success = "";
//			String res_msg_fail = "";
//			
//			// 결재자 중 마지막 결재자이면 결재 완료 처리
//			// 결재 번호로 결재 라인 조회
//			List<ApprovalLine> approvalLines = approvalLineService.getApprovalLineByApprovalNo(dto.getApproval_no());
//			int cnt = 0;
//			int myApprovalLineStep = -1;
//			Boolean isFinalApprovalLineisMe = false;
//			for(int i = 0; i < approvalLines.size(); i++) {
//				if(approvalLines.get(i).getApprovalLineStep() >= 1) {
//					cnt++;
//				}
//				if(approvalLines.get(i).getEmployee().getEmployeeNo() == employeeNo) {
//					myApprovalLineStep = approvalLines.get(i).getApprovalLineStep();
//				}
//			}
//			
//			int approveResult = 0;
//			int denyResult = 0;
//			if(cnt == myApprovalLineStep) {
//				isFinalApprovalLineisMe = true;
//			}
//			
//			if(dto.getApproval_line_status().equals("A")) {
//				approveResult = approvalService.approvalApproveDeny(approvalLine, isFinalApprovalLineisMe);
//				res_msg_success = "결재 합의 성공";
//				res_msg_fail = "결재 합의 실패(서버1 오류)";
//			} else if(dto.getApproval_line_status().equals("D")) {
//				denyResult = approvalService.approvalApproveDeny(approvalLine, isFinalApprovalLineisMe);
//				res_msg_success = "합의 거절 성공";
//				res_msg_fail = "합의 거절 실패(서버2 오류)";
//			}
//			
//			cnt = 0;
//			isFinalApprovalLineisMe = false;
//			
//			if(result > 0 && (approveResult > 0 || denyResult > 0)) {
//				map.put("res_code", "200");
//				map.put("res_msg", res_msg_success);
//			} else {
//				map.put("res_code", "500");
//				map.put("res_msg", res_msg_fail);
//			}
//		}
//		
		return map;
		
	}
	
}
