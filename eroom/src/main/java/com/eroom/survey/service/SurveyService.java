package com.eroom.survey.service;

import org.springframework.stereotype.Service;

import com.eroom.survey.repository.SurveyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {
	private final SurveyRepository surveyRepository;
}
