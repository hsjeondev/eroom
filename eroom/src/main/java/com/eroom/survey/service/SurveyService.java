package com.eroom.survey.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
			Survey surveyEntity = surveyDto.toEntity();
			List<SurveyItem> itemEntity = surveyItemDto.toEntityList();
			
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
