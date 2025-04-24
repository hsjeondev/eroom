package com.eroom.survey.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.survey.dto.SurveyDto;
import com.eroom.survey.dto.SurveyItemDto;
import com.eroom.survey.entity.Survey;
import com.eroom.survey.entity.SurveyItem;
import com.eroom.survey.repository.SurveyItemRepository;
import com.eroom.survey.repository.SurveyRepository;
import com.eroom.survey.repository.SurveyVoteRepository;
import com.eroom.survey.repository.SurveyVoterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final SurveyItemRepository surveyItemRepository;

	public Long saveSurvey(SurveyDto surveyDto, SurveyItemDto surveyItemDto) {
		Long result = null;
		try {
			Survey savedSurvey = surveyRepository.save(surveyDto.toEntity());
			Long surveyNo = savedSurvey.getSurveyNo();

			for (String item : surveyItemDto.getItems()) {
				SurveyItem surveyItem = SurveyItem.builder().item(item).surveyNo(surveyNo).build();
				surveyItemRepository.save(surveyItem);
			}
			result = surveyNo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Survey> findAllSurvey() {
		return surveyRepository.findAll();
	}

}
