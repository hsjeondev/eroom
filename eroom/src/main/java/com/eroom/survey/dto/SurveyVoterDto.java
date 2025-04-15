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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SurveyVoterDto {
	private Long voterNo;
	private Long surveyNo;
	private List<Long> voters;
	
	public List<SurveyVoter> toEntityList() {
		return voters.stream()
				.map(voterStr -> SurveyVoter.builder()
						.surveyNo(surveyNo)
						.voter(voterStr)
						.build())
				.collect(Collectors.toList());
	}
	
	public SurveyVoterDto toDto(SurveyVoter entity) {
		return SurveyVoterDto.builder()
				.voterNo(entity.getVoterNo())
				.surveyNo(entity.getSurveyNo())
				.voters(List.of(entity.getVoter()))
				.build();
	}
}
