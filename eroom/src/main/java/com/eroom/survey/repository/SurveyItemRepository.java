package com.eroom.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eroom.survey.dto.SurveyItemDto;
import com.eroom.survey.entity.SurveyItem;

@Repository
public interface SurveyItemRepository extends JpaRepository<SurveyItem, Long>{

	@Query("SELECT i FROM SurveyItem i WHERE i.surveyNo = :surveyNo")
	List<SurveyItem> findItemsBySurveyNo(@Param("surveyNo") Long surveyNo);
}
