package com.eroom.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.survey.entity.Survey;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

	Survey findBySurveyNo(Long surveyId);
	
	@Query("SELECT s FROM Survey s WHERE s.visible = 'Y'")
	List<Survey> findAllVisibleSurvey();
	
	@Modifying
	@Query("UPDATE Survey s SET s.visible = 'N' WHERE s.surveyNo = :surveyNo")
	int updateVisible(@Param("surveyNo") Long surveyNo);
}
