package com.eroom.report.controller;

import java.io.ByteArrayOutputStream;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eroom.report.service.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

	// ReportService를 의존성 주입해서 사용
	private final ReportService reportService;
	
	// 생성자 기반 주입(Spring이 자동으로 주입해줌)
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}
	
	// GET 요청으로 sample pdf 리포트를 다운로드할 수 있게 하는 API
//	@GetMapping("/sample/pdf")
//	public ResponseEntity<byte[]> downloadSamplePdf() throws Exception{
//		// Service 에서 PDF 리포트 데이터를 받아옴
//		ByteArrayOutputStream baos = reportService.generateSimplePdfReport();
//		
//		// HTTP 응답 헤더 설정
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_PDF); // Content-Type : application/pdf
//		headers.setContentDisposition(ContentDisposition.attachment().filename("sample_report.pdf").build()); // 다운로드용
//		
//		// 실제 PDF 바이트 배열, 헤더, 상태코드를 함꼐 응답으로 보냄
//		return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
//	}
	
}
