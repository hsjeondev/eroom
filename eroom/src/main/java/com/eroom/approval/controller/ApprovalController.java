package com.eroom.approval.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;
import java.util.Locale;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Controller
@RequiredArgsConstructor
public class ApprovalController {
	
	private final TemplateEngine templateEngine;
	
	@GetMapping("/approval/agreementApprovals")
	public String selectAgreementApprovalsList() {
		return "/approval/agreementApprovals";
	}
	@GetMapping("/approval/myRequestedApprovals")
	public String selectMyRequestedApprovalsList() {
		return "/approval/myRequestedApprovals";
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
