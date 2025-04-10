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
	public String saveSurvey(SurveyDto surveyDto) {
	    System.out.println("DTO 내용: " + surveyDto);

	    return "redirect:/survey/list";
	}

}
