package com.eroom.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eroom.survey.entity.SurveyItem;

@Repository
public interface SurveyItemRepository extends JpaRepository<SurveyItem, Long>{
	
}
