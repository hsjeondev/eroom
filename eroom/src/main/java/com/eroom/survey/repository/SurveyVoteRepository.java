package com.eroom.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.survey.entity.SurveyVote;

public interface SurveyVoteRepository extends JpaRepository<SurveyVote, Long>{
	// 중복 투표 여부 확인 메서드
    boolean existsBySurveyNoAndItemNoAndVoter(Long surveyNo, Long itemNo, Long voter);
}
