package com.eroom.survey.entity;

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
@Table(name="survey_voter")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyVoter {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "voter_no")
	private Long voterNo;
	
	@Column(name = "survey_no")
	private Long surveyNo;
	
	@Column(name = "voter")
	private Long voter;
}
