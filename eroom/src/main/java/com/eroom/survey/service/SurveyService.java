package com.eroom.survey.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.survey.dto.SurveyDto;
import com.eroom.survey.dto.SurveyItemDto;
import com.eroom.survey.entity.Survey;
import com.eroom.survey.entity.SurveyItem;
import com.eroom.survey.repository.SurveyItemRepository;
import com.eroom.survey.repository.SurveyRepository;
import com.eroom.survey.repository.SurveyVoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final SurveyItemRepository surveyItemRepository;
	private final SurveyVoteRepository surveyVoteRepository;

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
	
	// 응답자 수 조회
	public List<SurveyDto> findAllSurveyWithVoterCount() {
	    List<Survey> surveys = surveyRepository.findAll();
	    List<SurveyDto> dtoList = new ArrayList<>();

	    for (Survey survey : surveys) {
	        SurveyDto dto = new SurveyDto().toDto(survey);
	        int voterCount = surveyVoteRepository.countDistinctVotersBySurveyNo(survey.getSurveyNo());
	        dto.setVoterCount(voterCount);
	        dtoList.add(dto);
	    }

	    return dtoList;
	}

}
