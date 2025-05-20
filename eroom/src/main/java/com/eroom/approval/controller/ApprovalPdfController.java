package com.eroom.approval.controller;

// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.entity.ApprovalSignature;
import com.eroom.approval.service.ApprovalLineService;
import com.eroom.approval.service.ApprovalService;
import com.eroom.approval.service.ApprovalSignatureService;
import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.service.DriveService;
import com.eroom.employee.entity.Employee;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApprovalPdfController {
	
	private final TemplateEngine templateEngine;
	private final ApprovalService approvalService;
	private final ApprovalLineService approvalLineService;
	private final DriveService driveService;
	private final ApprovalSignatureService approvalSignatureService;
	
	@GetMapping("/approval/{approvalNo}/pdf")
	public ResponseEntity<byte[]> generateApprovalPdf(Authentication authentication, @PathVariable("approvalNo") Long approvalNo) {
		// Model 역할을 하는 context 생성.
		Context context = new Context();
		
		
		
//		ApprovalController의 selectApprovalDetail메소드와 동일 로직. 컨트롤러끼리 메소드를 불러오면 구조 이상.
//		----------------------------------------------------------------
		// 선택한 결재 번호로 결재 정보 조회
		Approval approval = approvalService.selectApprovalByApprovalNo(approvalNo);
		// 결재 정보가 없으면 404 에러 페이지로 이동
		if (approval == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "결재 정보 없음");
		}
		ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
		context.setVariable("approval", approvalDto);
		
		// 로그인한 사용자 정보 조회
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		context.setVariable("employee", employee);
		
		// 결재 라인 정보 조회
		List<ApprovalLine> temp = approvalLineService.getApprovalLineByApprovalNo(approvalNo);
		List<ApprovalLineDto> approvalLineDtoList = new ArrayList<ApprovalLineDto>();
		for (ApprovalLine approvalLine : temp) {
			ApprovalLineDto approvalLineDto = new ApprovalLineDto().toDto(approvalLine);
			ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(approvalLineDto.getEmployee());
			if(approvalSignature != null) {
				String encodedBase64 = approvalSignatureService.encodeToBase64(approvalSignature.getApprovalSignatureBlob());
				approvalLineDto.setBase64URL(encodedBase64);
			}
			approvalLineDtoList.add(approvalLineDto);
		} 
		context.setVariable("approvalLineList", approvalLineDtoList);
		
		// 권한 리스트 조회
		Set<Long> authorityList = new HashSet<Long>();
		for(ApprovalLine t : temp) {
			// 결재자, 합의자, 참조자 중 결재라인에 있는 사람들
			authorityList.add(t.getEmployee().getEmployeeNo());
		}
		// 기안자
		authorityList.add(approval.getEmployee().getEmployeeNo());
		
		// 결재 관련 인원 + 관리자만 접근 가능
		if (!authorityList.contains(employee.getEmployeeNo()) && !employee.getEmployeeName().contains("admin")) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "권한 없음");
	    }
		
		String strTemp = String.valueOf(approvalDto.getApproval_no());
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 8 - strTemp.length(); i++) {
			sb.append("0");
		}
		sb.append(strTemp);
		String approvalNoFormatted = "FL-007-" + sb.toString();
		context.setVariable("approvalNoFormatted", approvalNoFormatted); 
		
//		파일 조회 - 해당 결재글이 드라이브의 param1에 들어있어야함.
		List<DriveDto> driveList = driveService.findApprovalDriveFiles(approvalNo);
		context.setVariable("driveList", driveList);
//		파일 조회 - 해당 결재글이 드라이브의 param1에 들어있어야함.
//		----------------------------------------------------------------
		
		
		
		
		
//	    context.setVariable("employeeName", "홍길동");
//	    context.setVariable("title", "경조금 신청");

//	    String html = templateEngine.process("approval/detailPdfTemplate", context);
	    String html = templateEngine.process("approval/templatePdf/detailPdfTemplate", context);

	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    Map<String, String> body = new HashMap<>();
	    body.put("html", html);

	    HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
	    ResponseEntity<byte[]> response = restTemplate.postForEntity(
//	    		"http://pdf-server:3001/generate-pdf", entity, byte[].class
	    		"http://localhost:3001/generate-pdf", entity, byte[].class
	    );
	    
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=approvalDetail.pdf")
	        .contentType(MediaType.APPLICATION_PDF)
	        .body(response.getBody());
	}

}
