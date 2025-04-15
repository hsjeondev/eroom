package com.eroom.survey.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.survey.dto.SurveyDto;
import com.eroom.survey.dto.SurveyItemDto;
import com.eroom.survey.entity.Survey;
import com.eroom.survey.entity.SurveyItem;
import com.eroom.survey.repository.SurveyItemRepository;
import com.eroom.survey.repository.SurveyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final SurveyItemRepository surveyItemRepository;

	public int saveSurvey(SurveyDto surveyDto, SurveyItemDto surveyItemDto) {
		int result = 0;
		try {
			Survey savedSurvey = surveyRepository.save(surveyDto.toEntity());
			Long surveyNo = savedSurvey.getSurveyNo();
			
			for(String item : surveyItemDto.getItems()) {
				SurveyItem surveyItem = SurveyItem.builder()
						.item(item)
						.surveyNo(surveyNo)
						.build();
				surveyItemRepository.save(surveyItem);
			}
			
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Survey> findAllSurvey() {
		return surveyRepository.findAll();
	}

}
