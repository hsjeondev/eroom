package com.eroom.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.survey.entity.SurveyVoter;

public interface SurveyVoterRepository extends JpaRepository<SurveyVoter, Long> {

	boolean existsBySurveyNoAndVoter(Long surveyId, Long voterNo);
	
}
