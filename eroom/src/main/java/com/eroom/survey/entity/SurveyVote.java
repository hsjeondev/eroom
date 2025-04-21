package com.eroom.survey.entity;

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
@Table(name = "survey_vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyVote {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteNo;

    private Long surveyNo;

    private Long itemNo;

    private Long voter;
}
