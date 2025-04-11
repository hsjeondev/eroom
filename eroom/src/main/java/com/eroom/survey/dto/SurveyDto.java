package com.eroom.survey.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eroom.survey.entity.Survey;

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
public class SurveyDto {
	private Long surveyNo;
	private String surveyTitle;
	private LocalDate deadline;
    private String anonymousVote = "N";
    private String allowMultiple = "N";
	private Long employeeNo;
	private LocalDateTime regDate;
	
	public Survey toEntity() {
		return Survey.builder()
				.surveyNo(surveyNo)
				.surveyTitle(surveyTitle)
				.deadline(deadline)
				.anonymousVote(anonymousVote != null ? anonymousVote : "N")
				.allowMultiple(allowMultiple != null ? allowMultiple : "N")
				.employeeNo(employeeNo)
				.build();
	}
	
	public SurveyDto toDto(Survey survey) {
		return SurveyDto.builder()
				.surveyNo(survey.getSurveyNo())
				.surveyTitle(survey.getSurveyTitle())
				.deadline(survey.getDeadline())
				.anonymousVote(survey.getAnonymousVote())
				.allowMultiple(survey.getAllowMultiple())
				.employeeNo(survey.getEmployeeNo())
				.build();
	}
}
