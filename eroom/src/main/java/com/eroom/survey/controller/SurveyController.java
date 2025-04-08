package com.eroom.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.eroom.survey.dto.SurveyDto;
import com.eroom.survey.service.SurveyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SurveyController {
	private final SurveyService surveyService;
	
	@GetMapping("/survey")
	public String survey() {
		return "survey/list";
	}
	
	@PostMapping("/survey/create")
	public String createSurvey(SurveyDto surveyDto) {
	    //surveyService.createSurvey(surveyDto); // 서비스에서 DB 저장 처리
	    return "redirect:/survey"; // 목록 페이지로 리다이렉트
	}

}
