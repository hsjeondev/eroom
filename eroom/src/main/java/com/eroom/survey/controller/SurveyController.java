package com.eroom.survey.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.survey.dto.SurveyDto;
import com.eroom.survey.dto.SurveyItemDto;
import com.eroom.survey.entity.Survey;
import com.eroom.survey.service.SurveyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyController {
	private final SurveyService surveyService;

	@GetMapping("/list")
	public String surveyList(Model model, SurveyDto surveyDto, SurveyItemDto surveyItemDto) {
		
		List<Survey> surveyList = surveyService.findAllSurvey();
//		List<Employee> empList = surveyService.findAllEmployee();
		
		model.addAttribute("surveyList", surveyList);
//		model.addAttribute("empList", empList);
		
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
	public String createSurvey(SurveyDto surveyDto, SurveyItemDto surveyItemDto) {
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
