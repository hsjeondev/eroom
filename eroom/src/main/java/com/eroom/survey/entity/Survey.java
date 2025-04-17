package com.eroom.survey.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="survey")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Survey {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="survey_no")
	private Long surveyNo;

	@Column(name="writer")
	private String writer;
	
	@Column(name="survey_title")
	private String surveyTitle;
	
	@Column(name="deadline")
	private LocalDateTime deadline;
	
	@Column(name="anonymous_vote")
	private String anonymousVote;
	
	@Column(name="allow_multiple")
	private String allowMultiple;
	
	@Column(name="reg_date")
	private LocalDateTime regDate;

}
