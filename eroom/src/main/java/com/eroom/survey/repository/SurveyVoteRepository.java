package com.eroom.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.survey.entity.SurveyVote;

public interface SurveyVoteRepository extends JpaRepository<SurveyVote, Long>{
	// 중복 투표 여부 확인 메서드
    boolean existsBySurveyNoAndItemNoAndVoter(Long surveyNo, Long itemNo, Long voter);

	List<SurveyVote> findByItemNo(Long itemNo);
	
	@Query("SELECT sv.itemNo FROM SurveyVote sv WHERE sv.surveyNo = :surveyNo AND sv.voter = :voterId")
	List<Long> findItemBySurveyNoAndVoter(@Param("surveyNo") Long surveyNo, @Param("voterId") Long voterId);

	int countByItemNo(Long itemNo);

	@Modifying
	@Query("DELETE FROM SurveyVote v WHERE v.surveyNo = :surveyNo AND v.voter = :voter")
	void deleteBySurveyNoAndVoter(@Param("surveyNo") Long surveyNo, @Param("voter") Long voter);
	
	@Query("SELECT COUNT(DISTINCT v.voter) FROM SurveyVote v WHERE v.surveyNo = :surveyNo")
	int countDistinctVotersBySurveyNo(@Param("surveyNo") Long surveyNo);

}
