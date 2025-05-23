package com.eroom.survey.service;

import org.springframework.stereotype.Service;

import com.eroom.survey.dto.SurveyVoterDto;
import com.eroom.survey.repository.SurveyVoterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyVoterService {
	private final SurveyVoterRepository surveyVoterRepository;
	
	public int saveSurveyVoters(Long surveyNo, Long employeeNo) {
		int result = 0;
		try {
			SurveyVoterDto voter = new SurveyVoterDto();
			voter.setSurveyNo(surveyNo);
			voter.setVoter(employeeNo);
			
			surveyVoterRepository.save(voter.toEntity());
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }
}
