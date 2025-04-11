package com.eroom.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.survey.dto.SurveyDto;
import com.eroom.survey.dto.SurveyItemDto;
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
	public String createSurvey(@ModelAttribute SurveyDto surveyDto,
	                           @ModelAttribute SurveyItemDto surveyItemDto) {
	    // SurveyDto 값 확인
	    System.out.println("제목: " + surveyDto.getSurveyTitle());
	    System.out.println("복수 선택: " + surveyDto.getAllowMultiple());
	    System.out.println("익명 투표: " + surveyDto.getAnonymousVote());
	    System.out.println("마감일: " + surveyDto.getDeadline());

	    // SurveyItemDto 값 확인
	    System.out.println("항목 리스트:");
	    for (String item : surveyItemDto.getItems()) {
	        System.out.println("- " + item);
	    }
	    
	    int result = surveyService.saveSurvey(surveyDto, surveyItemDto);
	    
	    return "survey/list";
	}


}
