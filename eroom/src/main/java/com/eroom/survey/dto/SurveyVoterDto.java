package com.eroom.survey.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.eroom.survey.entity.SurveyVoter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SurveyVoterDto {
	private Long voterNo;
	private Long surveyNo;
	private List<Long> selectedTeam;     // 폼에서 선택된 팀 ID
	private Long voter;

	// 단일 DTO → 단일 엔티티로 변환
	public SurveyVoter toEntity() {
	    return SurveyVoter.builder()
	            .surveyNo(surveyNo)
	            .voter(voter)
	            .build();
	}

	// 단일 엔티티 → DTO 변환
	public SurveyVoterDto toDto(SurveyVoter entity) {
		return SurveyVoterDto.builder()
				.voterNo(entity.getVoterNo())
				.surveyNo(entity.getSurveyNo())
				.voter(entity.getVoter())
				.build();
	}

}
