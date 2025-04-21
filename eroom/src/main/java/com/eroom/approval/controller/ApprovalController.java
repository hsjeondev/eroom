package com.eroom.approval.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.dto.ApprovalFormatDto;
import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.dto.ApprovalRequestDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.service.ApprovalFormatService;
import com.eroom.approval.service.ApprovalLineService;
import com.eroom.approval.service.ApprovalService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
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
	private final EmployeeService employeeService;
	
	// 내가 올린 결재 리스트 조회
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
	// 결재 생성 페이지 진입
	@GetMapping("/approval/create")
	public String selectApprovalCreate(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
//		System.out.println("employee : " + employee);
		// 결재 양식 리스트 조회
		List<ApprovalFormat> temp = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : temp) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
		// 부서 정보 조회
		Structure structure = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(employee.getStructure().getParentCode());
        String departmentName = structure != null ? structure.getCodeName() : "-";
        model.addAttribute("departmentName", departmentName);
        // 현재 날짜
        model.addAttribute("now", DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDateTime.now()));
        // 부서 코드네임 조회
		List<SeparatorDto> structureList = employeeService.findDistinctStructureNames();
		model.addAttribute("structureList", structureList);
		return "/approval/create";
	}
	
	// 결재 생성 결재라인 추가를 위한 조회
	@GetMapping("/approval/employes")
	@ResponseBody
	public List<EmployeeDto> getEmployeesByDepartment(@RequestParam(name = "separator_code") String separatorCode) {
		String temp = separatorCode.substring(0,1);
//		System.out.println(temp + " | substring 자르기 1글자 나와야해");
		if ("T".equals(temp)) {
			// 팀(소속) 선택한 경우: separatorCode 기준 조회
			return employeeService.findEmployeesByStructureName(separatorCode);
		} else {
			// 부서를 선택한 경우: parentCode 기준 조회
			return employeeService.findEmployeesByParentCode(separatorCode);
		}
	}
	
	// 결재 생성 양식 리스트 조회
	@PostMapping("/approval/format")
	@ResponseBody
	public Map<String, String> selectApprovalFormat(@RequestBody ApprovalFormatDto dto) {
		// 결재 양식 조회
		ApprovalFormat approvalFormat = approvalFormatService.getApprovalFormat(dto.getApproval_format_no());
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "양식 조회 실패");
		if(approvalFormat != null) {
			map.put("res_code", "200");
			map.put("res_msg", "양식 조회 성공");
			ApprovalFormatDto approvalFormatDto = new ApprovalFormatDto().toDto(approvalFormat);
			map.put("approvalFormatContent", approvalFormatDto.getApproval_format_content());
			map.put("approvalFormatTitle", approvalFormatDto.getApproval_format_title());
		}
		return map;
	}
	
	// 결재 생성 라인 추가
	@PostMapping("/approval/addApprovalLine")
	@ResponseBody
	public Map<String, String> addApprovalLine(@RequestBody ApprovalLineDto dto) {
		// 결재 라인 추가
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "결재 라인 추가 실패");
//		if() {
//			map.put("res_code", "200");
//			map.put("res_msg", "결재 라인 추가 성공");
//		}
		return map;
		
	}
	
	// 결재 생성 Create
	@PostMapping("/approval/create")
	@ResponseBody
	public Map<String, String> createApproval(@RequestBody ApprovalRequestDto dto, Authentication authentication) {
		Map<String, String> map = new HashMap<String, String>();

		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		int result = approvalService.createApproval(dto, employeeNo);
		
		
		map.put("res_code", "500");
		map.put("res_msg", "결재 생성 실패");
		if (result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "결재 생성 성공");
		}
		return map;

	}
	
	@GetMapping("/approval/detail")
	public String selectApprovalDetail() {
		
		return "/approval/detail";
	}
	
	

	

}
