package com.eroom.survey.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

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
	private String writer;
	private String surveyTitle;
	private LocalDateTime deadline;
    private String anonymousVote = "off";
    private String allowMultiple = "off";
	@CreationTimestamp
	private LocalDateTime regDate;
	
	public Survey toEntity() {
		return Survey.builder()
				.surveyNo(surveyNo)
				.writer(writer)
				.surveyTitle(surveyTitle)
				.deadline(deadline)
				.anonymousVote(anonymousVote)
				.allowMultiple(allowMultiple)
				.build();
	}
	
	public SurveyDto toDto(Survey survey) {
		return SurveyDto.builder()
				.surveyNo(survey.getSurveyNo())
				.writer(survey.getWriter())
				.surveyTitle(survey.getSurveyTitle())
				.deadline(survey.getDeadline())
				.anonymousVote(survey.getAnonymousVote())
				.allowMultiple(survey.getAllowMultiple())
				.build();
	}
}
