package com.eroom.survey.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.survey.repository.SurveyItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyItemService {
	
	private final SurveyItemRepository surveyItemRepository;
	
	public List<String> findItemsBySurveyNo(Long surveyNo) {
		return surveyItemRepository.findItemsBySurveyNo(surveyNo);
	}
}
