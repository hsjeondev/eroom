package com.eroom.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.survey.entity.Survey;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
	
}
