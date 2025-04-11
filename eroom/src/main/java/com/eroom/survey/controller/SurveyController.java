package com.eroom.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.survey.dto.SurveyDto;
import com.eroom.survey.service.SurveyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyController {
	private final SurveyService surveyService;
	
	@GetMapping("/list")
	public String surveyList() {
		return "survey/list";
	}
	
	@GetMapping("/ongoing")
	public String surveyOngoing() {
		return "survey/ongoing";
	}
	
	@GetMapping("/closed")
	public String surveyClosed() {
		return "survey/closed";
	}
	
	@PostMapping("/create")
	public String saveSurvey(SurveyDto surveyDto) {
	    System.out.println("DTO 내용: " + surveyDto);

	    return "redirect:/survey/list";
	}

}
