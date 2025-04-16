package com.eroom.approval.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApprovalPdfController {
	
	private final TemplateEngine templateEngine;
	
	@GetMapping("/pdf")
	public ResponseEntity<byte[]> generateApprovalPdf() {
	    Context context = new Context();
	    context.setVariable("employeeName", "홍길동");
	    context.setVariable("title", "경조금 신청");

	    String html = templateEngine.process("approval/detailPdfTemplate", context);

	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    Map<String, String> body = new HashMap<>();
	    body.put("html", html);

	    HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
	    ResponseEntity<byte[]> response = restTemplate.postForEntity(
	        "http://localhost:3001/generate-pdf", entity, byte[].class
	    );

	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=approvalDetail.pdf")
	        .contentType(MediaType.APPLICATION_PDF)
	        .body(response.getBody());
	}

}
