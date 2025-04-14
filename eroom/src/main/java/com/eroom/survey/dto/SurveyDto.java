package com.eroom.survey.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
    private String anonymousVote = "off";
    private String allowMultiple = "off";
	private Long employeeNo;
	@CreationTimestamp
	private LocalDateTime regDate;
	
	public Survey toEntity() {
		return Survey.builder()
				.surveyNo(surveyNo)
				.surveyTitle(surveyTitle)
				.deadline(deadline)
				.anonymousVote(anonymousVote)
				.allowMultiple(allowMultiple)
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
